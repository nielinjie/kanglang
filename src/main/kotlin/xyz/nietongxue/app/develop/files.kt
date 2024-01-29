package xyz.nietongxue.app.develop

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.common.base.startBy
import xyz.nietongxue.common.coordinate.*
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.material.WithName
import java.io.File


class FileLib(val root: File) : MaterialLib {
    val material: MutableMap<Location, List<Material>> =
        filesToMaterial(root, listFiles(root)).groupBy { it.first }.mapValues { it.value.map { it.second } }
            .toMutableMap()

    override fun get(selector: Selector): List<Material> {
        return if (selector.predicates.all {
                it.dimension == MaterialDimension.Phase
            }) {
            selector.select(material.keys.toList()).map { material[it]!! }.flatten()
        } else {
            error("not supported yet")
        }
    }

    override fun set(location: Location, material: Material) {

        this.material.set(location, (this.material.get(location) ?: listOf()) + material)
        val path = localToPath(location)
        val file = File(root, path.asString())
        file.mkdirs()
        File(file, material.fileName()).writeText(material.content)
    }
}

fun listFiles(dir: File): List<File> {
    val files = mutableListOf<File>()
    dir.listFiles()?.forEach {
        if (it.isDirectory) {
            files.addAll(listFiles(it))
        } else {
            files.add(it)
        }
    }
    return files
}

fun filesToMaterial(root: File, files: List<File>): List<Pair<Location, Material>> {
    return files.map { file ->
        val pathString = file.parentFile.toRelativeString(root)
        val path = Path.fromString(pathString)
        val location = pathToLocation(path)
        val fileName = file.name
        val material = if (file.startsWith("_")) {
            object : Material {
                override val content: String = file.readText()
            }
        } else {
            object : Material, WithName {
                override val content: String = file.readText()
                override val name: String = fileName
            }
        }
        location to material

    }
}

fun pathToLocation(path: Path): Location {
    return object : Location {
        override val values: List<Value> = path.parts.map {
            when {
                it.startsWith("phase_") -> CategoryValue(MaterialDimension.Phase, it.startBy("phase_")!!)
                it.startsWith("layer_") -> CategoryValue(MaterialDimension.Layer, it.startBy("layer_")!!)
                it.startsWith("aspect_") -> CategoryValue(MaterialDimension.Aspect, it.startBy("aspect_")!!)
//                it.startsWith("area_") -> PathLikeValue(MaterialDimension.Area,it.startBy("area_")!!.slp)
                else -> {
                    error("not supported yet")
                }
            }
        }
    }
}


fun localToPath(location: Location): Path {
    return sortOrder.map { dim ->
        location.values.firstOrNull {
            it.dimension == dim
        }?.string()
    }.filterNotNull().let {
        Path(it)
    }
}

fun Value.string(): String {
    return when (val d = this.dimension) {
        is MaterialDimension.Phase -> "phase_" + (this as CategoryValue<String>).d
        is MaterialDimension.Area -> "area_" + (this as PathLikeValue).path.toString()
        is MaterialDimension.Aspect -> "aspect_" + (this as CategoryValue<String>).d
        is MaterialDimension.Layer -> "layer_" + (this as CategoryValue<String>).d
//        is MaterialDimension.Version ->  "version_"+(this as NumberValue<SingleBaseVersion>).d.toString()
        else -> error("not supported yet")
    }.lowercase()
}
