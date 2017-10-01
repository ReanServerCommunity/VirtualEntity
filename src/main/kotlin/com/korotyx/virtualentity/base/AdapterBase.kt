package com.korotyx.virtualentity.base

import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.korotyx.virtualentity.system.GenericIdentity

abstract class AdapterBase<P> : GenericIdentity<P>(), JsonSerializer<P>, JsonDeserializer<P>