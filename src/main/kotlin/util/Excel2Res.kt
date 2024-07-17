package org.example.util

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

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
        println("it=${workbook.map { sheet -> sheet.sheetName }}")
        workbook.forEach { sheet ->
            parse(sheet as XSSFSheet)
        }
    }

    private fun parse(sheet: XSSFSheet) {
        val firstRowNum = sheet.firstRowNum
        val lastRowNum = sheet.lastRowNum

        /* 第一行第一列 */
        val firstRow = sheet.getRow(firstRowNum)
        val firstCellNum = firstRow.firstCellNum
        val lastCellNum = firstRow.lastCellNum

        for (cellnum in firstCellNum + 1 until lastCellNum) {
            val languageTag = firstRow.getCell(cellnum)?.stringCellValue
            /* 目前只要翻译这三种语言 */
            if (!(languageTag == "ar" || languageTag == "en" || languageTag == "zh-Hans")) continue

            val mutableMap = mutableMapOf<String, String>()
            println(sheet.sheetName + "-" + languageTag)
            for (rownum in firstRowNum + 1..lastRowNum) {
                val row = sheet.getRow(rownum) ?: continue
                if (row.firstCellNum.toInt() == -1) continue

                val key = row.getCell(row.firstCellNum.toInt()).stringCellValue
                val value = row.getCell(cellnum)?.stringCellValue ?: ""
                if (key.isNotBlank()) mutableMap[key] = value
                //println("it=ok=${sheet.sheetName}_${languageTag} ${row.firstCellNum} $key")
            }
            println(mutableMap)
            val valuesDir = when (languageTag) {
                "zh-Hans" -> "values-zh-rCN"
                "zh-Hant" -> "values-zh-rTW"
                else -> "values-$languageTag"
            }
            val xmlFile = "strings_${sheet.sheetName}.xml"
            val parent = File("$outputDir${File.separator}${valuesDir}")
            if (!parent.exists()) parent.mkdirs()
            val dstXmlFile = File(parent.absolutePath, xmlFile)
            println(dstXmlFile.absolutePath)
            XMLUtil.writFormatXML(dstXmlFile, mutableMap)
            /* 默认语言 */
            if (languageTag == "en") {
                val default = File("$outputDir${File.separator}values${File.separator}strings_${sheet.sheetName}.xml")
                if (!default.parentFile.exists()) default.parentFile.mkdirs()
                XMLUtil.writFormatXML(default, mutableMap)
            }
        }
    }
}