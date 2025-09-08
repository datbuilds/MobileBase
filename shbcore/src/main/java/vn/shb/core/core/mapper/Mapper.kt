package vn.shb.core.core.mapper

interface Mapper<in T, out R> {

    fun convert(t: T): R
}