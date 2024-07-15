package org.example.util

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

object Xml4ExcelUtil {
    fun convert(xmlFile: File) {
        val outputFile = File("/Users/lcj/Downloads/i18n_new.xlsx")
        val workbook = XSSFWorkbook()

        val sheetName = xmlFile.name.substring(8, xmlFile.name.length - 4)
        val sheet = workbook.createSheet(sheetName)
        val firstRow = sheet.createRow(0)
        listOf(
            "key",
            "ar",
            "de",
            "en",
            "es",
            "fr",
            "ja",
            "ko",
            "ru",
            "zh-Hans",
            "zh-Hant",
        ).forEachIndexed { columnIndex, value ->
            firstRow.createCell(columnIndex).setCellValue(value)
        }

        val map = XMLUtil.readFormatXML(xmlFile)
        map.toList().forEachIndexed { rownum, (key, value) ->
            val row = sheet.createRow(rownum + 1)
            row.createCell(0).setCellValue(key)
            row.createCell(9).setCellValue(value)
        }

        outputFile.outputStream().use { output ->
            workbook.use {
                workbook.write(output)
            }
        }
    }
}