# Session 08 - Bài 03

Bài này mở rộng hệ thống đặt lịch, dùng Appointment-Service `8083`, Doctor-Service `8082`, Patient-Service `8081` và Eureka `8761`.

## Chạy

```bash
docker compose up -d
cd medical-discovery-server && gradle bootRun
```

Mở terminal riêng để chạy lần lượt `patient-service`, `doctor-service`, rồi `appointment-service` bằng `gradle bootRun`. Đăng ký tối thiểu một bệnh nhân và một bác sĩ trước khi tạo lịch.

Tạo lịch bằng `POST http://localhost:8083/api/v1/appointments` với JSON có `patientId`, `doctorId`, `appointmentDate` ở tương lai và `reason`.

## Nội dung

Thêm Retry 3 lần, cách nhau 2 giây cho lỗi kết nối Patient-Service.

Các log trạng thái Resilience4j bật ở mức DEBUG. Cấu hình thời gian chờ client ngắn để lỗi kết nối được ghi nhận nhanh.
