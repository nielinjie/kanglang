package xyz.nietongxue.app.develop

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.nietongxue.common.base.md5
import xyz.nietongxue.common.base.startBy
import xyz.nietongxue.common.coordinate.*
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.material.WithName

@Configuration
class DomainConfig {
    @Bean
    fun domain(materialLib: MaterialLib): Domain {
        return LibraryAsDomain(materialLib)
    }

    @Bean
    fun materialLib(): MaterialLib {
        return MemoryLib()
    }
}

class LibraryAsDomain(val materialLib: MaterialLib) : Domain {
    override fun get(name: String): Any {
        require(name.startsWith("phase:"))
        return materialLib.get(selectorPhaseIs(name.startBy("phase:")!!))
    }

    override fun set(name: String, value: Any) {
        require(name.startsWith("phase:"))
        val material = when (value) {
            is Material -> value
            is String -> object : Material {
                override val content: String = value
            }

            else -> error("not supported yet")
        }
        materialLib.set(phaseLocation(name.startBy("phase:")!!), material)
    }

    private fun phaseLocation(phase: String): Location {
        return object : Location {
            override val values: List<Value> = listOf(
                CategoryValue<String>(MaterialDimension.Phase, phase)
            )
        }
    }

}


fun selectorPhaseIs(phase: String): Selector {
    return object : Selector {
        override val predicates: List<Predicate> = listOf(
            object : Predicate {
                override fun test(value: Value): Boolean {
                    return value.dimension == MaterialDimension.Phase && (
                            ((value as CategoryValue<String>).d).let { it == phase })
                }

                override val dimension: Dimension
                    get() = MaterialDimension.Phase
            }
        )
    }
}

fun selectorAspectIs(aspect: String): Selector {
    return object : Selector {
        override val predicates: List<Predicate> = listOf(
            object : Predicate {
                override fun test(value: Value): Boolean {
                    return value.dimension == MaterialDimension.Aspect && (
                            ((value as CategoryValue<String>).d).let { it == aspect })
                }

                override val dimension: Dimension
                    get() = MaterialDimension.Aspect
            }
        )
    }
}

interface MaterialLib {
    fun get(selector: Selector): List<Material>
    fun set(location: Location, material: Material)
}

class MemoryLib : MaterialLib {
    var material: MutableMap<Location, List<Material>> = mutableMapOf()
    override fun get(selector: Selector): List<Material> {
        return selector.select(material.keys.toList()).map { material[it]!! }.flatten()

    }


    override fun set(location: Location, material: Material) {
        this.material.set(location, (this.material.get(location) ?: listOf()) + material)
    }
}


fun Material.fileName(): String {
    return when (this) {
        is WithName -> this.name
        else -> "_"+this.content.md5()
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

fun phase(name: String): Value {
    return CategoryValue<String>(MaterialDimension.Phase, name)
}

fun aspect(name: String): Value {
    return CategoryValue<String>(MaterialDimension.Aspect, name)
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
