package com.korotyx.virtualentity

object KotlinMain
{
    @JvmStatic
    fun main(args: Array<String>)
    {
        val entity: ExampleEntity = ExampleEntity("UNIQUE_ID_EXAMPLE")
        entity.create()
        println("before: " + entity.serialize())

        entity.value1 = "MyName"
        entity.value2 = false
        entity.value3 = arrayOf("Hello","World")

        val generatedEntity : ExampleEntity = ExampleEntity.get("UNIQUE_ID_EXAMPLE")!!
        println("after: " + generatedEntity.serialize())
    }
}