package com.example.chargeanim

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {

    private val STYLE = 2
    private val DISABLE_ANIMATION = false

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.android.systemui") return
        try {
            val cls = XposedHelpers.findClass("com.android.keyguard.charge.ChargeUtils", lpparam.classLoader)

            XposedBridge.hookAllMethods(cls, "getChargeAnimationType", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    param.result = STYLE
                }
            })
            XposedBridge.hookAllMethods(cls, "isChargeAnimationDisabled", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    param.result = DISABLE_ANIMATION
                }
            })
            XposedBridge.log("ChargeAnimHook: hooked ChargeUtils")
        } catch (t: Throwable) {
            XposedBridge.log("ChargeAnimHook: failed -> $t")
        }
    }
}
