package org.example.util.util

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.example.util.XMLUtil
import java.io.File
import java.io.FileOutputStream
import java.util.TreeMap
import java.util.regex.Pattern
import kotlin.streams.asSequence

class Excel2ResSingle(val excel: String, val res: String) {
    val inputFile = File(excel)
    val outputDir = File(res)
    private val cache = mutableMapOf<String, MutableMap<String, String>>()

    fun excel2Res() {
        inputFile.inputStream().use { input ->
            val workbook = XSSFWorkbook(input)
            iterateSheets(workbook)
        }
//        outputDir.listFiles().filter { it.isDirectory }.forEach { dir ->
//            val file = File(dir, "string.xml")
//            FileOutputStream(file, true)
//                .bufferedWriter()
//                .use { writer ->
//                    dir.listFiles().forEach { xml ->
//                        xml.bufferedReader().use { reader ->
//                            writer.write(reader.readText())
//                        }
//                        if (!xml.equals(file)) xml.delete()
//                    }
//                }
//        }
        cache.forEach { (key, value) ->
            val parent = File("$outputDir${File.separator}${key}").apply {
                if (!exists()) mkdirs()
            }

            val xmlFile = File(parent.absolutePath, "strings.xml")
            XMLUtil.writFormatXML(xmlFile, value.toSortedMap())

            /* 生成默认语言(values) */
            if (key == "values-en") {
                val parent = File("$outputDir${File.separator}values").apply {
                    if (!exists()) mkdirs()
                }
                val xmlFile = File(parent.absolutePath, "strings.xml")
                XMLUtil.writFormatXML(xmlFile, value.toSortedMap())
            }
        }
    }

    private fun iterateSheets(workbook: XSSFWorkbook) {
        workbook.asSequence()
            .sortedBy { sheet -> sheet.sheetName }
            .forEach { sheet -> parseSheet(sheet as XSSFSheet) }
    }

    private fun parseSheet(sheet: XSSFSheet) {
        val firstRow = sheet.getRow(sheet.firstRowNum)

        for (j in (firstRow.firstCellNum + 1)..firstRow.lastCellNum) {
            val language = firstRow.getCell(j)?.stringCellValue
            if (language.isNullOrBlank()) continue
            val valuesDir = when (language) {
                "zh-Hans" -> "values-zh-rCN"
                "zh-Hant" -> "values-zh-rTW"
                else -> "values-$language"
            }
            if (cache[valuesDir] == null) {
                cache[valuesDir] = mutableMapOf()
                println(valuesDir)
            }


            val mutableMap = mutableMapOf<String, String>()
            for (i in (sheet.firstRowNum + 1)..sheet.lastRowNum) {
                val row = sheet.getRow(i)
                val key = row.getCell(row.firstCellNum.toInt())?.stringCellValue
                if (key.isNullOrBlank()) continue
                val value = row.getCell(j)?.richStringCellValue?.toString()
                mutableMap[key] = resolveValue(value ?: "")
            }

            cache[valuesDir]?.putAll(mutableMap)
        }
    }

    // https://developer.android.com/guide/topics/resources/string-resource
    private fun resolveValue(value: String?): String {
        value ?: return ""
        val mutableList = mutableListOf<String>()
        val input = if (value.contains("'")) "\"$value\"" else value
        input.split("{string}").forEachIndexed { i, s ->
            if (i > 0) {
                mutableList.add("%$i\$s")
            }
            mutableList.add(s)
        }
        //println(mutableList)
        //println(mutableList.joinToString(""))
        return mutableList.joinToString("")
    }
}