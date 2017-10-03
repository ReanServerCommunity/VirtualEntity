package com.korotyx.virtualentity.implemention.command

import com.korotyx.virtualentity.command.property.ColorSet
import com.korotyx.virtualentity.command.property.CommandProperty
import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.util.StringUtil

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/**
 * Permission is a class created by subdividing the functions of the privileges used in the game.
 * @author korotyx
 * @since 1.0.0
 * @See Permission
 */
internal class Permission0(private var name: String, private var defaultOP: Boolean = true) : Permission
{
    init
    {
        name = name.trimMargin()
        if(name.startsWith('.')) name = name.substring(1)
    }

    //
    // Operator overloading
    //
    operator fun minus(str : String) : Permission
    {
        if(this.name.endsWith(".$str")) return Permission0(str.replaceAfterLast(".$str", ""), this.defaultOP)
        throw RuntimeException("You cannot use this object to process this calculation")
    }
    operator fun plus(str : String) : Permission = Permission0("$this.name.$str", this.defaultOP)

    override fun setValue(value: String) { this.name = value}
    override fun getValue(): String? = name

    override fun setDefaultOp(isDefault : Boolean) { this.defaultOP = isDefault }
    override fun isDefaultOp(): Boolean = defaultOP

    private var permissionMessage : String? = CommandProperty.PERMISSION_DEINED_MESSAGE
    override fun setMessage(message : String) { this.permissionMessage = message }
    override fun getMessage() : String? = this.permissionMessage

    override fun getPermission() : String = this.getPermission(null)
    override fun getPermission(target : CommandSender?) : String
    {
        target ?: return this.name
        var colorSet: String?
        target.let {
            colorSet = if(this.isDefaultOp() || this.hasPermission(target)) ColorSet.ALLOWED_PERM_COLORSET
                       else ColorSet.DEINED_PERM_COLORSET

            val value : String = if(this.isDisconnected()) "$this.name.$ColorSet.DEFAULT_CHILD_PERMISSION" else this.name
            return StringUtil.color("$colorSet" + value)
        }
    }

    private fun hasPermission(sender : CommandSender) : Boolean
    {
        return when(sender)
        {
            is ConsoleCommandSender -> true
            is Player ->
            {
                //TODO("It will be up-to-date")
                true
            }
            else -> false
        }
    }

    private fun isDisconnected(): Boolean = this.name.split(".").isEmpty()
}