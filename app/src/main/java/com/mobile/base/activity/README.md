# Package `com.mobile.base.activity`

Thư mục này chứa lớp Activity chính duy nhất của ứng dụng Android và các ViewModel liên quan để điều phối trạng thái toàn cục của ứng dụng.

---

## 1. Thiết kế Single-Activity
Ứng dụng Base SAHA CAM được phát triển theo kiến trúc **Single-Activity**.
- Toàn bộ giao diện của ứng dụng đều là các **Fragment**.
- [MainActivity](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/activity/MainActivity.kt) là Activity duy nhất được cấu hình thẻ `<intent-filter>` với hành động `MAIN` và danh mục `LAUNCHER` trong Manifest. Nó đóng vai trò là container chính để gắn các Fragment và quản lý vòng đời ứng dụng, các overlay bảo mật, và sự kiện chuyển đổi màn hình.

---

## 2. Các thành phần chính

### [MainActivity.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/activity/MainActivity.kt)
- Kế thừa từ `BaseActivity` để sử dụng các thiết lập dùng chung (như đăng ký `ActivityLifeCycleObserver`, quản lý hiển thị loading dialog, xử lý lỗi hệ thống).
- Là nơi giữ thực thể điều hướng chính (`NavigatorHost` và `AppNavigator`) giúp các Fragment con có thể truy xuất và thực hiện hành động chuyển trang.
- Quản lý cơ chế hiển thị màn hình che thông tin bảo mật (Privacy Overlay) khi ứng dụng chuyển vào trạng thái nền (Background) để chống chụp ảnh màn hình hoặc xem lén thông tin nhạy cảm.

### [MainViewModel.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/activity/MainViewModel.kt)
- ViewModel đi kèm với `MainActivity` để quản lý các sự kiện cấp ứng dụng nếu cần thiết.

### [CountDownViewModel.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/activity/CountDownViewModel.kt)
- Quản lý bộ đếm ngược thời gian hết hạn phiên làm việc (Session Timeout) khi người dùng không tương tác với màn hình trong một khoảng thời gian quy định.
- Tự động phát ra sự kiện đăng xuất (Auto Logout) hoặc hiển thị thông báo cảnh báo để bảo vệ tài khoản ngân hàng của người dùng.

### Thư mục `debug/`
- Chứa các Activity hoặc mã nguồn phụ trợ hỗ trợ việc hiển thị công cụ phân tích debug (như Flipper Diagnostics, logger) chỉ xuất hiện trong môi trường phát triển (`BuildConfig.DEBUG`).

---

## 3. Cách dựng code & Tương tác
- `MainActivity` được khởi tạo và cấu hình tự động thông qua Koin DI (`viewModel { MainViewModel() }`, `viewModel { CountdownViewModel() }` trong [AppComponent.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/AppComponent.kt)).
- Khi `MainActivity` được tạo, nó sẽ đăng ký `registerActivityLifecycleCallbacks` trong `BaseApplication` để theo dõi các sự kiện bật/tắt của chính nó.
