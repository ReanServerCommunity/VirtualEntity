package com.korotyx.virtualentity.command.misc

import com.korotyx.virtualentity.system.UserConsoleMode
import org.bukkit.command.CommandSender

interface Parameter : UserConsoleMode
{
    fun setChild(param: Parameter)
    fun getChild() : Parameter?
    fun getChild(index : Int) : Parameter?
    fun hasChild(): Boolean

    fun hasPermission(): Boolean
    fun getPermission(): String?

    fun setParameterType(type: ParameterType)
    fun getParameterType() : ParameterType

    fun getParameterLabel(): String

    fun isAllowed(sender : CommandSender) : Boolean

    fun getParam(): String
    fun serParam(param: String)
    fun length(): Int
}