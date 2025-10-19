# Authentication System với Access Token và Refresh Token

## Tổng quan

Hệ thống authentication này được thiết kế để tự động quản lý access token và refresh token, bao gồm:

- Tự động refresh token khi hết hạn
- Lưu trữ token an toàn trong SharedPreferences
- Xử lý lỗi mạng và authentication
- Interceptor tự động thêm token vào request

## Cấu trúc chính

### 1. RetrofitClient

- `getPublicInstance()`: Cho các endpoint không cần authentication
- `getAuthenticatedInstance()`: Cho các endpoint cần authentication
- Tự động quản lý token refresh

### 2. TokenManager

- Lưu trữ và quản lý access token, refresh token
- Kiểm tra token expiry
- Clear tokens khi logout

### 3. TokenAuthenticator (Interceptor)

- Tự động thêm access token vào header
- Tự động refresh token khi nhận 401 Unauthorized
- Xử lý concurrent requests

### 4. AuthRepository

- Quản lý login/logout
- Kiểm tra authentication status
- Xử lý network errors

## Cách sử dụng

### 1. Khởi tạo trong Application class

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.initialize(this);
    }
}
```

### 2. Login

```java
AuthRepository authRepository = new AuthRepository(context);
authRepository.login(username, password, new AuthRepository.LoginCallback() {
    @Override
    public void onSuccess(AuthResponse authResponse) {
        // Login thành công, token đã được lưu tự động
        // Navigate to main screen
    }

    @Override
    public void onError(String error) {
        // Xử lý lỗi login
    }
});
```

### 3. Sử dụng API với authentication

```java
// Tạo API instance
RetrofitAPI api = RetrofitClient.getAuthenticatedInstance().create(RetrofitAPI.class);

// Gọi API (token sẽ được tự động thêm vào header)
Call<PaginationResponse<List<Room>>> call = api.getRooms("", 0, 1, 10);
call.enqueue(callback);
```

### 4. Sử dụng API public (không cần authentication)

```java
RetrofitAPI api = RetrofitClient.getPublicInstance().create(RetrofitAPI.class);
Call<PaginationResponse<List<Room>>> call = api.getPublicRooms("", 0, 1, 10);
call.enqueue(callback);
```

### 5. Logout

```java
authRepository.logout(new AuthRepository.LogoutCallback() {
    @Override
    public void onSuccess() {
        // Logout thành công, tokens đã được clear
        // Navigate to login screen
    }

    @Override
    public void onError(String error) {
        // Vẫn navigate to login screen
    }
});
```

### 6. Kiểm tra authentication status

```java
// Kiểm tra xem user có đang login không
boolean isLoggedIn = RetrofitClient.isAuthenticated();

// Hoặc sử dụng AuthRepository
AuthRepository authRepository = new AuthRepository(context);
boolean isLoggedIn = authRepository.isLoggedIn();
```

## API Response Format

### AuthResponse

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600,
  "token_type": "Bearer"
}
```

### RefreshToken Request

```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Error Handling

Hệ thống tự động xử lý:

- Network errors
- Token expiry (401 Unauthorized)
- Server errors (5xx)
- Connection timeout

## Cấu hình

Có thể thay đổi các cấu hình trong `Constants.java`:

- Base URL
- Timeout values
- Token refresh threshold
- Error messages

## Lưu ý bảo mật

1. Tokens được lưu trong SharedPreferences với MODE_PRIVATE
2. Access token có thời gian sống ngắn
3. Refresh token được sử dụng để lấy access token mới
4. Tokens được clear khi logout hoặc khi refresh thất bại

## Ví dụ sử dụng trong Fragment

```java
public class HomeFragment extends Fragment {
    private RoomRepository roomRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomRepository = new RoomRepository();
    }

    private void loadRooms() {
        roomRepository.getRooms("", 0, 1, 10, new RoomRepository.RoomsCallback() {
            @Override
            public void onSuccess(PaginationResponse<List<Room>> response) {
                // Token sẽ được tự động thêm vào request
                // Nếu token expired, sẽ tự động refresh
                updateUI(response.getData());
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }
}
```

## Dependencies cần thiết

Thêm vào `build.gradle.kts`:

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.12.0")
implementation("com.squareup.retrofit2:converter-gson:2.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```
