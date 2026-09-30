# Package `com.mobile.base.base`

Thư mục này chứa toàn bộ các lớp cơ sở (Base Classes) và thành phần dùng chung giúp chuẩn hóa giao diện, logic xử lý và quản lý vòng đời (Lifecycle) của toàn bộ ứng dụng Base SAHA CAM.

---

## 1. Thành phần cốt lõi

### [BaseActivity.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseActivity.kt)
- Lớp cơ sở cho toàn bộ các Activity (bao gồm `MainActivity`).
- Quản lý trạng thái giao diện chung: Hiển thị/ẩn bàn phím ảo, hiển thị loading dialog tùy biến, hiển thị hộp thoại báo lỗi.
- Đăng ký `ActivityLifeCycleObserver` để giám sát vòng đời.

### [BaseFragmentBinding.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseFragmentBinding.kt)
- Lớp cơ sở cho tất cả các Fragment sử dụng ViewBinding / DataBinding.
- Tự động hóa việc tạo và hủy binding (tránh rò rỉ bộ nhớ - memory leak).
- Cung cấp các hàm tiện ích cho việc điều hướng:
  - `safeNavigate(destination: AppDestination)`
  - `backPress()`
- Lắng nghe và quan sát các trạng thái chung từ ViewModel (loading, thông báo lỗi, thông báo thành công).

### [BaseViewModel.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseViewModel.kt)
- Lớp cơ sở cho tất cả các ViewModel.

### [BaseBottomDialogBinding.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseBottomDialogBinding.kt)
- Lớp cơ sở dành cho các Bottom Sheet Dialog sử dụng ViewBinding/DataBinding.

### [BaseErrorDialog.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseErrorDialog.kt)
- Định nghĩa hiển thị thống nhất cho các thông báo lỗi từ API hoặc lỗi hệ thống.

### [BaseLoadMoreAdapter.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/BaseLoadMoreAdapter.kt)
- Adapter tùy biến của RecyclerView hỗ trợ cơ chế tải thêm dữ liệu (Pagination / Load More) một cách mượt mà.

### [ActivityLifeCycleObserver.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/base/ActivityLifeCycleObserver.kt)
- Lắng nghe vòng đời của các Activity để xử lý các logic toàn cục như đếm ngược timeout phiên làm việc hoặc overlay màn hình.

---

## 2. Thư mục con
- `dialog/`: Chứa các layout và logic cấu hình dialog cụ thể dùng chung (như dialog xác nhận, dialog thông báo thành công).
- `view/`: Chứa các Custom Views tự thiết kế phục vụ toàn bộ app.

---

## 3. Cách dựng code trong lớp Base
Khi tạo một màn hình mới, nhà phát triển luôn phải kế thừa từ các lớp Base này:
```kotlin
// Ví dụ khai báo một Fragment mới
class HomeFragment : BaseFragmentBinding<FragmentHomeBinding, HomeViewModel>() {
    override val layoutId: Int = R.layout.fragment_home
    override val viewModel: HomeViewModel by viewModel()

    override fun initView() {
        // Khởi tạo các view, adapter
    }

    override fun initData() {
        // Lấy dữ liệu hoặc đăng ký lắng nghe LiveData
    }
}
```
Điều này đảm bảo mã nguồn đồng bộ, dễ đọc và tránh lặp lại các đoạn code quản lý giao diện cơ bản.
