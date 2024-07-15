package org.example.util

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

object Excel4XmlUtil {
    lateinit var outputDir: File
    fun startConvert(inputFile: File, outputDir: File) {
        this.outputDir = outputDir
        inputFile.inputStream().use { input ->
            val workbook = XSSFWorkbook(input)
            iterateSheets(workbook)
        }
    }

    fun iterateSheets(workbook: XSSFWorkbook) {
        println("it=${workbook.map { sheet -> sheet.sheetName }}")
        workbook.forEach { sheet ->
            parse(sheet as XSSFSheet)
        }
    }

    fun parse(sheet: XSSFSheet) {
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
                val cell = row.getCell(cellnum)
                val key = row.getCell(row.firstCellNum.toInt()).stringCellValue
                val value = cell?.stringCellValue ?: ""
                if (key.isNotBlank()) mutableMap[key] = value
            }
            println(mutableMap)
            val valuesDir = "values-$languageTag"
            val xmlFile = "strings-${sheet.sheetName}.xml"
            val parent = File("$outputDir${File.separator}${valuesDir}")
            if (!parent.exists()) parent.mkdirs()
            val dstXmlFile = File(parent.absolutePath, xmlFile)
            println(dstXmlFile.absolutePath)
            XMLUtil.writFormatXML(dstXmlFile, mutableMap)
        }
    }
}