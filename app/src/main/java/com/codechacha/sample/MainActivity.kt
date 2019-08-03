package com.codechacha.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.content.FileProvider


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_INSTALL_PERMISSION = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apkPath= filesDir.absolutePath + "/app.apk"
        if (!File(apkPath).exists()) {
            copyApkToAppFolder()
        }
        try {
            installApk()
            if (packageManager.canRequestPackageInstalls()) {
                installApk()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, REQUEST_INSTALL_PERMISSION)
            }
        } catch (e: IOException) {
        }
    }

    private fun installApk() {
        val apkPath= filesDir.absolutePath + "/app.apk"
        val apkUri =
            FileProvider.getUriForFile(applicationContext,
                BuildConfig.APPLICATION_ID + ".fileprovider", File(apkPath))

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        startActivity(intent)
    }

    private fun copyApkToAppFolder() {
        val inputStream = assets.open("app.apk")
        val outPath= filesDir.absolutePath + "/app.apk"
        val outputStream = FileOutputStream(outPath)
        while (true) {
            val data = inputStream.read()
            if (data == -1) {
                break
            }
            outputStream.write(data)
        }
        inputStream.close()
        outputStream.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_INSTALL_PERMISSION) {
            if (packageManager.canRequestPackageInstalls()) {
                installApk()
            }
        }
    }

    private fun uninstallApp() {
        val packageURI = Uri.parse("package:com.komorebi.memo")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
        startActivity(uninstallIntent)
    }
}
