package org.example.util

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.util.TreeMap
import java.util.regex.Pattern
import kotlin.streams.asSequence

class Excel2Res(val excel: String, val res: String) {
    val inputFile = File(excel)
    val outputDir = File(res)

    fun excel2Res() {
        inputFile.inputStream().use { input ->
            val workbook = XSSFWorkbook(input)
            iterateSheets(workbook)
        }
    }

    private fun iterateSheets(workbook: XSSFWorkbook) {
        println("sheetNames=${workbook.map { sheet -> sheet.sheetName }}")
        workbook.asSequence()
            .sortedBy { sheet -> sheet.sheetName }
            .forEach { sheet ->
//                println(sheet.sheetName + "[" + sheet.firstRowNum + "," + sheet.lastRowNum + "]")
                val firstRow = sheet.getRow(sheet.firstRowNum)
//                println("..." + "[" + firstRow.firstCellNum + "," + firstRow.lastCellNum + "]")
                parse_(sheet as XSSFSheet)
            }
    }

    private fun parse_(sheet: XSSFSheet) {
        val firstRow = sheet.getRow(sheet.firstRowNum)

        for (j in (firstRow.firstCellNum + 1)..firstRow.lastCellNum) {
            val language = firstRow.getCell(j)?.stringCellValue
            if (!language.isNullOrBlank()) {
                val mutableMap = mutableMapOf<String, String>()
                for (i in (sheet.firstRowNum + 1)..sheet.lastRowNum) {
                    val row = sheet.getRow(i)
                    val key = row.getCell(row.firstCellNum.toInt())?.stringCellValue
                    if (key.isNullOrBlank()) continue
                    val value = row.getCell(j)?.richStringCellValue?.toString()
                    mutableMap[key] = resolveValue(value ?: "")
                }
//                when (language) {
//                    "ar", "de", "en", "es", "zh-Hans", "zh-Hant" -> {
                val valuesDir = when (language) {
                    "zh-Hans" -> "values-zh-rCN"
                    "zh-Hant" -> "values-zh-rTW"
                    else -> "values-$language"
                }
                val parent = File("$outputDir${File.separator}${valuesDir}").apply {
                    if (!exists()) mkdirs()
                }

                val xmlFileName = "strings_${sheet.sheetName}.xml"
                //                println("Create dir=$valuesDir file=$xmlFileName")
                val dstXmlFile = File(parent.absolutePath, xmlFileName)
                XMLUtil.writFormatXML(dstXmlFile, mutableMap)

                /* 生成默认语言(values) */
                if (language == "en") {
                    val default = File("$outputDir${File.separator}values${File.separator}${xmlFileName}")
                    if (!default.parentFile.exists()) default.parentFile.mkdirs()
                    XMLUtil.writFormatXML(default, mutableMap)
                }

//                    }
//                }
            }
        }
    }

    // https://developer.android.com/guide/topics/resources/string-resource
    fun resolveValue(value: String?): String {
        value ?: return ""
        val mutableList = mutableListOf<String>()
        val input = if (value.contains("'")) "\"$value\"" else value
        input.split("{string}").forEachIndexed { i, s ->
            if (i > 0) {
                mutableList.add("%$i\$s")
            }
            mutableList.add(s)
        }
        println(mutableList)
        println(mutableList.joinToString(""))
        return mutableList.joinToString("")
    }

    private fun parse(sheet: XSSFSheet) {
        if (sheet.sheetName == "wallet") println(sheet.sheetName + sheet.firstRowNum)
        val firstRow = sheet.getRow(sheet.firstRowNum)
//        val languageList = (firstRow.firstCellNum until firstRow.lastCellNum)
//            .map { cellnum -> firstRow.getCell(cellnum) }
//            .map { cell -> cell?.stringCellValue ?: "" }
//        val keyList = (sheet.firstRowNum until sheet.lastRowNum)
//            .map { rownum -> sheet.getRow(rownum) }
//            .map { row -> row.getCell(row.firstCellNum.toInt())?.stringCellValue ?: "" }

        if (sheet.sheetName == "contacts") println(firstRow.lastCellNum)
        ((firstRow.firstCellNum + 1)..firstRow.lastCellNum)
            .filterNot { j ->
                val language = firstRow.getCell(j)?.stringCellValue
                language.isNullOrBlank()
            }
            .filter { j ->
                val language = firstRow.getCell(j)?.stringCellValue
                language == "ar" || language == "en" || language == "zh-Hans"
            }
            .forEach { j ->
                val language = firstRow.getCell(j)?.stringCellValue

                val map = ((sheet.firstRowNum + 1)..sheet.lastRowNum)
                    .mapNotNull { rownum -> sheet.getRow(rownum) }
                    .filter { row -> row.firstCellNum.toInt() != -1 }
                    .filterNot { row ->
                        val key = row.getCell(row.firstCellNum.toInt())?.stringCellValue
                        key.isNullOrBlank()
                    }
                    .associate { row ->
                        val key = row.getCell(row.firstCellNum.toInt()).stringCellValue
                        val value = row.getCell(j)?.stringCellValue ?: ""
                        key to value
                    }

                val valuesDir = when (language) {
                    "zh-Hans" -> "values-zh-rCN"
                    "zh-Hant" -> "values-zh-rTW"
                    else -> "values-$language"
                }
                val xmlFileName = "strings_${sheet.sheetName}.xml"
//                println("Create dir=$valuesDir file=$xmlFileName")
                val parent = File("$outputDir${File.separator}${valuesDir}")
                if (!parent.exists()) parent.mkdirs()
                val dstXmlFile = File(parent.absolutePath, xmlFileName)
                XMLUtil.writFormatXML(dstXmlFile, map)

                /* 生成默认语言(values) */
                if (language == "en") {
                    val default = File("$outputDir${File.separator}values${File.separator}${xmlFileName}")
                    if (!default.parentFile.exists()) default.parentFile.mkdirs()
                    XMLUtil.writFormatXML(default, map)
                }
            }
    }
}