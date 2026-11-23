package com.example.chargeanim

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    // Cemiuiler wali values (yahi number badlo, baqi file mat chhedo):
    //   0 = Default
    //   1 = Disable (animation band)
    //   2 = Aura
    //   3 = Streamer
    //   4 = Alpha
    private val STYLE = 2

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        try {
            val chargeUtils = XposedHelpers.findClassIfExists(
                "com.android.keyguard.charge.ChargeUtils", lpparam.classLoader
            ) ?: return

            when (STYLE) {
                1 -> {
                    // Disable: animation band
                    XposedBridge.hookAllMethods(chargeUtils, "isChargeAnimationDisabled", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = true
                        }
                    })
                }
                4 -> {
                    // Alpha: wave animation
                    val names = setOf(
                        "com.android.keyguard.charge.ChargeUtils",
                        "com.android.keyguard.charge.container.MiuiChargeContainerView"
                    )
                    XposedBridge.hookAllMethods(chargeUtils, "supportWaveChargeAnimation", object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            param.result = Throwable().stackTrace.any { it.className in names }
                        }
                    })
                    val waveView = XposedHelpers.findClassIfExists(
                        "com.android.keyguard.charge.wave.WaveView", lpparam.classLoader
                    )
                    if (waveView != null) {
                        XposedBridge.hookAllMethods(waveView, "updateWaveHeight", object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                XposedHelpers.setIntField(param.thisObject, "mWaveXOffset", 0)
                            }
                        })
                    }
                }
                else -> {
                    // Default / Aura / Streamer: system ka asli type number
                    // Aura = 0, Streamer = 1 (Default bhi 0 hi hai)
                    val type = if (STYLE == 3) 1 else 0
                    XposedBridge.hookAllMethods(chargeUtils, "getChargeAnimationType", object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.result = type
                        }
                    })
                }
            }
            XposedBridge.log("ChargeAnimHook: hooked ChargeUtils, STYLE=$STYLE")
        } catch (t: Throwable) {
            XposedBridge.log("ChargeAnimHook: failed -> $t")
        }
    }
}
