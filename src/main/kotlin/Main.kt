package org.example

import org.example.util.Excel4XmlUtil
import org.example.util.Xml4ExcelUtil
import java.io.File

fun main() {
    println("Hello World!")

    val input = File("/Users/lcj/Downloads/i18n.xlsx")
    val output = File("/Users/lcj/Downloads/res")
    Excel4XmlUtil.startConvert(input, output)

//    Xml4ExcelUtil.convert(File("/Users/lcj/Downloads/strings_phone_change.xml"))
}