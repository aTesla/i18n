package org.example.util

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties
import kotlin.collections.iterator

class Excel2ProperiesRes(val excel: String, val res: String) {
    val inputFile = File(excel)
    val outputDir = File(res)
    val cache = mutableMapOf<File, List<File>>()

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

//        cache.forEach { (dir, files) ->
//            val j = JSONObject()
//            files.forEach {
//                val json = FileInputStream(it).bufferedReader().use { reader -> reader.readText() }
//                val jsonObject = JSONObject(json)
//                val keys = jsonObject.keys()
//                for (key in keys) {
//                    val value = jsonObject.get(key)
//                    j.put(key, value)
//                }
//            }
//            dir.deleteRecursively()
//            if (!dir.exists()) dir.mkdirs()
//
//            val file = File(dir, "value.json")
//            if (!file.exists()) {
//                file.createNewFile()
//            }
//            FileOutputStream(file).bufferedWriter().use { writer ->
//                writer.write(j.toString())
//            }
//        }
    }

    private fun parse_(sheet: XSSFSheet) {
        val languageRow = sheet.getRow(sheet.firstRowNum)
        for (languageIndex in (languageRow.firstCellNum + 1)..languageRow.lastCellNum) {
            val language = languageRow.getCell(languageIndex)?.stringCellValue
            if (language.isNullOrBlank()) continue
            val dir = File(outputDir, language)
            if (dir.exists() && !dir.isDirectory) {
                dir.delete()
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val properties = Properties()
            for (i in (sheet.firstRowNum + 1)..sheet.lastRowNum) {
                val row = sheet.getRow(i)
                val key = row.getCell(row.firstCellNum.toInt())?.stringCellValue
                if (key.isNullOrBlank()) continue
                val value = row.getCell(languageIndex)?.richStringCellValue?.toString()
                properties.setProperty(key, value ?: "")
            }
            val jsonFile = File(dir, "${sheet.sheetName}.properties")
            if (!jsonFile.exists()) {
                jsonFile.createNewFile()
            }
            properties.store(FileOutputStream(jsonFile).bufferedWriter(), "${sheet.sheetName}.json")

            if (cache[dir] == null) {
                cache.put(dir, listOf(jsonFile))
            } else {
                cache.put(dir, cache[dir]!! + jsonFile)
            }
        }
    }
}