package xyz.nietongxue.app.develop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.material.StringMaterial
import java.io.File

class FilesLab : StringSpec({
    "simple memory" {
        val memory = MemoryLib()
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"))
        memory.set(location, material)
        val getting = memory.get(selectorPhaseIs("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }

    }
    "two factor"{
        val memory = MemoryLib()
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"), aspect("entity"))
        memory.set(location, material)
        val getting = memory.get(selectorPhaseIs("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
        //FIXME selector must be able to composite
//        val getting2 = memory.get(selectorPhaseIs("require").pipe(selectorAspectIs("entity")))
        val getting2 = memory.get(selectorAspectIs("entity"))
        getting2.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
    }
    "file"{
        val fileLib = FileLib(root = File("./testLib"))
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"), aspect("entity"))
        fileLib.set(location, material)
        val getting = fileLib.get(selectorPhaseIs("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
    }
    "load"{
        val fileLib = FileLib(root = File("./testLib"))
        val getting = fileLib.get(selectorPhaseIs("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it.shouldBeInstanceOf<Material>().content.shouldBe("require content")
            }
        }
    }
})