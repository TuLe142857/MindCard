package vn.edu.ptithcm.mindcard.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import vn.edu.ptithcm.mindcard.config.properties.S3Properties;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;

import java.io.InputStream;
import java.time.Duration;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final S3Properties s3Properties;

    /**
     * Uploads a file to the S3 bucket.
     *
     * @param key the object key under which the file will be stored.
     * @param inputStream the input stream of the file content.
     * @param contentType the MIME type of the file.
     * @param contentLength the size of the file in bytes.
     *
     * @throws AppException if the upload fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#FILE_UPLOAD_FAILED}</li>
     * </ul>
     */
    public void uploadFile(String key, InputStream inputStream, String contentType, long contentLength)
            throws AppException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.defaultBucket())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, contentLength)
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }

    }

    /**
     * Delete file by key
     *
     * @param key s3 object key
     */
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.defaultBucket())
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }

    /**
     * Generate presigned url
     *
     * @param objectKey s3 object key
     * @param expiration expiration of presigned url
     *
     * @return presigned url as String
     */
    public String generatePresignedUrl(String objectKey, Duration expiration) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(b -> b.bucket(s3Properties.defaultBucket()).key(objectKey))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
