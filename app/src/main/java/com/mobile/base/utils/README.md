# Package `com.mobile.base.utils`

Thư mục này chứa các thư viện tiện ích, lớp cấu hình hằng số (constants), trình quản lý logic và các hàm mở rộng (Kotlin extensions) hỗ trợ đắc lực cho việc phát triển toàn bộ ứng dụng SHB SAHA CAM.

---

## 1. Hằng số & Quản lý logic nghiệp vụ chung

- **[ApiConst.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/utils/ApiConst.kt)**: Khai báo toàn bộ các đường dẫn endpoint tĩnh của API (như `/login`, `/auth/refresh-token`, `/transfer/validate`, v.v.).
- **[BankType.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/utils/BankType.kt)**: Định nghĩa Enum đại diện cho các loại ngân hàng (Napas, SHB, v.v.) đi kèm icon và tên hiển thị tương ứng.
- **[PaginationManager.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/com/mobile/base/utils/PaginationManager.kt)**: Quản lý trạng thái phân trang dữ liệu (Trang hiện tại, tổng số trang, trạng thái đang tải), thường dùng kết hợp với các danh sách lịch sử giao dịch hoặc danh bạ thụ hưởng.

---

## 2. Thư mục con & Tính năng đặc thù

- **`extensions/`**: Thư mục chứa các Kotlin Extension Functions dùng chung giúp rút gọn mã nguồn khi tương tác với các đối tượng cơ bản:
  - `ContextExtensions.kt`: Hiển thị Toast, Dialog nhanh, lấy kích thước màn hình.
  - `ViewExtensions.kt`: Ẩn/hiện View (`visible()`, `gone()`), chống click đúp (double click preventer).
  - `StringExtensions.kt`: Kiểm tra tính hợp lệ của số điện thoại, định dạng tiền tệ, hash SHA-256.
  - `DateExtensions.kt`: Chuyển đổi định dạng ngày tháng hiển thị trên app.
- **`glide/`**: Cấu hình tùy biến thư viện Glide (`AppGlideModule`) giúp tối ưu hóa việc tải ảnh, xử lý bo góc ảnh, cache ảnh thông minh, giải mã định dạng ảnh WebP động.
- **`pdfviewer/`**: Cung cấp các công cụ và cấu hình Javascript (kết hợp với `assets/pdf_viewer.html`) giúp hiển thị file PDF trực quan (ví dụ: biên lai chuyển tiền) ngay bên trong ứng dụng Android thông qua WebView.
- **`refreshTK/`**: Chứa các lớp tiện ích tự động refresh Token kết nối API (Token WSO2) chạy ngầm khi Token hiện tại bị hết hạn hoặc lỗi 401 Unauthorized.
- **`widgets/`**: Chứa các Helper quan trọng cấp ứng dụng:
  - `LocaleHelper.kt`: Trình quản lý đa ngôn ngữ động của ứng dụng, hỗ trợ 3 ngôn ngữ chính: **Tiếng Việt (`vi`)**, **Tiếng Khmer (`km`)**, và **Tiếng Anh (`en`)**.
  - `BiometricHelper.kt`: Hỗ trợ đăng nhập và xác thực bằng sinh trắc học vân tay hoặc nhận diện khuôn mặt (FaceID / Fingerprint).
- **`view/`**: Chứa các Custom Layouts tiện ích hoặc các UI Components nhỏ dùng chung.

---

## 3. Cách dựng code & Tái sử dụng
- Tránh viết lại các hàm xử lý chuỗi, định dạng tiền tệ hoặc xử lý ẩn hiện View. Hãy luôn tìm kiếm và tái sử dụng các hàm mở rộng trong `extensions`.
- Ví dụ tái sử dụng Extension:
  ```kotlin
  // Thay vì view.visibility = View.GONE
  view.gone()
  
  // Thay vì sử dụng NumberFormat thủ công
  val formattedMoney = "100000".formatCurrency()
  ```
