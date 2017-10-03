package com.korotyx.virtualentity.plugin.command

import com.korotyx.virtualentity.command.CommandBuilder
import com.korotyx.virtualentity.command.misc.ParameterType
import com.korotyx.virtualentity.plugin.RebukkitPlugin
import org.bukkit.command.CommandSender

class VirtualEntityCommand : CommandBuilder<VirtualEntityCommand>("ve")
{
    init
    {
        this.addAliasCommand("virtualentity", "ventity")
        this.setParameter(RebukkitPlugin.loadParameter("args", ParameterType.OPTIONAL))
    }

    override fun perform(sender: CommandSender, argc: Int, args: List<String>?): Boolean
    {
        return false
    }
}
