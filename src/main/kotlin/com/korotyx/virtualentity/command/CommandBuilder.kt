package com.korotyx.virtualentity.command

import com.korotyx.virtualentity.command.misc.CommandJsonMessage
import com.korotyx.virtualentity.command.misc.Parameter
import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.plugin.RebukkitPlugin
import com.korotyx.virtualentity.system.GenericIdentity
import com.korotyx.virtualentity.util.StringUtil

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

import java.util.*

typealias CommandType =  CommandBuilder<*>
typealias COMMAND_PRIORITY_DWORD = Int
/**
 * CommandBuilder is an abstraction command skeleton that allows you to register a command
 * directly to Bukkit without any setting. This is an extended of original function, which
 * allows the developer to easily skip the complex process of registering commands in the
 * game and do it easily.<br>
 * It uses a self-referencing generic to avoid errors in grammar settings. The developer can
 * determine whether the class is activated by Handle (Base Plugin). It cannot be used even
 * if the command is registered with Bukkit when It's deactivated.<br><br>
 * <b>About self-referencing generic class</b>
 * http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ206
 * @author Korotyx
 * @version 1.0.0
 * @param T The class type that inherits from CommandBuilder
 */
open abstract class CommandBuilder<T : CommandBuilder<T>>(private var mainCommand : String) : GenericIdentity<T>()
{
    companion object
    {
        var MAX_PAGE_SIZE : Short = 7
        var HEADER_MESSAGE : String = "&e====&f [&b Help commands for &e\"{0}\" &a1/{1} &bpage(s) &f] &e===="
        var COMMAND_OUTPUT_FORMAT : String = "/{0} {1}: {2}"
    }

    protected fun setCommand(maincommand : String) { this.mainCommand = mainCommand }
    fun getCommand() : String = mainCommand

    //
    private lateinit var handle : RebukkitPlugin
    protected fun setHandlePlugin(plugin : RebukkitPlugin) { handle = plugin }
    fun getHandlePlugin() : RebukkitPlugin? = handle

    //
    private var commandDescription : MutableList<String> = ArrayList()
    fun getCommandDescription() : MutableList<String> = commandDescription

    //
    private var aliasCommand : MutableList<String> = ArrayList()
    fun getAliasCommand() : MutableList<String> = aliasCommand
    fun hasAliasCommand(find : String? = null) : Boolean
    {
        find ?: return aliasCommand.isEmpty()
        find.let {
            return aliasCommand.contains(find.toLowerCase())
        }
    }

    /**
     * Decide if you want to allow the player to use this command.
     * This can also affect child commands.
     */
    private var consoleMode : Boolean = true
    fun setConsoleMode(enable : Boolean) { this.consoleMode = enable }

    /**
     * Decide if you want to allow the player to use this command.
     * This can also affect child commands.
     */
    private var userMode : Boolean = true
    fun setUserMode(enable : Boolean) { this.userMode = enable}

    private var parameter : Parameter? = null
    fun hasParameter() : Boolean = this.parameter != null
    fun getParameterPermission(index : Int = 0) : String?
    {
        val func : (Parameter, Int) -> String? = { p, i -> p.getChild(i)!!.getPermission() }
        var permissionValue : String? = this.getPermissionValue()
        permissionValue ?: return func(this.parameter!!, index)
        permissionValue.let {
            for(k in 0..index)
                permissionValue = "$permissionValue.${this.parameter!!.getChild(k)!!.getPermission()}"
            return permissionValue
        }
    }


    // The parent class of this class.
    // This connects the commands of that class to the parent class in tree form.
    private var parentCommand : CommandType? = null



    /**
     * @return
     */
    fun hasParent() : Boolean = this.parentCommand != null

    /**
     * @return
     */
    private fun setParent(parent : CommandType) { this.parentCommand = parent }

    /**
     * @return
     */
    fun isRoot(): Boolean = this.parentCommand == null



    // The Permission for this command.
    // The Permission value of the child class is used in conjunction with the parent
    // Permission in the parent class.
    private var permission : Permission? = null

    // Determines whether the information value is inherited from the parent.
    // This is convenient for managing commands and is not recommended.
    private var inheritedParentMode : Boolean = true

    /**
     * Specifies a permission value. This can be affected by the value of the parent class.<br>
     * For example, Here is the code:<br>
     * <pre>
     * <code>
     * class ParentCommand : CommandBuilder<ParentCommand>
     * {
     *   init
     *   {
     *       this.setCommand("ps")
     *       this.setPermission("prospacecore")
     *       this.addChildCommand(ChildCommand())
     *   }
     * }
     * class ChildCommand : CommandBuilder<ChildCommand>
     * {
     *   init
     *   {
     *      setCommand("run")
     *      setPermission("child")
     *
     *   }
     * }
     * ...
     * println(new ParentCommand().getChildCommand("run").getPermissionValue())
     * </code>
     * </pre>
     * The output of this code will <code>"prospacecore.child"</code>.<br> But InheritedFromParent is false,
     * It's "child". This shows that the value can vary depending on the parent class.
     * That is, the child information changes automatically according to the parent value and
     * need to enter <code>/ps run</code> to use the ChildCommand's command.<br>
     * @param perm The permission of the CommandBuilder
     * @see Permission.getPermissionName
     */
    protected fun setPermission(perm : Permission) { this.permission = permission }

    /**
     * Determines whether to enable parent inheritance mode.
     * @param enable The value for whether to activate mode
     */
    protected fun setInheritedFromParent(enable: Boolean) { this.inheritedParentMode = enable }

    /**
     * Get the Permission for this class.
     * It takes the permission value of this command and is not affected by the parent class's information.
     * @return The permission object
     */
    fun getPermission() : Permission? = this.permission

    /**
     * Check for Permission value. If not, this command will work without any separate permissions.
     * @return true If the permission is null, otherwise false
     */
    fun hasPermission() : Boolean = this.permission != null

    /**
     * Get the permission value.
     * This method created for Java compatibility.
     * @return the string of permission value
     */
    fun getPermissionValue() : String? = getPermissionValue(null)

    /**
     * Get the Permission value.
     * This is affected by the parent Permission value and it can colorize the string according to the target.
     * @param colorTarget The object upon which to retrieve the Permission value
     * @return the string of permission value
     */
    fun getPermissionValue(colorTarget : CommandSender? = null) : String?
    {
        this.permission ?: return null
        this.parentCommand ?: return this.permission!!.getPermissionName(colorTarget)

        var parent : CommandType? = this.parentCommand
        var perm : String = this.permission!!.getPermissionName()

        if(! inheritedParentMode) return perm

        while(parent != null)
        {
            perm = "${parent.getPermission()!!.getPermissionName(null)}.$perm"
            parent = parent.parentCommand!!
        }
        return if(colorTarget == null) perm else {
            RebukkitPlugin.loadPermission(perm, this.permission!!.isDefaultOp()).getPermissionName(colorTarget)
        }
    }

    /**
     * for java method. <br>
     * Unfortunately, the Java grammar does not support initial argument values.
     * If you want to program using Java, see that method.
     */
    protected fun getRelativeCommand(target : CommandSender? = null) : String = getRelativeCommand(this, null, false, target)

    /**
     * for java method. <br>
     * Unfortunately, the Java grammar does not support initial argument values.
     * If you want to program using Java, see that method.
     */
    protected fun getRelativeCommand(isMain : Boolean, target : CommandSender? = null) : String = getRelativeCommand(this, null, isMain, target)

    /**
     * This is a command for help page.
     * It prints all the sub-commands available in that class.
     * If there is no value in Main Command, return value is null.
     * The output format is as follows: <br>
     * <code><b><ROOT_COMMAND> <SUB_MAIN_COMMAND> <SUB_SUB_MAIN_COMMAND> ..... <CURRENT_ALL_COMMAND></b></code>
     * @param command
     * @param label
     * @param isMain
     * @param target If this argument is not null, It can colorize the command based on this target
     */
    protected fun getRelativeCommand(command : CommandType?, label : String? = null, isMain : Boolean = false, target : CommandSender? = null) : String
    {
        var subLabel: String? = label
        command ?: throw RuntimeException("command cannot be null")
        if(command.isRoot())
            subLabel = if(label == null) { command.getAllCommands() } else "${command.getAllCommands()} $label"
        else
            if(command.hasChildCommand())
            {
                subLabel = if(isMain) if(label == null) command.getAllCommands() else "${command.getAllCommands()} $label"
                else if(label == null) command.mainCommand else "${command.mainCommand} $label"
            }
        return if(command.isRoot()) subLabel!! else this.getRelativeCommand(command.parentCommand, subLabel, false, target)
    }

    /**
     * This is a command for help page.
     * Returns all commands in the class with commas.
     * The output format is as follows: <br>
     * <MAIN_COMMAND>,<ALIAS_COMMAND>,<ALIAS_COMMAND2>, ... ,<ALIAS_COMMAND>
     */
    protected fun getAllCommands(target: CommandSender? = null): String
    {
        var s : String = ""
        val iter : Iterator<String> = this.aliasCommand.iterator()
        while(iter.hasNext())
        {
            val s2 = iter.next()
            s += s2
            if(iter.hasNext()) s += ","
        }
        return s
    }

    // Saves subclasses that operate on this command.
    protected var childCommand : MutableList<CommandType> = ArrayList()
    fun hasChildCommand() : Boolean = when(childCommand.size) { 0 -> false; else -> true }
    fun getChildCommand(cmd : String) : CommandType?
    {
        if (this.hasChildCommand()) return null
        return this.childCommand.firstOrNull { it.mainCommand.equals(cmd, true) || it.hasAliasCommand(cmd) }
    }

    fun getChildCommands() : MutableList<CommandType> = childCommand

    protected var externalCommand : MutableList<CommandType> = ArrayList()
    fun addExternalCommand(command: CommandType) { this.externalCommand.add(command)}
    fun getExternalCommands() : MutableList<CommandType> = externalCommand


    enum class CommandPriority(val value : COMMAND_PRIORITY_DWORD)
    {
        ASCENDING(1),
        DESCENDING(3),
        RANDOM(4)
    }

    private var commandPriorityValue : COMMAND_PRIORITY_DWORD = 1

    /**
     *
     */
    protected fun setCommandPriority(value : COMMAND_PRIORITY_DWORD) { this.commandPriorityValue = value }

    private fun sortCommandList(list : MutableList<CommandType>, value : COMMAND_PRIORITY_DWORD)
    {
        when (value) {
            0x01 -> {
                Collections.sort(list, { o1, o2 -> o1.getCommand().compareTo(o2.getCommand()) })
            }
            0x03 -> {
                Collections.sort(list, { o1, o2 -> o2.getCommand().compareTo(o1.getCommand()) })
            }
            0x04 -> {
                Collections.shuffle(list)
            }
            else -> {
            }
        }
    }

    protected fun sendHelp(sender : CommandSender)
    {
        // Collect commands that will appear on the help page.
        // It also takes an external command that is not related to this command.
        // The help page will sort in ascending order.
        val commands : MutableList<CommandType> = ArrayList()

        commands.addAll(this.externalCommand)
        commands.addAll(this.childCommand)

        sortCommandList(commands, commandPriorityValue)

        if(commands.size != 0)
        {
            val commandTexts : MutableList<CommandJsonMessage> = ArrayList()
            val maxPage = if(sender is ConsoleCommandSender) 1 else (commands.size / (MAX_PAGE_SIZE - 1)) + 1

            // Create a header message.
            val headerMessage = CommandJsonMessage(StringUtil.replaceValue(HEADER_MESSAGE, this.mainCommand, maxPage))
            commandTexts.add(headerMessage)

            // Creates a message to output the information of the command registered in this class.
            // This is adding the main command (this class) at the top of the page.
            var mainCommand : String = this.getRelativeCommand(true, sender)
            if(this.hasParameter())
            {
                var param : Parameter = this.parameter!!
                while(param.hasChild())
                {
                    mainCommand = "$mainCommand ${param.getParamValue(sender)}"
                    param = param.getChild()!!
                }
            }
            commandTexts.add(CommandJsonMessage(mainCommand, this.commandDescription))

            // The following process processes child commands and external commands
            // except the main command.

            // This is a function that shows one page.
            // Therefore, there is no reason to calculate the page size.
            val size_index : Int = when(sender)
            {
                is ConsoleCommandSender -> commands.size
                is Player -> if(commands.size >= MAX_PAGE_SIZE) MAX_PAGE_SIZE - 1 else commands.size
                else -> -1
            }
            for(index in 0..size_index)
            {
                val command : CommandType = commands[index]
                var relativeCommand = command.getRelativeCommand(true, sender)
                if(command.hasParameter())
                {
                    var param : Parameter = command.parameter!!
                    while(param.hasChild())
                    {
                        relativeCommand = "$relativeCommand ${param.getParamValue(sender)}"
                        param = param.getChild()!!
                    }
                }
                commandTexts.add(CommandJsonMessage(relativeCommand, command.commandDescription))
            }

            // Finally, messages are printing.
            for(e in commandTexts)
            {
                e.send(sender)
            }
        }
        else
        {
            // No provided help page.
        }
    }
}