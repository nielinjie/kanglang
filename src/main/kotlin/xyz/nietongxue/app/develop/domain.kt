package xyz.nietongxue.app.develop

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.startBy
import xyz.nietongxue.common.coordinate.*
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.material.WithName
import java.io.File

@Configuration
class DomainConfig {
    @Bean
    fun domain(materialLib: MaterialLib): Domain {
        return LibraryAsDomain(materialLib)
    }

    @Bean
    fun materialLib(): MaterialLib {
        return FileLib(File("./classRoomDoc"))
    }
}

class LibraryAsDomain(val materialLib: MaterialLib) : Domain {
    override fun get(name: String): Any {
        return materialLib.get(pathToSelector(Path.fromString(name)))
    }

    override fun set(name: String, value: Any) {
        val material = when (value) {
            is Material -> value
            is String -> object : Material {
                override val content: String = value
            }

            else -> error("not supported yet")
        }
        materialLib.post(pathToLocation(Path.fromString(name)), material)
    }


}

fun phaseEqual(phase: String): Selector {
    return listOf(phase(phase).matchEq()).selector()
}

fun aspectEqual(aspect: String): Selector {
    return listOf(aspect(aspect).matchEq()).selector()
}


interface MaterialLib {
    fun get(selector: Selector): List<Material>
    fun post(location: Location, material: Material)
    fun update(location: Location, f: (Material) -> Material)
}

class MemoryLib : MaterialLib {
    var material: MutableMap<Location, List<Material>> = mutableMapOf()
    override fun get(selector: Selector): List<Material> {
        return selector.select(material.keys.toList()).map { material[it]!! }.flatten()

    }


    override fun post(location: Location, material: Material) {
        this.material.set(location, (this.material.get(location) ?: listOf()) + material)
    }

    override fun update(location: Location, f: (Material) -> Material) {
        TODO("Not yet implemented")
    }

}


fun Material.fileName(): String {
    return when (this) {
        is WithName -> this.name
        else -> "_" + this.content.md5()
    }
}

interface MaterialDimension {
    object Area : MaterialDimension, PathLikeDimension("area")
    object Aspect : MaterialDimension,
        CategoryDimension<String>("aspect", listOf("entity", "info", "function", "presentation", "page"))

    //TODO version repository
//    object Version : MaterialDimension,
//        OrderedDimension<SingleBaseVersion>("version", VersionSingleStream(emptyList()).toList().toOrdered())

    object Layer : MaterialDimension,
        CategoryDimension<String>("layer", listOf("material", "model", "component", "artifact", "runtime"))

    object Phase : MaterialDimension,
        CategoryDimension<String>("phase", listOf("require", "design", "develop", "test", "release"))

}

fun phase(stringPoint: String): CategoryValue<String> {
    return CategoryValue<String>(MaterialDimension.Phase, stringPoint)
}

fun aspect(stringPoint: String): CategoryValue<String> {
    return CategoryValue<String>(MaterialDimension.Aspect, stringPoint)
}


fun location(vararg values: Value): Location {
    return object : Location {
        override val values: List<Value> = values.toList()
    }
}

val sortOrder: List<MaterialDimension> = listOf(
    MaterialDimension.Area,
    MaterialDimension.Layer,
    MaterialDimension.Phase,
    MaterialDimension.Aspect,
//    MaterialDimension.Version,

)


/*
location的string形式是： area_xxxx/layer_xxx/phase_xxx/aspect_xxx
 */

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

/*
selector的string形式是： phase_xxx/aspect_xxx
 */
//TODO 支持通配符。
fun pathToSelector(path: Path): Selector {
    val predicates: List<Predicate> = path.parts.map { part ->
        when {
            part.startsWith("phase_") -> phase(part.startBy("phase_")!!).matchEq()
            part.startsWith("layer_") -> TODO()
            part.startsWith("aspect_") -> aspect(part.startBy("aspect_")!!).matchEq()
            else -> {
                error("not supported yet")
            }
        }
    }
    return predicates.selector()
}