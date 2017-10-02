@file:JvmName("VirtualEntity")
package com.korotyx.virtualentity.base

import com.google.gson.*
import com.korotyx.virtualentity.util.VirtualEntityUtil
import com.korotyx.virtualentity.system.GenericIdentity
import com.korotyx.virtualentity.system.TypeRegister
import java.lang.reflect.Method

import java.util.*
import java.util.ArrayList

@Suppress("UNCHECKED_CAST")
open class VirtualEntity<E : VirtualEntity<E>>(private val uniqueId : String = UUID.randomUUID().toString()) : GenericIdentity<E>()
{
    @Transient private lateinit var collector : VirtualEntityCollector<*>
    protected fun getEntityCollection() : VirtualEntityCollector<*> = collector

    @Transient private lateinit var gson : Gson
    @Transient private val requirementAdapters : List<Class<*>> = ArrayList()

    private var lastUpdated : Long = -1L
    fun getUniqueId() : String = uniqueId

    open fun create() : Boolean = create(this)

    @Synchronized private fun create(obj : Any) : Boolean
    {
        val classes : Array<Class<*>> = VirtualEntityUtil.getClasses(this.javaClass.`package`.name.split(".")[0])
        val gsonBuilder = GsonBuilder()
        try
        {
            for (clazz in classes)
            {
                clazz.annotations.filterIsInstance<TypeRegister>().forEach {
                    if (getAdapterBaseType(clazz.newInstance().javaClass) != null) {
                        val clazzAdapterType : AdapterBase<*> = clazz.newInstance() as AdapterBase<*>
                        gsonBuilder.registerTypeAdapter(clazzAdapterType.getGenericBaseType(), clazzAdapterType)
                    }
                }

                if (VirtualEntityCollector::class.java.isAssignableFrom(clazz) && VirtualEntityCollector::class.java != clazz)
                {
                    if ((clazz.newInstance() as VirtualEntityCollector<*>).getGenericBaseType() != obj.javaClass) continue
                    val met: Method = clazz.getDeclaredMethod("access\$getCollector\$cp")
                    val referCollection: VirtualEntityCollector<*> = met.invoke(clazz.newInstance()) as VirtualEntityCollector<*>
                    collector = referCollection
                }
            }
        }
        catch(e : ClassCastException) { return false }

        collector.register(obj)
        gson = gsonBuilder.serializeNulls().setPrettyPrinting().create()
        return true
    }

    private fun <C> getAdapterBaseType(clazz : Class<C>) : C?
    {
        return if(JsonDeserializer::class.java.isAssignableFrom(clazz) && JsonSerializer::class.java.isAssignableFrom(clazz))
        {
            return clazz.newInstance()
        }
        else null
    }

    fun serialize() : String
    {
        val jsonObject = JsonObject()
        val element : JsonElement = getJsonElement(gson.toJson(this))
        jsonObject.add(this.getGenericBaseType().name, element)
        return gson.toJson(jsonObject)
    }

    private infix fun getJsonElement(json : String) : JsonElement = Gson().fromJson(json, JsonElement::class.java)
}

