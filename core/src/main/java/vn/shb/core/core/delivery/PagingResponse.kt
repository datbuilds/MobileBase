package vn.shb.core.core.delivery

open class PagingResponse<T> {
    var list: List<T> = ArrayList()
    var pagination = Pagination()
}

open class Pagination {
    var page: Int = 0
    var size: Int = 0

    fun update(newPaging: Pagination) {
        page = newPaging.page
        size = newPaging.size
    }
}
