package com.korotyx.virtualentity.system

import java.lang.reflect.ParameterizedType

/**
 * GenericIdentity is used in the base class to receive the information and type of the child class.
 * This is very useful for object virtualization. If you frequently use these features, you only need
 * to inherit this class.
 *
 * @author korotyx
 */
open class GenericIdentity<P>
{
    // This is a generic object of this class. It contains information about the superclass.
    @Suppress("UNCHECKED_CAST")
    @Transient
    private val genericBaseType : Class<P> = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<P>

    /**
     * Gets an instance of this generic. It cans get the information of the upper command class.
     * @return The generic type
     * @exception InstantiationException Failed to create instance object
     * @exception IllegalAccessException Approaching an unacceptable area
     */
    fun getGenericBaseInstance() : P?
    {
        var instance : P? = null
        try
        {
            instance = genericBaseType.newInstance()
        }
        catch(e: InstantiationException)
        {
            e.printStackTrace()
        }
        catch(e : IllegalAccessException)
        {
            e.printStackTrace()
        }
        return instance
    }

    /**
     * Gets the generic type of the command.
     * @return The generic class type
     */
    fun getGenericBaseType() : Class<P> = genericBaseType
}