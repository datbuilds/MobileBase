# Thư mục `app/pro` - Production Build Outputs

Thư mục này được sử dụng để chứa các file APK/AAB build output cho môi trường **Production (pro)** của ứng dụng.

---

## 1. Cấu trúc thư mục con

- **`debug/`**:
  - Chứa các file APK hoặc file hỗ trợ cài đặt phục vụ cho việc kiểm thử chế độ gỡ lỗi (debug) trên môi trường Production.
  - Ví dụ file hiện có: `SHB SAHA Laos - 1.0.0(3)-pro-debug.apk.idsig` (file chữ ký cài đặt nhanh cho bản gỡ lỗi).
- **`release/`**:
  - Chứa các file đóng gói cuối cùng để phát hành chính thức lên Google Play Store hoặc triển khai thực tế.
  - Ví dụ file hiện có:
    - `SHB SAHA Cam - 1.0.0(1)-pro-release.apk` (Bản APK hoàn chỉnh dành cho thị trường Campuchia).
    - `SHB SAHA Laos - 1.0.0(1)-pro-release.aab` / `SHB SAHA Laos - 1.0.0(2)-pro-release.aab` (Bản Android App Bundle dùng để upload lên Google Play Store cho thị trường Lào).
  - `output-metadata.json`: File mô tả siêu dữ liệu của bản build (version code, version name, cấu hình split apk...).

---

## 2. Lưu ý khi làm việc
- Các file trong thư mục này được tạo ra sau khi chạy các task build của Gradle với flavor `pro`.
- Không nên đưa trực tiếp các file APK/AAB kích thước lớn lên hệ thống quản lý mã nguồn Git để tránh làm nặng kho chứa (Repository). Nên cấu hình bỏ qua thông qua `.gitignore` trừ khi cần thiết lưu trữ các bản build ổn định.
