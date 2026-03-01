package com.trimly.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BlockedSlotRequest {
    LocalDate date;
    LocalTime slotTime;
}