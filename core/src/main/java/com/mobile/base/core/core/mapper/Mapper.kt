package com.mobile.base.core.core.mapper

interface Mapper<in T, out R> {

    fun convert(t: T): R
}