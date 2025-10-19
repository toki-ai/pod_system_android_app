# Quick Test Guide - POD Booking Authentication

## 🚀 Cách test API Authentication

### 1. Cập nhật Test Credentials

Mở file `TestCredentials.java` và cập nhật các credentials phù hợp với backend:

```java
public static final String VALID_EMAIL = "your-test-email@domain.com";
public static final String VALID_PASSWORD = "your-test-password";
```

### 2. Cấu hình Base URL

Trong `Constants.java`, cập nhật BASE_URL:

```java
// Cho Android Emulator
public static final String BASE_URL = "http://10.0.2.2:8080/";

// Cho thiết bị thật (thay IP bằng IP máy backend)
public static final String BASE_URL = "http://192.168.1.100:8080/";
```

### 3. Run App và Test

1. **Launch app** → Sẽ mở LoginActivity
2. **Test với UI:**

   - Nhập email/password thực tế và click "Sign In"
   - Hoặc click "Test with Valid Credentials" (sử dụng TestCredentials)
   - Hoặc click "Test with Invalid Credentials" để test error handling

3. **Xem kết quả:**
   - Thành công: Chuyển đến MainActivity
   - Thất bại: Hiện error message và log trong Logcat

### 4. Debug với Logcat

Filter logs với tag: `LoginActivity`, `AuthRepository`, `TokenAuthenticator`

```
// Expected success logs:
I/LoginActivity: Attempting login with email: admin@test.com
I/AuthRepository: Login successful for user: admin@test.com
I/TokenAuthenticator: Token saved successfully

// Expected error logs:
E/AuthRepository: Login failed with code: 401
E/LoginActivity: Login failed for: admin@test.com, Error: Authentication failed
```

### 5. Test Cases Tự Động

Các test case được tích hợp trong UI:

- ✅ **Valid Login**: Test với credentials đúng
- ❌ **Invalid Login**: Test với credentials sai
- 🔍 **Auth Status**: Kiểm tra trạng thái authentication
- 🔄 **Token Refresh**: Tự động test khi token hết hạn

### 6. Network Debugging

Nếu gặp lỗi network:

1. **Check internet connection**
2. **Verify backend is running** (http://localhost:8080)
3. **Check firewall settings**
4. **For real device**: Ensure phone and computer on same WiFi

### 7. Common Issues & Solutions

| Issue              | Solution                                 |
| ------------------ | ---------------------------------------- |
| `ConnectException` | Backend không chạy hoặc wrong URL        |
| `401 Unauthorized` | Sai credentials hoặc endpoint không đúng |
| `Network timeout`  | Tăng timeout trong Constants             |
| `SSL/TLS errors`   | Use HTTP thay vì HTTPS cho development   |

### 8. Expected API Responses

**Login Success:**

```json
{
  "code": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "account": {
      "id": "123",
      "email": "user@test.com",
      "name": "Test User"
    }
  }
}
```

**Login Error:**

```json
{
  "code": 401,
  "message": "Email hoặc mật khẩu không đúng",
  "data": null
}
```

### 9. Next Steps

Sau khi login thành công:

1. **Token được lưu tự động** trong SharedPreferences
2. **Navigate to MainActivity**
3. **All authenticated API calls** sẽ tự động include token
4. **Token refresh** xảy ra tự động khi cần

### 10. Production Ready

Khi ready cho production:

1. Remove development test buttons
2. Update BASE_URL to production server
3. Remove test credentials
4. Add proper error handling
5. Add loading states
6. Add form validation
