# 📱 MobileBase

MobileBase là bộ khung (base project) ứng dụng ngân hàng di động Android, tổ chức theo kiến trúc đa module (MVVM + Clean Architecture) với package gốc `com.mobile.base`.

## 🏗️ Kiến trúc hệ thống

Ứng dụng áp dụng **MVVM + Clean Architecture** kết hợp thiết kế **modular** (multi-module Gradle). Các layer được tách theo module: tầng trình bày ở `app`, logic nghiệp vụ/domain và tiện ích lõi ở `core`, mô hình dữ liệu ở `data`.

```
┌─────────────────────────────────────────────────────────┐
│                Presentation Layer  (:app)               │
│   Activities · Fragments · ViewModels · ViewBinding      │
│   Navigation tự viết · Adapters · Screens                │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                Domain / Core Layer  (:core)             │
│   UseCases · Repository sources · Retrofit config        │
│   Security (root detect, encrypt) · Delivery · Mapper    │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                  Data Layer  (:data)                    │
│   Entities · Models theo domain (login, home, transfer…) │
└─────────────────────────────────────────────────────────┘
```

### **Cấu trúc Module**

```
MobileBase/
├── app/                              # Module ứng dụng chính (namespace: com.mobile.base)
│   └── src/main/java/com/mobile/base/
│       ├── activity/                 # MainActivity + ViewModels
│       ├── base/                     # Base classes
│       ├── di/                       # Koin dependency injection
│       ├── fcm/                      # Firebase Cloud Messaging
│       ├── navigation/               # Điều hướng tự viết
│       ├── screens/                  # Các màn hình
│       │   ├── account/  beneficiary/  home/  login/
│       │   ├── paste2pay/  profile/  splash/
│       │   └── transaction/  transfer/
│       ├── utils/                    # Tiện ích
│       └── MobileBaseApplication.kt  # Application class
├── core/                             # Domain + Core (namespace: com.mobile.base.core)
│   └── src/main/java/com/mobile/base/core/
│       ├── core/
│       │   ├── domain/               # usecases + source
│       │   ├── retrofit/             # Cấu hình network
│       │   ├── security/             # detectRoot + encrypt
│       │   ├── delivery/             # Xử lý response (ResultState...)
│       │   ├── local/  bus/  mapper/  helper/
│       └── utils/                    # extensions
├── data/                             # Data layer (namespace: com.mobile.base.data)
│   └── src/main/java/com/mobile/base/data/
│       └── entities/                 # login, home, splash, transfer,
│                                     #   beneficiary, wso2
├── localization/                     # Đa ngôn ngữ (namespace: com.mobile.base.localization)
│   └── src/main/res/                 # values (EN) + values-vi
├── library/                          # Thư viện nội bộ
│   ├── choosePhotoHelper/            # Chọn ảnh (namespace: com.mobile.base.choosephotohelper)
│   └── imagecrouse/                  # Xử lý ảnh (namespace: com.example.imagecrouse)
├── buildSrc/                         # Gradle plugins & config tùy biến
│   └── src/main/java/
│       ├── AndroidConfig.kt          # Version, applicationId, build flags
│       ├── Plugins.kt                # Định nghĩa plugin
│       └── BuildProductFlavors.kt    # Cấu hình flavor
├── keystore/                         # Chứng chỉ ký ứng dụng
├── gradle/libs.versions.toml         # Version catalog
└── settings.gradle.kts
```

## 🛠️ Công nghệ sử dụng

### **Core Stack**

| Technology               | Version                   | Mô tả                       |
| ------------------------ | ------------------------- | --------------------------- |
| **Kotlin**               | 1.9.24                    | Ngôn ngữ lập trình chính    |
| **Android SDK**          | API 26+                   | Hỗ trợ Android 8.0+         |
| **Architecture**         | MVVM + Clean Architecture | Kiến trúc ứng dụng          |
| **Dependency Injection** | Koin 3.1.3                | Quản lý dependencies        |
| **Android Gradle Plugin**| 8.2.2                     | Build toolchain             |

### **Networking & Data**

| Library      | Version | Mục đích             |
| ------------ | ------- | -------------------- |
| **Retrofit** | 2.9.0   | HTTP client          |
| **OkHttp**   | 4.9.2   | Network & interceptor|
| **Gson**     | 2.9.0   | JSON serialization   |
| **Room**     | 2.5.0   | Local database       |
| **Paging**   | 3.0.1   | Phân trang           |
| **Glide**    | 4.13.2  | Image loading        |

### **UI**

| Library             | Version | Mục đích                |
| ------------------- | ------- | ----------------------- |
| **ViewBinding**     | -       | Type-safe view binding  |
| **DataBinding**     | -       | Two-way data binding    |
| **Material Design** | 1.12.0  | UI components           |
| **ConstraintLayout**| 2.1.3   | Layout                  |
| **ViewPager2**      | 1.0.0   | Page navigation         |
| **Lottie**          | 5.0.3   | Animation               |
| **Shimmer**         | 0.5.0   | Skeleton loading        |

### **Security & Authentication**

| Library                        | Version       | Mục đích                 |
| ------------------------------ | ------------- | ------------------------ |
| **BiometricPrompt**            | 1.2.0-alpha05 | Sinh trắc học            |
| **JetPack Crypto**             | 1.1.0-alpha03 | Mã hóa dữ liệu           |
| **BouncyCastle**               | 1.68          | Thuật toán mã hóa        |
| **EncryptedSharedPreferences** | -             | Lưu trữ an toàn          |

### **Additional Libraries**

| Library          | Version | Mục đích                 |
| ---------------- | ------- | ------------------------ |
| **Coroutines**   | 1.7.3   | Lập trình bất đồng bộ    |
| **Joda Time**    | 2.10.2  | Xử lý ngày giờ           |
| **Media3/ExoPlayer** | 1.3.1 | Phát media              |
| **Firebase BOM** | 29.0.3  | Messaging, Auth, Analytics |
| **WorkManager**  | 2.7.1   | Tác vụ nền               |
| **Timber**       | 5.0.1   | Logging                  |
| **Flipper**      | 0.151.1 | Debug tools              |
| **Chucker**      | 3.5.2   | HTTP inspector           |

## 🚀 Cài đặt và chạy dự án

### **Yêu cầu hệ thống**

- **Android Studio**: Hedgehog | 2023.1.1 trở lên
- **JDK**: 17
- **Android SDK**: API 35
- **AGP**: 8.2.2

### **Cài đặt**

```bash
# Clone repository
git clone https://gitlab.shb.com.vn/shb-mobile-lao/shb-mobile-lao-android.git

# Di chuyển vào thư mục dự án
cd MobileBase

# Cấp quyền thực thi cho Gradle wrapper
chmod +x gradlew

# Build project
./gradlew build
```

> **Lưu ý:** Dự án yêu cầu file `local.properties` chứa các khóa cấu hình theo từng flavor
> (`dev_config`, `dev_grant_type`, `dev_username`, `dev_password`, và tương tự cho `uat_*`, `pro_*`).
> Build sẽ thất bại nếu thiếu các khóa này.

### **Cấu hình môi trường**

#### **Product Flavors**

Dự án dùng flavor dimension `environment` với 3 flavor:

| Environment     | app_name          | Base URL                                                                        | Build Command                  |
| --------------- | ----------------- | ------------------------------------------------------------------------------- | ------------------------------ |
| **dev**         | DEV SHB SAHA CAM  | `https://api-gw-ext-dev.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/`         | `./gradlew assembleDevDebug`   |
| **uat**         | UAT SHB SAHA Cam  | `https://t-apigw-cam.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/`            | `./gradlew assembleUatRelease` |
| **pro**         | SHB SAHA CAM      | `https://p-apigw-cam.shb.com.vn/external/shb-mobile-cam/1.0.0/mbcam/`            | `./gradlew assembleProRelease` |

#### **Build Configuration**

| Setting       | Value   | Mô tả                             |
| ------------- | ------- | --------------------------------- |
| `applicationId` | `com.mobile.base` | Application ID            |
| `compileSdk`  | 35      | Android SDK dùng để compile       |
| `minSdk`      | 26      | Android tối thiểu hỗ trợ (8.0)    |
| `targetSdk`   | 35      | Target Android SDK                |
| `versionCode` | 3       | Số hiệu build nội bộ              |
| `versionName` | "1.0.0" | Phiên bản hiển thị                |

### **Build Commands**

```bash
# Xóa cache build
./gradlew clean

# Build toàn bộ variant
./gradlew build

# Cài đặt bản dev debug
./gradlew installDevDebug

# Tạo app bundle production
./gradlew bundleProRelease
```

## 🔒 Bảo mật

### **Authentication & Authorization**

- **WSO2 Gateway**: Xác thực qua API gateway (grant type/username/password theo flavor)
- **Biometric Authentication**: Vân tay / Face ID (BiometricPrompt)
- **Session Management**: Quản lý phiên đăng nhập

### **Data Protection**

- **Encrypted Storage**: EncryptedSharedPreferences + JetPack Crypto
- **Public Key Encryption**: RSA (public_key.pem) qua BouncyCastle
- **Root Detection**: Phát hiện thiết bị đã root (`core/security/detectRoot`)

### **Network Security**

- **HTTPS Only**: Toàn bộ endpoint dùng HTTPS
- **OkHttp Interceptors**: Kiểm soát request/response

## 🔧 Development Tools

- **Flipper**: Giám sát network & debug (chỉ debug build, release dùng `flipper-noop`)
- **Chucker**: HTTP inspector (debug build)
- **Timber**: Structured logging

## 🧪 Testing

- **JUnit 4**: Unit test
- **AndroidX JUnit + Espresso**: Instrumented / UI test

## 🚀 Deployment

- **Semantic Versioning**: MAJOR.MINOR.PATCH (hiện tại 1.0.0, versionCode 3)
- **Git Flow**: Feature branch → develop → main
- Output đóng gói tại `app/pro/release/*.aab` và `app/pro/debug/*.apk`

## 📞 Support & Contact

- **Development Team**: MobileBase Development Team

**Made with ❤️ by MobileBase Development Team**

_Phiên bản: 1.0.0 (3) | Cập nhật: 2025_
