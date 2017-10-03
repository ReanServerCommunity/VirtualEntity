package com.korotyx.virtualentity.command

import com.korotyx.virtualentity.command.misc.CommandJsonMessage
import com.korotyx.virtualentity.command.misc.Parameter
import com.korotyx.virtualentity.command.misc.ParameterType
import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.command.property.ColorSet
import com.korotyx.virtualentity.command.property.CommandProperty
import com.korotyx.virtualentity.plugin.RebukkitPlugin
import com.korotyx.virtualentity.system.GenericIdentity
import com.korotyx.virtualentity.system.UserConsoleMode
import com.korotyx.virtualentity.util.StringUtil

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

import java.util.*
import kotlin.collections.ArrayList

typealias CommandType            = CommandBuilder<*>
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
 * @param T
 */
abstract class CommandBuilder<T : CommandBuilder<T>>(private var mainCommand : String) : GenericIdentity<T>(),
        CommandExecutable, UserConsoleMode
{
    companion object
    {
        // When you output the help page to the player, you decide how many commands to send.
        // On console, It's automatically assigned the maximum number of commands.
        // The recommended value's 5 ~ 8, Other's not recommended.
        var MAX_PAGE_SIZE : Byte = 7

        // This is the message to be printed at the top when printing the help page.
        var HEADER_MESSAGE : String = "&e====&f [&b Help commands for &e\"{0}\" &a1/{1} &bpage(s) &f] &e===="

        var COMMAND_OUTPUT_FORMAT : String = "/{0} {1}"
    }

    /**
     * Set the command value. However, Changing the value in the middle is not recommended.
     * @param command The command
     */
    protected fun setCommand(command : String) { this.mainCommand = command }

    /**
     * Get the command value.
     * @return the command value
     */
    fun getCommand() : String = mainCommand

    //
    private lateinit var handle : RebukkitPlugin

    /**
     *
     */
    protected fun setHandlePlugin(plugin : RebukkitPlugin) { handle = plugin }

    /**
     *
     */
    fun getHandlePlugin() : RebukkitPlugin? = handle

    //
    private var commandDescription : MutableList<String> = ArrayList()
    fun addCommandDescription(s : String) { commandDescription.add(s) }
    fun setCommandDescription(s : MutableList<String>) { this.commandDescription = s }

    /**
     *
     */
    fun getCommandDescription() : MutableList<String> = commandDescription

    //
    private var aliasCommand : MutableList<String> = ArrayList()

    /**
     *
     */
    fun getAliasCommand() : MutableList<String> = aliasCommand

    /**
     *
     */
    fun hasAliasCommand(find : String? = null) : Boolean
    {
        find ?: return aliasCommand.isEmpty()
        find.let {
            return aliasCommand.contains(find.toLowerCase())
        }
    }

    fun isAlias(find : String) : Boolean = aliasCommand.contains(find)

    /**
     *
     */
    fun addAliasCommand(vararg s : String) = s.filterNot { it.split(" ").isNotEmpty() }.forEach { aliasCommand.add(it) }

    /**
     *
     */
    fun setAliasCommand(s : MutableList<String>) { aliasCommand = s }

    /**
     * Decide if you want to allow the player to use this command.
     * This can also affect child commands.
     */
    private var consoleMode : Boolean = true

    /**
     *
     */
    override fun setConsoleMode(enable : Boolean) { this.consoleMode = enable }

    //Decide if you want to allow the player to use this command.
    //This can also affect child commands.
    private var userMode : Boolean = true

    /**
     *
     */
    override fun setUserMode(enable : Boolean) { this.userMode = enable}

    //
    private var parameter : Parameter? = null
    fun setParameter(p : Parameter) { parameter = p }
    fun hasParameter() : Boolean = this.parameter != null
    fun getParamPermission() : String? = this.getRelativeParamPermission(0)
    fun getRelativeParamPermission(toIndex : Int = 0) : String?
    {
        val func : (Parameter, Int) -> String? = { p, i -> p.getChild(i)!!.getPermission() }
        var permissionValue : String? = this.getRelativePermission()
        permissionValue ?: return func(this.parameter!!, toIndex)
        permissionValue.let {
            var k = 0
            do
            {
                permissionValue = "$permissionValue.${this.parameter!!.getChild(k)!!.getPermission()}"
                k++
            }while (k < toIndex)
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
    private fun isRoot(): Boolean = this.parentCommand == null

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
     * @see Permission.getPermission
     */
    protected fun setPermission(perm : Permission) { this.permission = perm }

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
     * Get the Permission value relatively. Also, This method does not return a string containing the color code.
     * This method created for Java compatibility.
     * @return the string of permission value
     */
    fun getRelativePermission() : String? = getRelativePermissionColored(null)

    /**
     * Get the Permission value relatively.
     * This is affected by the parent Permission value and it can colorize the string according to the target.
     * @param colorTarget The object upon which to retrieve the Permission value
     * @return the string of permission value
     */
    private fun getRelativePermissionColored(colorTarget : CommandSender? = null) : String?
    {
        this.permission ?: return null
        this.parentCommand ?: return this.permission!!.getPermissionColored(colorTarget)

        // Get information about parent command.
        var parent : CommandType? = this.parentCommand

        // This permission value.
        var perm : String = this.permission!!.getPermissionColored(colorTarget)
        if(! inheritedParentMode) return perm
        while(parent != null)
        {
            perm = "${parent.getPermission()!!.getPermissionColored(colorTarget)}.$perm"
            parent = parent.parentCommand
        }
        return perm
    }

    /**
     * This is a command for help page.
     * It prints all the sub-commands available in that class.
     * If there is no value in Main Command, return value is null.
     * The output format is as follows: <br>
     * <code><b><ROOT_MAIN_COMMAND> <SUB_MAIN_COMMAND> <SUB_SUB_MAIN_COMMAND> ..... <CURRENT_ALL_COMMAND></b></code>
     * This method created for Java compatibility.
     */
    protected fun getRelativeCommand(target : CommandSender? = null) : String = getRelativeCommandColored(this, null, false, target)

    /**
     * This is a command for help page.
     * It prints all the sub-commands available in that class.
     * If there is no value in Main Command, return value is null.
     * The output format is as follows: <br>
     * <code><b><ROOT_COMMAND> <SUB_MAIN_COMMAND> <SUB_SUB_MAIN_COMMAND> ..... <CURRENT_ALL_COMMAND></b></code>
     * This method created for Java compatibility.
     */
    private fun getRelativeCommand(isMain : Boolean, target : CommandSender? = null) : String = getRelativeCommandColored(this, null, isMain, target)

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
    private fun getRelativeCommandColored(command : CommandType?, label : String? = null, isMain : Boolean = false, target : CommandSender? = null) : String
    {
        var subLabel: String? = label
        command ?: throw RuntimeException("command cannot be null")

        if(command.isRoot())
            subLabel = if(label == null) { command.getAllCommands(target) } else "${command.getAllCommands(target)} $label"
        else
            if(command.hasChildCommand())
            {
                subLabel = if(isMain) if(label == null) command.getAllCommands(target) else "${command.getAllCommands(target)} $label"
                else if(label == null) command.mainCommand else "${command.mainCommand} $label"
            }
        return if(command.isRoot()) subLabel!! else this.getRelativeCommandColored(command.parentCommand, subLabel, false, target)
    }

    /**
     * This is a command for help page.
     * Returns all commands in the class with commas.
     * The output format is as follows: <br>
     *
     * <ALIAS_COMMAND>,<ALIAS_COMMAND2>, ... ,<ALIAS_COMMAND>
     */
    private fun getAllCommands(target: CommandSender? = null): String
    {
        var s : String = ""
        val iter : Iterator<String> = this.aliasCommand.iterator()
        while(iter.hasNext())
        {
            val s2 = iter.next()
            s += s2
            if(iter.hasNext()) s += commandSentenceSymbol
        }
        return if(target == null) s
        else { return if(this.isAllowedCommand(target)) ColorSet.ALLOWED_PERM_COLORSET + s else ColorSet.DEINED_PERM_COLORSET + s }
    }

    private var commandSentenceSymbol : String = ","

    fun isAllowedCommand(target : CommandSender) : Boolean
    {
        val perm : String? = this.getRelativePermission()
        perm ?: return true
        return target.hasPermission(perm)
    }

    // Saves subclasses that operate on this command.
    protected var childCommand : MutableList<CommandType> = ArrayList()
    fun hasChildCommand() : Boolean = when(childCommand.size) { 0 -> false; else -> true }
    fun getChildCommand(cmd : String) : CommandType?
    {
        if (this.hasChildCommand()) return null
        return this.childCommand.firstOrNull { it.mainCommand.equals(cmd, true) || it.hasAliasCommand(cmd) }
    }
    fun addChildCommand(ch : CommandBuilder<*>)
    {
        ch.setParent(this)
        childCommand.add(ch)
    }
    fun getChildCommands() : MutableList<CommandType> = childCommand

    protected var externalCommand : MutableList<CommandType> = ArrayList()
    fun addExternalCommand(command: CommandType) { this.externalCommand.add(command)}
    fun getExternalCommands() : MutableList<CommandType> = externalCommand

    object CommandPriority
    {
        const val ASCENDING  : COMMAND_PRIORITY_DWORD = 0x01
        const val DESCENDING : COMMAND_PRIORITY_DWORD = 0x03
        const val RANDOM     : COMMAND_PRIORITY_DWORD = 0x04
    }

    private var commandPriorityValue : COMMAND_PRIORITY_DWORD = CommandPriority.ASCENDING

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

    fun sendDocumentionPage(sender : CommandSender)
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
                    mainCommand = "$mainCommand ${param.getParameterLabel()}"
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
                        relativeCommand = StringUtil.replaceValue(COMMAND_OUTPUT_FORMAT, relativeCommand, param.getParameterLabel())
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

    fun sendDocumentionPage(sender : CommandSender, page : Int = 0)
    {

    }

    fun execute(sender : CommandSender, args : MutableList<String>) : Boolean
    {
        if(args.size == 0)
        {
            if(this.hasChildCommand())
            {
                this.sendDocumentionPage(sender)
                return true
            }
            else
            {
                if(this.hasParameter())
                {
                    if(this.parameter!!.getParameterType() == ParameterType.REQUIREMENT)
                    {
                        //msg.send(sender, "&cThe param \"${this.parameter!!.getParam()}\" is required value!")
                        return false
                    }

                    if(! this.isAllowedCommand(sender))
                    {
                        //msg.send(sender, "${this.permission!!.getMessage()} (${this.getRelativePermission()}")
                        return false
                    }
                    return this.perform(sender, args.size, args)
                }
            }
        }
        else
        {
            if(args[0].equals(CommandProperty.DEFAULT_CHILD_PERMISSION, true) || args[0] == "?")
            {
                if(this.hasChildCommand() && CommandExecutable::class.java.isAssignableFrom(this.getGenericBaseType()))
                {
                    if(args.size >= 2 && StringUtil.isNumber(args[1]))
                    {
                        this.sendDocumentionPage(sender, Integer.parseInt(args[1]))
                    }
                    this.sendDocumentionPage(sender)
                    return true
                }
                if(! sender.hasPermission(this.getRelativePermission()))
                {
                    //msg.send(sender, "${this.permission!!.getMessage()} (${this.getRelativePermission()}")
                    return false
                }
                this.perform(sender, args.size, args)
            }
            else
            {
                if(! this.hasChildCommand() && this.hasParameter())
                {
                    if(this.parameter!!.length() >= 1)
                    {
                        if(this.parameter!!.length() < args.size) {
                            var s = this.getRelativeCommand()
                            var p = this.parameter
                            while (p != null) {
                                s = s + " " + p.getParameterLabel()
                                p = p.getChild()
                            }
                            //Msg.sendTxt(sender, "&cArgument limit exceeded:&f \"{0}\"", args.get(this.parameter.length()));
                            //Msg.sendTxt(sender, "&6Use Command:&f /{0}", s);
                            return false
                        }

                        // Check the command sender entered requirement parameter value.
                        var paramMax = 0
                        var p = this.parameter
                        while(p != null)
                        {
                            if(p.getParameterType() == ParameterType.REQUIREMENT &&
                                    args.size <= paramMax) {
                                //Msg.sendTxt(sender, "&cThe param \"" + p.getParam() + "&c\" is required value!");
                                return false
                            }
                            p = p.getChild()
                            paramMax++
                        }

                        if(! sender.hasPermission(this.getRelativePermission()))
                        {
                            //Msg.sendTxt(sender, "&cYou are not authorized for this command. &8{0}", this.getPermission());
                            return false
                        }
                        return this.perform(sender, args.size, args)
                    }
                    else
                    {
                        // Unexpected error.
                    }
                    //this.getActivePlugin().getLangConfiguration().sendMessage(sender, "System.UNKNOWN_COMMAND", "&b/" +  this.getRelativeCommand(true) + " ?");
                    return false
                }
                else
                {
                    for(c in this.getChildCommands())
                    {
                        if(c.mainCommand.equals(args[0], true) || c.isAlias(args[0]))
                        {
                            var argList : MutableList<String> = ArrayList(args)
                            argList.removeAt(0)
                            return c.execute(sender, argList)
                        }
                    }
                    //this.getActivePlugin().getLangConfiguration().sendMessage(sender, "System.UNKNOWN_COMMAND", "&e/" +  this.getRelativeCommand(true) + " ?");
                    return false
                }
            }
        }
        return false
    }

    override fun perform(sender: CommandSender, argc: Int, args: List<String>?) : Boolean = true
}