# Module `app` - Base SAHA CAM Application

Thư mục này chứa module chính (`app`) của ứng dụng Android **Base SAHA CAM**. Đây là nơi khởi tạo ứng dụng, tích hợp các cấu hình build, tài nguyên giao diện chính (resources) và tích hợp các module dùng chung khác trong project.

---

## 1. Vai trò & Chức năng
- **Điểm khởi chạy ứng dụng**: Chứa `BaseApplication` kế thừa từ `Application` và `MainActivity` (kiến trúc Single-Activity).
- **Cấu hình Build & Môi trường**: Định nghĩa các build variant, product flavors (`dev`, `uat`, `pro`) và base URL theo từng môi trường.
- **Tập hợp Dependencies**: Nơi khai báo và liên kết toàn bộ thư viện bên thứ ba và các module nội bộ của hệ thống như `:core`, `:localization`, các thư viện chỉnh sửa ảnh, player, v.v.
- **Tự động hóa Layout**: Sử dụng `autodimension.gradle` để tự tạo các file kích thước (`dimens.xml`) tương thích với các độ phân giải màn hình khác nhau (Smallest Width từ 320dp đến 3840dp).

---

## 2. Cấu trúc thư mục con chính
- `src/`: Thư mục chứa mã nguồn Java/Kotlin chính, tài nguyên (res), file cấu hình Manifest và các file build riêng cho môi trường phát triển.
- `kotlin/`: Thư mục chứa cấu trúc gói Kotlin trống (hoặc dành cho mục đích mở rộng trong tương lai).
- `pro/`: Thư mục chứa các file APK/AAB build output cho môi trường Production (pro).
- `uat/`: Thư mục chứa các file APK build output cho môi trường UAT (Testing).

---

## 3. Cách dựng code & Cấu hình chính

### Các Flavor & Môi trường Build
Trong [build.gradle.kts](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/build.gradle.kts), ứng dụng được chia thành 3 Flavor chính dưới dimension `environment`:

| Flavor | Tên ứng dụng hiển thị | Base URL (API Gateway CAM) |
| :--- | :--- | :--- |
| **`dev`** | DEV Base SAHA CAM | `https://api-gw-ext-dev.base.com.vn/external/base-mobile-cam/1.0.0/mbcam/` |
| **`uat`** | UAT Base SAHA Cam | `https://t-apigw-cam.base.com.vn/external/base-mobile-cam/1.0.0/mbcam/` |
| **`pro`** | Base SAHA CAM | `https://p-apigw-cam.base.com.vn/external/base-mobile-cam/1.0.0/mbcam/` |

### Gradle Build Command tham khảo
Để build ứng dụng hoặc cài đặt trực tiếp lên thiết bị thông qua terminal:

- **Build bản Dev (Debug)**:
  ```bash
  ./gradlew assembleDevDebug
  ```
- **Build bản UAT (Release)**:
  ```bash
  ./gradlew assembleUatRelease
  ```
- **Build bản Production (Release)**:
  ```bash
  ./gradlew assembleProRelease
  ```

---

## 4. Cơ chế Kích thước Tự động (Autodimension)
Dự án áp dụng script Gradle tùy biến [autodimension.gradle](file:///Users/dt_linhdq/Desktop/base-mobile-CAM/app/autodimension.gradle). Khi chạy task `createDimen`, script này sẽ:
1. Đọc kích thước gốc từ file `values/dimens.xml` (sử dụng base width chuẩn là `360dp`).
2. Tự động tính toán tỷ lệ tương ứng và tạo ra các thư mục `values-sw<width>dp/auto_dimens.xml` cho các màn hình có độ rộng khác nhau.
3. Hỗ trợ tạo cả các giá trị âm (`dp_minus` và `sp_minus`).

Cách kích hoạt task:
```bash
./gradlew app:createDimen
```
