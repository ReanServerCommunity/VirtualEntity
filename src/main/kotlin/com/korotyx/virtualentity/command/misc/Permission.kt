package com.korotyx.virtualentity.command.misc

import org.bukkit.command.CommandSender

/**
 * Permission is a class created by subdividing the functions of the privileges used in the game.
 * @author Kunonx
 * @since 1.0.0-SNAPSHOT
 */
interface Permission
{
    fun getBody() : String?

    fun getValue() : String?

    fun setBody(base : String)

    fun setValue(child : String)

    fun isDefaultOp(): Boolean

    fun getPermissionName() : String

    fun getPermissionName(target : CommandSender?) : String
}