# HTTP Status Codes

## 1xx – Informational (Thông tin)

> Ít dùng trong ứng dụng thực tế. Cho biết server đã nhận request và đang xử lý.

* **100 Continue** – Tiếp tục gửi request body.
* **101 Switching Protocols** – Đổi giao thức (VD: HTTP → WebSocket).

---

## 2xx – Success (Thành công)

> Request được xử lý thành công.

* **200 OK** – Thành công, trả về dữ liệu (JSON, HTML…).
* **201 Created** – Tạo mới resource thành công (VD: tạo user, order).
* **202 Accepted** – Đã nhận request nhưng xử lý bất đồng bộ (async).
* **203 Non-Authoritative Information** – Dữ liệu không phải bản gốc (qua proxy).
* **204 No Content** – Thành công nhưng không trả data (VD: delete).
* **205 Reset Content** – Thành công, yêu cầu client reset form/UI.
* **206 Partial Content** – Trả về một phần dữ liệu (resume download).

---

## 3xx – Redirection (Chuyển hướng)

> Client cần thực hiện thêm hành động (redirect, cache).

* **300 Multiple Choices** – Có nhiều lựa chọn resource.
* **301 Moved Permanently** – URL đổi vĩnh viễn (SEO dùng nhiều).
* **302 Found** – Chuyển hướng tạm thời.
* **303 See Other** – Redirect sang URL khác bằng **GET**.
* **304 Not Modified** – Resource chưa thay đổi, dùng cache.
* **305 Use Proxy** – Bắt buộc truy cập qua proxy (hiếm dùng).

---

## 4xx – Client Error (Lỗi phía Client)

> Request sai hoặc client không đủ quyền.

* **400 Bad Request** – Sai format, thiếu param, validate fail.
* **401 Unauthorized** – Chưa xác thực (chưa login / token hết hạn).
* **402 Payment Required** – Yêu cầu thanh toán (hiếm dùng).
* **403 Forbidden** – Đã login nhưng không có quyền (role).
* **404 Not Found** – Không tồn tại API hoặc resource.
* **405 Method Not Allowed** – Sai HTTP method (GET/POST…).
* **406 Not Acceptable** – Server không trả được format client yêu cầu.
* **407 Proxy Authentication Required** – Chưa xác thực proxy.
* **408 Request Timeout** – Request quá thời gian cho phép.
* **409 Conflict** – Xung đột dữ liệu (version, duplicate).
* **410 Gone** – Resource đã bị xóa vĩnh viễn.
* **411 Length Required** – Thiếu header `Content-Length`.
* **412 Precondition Failed** – Điều kiện header không thỏa (If-Match…).
* **413 Payload Too Large** – Dữ liệu/file quá lớn.
* **414 URI Too Long** – URL hoặc query string quá dài.
* **415 Unsupported Media Type** – Sai `Content-Type` (VD: không phải JSON).

---

## 5xx – Server Error (Lỗi phía Server)

> Server xử lý request bị lỗi.

* **500 Internal Server Error** – Lỗi chung (bug, exception).
* **501 Not Implemented** – API chưa được implement.
* **502 Bad Gateway** – Gateway nhận response lỗi từ backend.
* **503 Service Unavailable** – Server quá tải hoặc bảo trì.
* **504 Gateway Timeout** – Backend không phản hồi kịp.
* **505 HTTP Version Not Supported** – Không hỗ trợ HTTP version client dùng.

---

## Dùng trong REST API

* **GET thành công** → `200`
* **POST tạo mới** → `201`
* **DELETE thành công** → `204`
* **Sai input** → `400`
* **Chưa login** → `401`
* **Không có quyền** → `403`
* **Không tồn tại resource** → `404`
* **Lỗi hệ thống** → `500`