# Thư mục `app/src` - Android Source Directory

Thư mục này chứa toàn bộ mã nguồn, tài nguyên giao diện, tài nguyên tĩnh (assets) và các mã nguồn phục vụ kiểm thử (unit test & instrumentation test) của module `app`.

---

## 1. Cấu trúc thư mục con

- **`main/`**: Thư mục mã nguồn và tài nguyên chính của ứng dụng. Đây là nơi chứa logic nghiệp vụ và giao diện dùng chung cho tất cả các bản build.
  - `java/`: Thư mục gốc chứa các package Java/Kotlin của dự án (`vn.shb.cam`).
  - `res/`: Thư mục chứa tài nguyên giao diện (Layouts, Drawables, Mipmaps, Values, Colors, Animations, XML configs).
  - `assets/`: Chứa các tài nguyên tĩnh như file Lottie animation (`loading.lottie`), mã HTML hiển thị tài liệu PDF (`pdf_viewer.html`), và khóa bảo mật Public Key dạng PEM (`public_key.pem`).
  - `AndroidManifest.xml`: File cấu hình hệ thống Android, khai báo các quyền (Permissions), Activity chính (`MainActivity`), Services (`FcmMessageService`), và FileProvider.
- **`dev/`**: Thư mục chứa tài nguyên cấu hình dành riêng cho phiên bản `dev` (development). Thư mục này hiện tại chứa icon launcher riêng để phân biệt ứng dụng cài đặt trên máy dev với các phiên bản UAT/Production.
- **`test/`**: Chứa các file unit test chạy trực tiếp trên máy ảo JVM nội bộ (Local Unit Tests) để kiểm thử logic nghiệp vụ không phụ thuộc vào framework Android.
- **`androidTest/`**: Chứa các file UI test hoặc integration test (Instrumented Tests) cần chạy trên thiết bị thật hoặc máy giả lập Android.

---

## 2. Cách thức dựng code và liên kết
Hệ thống build Gradle của Android sẽ tự động gộp (merge) mã nguồn và tài nguyên từ các thư mục flavor tương ứng với cấu hình build hiện tại:
- Khi build **`devDebug`**: Gradle sẽ gộp tài nguyên từ `main/` và `dev/` (ví dụ: đè icon launcher của bản dev lên bản main).
- Khi build **`uatRelease`** hoặc **`proRelease`**: Gradle sẽ sử dụng tài nguyên mặc định từ `main/` hoặc các cấu hình tương ứng được khai báo trong `build.gradle.kts`.
- Cấu trúc thư mục mã nguồn chính trong `main/java/vn/shb/cam` được phân chia thành các lớp cấu trúc nghiệp vụ rõ ràng:
  - `activity/` - Nơi khởi chạy MainActivity.
  - `base/` - Các lớp cơ sở dùng chung (BaseActivity, BaseFragment, ViewModel).
  - `di/` - Nơi cấu hình Koin Dependency Injection.
  - `fcm/` - Cơ chế push notification của Firebase.
  - `navigation/` - Module tự định nghĩa cơ chế chuyển đổi màn hình (Fragment Navigation).
  - `screens/` - Chứa giao diện và logic của từng màn hình nghiệp vụ cụ thể.
  - `utils/` - Chứa các hàm/lớp tiện ích mở rộng.
