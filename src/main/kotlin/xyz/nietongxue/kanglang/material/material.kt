package xyz.nietongxue.kanglang.material

import xyz.nietongxue.common.base.toList
import xyz.nietongxue.common.base.toOrdered
import xyz.nietongxue.common.coordinate.*
import xyz.nietongxue.common.version.SingleBaseVersion
import xyz.nietongxue.common.version.VersionSingleStream

interface Material{
    val content:String
}


interface WithName{
    val name:String
}


class StringMaterial(override val content: String) : Material
