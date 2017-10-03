package com.korotyx.virtualentity

import com.korotyx.virtualentity.command.CommandBuilder
import com.korotyx.virtualentity.plugin.RebukkitPlugin
import org.bukkit.command.CommandSender

class ExampleCommand : CommandBuilder<ExampleCommand>("Example")
{
    val ex2 : ExampleCommand2 = ExampleCommand2()
    init
    {
        this.addAliasCommand("ex", "exam")
        this.setPermission(RebukkitPlugin.loadPermission("examplecommand", true))
        this.addCommandDescription("Hello my command example!")
        this.addChildCommand(ex2)
    }

    override fun perform(sender: CommandSender, argc: Int, args: List<String>?): Boolean
    {
        return super.perform(sender, argc, args)
    }
}

class ExampleCommand2 : CommandBuilder<ExampleCommand2>("Example2")
{
    init{
        this.addAliasCommand("ex2", "Exam2")
        this.setPermission(RebukkitPlugin.loadPermission("example2", true))
        this.addCommandDescription("example2!")
    }

    override fun perform(sender: CommandSender, argc: Int, args: List<String>?): Boolean {
        return super.perform(sender, argc, args)
    }
}