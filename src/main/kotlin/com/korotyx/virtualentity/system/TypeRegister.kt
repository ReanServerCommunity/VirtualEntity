package com.korotyx.virtualentity.system

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class TypeRegister(val target : String = "undefined")