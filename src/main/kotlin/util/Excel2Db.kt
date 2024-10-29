package org.example.util

import com.google.gson.Gson
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class Excel2Db(val excel: String, val res: String) {
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
        val list = workbook.map { sheet ->
            parse(sheet as XSSFSheet)
        }.flatten()

        File(outputDir, "res.json").outputStream().bufferedWriter().use { output ->
            output.write(Gson().toJson(list))
        }
//        workbook.find { it.sheetName == "main" }?.let { sheet -> parse(sheet as XSSFSheet) }
    }

    private fun parse(sheet: XSSFSheet): List<Map<String, String?>> {
        val firstRow = sheet.getRow(sheet.firstRowNum)
        val thKeyList = (firstRow.firstCellNum until firstRow.lastCellNum)
            .map { cellnum -> firstRow.getCell(cellnum) }
            .map { it?.stringCellValue ?: "" }


        val jsonArray = ((sheet.firstRowNum + 1)..sheet.lastRowNum)
            .mapNotNull { rownum -> sheet.getRow(rownum) }
            .filter { row -> row.firstCellNum.toInt() != -1 }
            .map { row ->
                val values = (firstRow.firstCellNum..firstRow.lastCellNum)
                    .map { cellnum -> row.getCell(cellnum) }
                    .map { it?.stringCellValue }
//                thKeyList.zip(values).joinToString(
//                    separator = ",",
//                    prefix = "{ ",
//                    postfix = " }"
//                ) { "\"${it.first}\":${if (it.second.isNullOrBlank()) null else "\"${it.second.toString().replace("\"","\\\"")}\""}" }
                thKeyList.zip(values).toMap()
            }

        return jsonArray
    }
}

//    Locale.getDefault()
//    println(Locale.getISOLanguages().toList().also { println(it.size) })
//    println(Locale.getISOCountries().toList().also { println(it.size) })
//    println(Locale.getAvailableLocales().toList().map { it.toLanguageTag() }.filter { it.startsWith("zh") }.sorted().also { println(it.size) })
//    val thList = listOf(
//        "unicodeLocaleAttributes",
//        "script",
//        "country",
//        "variant",
//        "language",
//        "displayCountry",
//        "displayLanguage",
//        "displayName",
//        "displayScript",
//        "displayVariant",
//        "extensionKeys",
//        "isO3Country",
//        "isO3Language",
//        "unicodeLocaleAttributes",
//        "unicodeLocaleKeys",
//        "hasExtensions",
//        "stripExtensions",
//        "toLanguageTag",
//    )
//    val maxLength = thList.maxOf { it.length }
//    val maxKey = thList.find { it.length == maxLength } ?: ""
//    println("--" + maxKey)
//    println(
//        listOf(
//            "unicodeLocaleAttributes",
//            "script",
//            "country",
//            "variant",
//            "language",
//            "displayCountry",
//            "displayLanguage",
//            "displayName",
//            "displayScript",
//            "displayVariant",
//            "extensionKeys",
//            "isO3Country",
//            "isO3Language",
//            "unicodeLocaleAttributes",
//            "unicodeLocaleKeys",
//            "hasExtensions",
//            "stripExtensions",
//            "toLanguageTag",
//        )
//            .map { "${it}${(0 until (maxKey.length - it.length)).map { "-" }.joinToString("")}" }
//            .joinToString("\t")
//    )
//    Locale.getAvailableLocales()
//        .toList()
////        .filter { it.language == "zh" }
//        .filter { it.language == "en" }
//        .mapIndexed { i, it ->
//            val localStr = listOf(
//                "script" to it.script,
//                "country" to it.country,
//                "variant" to it.variant,
//                "language" to it.language,
//                "displayCountry" to it.displayCountry,
//                "displayLanguage" to it.displayLanguage,
//                "displayName" to it.displayName,
//                "displayScript" to it.displayScript,
//                "displayVariant" to it.displayVariant,
//                "extensionKeys" to it.extensionKeys,
//                "isO3Country" to runCatching { it.isO3Country }.getOrNull(),
//                "isO3Language" to runCatching { it.isO3Language }.getOrNull(),
//                "unicodeLocaleAttributes" to it.unicodeLocaleAttributes,
//                "unicodeLocaleKeys" to it.unicodeLocaleKeys,
//                "hasExtensions" to it.hasExtensions(),
//                "stripExtensions" to it.stripExtensions(),
//                "toLanguageTag" to it.toLanguageTag(),
//            )
//            localStr.map { "\"${it.first}\" : \"${it.second}\"" }.joinToString(prefix = "{", postfix = "}")
//        }
//        .joinToString(prefix = "[", postfix = "]")
//        .also { println(it) }

//    Excel2Res(
//        "/home/lcj/Downloads/i18n.xlsx",
//        "/home/lcj/Downloads/res"
//    ).excel2Res()