package com.dooboolab.flutterinapppurchase

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import android.content.Context
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import android.content.pm.PackageManager.NameNotFoundException

/** FlutterInappPurchasePlugin  */
class FlutterInappPurchasePlugin : FlutterPlugin, ActivityAware {
    private var androidInappPurchasePlugin: AndroidInappPurchasePlugin? = null
    private var channel: MethodChannel? = null
    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        onAttached(binding.applicationContext, binding.binaryMessenger)
    }

    private fun onAttached(context: Context, messenger: BinaryMessenger) {
        isAndroid = isPackageInstalled(context, "com.android.vending")

        channel = MethodChannel(messenger, "flutter_inapp")
        androidInappPurchasePlugin = AndroidInappPurchasePlugin()
        androidInappPurchasePlugin!!.setContext(context)
        androidInappPurchasePlugin!!.setChannel(channel)
        channel!!.setMethodCallHandler(androidInappPurchasePlugin)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        channel!!.setMethodCallHandler(null)
        channel = null
        androidInappPurchasePlugin!!.setChannel(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        androidInappPurchasePlugin!!.setActivity(binding.activity)
    }

    override fun onDetachedFromActivity() {
        androidInappPurchasePlugin!!.setActivity(null)
        androidInappPurchasePlugin!!.onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    private fun setAndroidInappPurchasePlugin(androidInappPurchasePlugin: AndroidInappPurchasePlugin) {
        this.androidInappPurchasePlugin = androidInappPurchasePlugin
    }

    companion object {
        private var isAndroid = false

        fun getStore(): String = "play_store"

        private fun isPackageInstalled(ctx: Context, packageName: String): Boolean {
            return try {
                ctx.packageManager.getPackageInfo(packageName, 0)
                true
            } catch (e: NameNotFoundException) {
                false
            }
        }

        fun isAppInstalledFrom(ctx: Context, installer: String?): Boolean {
            val installerPackageName = ctx.packageManager.getInstallerPackageName(ctx.packageName)
            return installer != null && installerPackageName != null && installerPackageName.contains(
                installer
            )
        }
    }
}
