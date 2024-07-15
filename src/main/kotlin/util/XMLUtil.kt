package org.example.util

import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.Node
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import java.io.File

object XMLUtil {
    fun writFormatXML(xmlFile: File, map: Map<String, Any?>) {
        val document = DocumentHelper.createDocument()
        val resources = document.addElement("resources")
        map.forEach { (name, value) ->
            val string = resources.addElement("string")
            string.addAttribute("name", name)
            string.text = value.toString()
        }

        xmlFile.outputStream().use { output ->
            val format = OutputFormat.createPrettyPrint()
            format.encoding = "UTF-8"
            val xmlWriter = XMLWriter(output, format)
            xmlWriter.write(document)
            //xmlWriter.close()
        }
    }

    fun readFormatXML(xmlFile: File): Map<String, String> {
        val mutableMap = mutableMapOf<String, String>()

        val reader = SAXReader()
        val document = reader.read(xmlFile)
        val root = document.rootElement
        repeat(root.nodeCount()) { i ->
            val node = root.node(i)
            when (node.nodeType) {
                Node.ELEMENT_NODE -> {
                    val element = node as Element
                    val key = element.attribute("name").value
                    val value = element.text ?: ""
                    mutableMap[key] = value
                }
            }
        }

        return mutableMap.toMap()
    }
}