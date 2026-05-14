# FlowerShop - Ứng dụng Quản lý & Đặt Hoa Trực Tuyến

FlowerShop là một ứng dụng di động Android (Java) cho phép người dùng khám phá, tìm kiếm và đặt mua các loại hoa đa dạng. Ứng dụng tích hợp các công nghệ hiện đại như Supabase cho cơ sở dữ liệu, Firebase cho xác thực và Groq AI cho hệ thống Chatbot tư vấn thông minh.

## 🌟 Tính năng chính

### Đối với Người dùng (User Flow)
- **Xác thực:** Đăng ký, đăng nhập và khôi phục mật khẩu qua Firebase Auth.
- **Trang chủ:** Xem các biểu ngữ khuyến mãi (Banners) và các sản phẩm bán chạy nhất.
- **Danh mục sản phẩm:** Phân loại hoa theo dịp (Sinh nhật, Khai trương, Chia buồn, Hoa bó).
- **Tìm kiếm:** Tìm kiếm sản phẩm theo tên một cách nhanh chóng.
- **Chi tiết sản phẩm:** Xem thông tin chi tiết, giá cả và thêm vào giỏ hàng hoặc danh sách yêu thích.
- **Giỏ hàng:** Quản lý số lượng sản phẩm, áp dụng mã giảm giá và tính tổng tiền.
- **Thanh toán:** Hỗ trợ thanh toán tại quầy hoặc giao hàng tận nơi với mã QR thanh toán (ZXing).
- **Yêu thích:** Lưu trữ các sản phẩm yêu thích để xem lại sau.
- **Chatbot:** Tư vấn chọn hoa thông minh thông qua Groq AI.
- **Cá nhân:** Quản lý hồ sơ cá nhân và cài đặt ứng dụng.

### Đối với Quản trị viên (Admin Flow)
- **Dashboard:** Thống kê và quản lý tổng quan.
- **Quản lý sản phẩm:** Thêm mới, chỉnh sửa thông tin hoặc xóa bỏ các sản phẩm hoa trực tiếp trên ứng dụng.

## 🛠 Tech Stack

- **Ngôn ngữ:** Java 25
- **Giao diện (UI/UX):**
    - Material Design Components
    - **Glide:** Tải và xử lý hình ảnh.
    - **Lottie:** Hiển thị các hiệu ứng hoạt họa (animations).
- **Xử lý mạng (Networking):**
    - **Retrofit 2 & OkHttp:** Giao tiếp với RESTful API.
    - **Gson:** Chuyển đổi dữ liệu JSON.
- **Backend:**
    - **Supabase:** Lưu trữ dữ liệu sản phẩm, danh mục, giỏ hàng qua REST API.
    - **Firebase Authentication:** Quản lý tài khoản người dùng.
    - **Firebase Firestore & Storage:** Lưu trữ dữ liệu bổ sung và hình ảnh sản phẩm.
- **Tiện ích khác:**
    - **ZXing:** Tích hợp quét và tạo mã QR.
    - **Groq AI:** API cho mô hình ngôn ngữ lớn (LLM) hỗ trợ Chatbot.

## 🏗 Kiến trúc hệ thống

Dự án tuân theo mô hình **Layered Architecture** (Kiến trúc phân lớp):
- **UI Layer (`activities`, `adapters`):** Xử lý giao diện và tương tác người dùng.
- **Logic Layer (`sync`, `utils`):** Chứa các logic nghiệp vụ và điều phối dữ liệu (ví dụ: `SupabaseSync`, `GroqApiService`).
- **Data Layer (`api`, `model`):** Định nghĩa cấu trúc dữ liệu (POJO) và cấu hình Retrofit client.

## 📂 Cấu trúc thư mục tiêu biểu

```text
app/src/main/java/com/example/flowershop/
├── activities/       # Các màn hình của ứng dụng
├── adapters/         # Bộ điều phối hiển thị dữ liệu cho RecyclerView
├── api/              # Định nghĩa API (Supabase, Groq)
├── model/            # Các lớp thực thể (Data Models)
├── sync/             # Lớp trung gian xử lý đồng bộ dữ liệu
└── utils/            # Các công cụ hỗ trợ (Chatbot, QR, etc.)
```

## 🚀 Hướng dẫn cài đặt

1. **Clone project:**
   ```bash
   git clone https://github.com/Hadesghostkiller/FlowerShop
   ```
2. **Cấu hình Firebase:**
   - Vào bên trong Firebase, tạo new project, enable authentications, enable google sau đó thêm tệp `google-services.json` vào thư mục `app/`.
3. **Cấu hình Supabase:**
   - Vào bên trong Supabase, tự tạo new project và thay đổi link web thành `BASE_URL` và api_key thành `ANON_KEY` trong `SupabaseClient.java`.
4. **Cấu hình Groq AI:**
   - Thêm API Key của Groq vào `GroqApiService.java`.
5. **Build dự án:**
   - Mở dự án bằng Android Studio và đồng bộ Gradle.

## Repos được build bởi 4thanggay

