package com.korotyx.virtualentity.util

import com.korotyx.virtualentity.security.IntegrityChecker
import org.bukkit.ChatColor
import java.security.NoSuchAlgorithmException
import java.util.*

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
class StringUtil
{
    companion object
    {
        fun color(str: String): String = ChatColor.translateAlternateColorCodes('&', str)

        fun replaceValue(string: String, vararg values: Any): String
        {
            var str = string
            if (values.isEmpty()) return str
            var i = 0
            while (str.matches(".*\\{[0-9]}.*".toRegex()))
            {
                var j = 0
                var value: String? = null
                j = if (i >= values.size) { values.size - 1 } else i
                if (values[j] is String)
                {
                    value = values[j] as String
                }
                else if (values[j] is Number)
                {
                    val num = (values[i] as Number).toDouble()
                    if (num - num.toInt() == 0.0)
                    {
                        value = num.toInt().toString()
                    }
                    else
                    {
                        value = num.toString()
                    }
                }
                else if (values[j] is Boolean)
                {
                    value = (values[j] as Boolean).toString()
                }
                else
                {
                    value = values[j].toString()
                }
                str = str.replace(("\\{" + i.toString() + "\\}").toRegex(), replacement = value)
                i++
            }
            return str
        }

        fun getColorHash(input: String): String?
        {
            var SHA: String? = null
            try { SHA = IntegrityChecker.sha1(input) }
            catch (e: NoSuchAlgorithmException) { e.printStackTrace() }
            if (SHA == null) return null
            val hexCode = String(charArrayOf(SHA[0], SHA[2], SHA[4], SHA[5], SHA[6]))
            val v = Math.abs(Integer.valueOf(hexCode, 16)!! / 16)
            return String(charArrayOf('&', ChatColor.getByChar(v.toString().toCharArray()[0]).char))
        }

        fun colorStringList(l: MutableList<String>): List<String>
        {
            for (i in l.indices) {
                val s = l[i]
                val reg = "&f" + s
                l[i] = ChatColor.translateAlternateColorCodes('&', reg)
            }
            return l
        }

        fun isNumber(str: String): Boolean
        {
            return try { java.lang.Double.parseDouble(str); true }
            catch (e: NumberFormatException) { false }
        }

        fun isSymbolFormatted(format: String, value: String): Boolean = value.equals(String.format(format, value), ignoreCase = true)

        fun removeSymbolFormat(format: String, value: String): String
        {
            val str = String.format(format, value)
            val buffer = StringBuffer()
            (0 until str.length).filter { Character.isLetterOrDigit(str[it]) }.forEach { buffer.append(str[it]) }
            return buffer.toString()
        }

        fun isUniqueId(uuid: String): Boolean
        {
            return try {
                UUID.fromString(uuid)
                true
            } catch (e: IllegalArgumentException)
            {
                false
            }

        }

        fun decode(escape: String): String
        {
            var escaped = escape
            if (escaped.indexOf("\\u") == -1) return escaped
            var processed = ""
            var position = escaped.indexOf("\\u")
            while (position != -1) {
                if (position != 0) processed += escaped.substring(0, position)
                val token = escaped.substring(position + 2, position + 6)
                escaped = escaped.substring(position + 6)
                processed += Integer.parseInt(token, 16).toChar()
                position = escaped.indexOf("\\u")
            }
            processed += escaped
            return processed
        }
    }
}