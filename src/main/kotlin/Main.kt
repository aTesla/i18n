package org.example

import org.example.util.Excel2Db
import org.example.util.Excel2Res
import org.example.util.Res2Excel
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun main() {
//    Excel2Db(
//        "/Users/lcj/Downloads/i18n.xlsx",
//        "/Users/lcj/Downloads/res"
//    ).excel2Res()

    Excel2Db(
        "/home/lcj/Downloads/i18n.xlsx",
        "/home/lcj/Downloads/res"
    ).excel2Res()

//    Excel2Res(
//        "/Users/lcj/Downloads/i18n.xlsx",
//        "/Users/lcj/Downloads/res"
//    ).excel2Res()

    //Res2Excel().res2Excel()

//    val file = File("/Users/lcj/HuoLian/wisdom-uae-Android/product/mobile/build/outputs/apk/debug/wisdom-0.0.3.apk")
//    val hash = calculateFileHash(file)
//    println(hash)
    /*

    0b261a5462740a0ce29cf4a2dffcd0385cbfed7ff887fa32ca2182cd769079e4
    0b261a5462740a0ce29cf4a2dffcd0385cbfed7ff887fa32ca2182cd769079e4

     */
}

fun calculateFileHash(file: File, algorithm: String = "SHA-256"): String {
    val digest = MessageDigest.getInstance(algorithm)
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

    FileInputStream(file).use { input ->
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
    }

    // 转换为十六进制字符串
    val hashBytes = digest.digest()
    return hashBytes.joinToString("") { "%02x".format(it) }
}

fun testApkUpload() {
//    viewModelScope.launch {
//        val cmd = ApkDTO(
//            version_code = 3,
//            version_name = "0.0.3",
//            upgrade_content = "1.we fix mpc issue\n2.we fix login issue\n3.we fix avatar issue\n4.we optimize wallet\n5.we support fingerprint\n6.we fix some bug",
//            pkg_url = "https://wisdom-pkg.s3.us-east-1.amazonaws.com/apk-version/wisdom-0.0.3.apk",
//            is_force_upgrade = true,
//            app_platform = 1,
//            hash_256 = "0b261a5462740a0ce29cf4a2dffcd0385cbfed7ff887fa32ca2182cd769079e4",
//        )
//        val result = runCatching { walletApi.postApk(cmd) }
//        if (result.isSuccess) {
//            ToastUtil.showShort("Upload successful")
//        }
//    }
}