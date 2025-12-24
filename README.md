<div align="center">

# 📅 Event Management System
**(Hệ thống Quản lý Sự kiện)**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-lightgrey.svg)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

*Một ứng dụng Desktop hiện đại giúp đơn giản hóa quy trình tổ chức sự kiện, quản lý khách mời và theo dõi check-in.*

[Tính năng](#-tính-năng-chính) •
[Cài đặt](#-hướng-dẫn-cài-đặt) •
[Công nghệ](#-công-nghệ-sử-dụng) •
[Cấu trúc](#-cấu-trúc-dự-án)

</div>

---

## 📖 Giới thiệu

**Event Management System** là giải pháp phần mềm được xây dựng để giải quyết bài toán quản lý sự kiện trong các tổ chức. Hệ thống cung cấp giao diện trực quan để lên lịch sự kiện, quản lý danh sách người tham gia, phân công nhân sự và theo dõi trạng thái check-in theo thời gian thực.

Dự án áp dụng mô hình kiến trúc **DAO (Data Access Object)** giúp tách biệt lớp giao diện và lớp dữ liệu, đảm bảo code sạch và dễ bảo trì.

## ✨ Tính năng chính

### 1. 📊 Dashboard (Bảng điều khiển)
- Thống kê tổng quan: Số lượng sự kiện, khách mời, người tham gia, nhân viên.
- **Biểu đồ tròn trực quan**: Tỉ lệ trạng thái sự kiện, tỉ lệ check-in/chưa check-in.
- Danh sách các sự kiện gần nhất.

### 2. 🗓 Quản lý Sự kiện (Events)
- Tạo mới, cập nhật, xóa sự kiện.
- Tự động cập nhật trạng thái: **Sắp diễn ra**, **Đang diễn ra**, **Đã qua**.
- Lọc sự kiện theo thời gian và địa điểm.

### 3. 👥 Quản lý Con người
- **Khách mời (Guests):** Quản lý thông tin VIP, gửi thư mời (giả lập).
- **Người tham gia (Participants):**
  - Hệ thống **Check-in / Check-out** chính xác từng phút.
  - Theo dõi lịch sử tham gia.
- **Nhân viên (Employees):** Phân công nhân viên vào từng sự kiện cụ thể.

### 4. 🛡 Phân quyền & Bảo mật
- **Admin:** Toàn quyền truy cập hệ thống (bao gồm quản lý nhân viên).
- **Staff:** Quyền hạn chế (chỉ xem và thực hiện check-in/out, không được xóa dữ liệu quan trọng).
- Mật khẩu được mã hóa an toàn bằng thuật toán **BCrypt**.

---

## 🛠 Công nghệ sử dụng

| Thành phần | Công nghệ |
| :--- | :--- |
| **Ngôn ngữ** | Java 17 (OpenJDK) |
| **Giao diện (UI)** | JavaFX, CSS |
| **Cơ sở dữ liệu** | MySQL 8.0 |
| **Build Tool** | Apache Maven |
| **Bảo mật** | jBCrypt |
| **Thư viện khác** | MySQL Connector/J, SLF4J |

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu tiên quyết
- Java Development Kit (JDK) 17 trở lên.
- MySQL Server đã được cài đặt và đang chạy.
- Maven (tùy chọn, nếu dùng IDE như IntelliJ/Eclipse thì đã tích hợp sẵn).

### Các bước thực hiện

1. **Clone dự án:**
   ```bash
   git clone https://github.com/Jiwos410/EventManagementSystem.git
   cd EventManagementSystem

2. **Cài đặt Cơ sở dữ liệu:**

   **Cách 1: Dùng MySQL Workbench (Khuyên dùng)**
   - Mở MySQL Workbench và kết nối vào server.
   - Vào menu **File** -> **Open SQL Script...**
   - Tìm đến thư mục `db` và chọn file `schema.sql`.
   - Nhấn biểu tượng tia sét ⚡ (Execute) để chạy script.
   - Nhấn chuột phải vào vùng "Schemas" chọn **Refresh All** để kiểm tra.

   **Cách 2: Dùng Terminal**
   - Mở Terminal tại thư mục gốc của dự án.
   - Đăng nhập MySQL và chạy lệnh:
     ```bash
     mysql -u root -p < db/schema.sql
     ```
     *(Hoặc đăng nhập vào mysql rồi gõ: `source db/schema.sql;`)*

 3. **Cấu hình kết nối:**
   - Vào thư mục `src/main/resources/`.
   - Mở file `db.properties` bằng Notepad hoặc VS Code và điền mật khẩu MySQL của bạn vào (mỗi cấu hình một dòng):
     ```properties
     db.url=jdbc:mysql://localhost:3306/eventdb?useSSL=false&allowPublicKeyRetrieval=true
     db.user=root
     db.password=MAT_KHAU_CUA_BAN
     ```

 4. **Chạy ứng dụng:**

    *Lưu ý: Đảm bảo MySQL Server đang chạy trước khi khởi động ứng dụng.*

    **Cách 1: Dùng Terminal (Khuyên dùng)**
    ```bash
    mvn clean javafx:run
    ```

    **Cách 2: Dùng IDE (IntelliJ IDEA / Eclipse)**
    - Mở dự án và đợi Maven tải thư viện xong.
    - Tìm file `src/main/java/com/eventmanagementsystem/Main.java`.
    - Chuột phải chọn **Run 'Main.main()'**.

 ---
 
 ## 🔐 Tài khoản đăng nhập mặc định

 Sau khi ứng dụng chạy, bạn sử dụng thông tin sau để đăng nhập:

 | Quyền (Role) | Tên đăng nhập | Mật khẩu | Ghi chú |
 | :--- | :--- | :--- | :--- |
 | **Admin** | `admin` | `admin123` | Toàn quyền (Quản lý cả nhân viên) |
 | **Staff** | `staff` | `staff123` | Chỉ xem và check-in/out |

 ---

 ## 📂 Cấu trúc dự án

 ```text
 EventManagementSystem
 ├── db                       # Chứa script khởi tạo Database
 │   └── schema.sql           # Script tạo bảng và dữ liệu mẫu
 ├── src
 │   └── main
 │       ├── java/com/eventmanagementsystem
 │       │   ├── dao          # Data Access Objects
 │       │   ├── model        # Các đối tượng thực thể (Entity)
 │       │   ├── ui           # Giao diện người dùng (View & Util)
 │       │   ├── util         # Tiện ích chung (DB, Password...)
 │       │   └── Main.java    # Class chạy ứng dụng
 │       └── resources
 │           ├── db.properties.example  # File mẫu cấu hình DB
 │           └── style.css              # File CSS giao diện
 ├── pom.xml                  # File cấu hình Maven
 └── README.md                # Tài liệu hướng dẫn
 ```

 ---

 <div align="center">
     Made with ❤️ by <b>Jiwos410</b>
 </div>
