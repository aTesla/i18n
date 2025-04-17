package org.example

import org.example.upload.ApkUploadUseCase
import org.example.util.Excel2Db
import org.example.util.Excel2JsonRes
import org.example.util.Excel2ProperiesRes
import org.example.util.Excel2Res
import org.example.util.Res2Excel
import org.example.util.util.Excel2ResSingle
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun main() {
//    Excel2Res(
//        "/Users/lcj/Downloads/i18n.xlsx",
//        "/Users/lcj/Downloads/res"
//    ).excel2Res()
//    ApkUploadUseCase().invoke()

    val home = System.getenv("HOME")
    Excel2ResSingle(
        "$home/Downloads/i18n.xlsx",
        "$home/Downloads/res"
    ).excel2Res()
//    Excel2JsonRes(
//        "/home/lcj/IdeaProjects/i18n/src/main/kotlin/i18n.xlsx",
//        "/home/lcj/IdeaProjects/i18n/src/main/kotlin/resources"
//    ).excel2Res()
//    Excel2ProperiesRes(
//        "/home/lcj/IdeaProjects/i18n/src/main/kotlin/i18n.xlsx",
//        "/home/lcj/IdeaProjects/i18n/src/main/resources"
//    ).excel2Res()

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