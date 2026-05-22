# Package `vn.shb.cam.screens`

Thư mục này chứa toàn bộ các màn hình chức năng (màn hình nghiệp vụ) của ứng dụng SHB SAHA CAM, được tổ chức theo kiến trúc **MVVM (Model - View - ViewModel)**.

---

## 1. Danh sách các phân hệ màn hình chính

- **`splash/`**: Màn hình chào mừng khi khởi chạy app, kiểm tra phiên bản ứng dụng, thiết lập cấu hình ban đầu.
- **`login/`**: Tính năng đăng nhập (bằng mật khẩu, sinh trắc học vân tay/khuôn mặt), quản lý thiết bị tin cậy (device verification).
- **`home/`**: Giao diện trang chủ sau khi đăng nhập, hiển thị thông tin tài khoản, danh sách các tính năng nhanh, v.v.
- **`account/`**: Xem chi tiết tài khoản thanh toán, số dư và lịch sử giao dịch.
- **`transfer/`**: Chức năng chuyển tiền (Chuyển tiền nội bộ SHB, chuyển tiền nhanh Napas 247).
- **`beneficiary/`**: Quản lý danh bạ thụ hưởng (thêm, sửa, xóa, tìm kiếm tài khoản thụ hưởng).
- **`profile/`**: Quản lý thông tin cá nhân của người dùng, đổi mật khẩu.
- **`transaction/`**: Lịch sử giao dịch, chi tiết giao dịch và xác nhận giao dịch.
- **`paste2pay/`**: Tính năng hỗ trợ sao chép và thanh toán nhanh (Paste to Pay) bằng trợ lý ảo hoặc quét/sao chép thông tin hóa đơn.

---

## 2. Quy chuẩn cấu trúc trong một màn hình
Mỗi thư mục màn hình (ví dụ: `login/` hoặc `home/`) được thiết kế đóng gói độc lập theo cấu trúc MVVM:

```
screens/<tên-nghiệp-vụ>/
├── <Name>Fragment.kt       <- View: Kế thừa BaseFragmentBinding, xử lý giao diện, binding XML.
├── <Name>ViewModel.kt      <- ViewModel: Chứa logic nghiệp vụ, gọi UseCases, quản lý LiveData/Flow.
├── helper/                 <- Chứa các lớp tiện ích phụ trợ riêng cho màn hình này.
├── widget/ hoặc ui/        <- Chứa các Custom View, Dialog hoặc Adapter chuyên biệt cho màn hình này.
├── state/ hoặc model/      <- (Tùy chọn) Chứa UI State đại diện cho trạng thái màn hình hoặc data model đặc thù.
```

---

## 3. Cách dựng code cho màn hình mới

1. **Khởi tạo Fragment**: Kế thừa `BaseFragmentBinding`, gắn layout xml tương ứng.
2. **Khởi tạo ViewModel**: Kế thừa `BaseViewModel` hoặc ViewModel thông thường, inject các UseCases cần thiết vào hàm khởi tạo.
3. **Đăng ký Dependency Injection**: Khai báo ViewModel mới trong file [featureModule.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/vn/shb/cam/di/feature/featureModule.kt).
4. **Đăng ký Route điều hướng**: Khai báo destination mới trong file [AppDestination.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/vn/shb/cam/navigation/AppDestination.kt) và ánh xạ nó với Fragment vừa tạo.
5. **Liên kết dữ liệu**: Sử dụng DataBinding để liên kết trực tiếp các biến của ViewModel vào XML layout hoặc quan sát các LiveData/Flow từ ViewModel trong hàm `initData()` của Fragment để cập nhật UI.
