package org.complete.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateLostItemRequest {

    @NotBlank(message = "Title is a required field.")
    private String title;

    private String description;

    @NotBlank(message = "Lost location is a required field")
    private String lostLocation;

    @NotNull
    private LocalDateTime lostDate;

    private String imageUrl;

    @NotBlank(message = "Status is a required field")
    private String status;
}