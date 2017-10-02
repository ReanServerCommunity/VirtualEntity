package com.korotyx.virtualentity

import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.base.VirtualEntity
import com.korotyx.virtualentity.plugin.RebukkitPlugin

class ExampleEntity(uniqueId: String) : VirtualEntity<ExampleEntity>(uniqueId)
{
    companion object
    {
        fun get(obj : Any) : ExampleEntity? = ExampleEntityCollector.getInstance().getEntity(obj)
    }

    private var value0 : Int = 1

     var value1 : String = "2"

     var value2 : Boolean = true

     var value3 : Array<String> = arrayOf("a", "b", "c")

    private var value4 : List<String>  = ArrayList()

    private var value5 : Double = 3.4

    private var value6 : Float = 4.56f

    private var value7 : Permission

    fun getValue7() : Permission = value7

    init
    {
        val perm : Permission = RebukkitPlugin.loadPermission("rebukkitexample.root", true)
        this.value7 = perm
    }
}