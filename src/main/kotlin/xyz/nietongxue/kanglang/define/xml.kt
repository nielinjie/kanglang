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


fun toXML(caseDefine: CaseDefine): String {
    return xmlHeadEnd(
        """
        |<case name="${caseDefine.name}"  id="${modelId(caseDefine.name)}">
        |   <casePlanModel id="${planModelId(caseDefine.name)}">
        |   ${caseDefine.stages.joinToString("\n") { planItem(it) }}
        |   ${caseDefine.stages.joinToString("\n") { toXML(it) }}
        |   </casePlanModel>
        |</case>
    """.trimMargin()
    )
}

fun planItem(stageDefine: StageDefine): String {
    return """
        |<planItem id="${planItemId(stageDefine.name)}" definitionRef="${modelId(stageDefine.name)}"/>
    """.trimMargin()

}

fun toXML(stageDefine: StageDefine): String {
    return """
        |<stage name="${stageDefine.name}" id="${modelId(stageDefine.name)}">
        |    ${stageDefine.tasks.joinToString("\n") { planItem(it) }}
        |    ${stageDefine.tasks.filter { it.entry != null }.joinToString("\n") { sentryXML(it as EntryCriterion) }}
        |    ${stageDefine.tasks.joinToString("\n") { toXML(it) }}
        |</stage>
    """.trimMargin()
}

fun toXML(taskDefine: TaskDefine): String {
    return """
        |<humanTask name="${taskDefine.name}" id="${modelId(taskDefine.name)}"/>
    """.trimMargin()
}

fun planItem(taskDefine: TaskDefine): String {
    return """
        |<planItem id="${planItemId(taskDefine.name)}" definitionRef="${modelId(taskDefine.name)}">
        |   ${taskDefine.entry?.let { inPlanItem(it) } ?: ""}
        |</planItem>
    """.trimMargin()
}

fun inPlanItem(sentryDefine: SentryDefine): String {
    return """
        |<entryCriterion id="${entryCriteronId(sentryDefine)}" sentryRef="${sentryId(sentryDefine.name)}"/>
    """.trimMargin()
}
fun entryCriteronId(sentryDefine: SentryDefine): String {
    return "entry_criterion_id_${sentryDefine.name}".lowerUnderscore()
}

fun sentryXML(entryCriterion: EntryCriterion): String {
    return """
        |<sentry id="${sentryId(entryCriterion.entry!!.name)}">
        |   <planItemOnPart id="sentry_on_${sentryId(entryCriterion.entry!!.name)}" sourceRef="${planItemId(entryCriterion.entry!!.planItemOn)}">
        |    ${eventXML(entryCriterion.entry!!.event)}
        |   </planItemOnPart>
        |</sentry>
    """.trimMargin()
}

fun sentryId(name: String): String {
    return "sentry_id_$name".lowerUnderscore()
}

fun eventXML(event: SentryEvent): String {
    return when (event) {
        SentryEvent.Complete -> "<standardEvent>complete</standardEvent>"
        else -> error("unknown event: $event")
    }

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


fun xmlHeadEnd(body: String) = """
    <?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/CMMN/20151109/MODEL"
             xmlns:flowable="http://flowable.org/cmmn"
             targetNamespace="http://www.flowable.org/casedef">
""".trimIndent() + "\n" + body + "\n" + "</definitions>"

fun planModelId(modelName: String): String {
    return "planModel_id_${modelId(modelName)}".lowerUnderscore()
}

fun planItemId(modelName: String): String {
    return "planItem_id_${modelId(modelName)}".lowerUnderscore()
}

fun modelId(modelName: String): String {
    return "model_id_$modelName".lowerUnderscore()
}