package com.korotyx.virtualentity.adapter

import com.google.gson.*

import com.korotyx.virtualentity.command.Permission
import com.korotyx.virtualentity.base.AdapterBase
import com.korotyx.virtualentity.implemention.command.PermissionImpl
import com.korotyx.virtualentity.system.TypeRegister

import java.lang.reflect.Type

@TypeRegister
class PermissionAdapter : AdapterBase<Permission>(), AdapterListener
{
    override fun deserialize(p0: JsonElement?, p1: Type?, p2: JsonDeserializationContext?): Permission
    {
        val jsonObject = p0 as JsonObject
        val permission : Permission = PermissionImpl()
        permission.setBody(jsonObject["base"].asString)
        permission.setValue(jsonObject["value"].asString)
        return permission
    }

    override fun serialize(p0: Permission?, p1: Type?, p2: JsonSerializationContext?): JsonElement
    {
        return JsonObject().apply {
            addProperty("base", p0!!.getBody())
            addProperty("value", p0.getValue())
            addProperty("defaultOp", p0.isDefaultOp())
        }
    }
}