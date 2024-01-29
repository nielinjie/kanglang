package xyz.nietongxue.app.develop

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import xyz.nietongxue.common.coordinate.Selector
import xyz.nietongxue.common.coordinate.and
import xyz.nietongxue.common.coordinate.selector
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.material.StringMaterial
import java.io.File

class FilesLab : StringSpec({
    "simple memory" {
        val memory = MemoryLib()
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"))
        memory.post(location, material)
        val getting = memory.get(phaseEqual("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }

    }
    "two factor" {
        val memory = MemoryLib()
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"), aspect("entity"))
        memory.post(location, material)
        val getting = memory.get(phaseEqual("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
        val selector: Selector =
            (phaseEqual("require").and(aspectEqual("entity")))
        val getting2 = memory.get(aspectEqual("entity"))
        getting2.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
    }
    "file" {
        val fileLib = FileLib(root = File("./testLib"))
        val material: Material = StringMaterial("require content")
        val location = location(phase("require"), aspect("entity"))
        fileLib.post(location, material)
        val getting = fileLib.get(phaseEqual("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it shouldBe material
            }
        }
    }
    "load" {
        val fileLib = FileLib(root = File("./testLib"))
        val getting = fileLib.get(phaseEqual("require"))
        getting.shouldHaveSize(1).also {
            it.first().also {
                it.shouldBeInstanceOf<Material>().content.shouldBe("require content")
            }
        }
    }
})