package xyz.nietongxue.kanglang.define

import org.w3c.dom.Document
import org.xml.sax.InputSource
import xyz.nietongxue.common.base.lowerUnderscore
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult



fun entryCriterionId(sentryDefine: SentryDefine): String {
    return "entry_criterion_id_${sentryDefine.name}".lowerUnderscore()
}
fun exitCriterionId(sentryDefine: SentryDefine): String {
    return "exit_criterion_id_${sentryDefine.name}".lowerUnderscore()
}



fun sentryId(name: String): String {
    return "sentry_id_$name".lowerUnderscore()
}

fun planModelId(modelName: String): String {
    return "planModel_id_${defineModelId(modelName)}".lowerUnderscore()
}

fun planItemId(modelName: String): String {
    return "planItem_id_${defineModelId(modelName)}".lowerUnderscore()
}

fun defineModelId(modelName: String): String {
    return "model_id_$modelName".lowerUnderscore()
}


fun pretty(xmlString: String): String {
    return xmlString.lines().map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n").let {
        prettyPrint(it, 4, false)
    }
}

fun prettyPrint(xmlString: String, indent: Int = 4, ignoreDeclaration: Boolean = true): String {
    return try {
        val src = InputSource(StringReader(xmlString))
        val document: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src)
        val transformerFactory = TransformerFactory.newInstance()
        transformerFactory.setAttribute("indent-number", indent)
        val transformer: Transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, if (ignoreDeclaration) "yes" else "no")
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        val out: Writer = StringWriter()
        transformer.transform(DOMSource(document), StreamResult(out))
        out.toString()
    } catch (e: Exception) {
        throw RuntimeException("Error occurs when pretty-printing xml:\n$xmlString", e)
    }
}



