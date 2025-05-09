package org.complete.founditem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddFoundItemRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String description;

    @NotBlank(message = "습득 장소는 필수입니다.")
    private String foundLocation;

    @NotNull(message = "습득 날짜는 필수입니다.")
    private LocalDateTime foundDate;

    private MultipartFile image;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    @NotBlank(message = "보관 장소는 필수입니다.")
    private String storageLocation;
}

