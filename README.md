# Chat System - dialo-backend

## 📌 Overview

**Chat System** là một hệ thống nhắn tin trực tuyến (OTT - Over-The-Top) được xây dựng theo kiến trúc **Microservices** kết hợp **Event-Driven Architecture (EDA)** nhằm đảm bảo khả năng mở rộng, tính sẵn sàng cao và dễ dàng bảo trì.

Dự án hướng đến việc cung cấp một nền tảng giao tiếp hiện đại với các chức năng nhắn tin, gọi điện và quản lý người dùng tương tự các ứng dụng OTT phổ biến hiện nay.

---

## 🎯 Objectives

Mục tiêu của dự án là hiện thực một ứng dụng OTT với các tính năng chính:

* 💬 Nhắn tin cá nhân (One-to-One Chat)
* 👥 Nhắn tin nhóm (Group Chat)
* 📞 Gọi điện giữa người dùng
* 👤 Quản lý thông tin cá nhân

  * Cập nhật hồ sơ
  * Thay đổi ảnh đại diện
  * Quản lý thông tin tài khoản

---

## 🏗️ System Architecture

Hệ thống được thiết kế theo:

* **Microservices Architecture**
* **Event-Driven Architecture (EDA)**

Kiến trúc này giúp:

* Tăng khả năng mở rộng (Scalability)
* Giảm sự phụ thuộc giữa các dịch vụ
* Dễ dàng triển khai và bảo trì
* Hỗ trợ xử lý bất đồng bộ thông qua Message Broker

---

## 🛠️ Technologies

### Backend

* Java
* Spring Boot
* RESTful API
* WebSocket

### Databases

* PostgreSQL
* MongoDB
* Neo4j

### Messaging & Event Processing

* RabbitMQ

### Cloud Services

* AWS SNS
* AWS S3

### DevOps

* Docker

---

## ✨ Core Features

* User Authentication
* User Profile Management
* One-to-One Messaging
* Group Messaging
* Real-time Communication using WebSocket
* Voice Calling
* File Storage
* Event-based Communication giữa các Microservices

---

## 🚀 Future Improvements

Trong tương lai, dự án sẽ được mở rộng với các tính năng:

### New Features

* Đăng nhập bằng mã QR
* Kết bạn thông qua mã QR
* Gợi ý kết bạn dựa trên mối quan hệ và dữ liệu người dùng

### DevOps

* Tích hợp quy trình CI/CD để tự động:

  * Build
  * Test
  * Deploy

---

## 📖 Project Goal

Dự án được xây dựng nhằm nghiên cứu và áp dụng các công nghệ hiện đại trong việc phát triển hệ thống phân tán, đồng thời nâng cao khả năng thiết kế và triển khai một ứng dụng OTT theo kiến trúc Microservices kết hợp Event-Driven Architecture.

---

## 📄 License

This project is developed for learning and research purposes.
