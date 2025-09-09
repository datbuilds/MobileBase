# 🏦 SHB SAHA LAOS

SHB SAHA LAOS là ứng dụng di động được thiết kế đặc biệt của Ngân hàng SHB tại thị trường Lào.

## 🏗️ Kiến trúc hệ thống

### **Clean Architecture với Modular Design**

```
┌─────────────────────────────────────────────────────────┐
│                Presentation Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │  Activities │ │  Fragments  │ │  Dialogs    │        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │ ViewModels  │ │   Adapters  │ │  ViewBinding│        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                 Domain Layer                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │  Use Cases  │ │ Repositories│ │  Entities   │        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │  Models     │ │  Mappers    │ │  Validators │        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                  Data Layer                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │   APIs      │ │  Services   │ │ Repositories│        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │   Database  │ │  Cache      │ │  File Store │        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
└─────────────────────────────────────────────────────────┘
```

### **Cấu trúc Module**

```
SHB_SAHA_LAOS/
├── app/                          # Main application module
│   ├── src/main/java/vn/shb/lao/
│   │   ├── activity/             # Activities và ViewModels
│   │   ├── base/                 # Base classes và utilities
│   │   ├── di/                   # Dependency Injection modules
│   │   ├── screens/              # UI screens và fragments
│   │   ├── utils/                # Utility classes và extensions
│   │   └── SHBApplication.kt     # Application class
│   └── build.gradle.kts
├── data/                         # Data layer implementation
│   ├── src/main/java/vn/shb/data/
│   │   ├── entities/             # Room entities
│   │   └── utils/                # Data utilities
│   └── build.gradle.kts
├── shbcore/                      # Core utilities & Domain layer
│   ├── src/main/java/vn/shb/core/
│   │   ├── core/
│   │   │   ├── domain/           # Use cases và repositories
│   │   │   ├── retrofit/         # Network configuration
│   │   │   ├── security/         # Security utilities
│   │   │   └── delivery/         # Response handling
│   │   └── utils/                # Core utilities
│   └── build.gradle.kts
├── ui/                           # Shared UI components
│   └── build.gradle.kts
├── localization/                 # Multi-language support
│   └── build.gradle.kts
├── library/                      # Custom libraries
│   └── choosePhotoHelper/        # Photo selection library
├── buildSrc/                     # Custom Gradle plugins
│   └── src/main/java/
│       ├── AndroidConfig.kt      # Build configuration
│       ├── Plugins.kt            # Plugin definitions
│       └── BuildProductFlavors.kt # Flavor configuration
└── keystore/                     # Signing certificates
```

## 🛠️ Công nghệ sử dụng

### **Core Stack**

| Technology               | Version                   | Mô tả                    |
| ------------------------ |---------------------------| ------------------------ |
| **Kotlin**               | 1.9.10                    | Ngôn ngữ lập trình chính |
| **Android SDK**          | API 26+                   | Hỗ trợ Android 8.0+      |
| **Architecture**         | MVVM + Clean Architecture | Kiến trúc ứng dụng       |
| **Dependency Injection** | Koin 3.1.3                | Quản lý dependencies     |
| **Build System**         | Gradle 8.2.2              | Hệ thống build           |

### **Networking & Data**

| Library      | Version | Mục đích            |
| ------------ | ------- | ------------------- |
| **Retrofit** | 2.9.0   | HTTP client         |
| **OkHttp**   | 4.9.2   | Network interceptor |
| **Gson**     | 2.9.0   | JSON serialization  |
| **Room**     | 2.5.0   | Local database      |
| **Glide**    | 4.13.2  | Image loading       |

### **UI & Navigation**

| Library                  | Version | Mục đích               |
| ------------------------ | ------- | ---------------------- |
| **ViewBinding**          | -       | Type-safe view binding |
| **DataBinding**          | -       | Two-way data binding   |
| **Navigation Component** | 2.4.2   | Fragment navigation    |
| **Material Design**      | 1.12.0  | UI components          |
| **ViewPager2**           | 1.0.0   | Page navigation        |

### **Security & Authentication**

| Library                        | Version       | Mục đích                 |
| ------------------------------ | ------------- | ------------------------ |
| **BiometricPrompt**            | 1.2.0-alpha05 | Sinh trắc học            |
| **JetPack Crypto**             | 1.1.0-alpha03 | Mã hóa dữ liệu           |
| **BouncyCastle**               | 1.68          | Cryptographic algorithms |
| **EncryptedSharedPreferences** | -             | Lưu trữ an toàn          |

### **Additional Libraries**

| Library          | Version | Mục đích                 |
| ---------------- | ------- | ------------------------ |
| **Coroutines**   | 1.7.3   | Asynchronous programming |
| **Joda Time**    | 2.10.2  | Date/time handling       |
| **ExoPlayer**    | 1.3.1   | Media playback           |
| **Firebase BOM** | 29.0.3  | Firebase services        |
| **Timber**       | 5.0.1   | Logging                  |
| **Flipper**      | 0.151.1 | Debug tools              |

## 🚀 Cài đặt và chạy dự án

### **Yêu cầu hệ thống**

- **Android Studio**: Hedgehog | 2023.1.1+
- **JDK**: 17
- **Android SDK**: API 35
- **AGP**: 8.2.2
- **Gradle**: 8.5

### **Cài đặt**

```bash
# Clone repository
git clone https://gitlab.shb.com.vn/shb-mobile-lao/shb-mobile-lao-android.git

# Di chuyển vào thư mục dự án
cd SHB_SAHA_LAOS

# Cấp quyền thực thi cho Gradle wrapper
chmod +x gradlew

# Sync project với Gradle
./gradlew build
```

### **Cấu hình môi trường**

#### **Product Flavors**

| Environment     | Application ID   | Base URL                             | Build Command                  |
| --------------- | ---------------- | ------------------------------------ | ------------------------------ |
| **Development** | `vn.shb.lao.dev` | `https://dev-app.shb.com.vn/api/v1/` | `./gradlew assembleDevDebug`   |
| **UAT**         | `vn.shb.lao.uat` | `https://uat-app.shb.com.vn/api/v1/` | `./gradlew assembleUatRelease` |
| **Production**  | `vn.shb.lao`     | `https://app.shb.com.vn/api/v1/`     | `./gradlew assembleProRelease` |

#### **Build Configuration**

| Setting       | Value   | Mô tả                             |
| ------------- |---------| --------------------------------- |
| `compileSdk`  | 35      | Android SDK version để compile    |
| `minSdk`      | 26      | Minimum supported Android version |
| `targetSdk`   | 35      | Target Android SDK version        |
| `versionCode` | 1       | Internal version number           |
| `versionName` | "1.0.0" | User-visible version string       |

### **Build Commands**

```bash
# Clean build cache
./gradlew clean

# Build all variants
./gradlew build

# Build debug APK for all flavors
./gradlew assembleDebug

# Build release APK for all flavors
./gradlew assembleRelease

# Install dev debug APK
./gradlew installDevDebug

# Create production app bundle
./gradlew bundleProRelease
```

## 🔒 Bảo mật

### **Authentication & Authorization**

- **JWT Token**: Xác thực người dùng
- **Biometric Authentication**: Vân tay/Face ID
- **Auto Token Refresh**: Tự động làm mới token
- **Session Management**: Quản lý phiên đăng nhập

### **Data Protection**

- **Encrypted Storage**: Mã hóa dữ liệu nhạy cảm
- **RSA-4096**: Mã hóa khóa công khai
- **Root Detection**: Phát hiện thiết bị đã root
- **Accessibility Warning**: Cảnh báo quyền accessibility

### **Network Security**

- **Certificate Pinning**: Xác thực SSL certificate
- **Network Security Config**: Cấu hình bảo mật mạng
- **HTTPS Only**: Chỉ sử dụng kết nối an toàn

## 🔧 Development Tools

### **Debug Tools**

- **Flipper**: Network monitoring và debugging
- **Timber**: Structured logging
- **Chucker**: HTTP inspector
- **LeakCanary**: Memory leak detection

### **Code Quality**

- **Kotlin Lint**: Code style checking
- **Detekt**: Static code analysis
- **Unit Tests**: JUnit 4 testing framework
- **UI Tests**: Espresso testing

### **Build Optimization**

- **R8/ProGuard**: Code obfuscation và optimization
- **Gradle Build Cache**: Build performance
- **Parallel Builds**: Multi-module builds
- **Incremental Compilation**: Faster builds

## 📊 Performance & Monitoring

### **Performance Metrics**

- **App Startup Time**: Optimized splash screen
- **Memory Usage**: Efficient image loading với Glide
- **Network Efficiency**: Retrofit với OkHttp
- **Database Performance**: Room với indexing

### **Monitoring & Analytics**

- **Firebase Analytics**: User behavior tracking
- **Crashlytics**: Crash reporting
- **Performance Monitoring**: App performance metrics
- **Custom Events**: Business metrics tracking

## 🚀 Deployment

### **Version Management**

- **Semantic Versioning**: MAJOR.MINOR.PATCH
- **Git Flow**: Feature branches → Develop → Main
- **Release Tags**: Tagged releases với changelog
- **Hotfix Process**: Critical bug fixes

### **Pull Request Process**

1. Create feature branch từ `develop`
2. Implement changes với tests
3. Update documentation nếu cần
4. Create pull request với description
5. Code review và approval
6. Merge vào `develop` branch

## 📞 Support & Contact

### **Development Team**

- **Lead Developer**: SHB Development Team
- **Architecture**: Clean Architecture implementation
- **Security**: Enterprise security standards

**Made with ❤️ by SHB Development Team**

_Phiên bản: 1.0.0 | Cập nhật: 2025_
