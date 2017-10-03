package com.korotyx.virtualentity.command.misc

import com.korotyx.virtualentity.security.DefaultOperator
import com.korotyx.virtualentity.system.MessageProvider
import org.bukkit.command.CommandSender

/**
 * Permission is a class created by subdividing the functions of the privileges used in the game.
 * @author Kunonx
 * @since 1.0.0-SNAPSHOT
 */
interface Permission : DefaultOperator, MessageProvider
{
    fun getValue() : String?

    fun setValue(value : String)

    fun setCustomPermission(perm: String)

    fun getPermission() : String

    fun getPermission(target : CommandSender?) : String

    fun setMessage(message: String)
}