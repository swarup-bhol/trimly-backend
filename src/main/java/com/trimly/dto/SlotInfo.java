package com.trimly.dto;
import lombok.*; import java.time.LocalTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SlotInfo {
    LocalTime time; String label; boolean taken; boolean available;
    int seatsTotal; int seatsUsed; int seatsLeft;
}
