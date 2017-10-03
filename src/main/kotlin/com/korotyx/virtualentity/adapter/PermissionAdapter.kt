package com.korotyx.virtualentity.adapter

import com.google.gson.*

import com.korotyx.virtualentity.command.misc.Permission
import com.korotyx.virtualentity.base.AdapterBase
import com.korotyx.virtualentity.plugin.RebukkitPlugin
import com.korotyx.virtualentity.system.TypeRegister

import java.lang.reflect.Type

@TypeRegister
class PermissionAdapter : AdapterBase<Permission>(), AdapterListener
{
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Permission
    {
        val jsonObject = p0 as JsonObject
        val permission : Permission = RebukkitPlugin.loadPermission(jsonObject["value"].asString, jsonObject["defaultOp"].asBoolean)
        permission.setMessage(jsonObject["message"].asString)
        permission.setPermission(jsonObject["permission"].asString)
        return permission
    }

    override fun serialize(p0: Permission?, p1: Type?, p2: JsonSerializationContext?): JsonElement
    {
        return JsonObject().apply {
            addProperty("value", p0!!.getValue())
            addProperty("defaultOp", p0.isDefaultOp())
            addProperty("message", p0.getMessage())
            addProperty("permission", p0.getPermission())
        }
    }
}