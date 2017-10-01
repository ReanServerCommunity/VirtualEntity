package com.korotyx.virtualentity.implemention.command

import com.korotyx.virtualentity.command.Permission

internal class PermissionImpl : Permission
{
    override fun isDefaultOp(): Boolean = defaultOp

    override fun setValue(child: String)
    {
        this.value = child
    }

    override fun setBody(base: String)
    {
        this.body = base
    }

    override fun getValue(): String? = value

    override fun getBody(): String? = body

    private var defaultOp : Boolean = true

    private var body : String? = null

    private var value : String? = null
}