package org.example.util

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class Res2Excel {
    fun res2Excel() {
        //"/Users/lcj/Downloads/res"
        //"/Users/lcj/Downloads/i18n_new.xlsx"
        res2Excel(
            resDir = "/Users/lcj/Downloads/res",
            outputExcelFile = "/Users/lcj/Downloads/i18n_new.xlsx",
        )
    }

    /* res文件夹转excel */
    fun res2Excel(resDir: String, outputExcelFile: String) {
        val workbook = XSSFWorkbook()

        val valuesDirList = File(resDir).listFiles { pathname ->
            pathname != null && pathname.isDirectory && pathname.name.startsWith("values-")
        }?.sorted()
        //println("valueDirs=" + valuesDirList?.sorted())

        /* 所有sheet的列名 */
        val columnNames = mutableListOf("name")
        val languageTags = valuesDirList?.map { valuesDir ->
            when (valuesDir.name) {
                "values-zh-rCN" -> "zh-Hans"
                "values-zh-rTW" -> "zh-Hant"
                else -> valuesDir.name.substring(7)
            }
        }
        if (!languageTags.isNullOrEmpty()) columnNames.addAll(languageTags)

        /* 遍历res->values */
        valuesDirList?.forEach { valuesDir ->
            val languageTag = when (valuesDir.name) {
                "values-zh-rCN" -> "zh-Hans"
                "values-zh-rTW" -> "zh-Hant"
                else -> valuesDir.name.substring(7)
            }

            /* 功能模块分类 */
            valuesDir.listFiles { pathname ->
                pathname != null && pathname.isFile && pathname.name.startsWith("strings")
            }?.sorted()?.forEach { xmlFile ->
                val sheetname = xmlFile.name.substring(8, xmlFile.name.length - 4)
                var sheet = workbook.getSheet(sheetname)
                if (sheet == null) {
                    //println("[ sheetname:$sheetname, name:${languageTag} ]")
                    sheet = workbook.createSheet(sheetname)
                    /* 创建第一行-列名 */
                    val firstRow = sheet.createRow(0)
                    columnNames.forEachIndexed { columnIndex, value ->
                        firstRow.createCell(columnIndex).setCellValue(value)
                    }
                }

                val columnindex = columnNames.indexOf(languageTag)
                //println("[ sheetname:$sheetname, name:${languageTag} ${} ]")
                val map = XMLUtil.readFormatXML(xmlFile)
                //println(map)
                map.toList().forEachIndexed { rownum, (key, value) ->
                    var row = sheet.getRow(rownum + 1)
                    if (row == null) {
                        row = sheet.createRow(rownum + 1)
                    }
                    row.createCell(0).setCellValue(key)
                    row.createCell(columnindex).setCellValue(value)
                    //println("index=${columnNames.indexOf(languageTag)} $value")
                }

            }
        }

        /* 写入到Excel文件中 */
        val excelFile = File(outputExcelFile)
        excelFile.outputStream().use { output ->
            workbook.use {
                workbook.write(output)
            }
        }

    }
}