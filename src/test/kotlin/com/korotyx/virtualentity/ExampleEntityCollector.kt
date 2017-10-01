package com.korotyx.virtualentity

import com.korotyx.virtualentity.base.VirtualEntityCollector

class ExampleEntityCollector : VirtualEntityCollector<ExampleEntity>()
{
    companion object
    {
        private val collector : ExampleEntityCollector = ExampleEntityCollector()
        fun getInstance() : ExampleEntityCollector = collector
    }
}