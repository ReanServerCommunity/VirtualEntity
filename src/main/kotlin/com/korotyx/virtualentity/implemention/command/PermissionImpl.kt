package com.korotyx.virtualentity.implemention.command

import com.korotyx.virtualentity.command.ColorSet
import com.korotyx.virtualentity.command.misc.Permission
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Permission is a class created by subdividing the functions of the privileges used in the game.
 * @author korotyx
 * @since 1.0.0
 */
internal class PermissionImpl(var name: String, private var defaultOP: Boolean = true) : Permission
{
    override fun isDefaultOp(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setValue(child: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setBody(base: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getValue(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBody(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun isDefaultOP() : Boolean = defaultOP

    fun hasPermission(sender : CommandSender) : Boolean = sender.hasPermission(this.name)

    override fun getPermissionName(): String = this.getPermissionName(null)
    override fun getPermissionName(target : CommandSender?) : String
    {

        target ?: return this.name
        var colorSet : String? = null
        target.let {
            if(target.isOp) colorSet = if(this.isDefaultOP()) ColorSet.ALLOWED_PERM_COLORSET
            else if(this.hasPermission(target)) ColorSet.ALLOWED_PERM_COLORSET
            else ColorSet.DEINED_PERM_COLORSET

            return ChatColor.translateAlternateColorCodes('&',colorSet +
                    if(this.isDisconnected()) this.name = "$this.name.$ColorSet.DEFAULT_CHILD_PERMISSION" else this.name)
        }
    }

    init
    {
        name = name.trimMargin()
        if(name.startsWith('.')) name = name.substring(1)
    }

    operator fun minus(str : String) : Permission
    {
        if(this.name.endsWith(".$str"))
        {
            return PermissionImpl(str.replaceAfterLast(".$str", ""), this.isDefaultOP())
        }
        throw RuntimeException("You cannot use this object to process this calculation")
    }

    operator fun plus(str : String) : Permission = PermissionImpl("$this.name.$str", this.defaultOP)

    fun  isDisconnected(): Boolean = this.name.split(".").isEmpty()

    var permissionMessage : String? = null

    fun  getMessage(): String? = this.permissionMessage
}