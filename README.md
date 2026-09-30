# MobileBase

MobileBase là base project Android đa module, sử dụng MVVM + Clean Architecture và package gốc `com.mobile.base`.

Phạm vi hiện tại gồm ba luồng chính:

- Splash
- Login, biometric và xác nhận thiết bị
- Home tối giản với chức năng đăng xuất

Các màn hình account, transfer, transaction, beneficiary, profile/change-password và Paste2Pay đã được loại khỏi module `app`. Một số model dùng chung hoặc legacy vẫn còn trong `data` và `core`.

## Kiến trúc

```text
┌─────────────────────────────────────────────────────────────┐
│                         :app                                │
│  Activity · Fragment · ViewModel · Navigation · DI · FCM   │
├─────────────────────────────────────────────────────────────┤
│                         :core                               │
│  Use case · Repository source · Network · Security · Utils │
├─────────────────────────────────────────────────────────────┤
│                         :data                               │
│  Entity · Request/response model · Converter               │
└─────────────────────────────────────────────────────────────┘
```

### Các module

```text
MobileBase/
├── app/                       # Android application
├── core/                      # Domain, network, security và core utilities
├── data/                      # Data models và converters
├── localization/              # Tài nguyên ngôn ngữ mặc định và tiếng Việt
├── library/
│   ├── choosePhotoHelper/     # Chọn/chụp ảnh
│   └── imagecrouse/           # Crop và xử lý ảnh
├── buildSrc/                  # Convention plugins và cấu hình Gradle dùng chung
└── gradle/libs.versions.toml  # Version catalog
```

## Công nghệ chính

| Thành phần | Phiên bản/cấu hình |
| --- | --- |
| Kotlin | 1.9.24 |
| Gradle | 8.5 |
| Android Gradle Plugin | 8.2.2 |
| Min SDK | 26 |
| Compile SDK | 35 |
| Target SDK | 36 |
| Java source/target | 11 |
| Kiến trúc | MVVM + Clean Architecture |
| Dependency injection | Koin 3.1.3 |
| UI | XML, ViewBinding, DataBinding, Material Components |
| Network | Retrofit 2.9.0, OkHttp 4.9.2, Gson 2.9.0 |
| Local data | Room 2.5.0 |
| Async | Kotlin Coroutines 1.7.3 |
| Image | Glide 4.13.2, Android Image Cropper 4.3.3 |
| Firebase | Messaging, Auth và Analytics qua Firebase BOM 29.0.3 |

## Yêu cầu môi trường

- Android Studio Hedgehog 2023.1.1 hoặc mới hơn
- JDK 17 để chạy Android Gradle Plugin
- Android SDK Platform 35 và 36

## Cài đặt

```bash
git clone https://github.com/datbuilds/MobileBase.git
cd MobileBase
chmod +x gradlew
./gradlew assembleDevDebug
```

## Product flavors

Dự án sử dụng flavor dimension `environment`:

| Flavor | App name | Application ID | Base URL |
| --- | --- | --- | --- |
| `dev` | DEV MobileBase | `com.mobile.base.dev` | `https://base.com.vn/external/` |
| `uat` | UAT MobileBase | `com.mobile.base.uat` | `https://base.com.vn/external/` |
| `pro` | MobileBase | `com.mobile.base` | `https://base.com.vn/external/` |

`BASE_URL` và `app_name` được cấu hình tập trung qua `configureAppFlavor` trong `buildSrc/src/main/java/BuildProductFlavors.kt`.

## Cấu hình build

| Thuộc tính | Giá trị |
| --- | --- |
| Namespace/Application ID | `com.mobile.base` |
| Version name | `1.0.0` |
| Version code | `3` |
| Debug | Không minify, bật test coverage |
| Release | Bật R8/minify và shrink resources |

Các lệnh thường dùng:

```bash
# Kiểm tra danh sách task/variant
./gradlew :app:tasks

# Build và cài dev debug
./gradlew assembleDevDebug
./gradlew installDevDebug

# Tạo production App Bundle
./gradlew bundleProRelease

# Chạy unit test
./gradlew testDevDebugUnitTest

# Chạy lint
./gradlew lintDevDebug
```

APK và App Bundle được tạo trong `app/build/outputs/`.

## Signing

Debug build sử dụng debug signing mặc định của Android. Release build đọc cấu hình từ `keystore/release.properties`; file keystore thực tế cần được cấp riêng và không nên commit lên repository.

## Bảo mật và công cụ phát triển

- BiometricPrompt cho vân tay/Face ID
- EncryptedSharedPreferences và Jetpack Security Crypto
- Root detection trong module `core`
- HTTPS và OkHttp interceptors
- Flipper, Chucker và Timber cho debug/logging

## Testing

- JUnit 4 cho unit test
- AndroidX JUnit và Espresso cho instrumented test

## Trạng thái build hiện tại

`./gradlew :app:compileDevDebugSources` hiện dừng tại `BankType.kt` vì các drawable `ic_bank_*` đã được loại khỏi project nhưng mapping legacy vẫn còn tham chiếu tới chúng. Cần xóa mapping không còn dùng hoặc bổ sung asset thay thế trước khi build hoàn chỉnh.

## License và hỗ trợ

Dự án được duy trì bởi MobileBase Development Team.

_Phiên bản tài liệu: 2026-09-30_
