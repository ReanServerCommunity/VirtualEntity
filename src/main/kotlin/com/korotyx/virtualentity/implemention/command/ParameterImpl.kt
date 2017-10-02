package com.korotyx.virtualentity.implemention.command

import com.korotyx.virtualentity.command.ColorSet
import com.korotyx.virtualentity.command.CommandType
import com.korotyx.virtualentity.command.misc.Parameter

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

internal class ParameterImpl(private var param : String, private var requirement : Boolean,
                             private var allowConsole : Boolean = true,
                             private var allowPlayer : Boolean = true) : Parameter
{
    private var permission : String? = null
    fun hasPermission() : Boolean = this.permission != null
    fun checkPermission(relativeCommand : CommandType, sender : CommandSender) : Boolean
    {
        return if(relativeCommand.hasPermission())
        {
            val perm : String? = relativeCommand.getPermissionValue()
            sender.hasPermission(perm + permission)
        }
        else
        {
            sender.hasPermission(this.permission)
        }
    }

    override fun getPermission() : String? = this.permission

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

    companion object
    {
        val REQUIREMENT_FORMAT : String = ColorSet.PARAM_REQUIREMENT_COLORSET + "<%s>"

        val OPTIONAL_FORMAT    : String = ColorSet.PARAM_OPTIONAL_COLORSET + "[&s]"
    }

    fun isAllowed(sender : CommandSender) : Boolean = when(sender)
    {
        is Player -> allowPlayer
        is ConsoleCommandSender -> allowConsole
        else -> false
    }

    override fun getParamValue(target : CommandSender) : String = when(this.requirement)
    {
        true  -> String.format(REQUIREMENT_FORMAT, this.param)
        false -> String.format(OPTIONAL_FORMAT, this.param)
    }
}