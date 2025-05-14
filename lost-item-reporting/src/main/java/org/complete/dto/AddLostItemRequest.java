package org.complete.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/*
 * Spring MVC는 @ModelAttribute 또는 multipart/form-data 요청을 처리할 때 다음 순서로 객체를 생성
 * 1. 기본 생성자 (@NoArgsConstructor)로 객체를 생성
 * 2. Setter 메서드를 통해 각 필드에 값 주입
 */
@NoArgsConstructor
@Getter
@Setter
public class AddLostItemRequest {

    @NotBlank(message = "Title is a required field.")
    private String title;

    private String description;

    @NotBlank(message = "Lost location is a required field")
    private String lostLocation;

    @NotNull
    private LocalDateTime lostDate;

    private MultipartFile imageFile;
}