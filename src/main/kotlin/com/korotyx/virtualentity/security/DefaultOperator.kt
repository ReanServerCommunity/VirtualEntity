package com.korotyx.virtualentity.security

interface DefaultOperator
{
    fun isDefaultOp(): Boolean

    fun setDefaultOp(isDefault: Boolean)
}