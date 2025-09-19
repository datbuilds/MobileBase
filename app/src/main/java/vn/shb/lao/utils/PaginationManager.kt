package vn.shb.lao.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Utility class để quản lý state phân trang
 */
class PaginationManager(
    private val itemsPerPage: Int = 20,
    private val loadMoreThreshold: Int = 5
) {
    
    private val _paginationState = MutableStateFlow(PaginationState())
    val paginationState: StateFlow<PaginationState> = _paginationState.asStateFlow()
    
    private var totalItems = 0
    private var allItems: List<Any> = emptyList()
    
    /**
     * Khởi tạo dữ liệu mới
     */
    fun initializeData(items: List<Any>) {
        allItems = items
        totalItems = items.size
        
        _paginationState.value = PaginationState(
            currentPage = 1,
            totalItems = totalItems,
            hasMoreData = totalItems > itemsPerPage,
            isLoading = false,
            itemsPerPage = itemsPerPage
        )
    }
    
    /**
     * Lấy items cho page cụ thể
     */
    fun getPageItems(page: Int): List<Any> {
        val startIndex = (page - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, totalItems)
        
        return if (startIndex < totalItems && endIndex > startIndex) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }
    
    /**
     * Load more data
     */
    fun loadMore(): LoadMoreResult {
        val currentState = _paginationState.value
        
        if (currentState.isLoading || !currentState.hasMoreData) {
            return LoadMoreResult.NoMoreData
        }
        
        val nextPage = currentState.currentPage + 1
        val pageItems = getPageItems(nextPage)
        
        if (pageItems.isNotEmpty()) {
            _paginationState.value = currentState.copy(
                currentPage = nextPage,
                hasMoreData = (nextPage * itemsPerPage) < totalItems,
                isLoading = false
            )
            return LoadMoreResult.Success(pageItems, nextPage)
        } else {
            _paginationState.value = currentState.copy(
                hasMoreData = false,
                isLoading = false
            )
            return LoadMoreResult.NoMoreData
        }
    }
    
    /**
     * Set loading state
     */
    fun setLoading(isLoading: Boolean) {
        _paginationState.value = _paginationState.value.copy(isLoading = isLoading)
    }
    
    /**
     * Reset pagination
     */
    fun reset() {
        allItems = emptyList()
        totalItems = 0
        _paginationState.value = PaginationState()
    }
    
    /**
     * Kiểm tra xem có cần load more không
     */
    fun shouldLoadMore(lastVisiblePosition: Int, totalItemCount: Int): Boolean {
        val currentState = _paginationState.value
        return !currentState.isLoading && 
               currentState.hasMoreData && 
               totalItemCount <= (lastVisiblePosition + loadMoreThreshold)
    }
}

/**
 * State của pagination
 */
data class PaginationState(
    val currentPage: Int = 1,
    val totalItems: Int = 0,
    val hasMoreData: Boolean = false,
    val isLoading: Boolean = false,
    val itemsPerPage: Int = 20
)

/**
 * Kết quả của load more
 */
sealed class LoadMoreResult {
    data class Success(val items: List<Any>, val page: Int) : LoadMoreResult()
    object NoMoreData : LoadMoreResult()
    data class Error(val message: String) : LoadMoreResult()
}
