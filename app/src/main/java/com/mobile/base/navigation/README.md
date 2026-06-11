# Package `com.mobile.base.navigation`

Thư mục này định nghĩa hệ thống điều hướng (Navigation System) tự thiết kế của ứng dụng SHB SAHA CAM, được tối ưu hóa cho kiến trúc **Single-Activity + Multi-Fragments**.

---

## 1. Kiến trúc thiết kế
Hệ thống điều hướng này không sử dụng thư viện **Jetpack Navigation** truyền thống (`nav_graph.xml`), tránh được sự cồng kềnh và các giới hạn khi truyền tham số phức tạp giữa các Fragment. Thay vào đó, ứng dụng tự quản lý việc chuyển trang trực tiếp thông qua **`FragmentManager`** kết hợp với các lớp định nghĩa đích đến.

---

## 2. Các thành phần chính

### [AppDestination.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/navigation/AppDestination.kt)
- Định nghĩa tất cả các điểm đến (routes) trong ứng dụng dưới dạng một `sealed interface/class`.
- Mỗi màn hình Fragment tương ứng với một đối tượng của `AppDestination`.
- Hỗ trợ đóng gói dữ liệu truyền qua màn hình (`Bundle`) ngay trong đối tượng destination.
- Định nghĩa ánh xạ 2 chiều:
  - `AppDestination -> Fragment` (để tạo instance mới khi mở trang).
  - `Fragment -> AppDestination` (để định vị vị trí hiện tại trong Stack).

### [Navigator.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/navigation/Navigator.kt)
- Định nghĩa Interface `Navigator` chứa các phương thức điều hướng trừu tượng:
  - `open(destination: AppDestination, transition: NavigationTransition)`: Mở màn hình mới kèm hiệu ứng chuyển cảnh.
  - `goBack()`: Quay lại màn hình phía trước.
  - `popTo(destination: AppDestination, inclusive: Boolean)`: Quay lại một màn hình cụ thể trong Stack.
- Định nghĩa interface `NavigatorHost` mà Activity chính (`MainActivity`) phải triển khai để cung cấp instance của Navigator.
- Cung cấp extension function `Fragment.requireNavigator()` để giúp bất kỳ Fragment nào cũng dễ dàng lấy ra bộ điều hướng.

### [AppNavigator.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/navigation/AppNavigator.kt)
- Lớp triển khai thực tế (Implementation) của interface `Navigator`.
- Thực hiện các thao tác trực tiếp với `FragmentManager` của Android:
  - `beginTransaction()`
  - Thay thế fragment (`replace`), thêm vào ngăn xếp (`addToBackStack`).
  - Gán `arguments` (Bundle) vào Fragment trước khi đính kèm.
  - Quản lý hiệu ứng chuyển động chuyển trang (Animations).

---

## 3. Cách dựng code & Tương tác điều hướng
Khi muốn chuyển từ Fragment A sang Fragment B:

1. **Định nghĩa Đích đến** (trong `AppDestination.kt`):
   ```kotlin
   object Profile : AppDestination
   data class TransactionDetail(val transactionId: String) : AppDestination {
       // logic đóng gói dữ liệu vào Bundle
   }
   ```
2. **Thực hiện điều hướng** (trong Fragment A):
   ```kotlin
   // Mở màn hình thông thường
   safeNavigate(AppDestination.Profile)
   
   // Mở màn hình kèm dữ liệu
   safeNavigate(AppDestination.TransactionDetail(transactionId = "123456"))
   ```
3. **Quay lại trang trước** (trong Fragment B):
   ```kotlin
   backPress()
   ```

> [!TIP]
   > Để xem thông tin hướng dẫn kỹ thuật chi tiết hơn, bạn có thể tham khảo file tài liệu nội bộ: [NAVIGATION_DOCS.md](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/navigation/NAVIGATION_DOCS.md).
