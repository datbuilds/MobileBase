HTTP Status
├── 1xx: Thông tin (Informational) – Ít dùng
│ → Server nhận request, đang xử lý tiếp (app gần như không gặp).
│
├── 2xx: Thành công (Success)
│ ├── 200 OK → Request thành công, trả về dữ liệu (JSON, HTML…)
│ ├── 201 Created → Tạo mới resource thành công (VD: đăng ký user, tạo order).
│ ├── 202 Accepted → Server chấp nhận nhưng chưa xử lý xong (thường xử lý async).
│ ├── 203 Non-Authoritative → Dữ liệu trả về không phải bản gốc, có thể qua proxy.
│ ├── 204 No Content → Thành công nhưng không có data (VD: xóa user).
│ ├── 205 Reset Content → Thành công và yêu cầu reset UI/form.
│ └── 206 Partial Content → Tải file 1 phần (resume download).
│
├── 3xx: Chuyển hướng (Redirection)
│ ├── 300 Multiple Choices → Có nhiều link, user chọn 1.
│ ├── 301 Moved Permanently → URL đổi vĩnh viễn (dùng URL mới).
│ ├── 302 Found (Temporary) → Chuyển hướng tạm thời (URL cũ vẫn dùng được).
│ ├── 303 See Other → Chuyển hướng tới một URL khác bằng GET.
│ ├── 304 Not Modified → Dữ liệu chưa thay đổi, lấy từ cache.
│ └── 305 Use Proxy → Phải dùng proxy để truy cập (ít gặp).
│
├── 4xx: Lỗi phía Client (Client Error)
│ ├── 400 Bad Request → Request sai format/thiếu params.
│ ├── 401 Unauthorized → Chưa login hoặc token hết hạn.
│ ├── 402 Payment Required → Yêu cầu thanh toán (ít dùng).
│ ├── 403 Forbidden → Có login nhưng không có quyền (role không đúng).
│ ├── 404 Not Found → Không tìm thấy API hoặc resource.
│ ├── 405 Method Not Allowed → Gọi sai method (GET/POST…).
│ ├── 406 Not Acceptable → Server không trả dữ liệu theo format client chấp nhận.
│ ├── 407 Proxy Auth Required → Cần login vào proxy trước.
│ ├── 408 Request Timeout → Request quá lâu, bị hủy.
│ ├── 409 Conflict → Dữ liệu xung đột (update bản cũ).
│ ├── 410 Gone → Resource từng có nhưng bị xóa vĩnh viễn.
│ ├── 411 Length Required → Thiếu header Content-Length.
│ ├── 412 Precondition Failed → Điều kiện gửi kèm (If-Match, If-Unmodified) không đúng.
│ ├── 413 Payload Too Large → File/data gửi lên quá lớn.
│ ├── 414 URI Too Long → URL quá dài (query string quá nhiều).
│ └── 415 Unsupported Media Type → Gửi sai Content-Type (VD: text/plain thay vì JSON).
│
└── 5xx: Lỗi phía Server (Server Error)
├── 500 Internal Server Error → Lỗi chung server (bug hoặc crash).
├── 501 Not Implemented → API chưa hỗ trợ.
├── 502 Bad Gateway → Gateway/proxy nhận phản hồi sai từ server khác.
├── 503 Service Unavailable → Server quá tải hoặc bảo trì.
├── 504 Gateway Timeout → Server backend không phản hồi kịp.
└── 505 HTTP Version Not Supported → Server không hỗ trợ HTTP version client dùng.
