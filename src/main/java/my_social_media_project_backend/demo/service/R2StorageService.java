package my_social_media_project_backend.demo.service;

import my_social_media_project_backend.demo.utility.BatikTranscoderUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class R2StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicEndPoint;

    public R2StorageService(
            @Value("${cloudflare.r2.accessKey}") String accessKey,
            @Value("${cloudflare.r2.secretKey}") String secretKey,
            @Value("${cloudflare.r2.endpoint}") String endpoint,
            @Value("${cloudflare.r2.bucketName}") String bucketName,
            @Value("${cloudflare.r2.publicEndPoint}") String publicEndPoint) {

        this.bucketName = bucketName;
        this.publicEndPoint = publicEndPoint;
        this.s3Client = S3Client.builder()
                .endpointOverride(java.net.URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .build();
    }

    public String uploadFile(String desiredFilePath, byte[] fileBytes) {
        try {
            String detectedContentType = getContentTypeFromFilePath(desiredFilePath);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(desiredFilePath)
                    .contentType(detectedContentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
            return publicEndPoint + desiredFilePath;
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to R2", e);
        }
    }

    String getContentTypeFromFilePath(String filePath) {
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }

    public String generateUserAvatarPath(Long userId) {
        return String.format("user/%d/avatar/avatar.png", userId);
    }
}
