package com.korotyx.virtualentity.command.misc

import org.bukkit.command.CommandSender

interface Parameter
{
    fun setChild(param: Parameter)

    fun getChild() : Parameter?

    fun getChild(index : Int) : Parameter?

    fun hasChild(): Boolean

    fun getPermission(): String?
    fun getParamValue(target: CommandSender): String
}