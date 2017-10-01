package com.korotyx.virtualentity.base

import com.korotyx.virtualentity.system.GenericIdentity
import java.util.*

abstract class VirtualEntityCollector<E : VirtualEntity<E>>: GenericIdentity<E>()
{
    companion object
    {
        private val REGISTER_DATA : HashMap<Class<*>, VirtualEntityCollector<*>> = HashMap()
        fun getVirtualCollector(c : Class<*>) = REGISTER_DATA[c]
    }

    private val entityCollector : ArrayList<E> = ArrayList()

    @Suppress("UNCHECKED_CAST")
    fun register(e : Any) : Boolean = entityCollector.add(e as E)


    fun getEntity(obj : Any) : E?
    {
        val id : String = obj as String
        return entityCollector.firstOrNull {
            it.getUniqueId() == id
        }
    }

    init
    {
        if(! isProvenCollector(this))
        {
            REGISTER_DATA.put(this.getGenericBaseType(), this)
        }
    }

    private fun isProvenCollector(vec: VirtualEntityCollector<*>) = REGISTER_DATA.containsKey(vec.getGenericBaseType())
}