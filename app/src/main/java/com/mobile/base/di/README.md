# Package `com.mobile.base.di`

Thư mục này quản lý toàn bộ hệ thống **Dependency Injection (DI)** của ứng dụng sử dụng thư viện **Koin**. DI giúp quản lý vòng đời của các đối tượng (Singletons, Factories, ViewModels), giảm sự phụ thuộc lẫn nhau giữa các lớp và dễ dàng viết unit test.

---

## 1. Cấu trúc và Phân chia Module

Hệ thống DI được phân tách thành các module chuyên biệt và tập hợp lại tại file gốc [AppComponent.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/AppComponent.kt):

### [AppComponent.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/AppComponent.kt)
- Định nghĩa hàm khởi tạo `appComponent(context: Context)` chứa danh sách tất cả các module con.
- Cấu hình các thực thể toàn cục: `applicationScope`, `LocalBroadcastManager` và các ViewModel thuộc MainActivity (`MainViewModel`, `CountdownViewModel`).
- Liên kết và nạp (load) các module khác vào context của Koin khi khởi chạy ứng dụng (`startKoin` trong [BaseApplication.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/BaseApplication.kt)).

### [NetworkModule.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/NetworkModule.kt)
- Cấu hình các HTTP client (`OkHttpClient`), phân tách các loại request:
  - **Thông thường (Normal request)**: Sử dụng `HeaderInterceptor` để đính kèm version name, ngôn ngữ và token.
  - **Xác thực (Authentication)**: Dùng `HeaderAuthenticationInterceptor` cho các request login/refresh token.
- Đăng ký công cụ log mạng `loggingInterceptor` phục vụ cho debug.
- Khởi tạo Retrofit Instance và khai báo các API Service lấy từ module `:core` (`ServiceSplash`, `ServiceAuth`).

### [DomainModule.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/DomainModule.kt)
- Đăng ký các đối tượng thuộc tầng nghiệp vụ (**Use Cases / Interactors**) từ module `:core`.
- Khai báo dưới dạng `factory` (mỗi lần yêu cầu inject sẽ tạo một thực thể mới) để đảm bảo không bị xung đột trạng thái dữ liệu.
- Ví dụ: `UseCaseLogin`, `UseCaseTransactionTransfer`, `GetBeneficiariesUseCase`, v.v.

### [LocalModule.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/LocalModule.kt)
- Cung cấp các công cụ lưu trữ dữ liệu cục bộ:
  - `SharedPreferences` thông thường (tên file xml `"kotlincodes"`).
  - `AndroidSecureStorage` & `EncryptManager` dùng để mã hóa thông tin nhạy cảm lưu dưới dạng KeyStore của thiết bị.
  - `LocalData` quản lý cache tĩnh và trạng thái tạm thời trong RAM.
  - `DeviceManager` dùng để thu thập thông tin thiết bị phần cứng.

### [RepositoryModule.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/RepositoryModule.kt)
- Đăng ký các Repository đóng vai trò làm cổng kết nối dữ liệu (Data Sources) giữa Local và Remote API.

### Thư mục con `feature/`
- Chứa [featureModule.kt](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/src/main/java/com/mobile/base/di/feature/featureModule.kt) đăng ký tất cả các **ViewModel** của các màn hình chức năng (Splash, Login, Home, ChatPay, ChangePassword, Beneficiary).
- Các ViewModel được khai báo bằng từ khóa `viewModel` của Koin để tự động gắn với vòng đời (lifecycle) của Fragment/Activity tương ứng.

---

## 2. Cách thức dựng code & Tương tác
- Khi cần sử dụng một đối tượng được quản lý bởi Koin trong Fragment/Activity, ta sử dụng cú pháp:
  ```kotlin
  // Inject ViewModel trong Fragment
  private val loginViewModel: LoginViewModel by viewModel()
  
  // Inject một Service hoặc Usecase thông thường
  private val deviceManager: DeviceManager by inject()
  ```
- Việc đăng ký mới một ViewModel, UseCase hay Service luôn luôn phải được thêm vào các file module tương ứng trong thư mục `di` để ứng dụng có thể phân giải được phụ thuộc khi chạy.
