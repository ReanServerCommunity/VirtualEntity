package com.korotyx.virtualentity.implemention.command

import com.korotyx.virtualentity.command.property.ColorSet
import com.korotyx.virtualentity.command.misc.Parameter
import com.korotyx.virtualentity.command.misc.ParameterType

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

internal class Parameter0(private var param : String, private var type : ParameterType = ParameterType.OPTIONAL,
                             private var userMode : Boolean = true,
                             private var consoleMode : Boolean = true) : Parameter
{
    companion object
    {
        val REQUIREMENT_FORMAT : String = ColorSet.PARAM_REQUIREMENT_COLORSET + "<%s>"
        val OPTIONAL_FORMAT    : String = ColorSet.PARAM_OPTIONAL_COLORSET + "[%s]"
        val UNAVAILABLE_FORMAT  : String = ColorSet.PARAM_UNAVAILABLE_COLORSET + "{%s}"
    }

    override fun getParam() : String = param
    override fun serParam(param : String) { this.param = param }

    override fun getParameterType(): ParameterType = type
    override fun setParameterType(type : ParameterType) { this.type = type }

    private var permission : String? = null
    override fun hasPermission() : Boolean = this.permission != null
    override fun getPermission() : String? = this.permission

    override fun setUserMode(enable : Boolean) { userMode = enable}
    override fun setConsoleMode(enable : Boolean) { consoleMode = enable}

    private var childParameter : Parameter? = null
    override fun setChild(param : Parameter) { this.childParameter = param }
    override fun hasChild() : Boolean = childParameter != null
    override fun getChild(): Parameter? = getChild(0)
    override fun getChild(index : Int) : Parameter?
    {
        return if(index <= 0) childParameter
        else
        {
            var param : Parameter? = this.getChild()
            for(i in 0..index) param = this.getChild()
            return param
        }
    }

    override fun isAllowed(sender : CommandSender) : Boolean = when(sender)
    {
        is Player -> userMode
        is ConsoleCommandSender -> consoleMode
        else -> false
    }

    override fun getParameterLabel() : String = when(type)
    {
        ParameterType.REQUIREMENT -> String.format(REQUIREMENT_FORMAT, this.param)
        ParameterType.OPTIONAL    -> String.format(OPTIONAL_FORMAT,    this.param)
        ParameterType.UNAVAILABLE -> String.format(UNAVAILABLE_FORMAT, this.param)
    }
}