package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class StorageService {
    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Autowired
    private S3Properties s3Properties;

    public void uploadFile(String key, InputStream inputStream, String contentType, long contentLength)
        throws AppException
    {
        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.defaultBucket())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(inputStream, contentLength)
            );
        }catch (Exception e){
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, e.getMessage());
        }

    }

    public void deleteFile(String key){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Properties.defaultBucket())
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }


    public String generatePresignedUrl(String objectKey, Duration expiration) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(b -> b.bucket(s3Properties.defaultBucket()).key(objectKey))
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
