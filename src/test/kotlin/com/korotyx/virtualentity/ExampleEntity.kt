package com.korotyx.virtualentity

import com.korotyx.virtualentity.command.Permission
import com.korotyx.virtualentity.implemention.command.PermissionImpl
import com.korotyx.virtualentity.base.VirtualEntity

class ExampleEntity : VirtualEntity<ExampleEntity>
{
    companion object
    {
        fun get(obj : Any) : ExampleEntity? = ExampleEntityCollector.getInstance().getEntity(obj)
    }

    private fun init()
    {
        val perm : Permission = PermissionImpl()
        perm.setBody("examplevalue")
        perm.setValue("child")
        this.value7 = perm
    }

    constructor(uniqueId : String) : super(uniqueId)
    {
        init()
    }


    private var value0 : Int = 1

     var value1 : String = "2"

     var value2 : Boolean = true

     var value3 : Array<String> = arrayOf("a", "b", "c")

    private var value4 : List<String>  = ArrayList()

    private var value5 : Double = 3.4

    private var value6 : Float = 4.56f

    private var value7 : Permission = PermissionImpl()

    fun getValue7() : Permission = value7
}