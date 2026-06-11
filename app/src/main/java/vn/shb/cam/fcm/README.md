# Package `vn.shb.cam.fcm`

Thư mục này chứa lớp xử lý tin nhắn và thông báo đẩy (Push Notification) từ dịch vụ **Firebase Cloud Messaging (FCM)**.

---

## 1. Trạng thái hiện tại
> [!IMPORTANT]
> Toàn bộ nội dung lớp xử lý [FcmMessageService.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/vn/shb/cam/fcm/FcmMessageService.kt) hiện tại đang bị **đóng băng (comment out)** và chưa được kích hoạt sử dụng trong mã nguồn. 
> 
> Trong file cấu hình hệ thống [AndroidManifest.xml](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/AndroidManifest.xml), khai báo Service cho FCM (`FcmMessageService`) cũng đang bị comment out.

---

## 2. Thiết kế logic gốc (Khi được kích hoạt)

Nếu được mở lại để sử dụng, cấu trúc logic của [FcmMessageService.kt](file:///Users/dt_linhdq/Desktop/shb-mobile-CAM/app/src/main/java/vn/shb/cam/fcm/FcmMessageService.kt) sẽ hoạt động như sau:

### Quản lý Token thiết bị
- **Khởi tạo**: Lấy Token FCM hiện tại thông qua `FirebaseMessaging.getInstance().token`.
- **Lưu trữ**: So sánh Token FCM mới với Token đã lưu trong bộ nhớ tạm (`storage.getFcmToken()`). Nếu có thay đổi, lưu Token mới.
- **onNewToken(token: String)**: Lắng nghe sự thay đổi Token FCM từ hệ thống Firebase và cập nhật lên máy chủ thông qua hàm `sendRegistrationToServer`.

### Xử lý thông báo đến (`onMessageReceived`)
- Nhận Payload từ Firebase qua thực thể `RemoteMessage`.
- Kiểm tra ứng dụng có đang ở trạng thái hiển thị trên màn hình hay không (`isForeground`):
  - **Ở Foreground**: Hiển thị trực tiếp thông báo hoặc cập nhật UI theo thời gian thực.
  - **Ở Background**: Tạo Notification và đẩy lên khay thông báo hệ thống của thiết bị Android thông qua `NotificationManager`.

### Cấu hình kênh thông báo (Notification Channel)
- Đối với Android 8.0 (API 26) trở lên, Service sẽ tự tạo một `NotificationChannel` có độ ưu tiên cao (`IMPORTANCE_HIGH`), kèm theo âm thanh thông báo tùy chỉnh (`notification.raw`) và chế độ rung (`vibrationPattern`).
- Gắn `PendingIntent` vào thông báo để khi người dùng chạm vào, ứng dụng sẽ mở màn hình đăng nhập (`LoginActivity` hoặc `MainActivity`).
