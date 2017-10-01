package com.korotyx.virtualentity.command

interface Permission
{
    fun getBody() : String?

    fun getValue() : String?

    fun setBody(base : String)

    fun setValue(child : String)

    fun isDefaultOp(): Boolean
}