# Navigation

## Tổng quan

Hệ thống navigation của app được tinh gọn về package `vn.shb.cam.navigation`.

- App chỉ có `1 MainActivity`.
- Toàn bộ màn hình còn lại là `Fragment`.
- Không dùng `Jetpack Navigation`, `nav_graph.xml`, `safe-args` hay `R.id.homeFragment` để điều hướng.
- Fragment điều hướng trực tiếp bằng `AppDestination`.

## Cấu trúc file

Thư mục chính: `app/src/main/java/vn/shb/cam/navigation`

`AppDestination.kt`

- Khai báo toàn bộ route của app.
- Mỗi màn hình là một `AppDestination`.
- Những màn cần truyền dữ liệu sẽ giữ `Bundle` ngay trong destination.
- Đồng thời chứa luôn mapping:
  - `Fragment -> AppDestination`
  - `AppDestination -> Fragment`

`Navigator.kt`

- Interface điều hướng chung của app.
- Chứa luôn:
  - `Navigator`
  - `NavigatorHost`
  - `requireNavigator()`
  - `NavigationTransition`
  - `AppNavigationTransition`
- Các hàm chính của navigator:
  - `open(destination, ...)`
  - `goBack()`
  - `popTo(destination)`
  - `currentDestination`

`AppNavigator.kt`

- Nơi điều hướng thật sự xảy ra bằng `FragmentManager`.
- Chịu trách nhiệm `replace`, `addToBackStack`, `popBackStack`, và gắn `arguments`.

## Luồng hoạt động

Ví dụ ở fragment:

- gọi `safeNavigate(AppDestination.Profile)`
- `BaseFragmentBinding` lấy `navigator` từ `MainActivity`
- `AppNavigator` mở fragment đích bằng `FragmentManager`

Khi back:

- gọi `backPress()`
- `navigator.goBack()`
- `FragmentManager.popBackStack()`

Khi pop về một màn cụ thể:

- gọi `popBackTo(AppDestination.Home)`
- `AppNavigator` sẽ pop theo `tag` hoặc mở lại root screen nếu đó là màn gốc
