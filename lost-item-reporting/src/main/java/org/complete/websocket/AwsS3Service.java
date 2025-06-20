package org.complete.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private static final Logger logger = Logger.getLogger(AwsS3Service.class.getName());

    private final S3Client s3Client; // AWS SDK v2 기준
    private final String bucketName = "your-bucket-name"; // S3 버킷 이름

    public URL upload(MultipartFile file, String dirName) {
        // 파일 이름 생성
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // PutObjectRequest 빌드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            // 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            logger.info("파일 업로드 성공: " + fileName);

            // 업로드된 파일의 URL 반환
            return new URL("https://" + bucketName + ".s3.amazonaws.com/" + fileName);
        } catch (IOException e) {
            logger.severe("파일 업로드 실패: " + e.getMessage());
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.severe("예상치 못한 오류: " + e.getMessage());
            throw new RuntimeException("알 수 없는 오류가 발생했습니다.", e);
        }
    }
}
