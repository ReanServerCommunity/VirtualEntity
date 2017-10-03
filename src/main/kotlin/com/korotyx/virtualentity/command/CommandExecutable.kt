package com.korotyx.virtualentity.command

import org.bukkit.command.CommandSender

interface CommandExecutable
{
    /**
     *
     * The perform function is a function that is executed when the command is run. This is equivalent to the role of OnCommand.
     * This is implemented by overriding it in the subclass. If you don't need to do, you don't have to it.
     *
     * @param sender Source of the command
     * @param args Passed command arguments, List type
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(CommandSender, Command, String, String[])
     * @see CommandBuilder#execute(CommandSender, List)
     * @since 1.0.0
     * @author korotyx
    */
    fun perform(sender : CommandSender, argc : Int, args: List<String>?): Boolean
}