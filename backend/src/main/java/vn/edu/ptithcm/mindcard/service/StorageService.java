package vn.edu.ptithcm.mindcard.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.BufferedInputStream;
import java.io.IOException;
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
     * @return file object key if upload success.
     *
     * @throws AppException if the upload fails, specifically:
     * <ul>
     *     <li>{@link ErrorCode#FILE_UPLOAD_FAILED}</li>
     * </ul>
     */
    public String uploadFile(String key, InputStream inputStream, String contentType, long contentLength)
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

            return key;
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }

    }

    /**
     * Upload file
     *
     * @param key object key
     * @param file file to upload
     *
     * @return {@code null} if file is {@code null} or empty, else return object key if file upload success
     *
     * @throws AppException with {@link ErrorCode#FILE_UPLOAD_FAILED}
     */
    public String uploadMultipartFile(String key, MultipartFile file) throws AppException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            return this.uploadFile(
                    key,
                    new BufferedInputStream(file.getInputStream()),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
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
