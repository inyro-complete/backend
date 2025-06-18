package org.complete.founditem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class UpdateFoundItemRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "설명은 필수입니다.")
    private String description;

    @NotBlank(message = "습득 장소는 필수입니다.")
    private String foundLocation;

    @NotNull(message = "습득 날짜는 필수입니다.")
    private LocalDateTime foundDate;

    private String storageLocation;
    private String storageContact;
    private String loserName;

    private MultipartFile image;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;
}
