package com.korotyx.virtualentity.plugin

import com.korotyx.virtualentity.command.misc.Parameter
import com.korotyx.virtualentity.command.misc.ParameterType
import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.implemention.command.PermissionImpl

import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Constructor

abstract class RebukkitPlugin : JavaPlugin()
{
    companion object
    {
        fun loadPermission(name : String, defaultOP : Boolean) : Permission
        {
            val con : Constructor<PermissionImpl> = PermissionImpl::class.
                    java.getConstructor(String::class.java, Boolean::class.java)
            con.isAccessible = true
            return con.newInstance(name, defaultOP)
        }

        fun loadParameter(s: String, optional: ParameterType): Parameter
        {
            TODO("Up to date")
        }
    }

    override fun onEnable()
    {
        this.onEnableInner(null)
    }

    abstract protected fun onEnableInner(handleInstance : Any?)

    override fun onDisable()
    {
        this.onDisableInner(null)
    }

    abstract protected fun onDisableInner(handleInstance: Any?)
}