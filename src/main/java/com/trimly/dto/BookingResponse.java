package com.trimly.dto;
import com.trimly.enums.*; import lombok.*;
import java.math.BigDecimal; import java.time.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingResponse {
    Long id; Long shopId; String shopName; String shopEmoji;
    Long customerId; String customerName; String customerPhone;
    String servicesSnapshot; LocalDate bookingDate; LocalTime slotTime;
    int durationMinutes; int seats; BigDecimal totalAmount;
    BigDecimal platformFee; BigDecimal barberEarning;
    BookingStatus status; String cancelReason; Integer rating; String review;
    LocalDate rescheduleDate; LocalTime rescheduleTime; String rescheduleReason;
    RescheduleStatus rescheduleStatus; LocalDateTime createdAt;
}
