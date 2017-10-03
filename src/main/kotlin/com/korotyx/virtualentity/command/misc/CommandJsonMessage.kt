package com.korotyx.virtualentity.command.misc

import com.korotyx.virtualentity.json.FancyMessage
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

internal class CommandJsonMessage(private var message : String, private var desc : MutableList<String> = ArrayList())
{
    private val messageBuilder : FancyMessage = FancyMessage(message)

    fun getMessageBuilder() : FancyMessage = messageBuilder

    fun getDescription() : MutableList<String> = desc

    fun addMessage(message : String, index : Int = -1) : MutableList<String>
    {
        when (index)
        {
            -1 -> desc.add(ChatColor.translateAlternateColorCodes('&', message))
            else -> desc.add(index, message)
        }
        return desc
    }

    fun send(sender : CommandSender)
    {
        messageBuilder.tooltip(desc.asIterable())
        messageBuilder.send(sender)
    }
}