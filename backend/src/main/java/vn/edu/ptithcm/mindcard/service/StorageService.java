package vn.edu.ptithcm.mindcard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class StorageService {
    @Autowired
    private S3Client s3Client;

    public String generatePresignedUrl(String key, String bucket){
        return "";
    }

    public void uploadObject(String key, String bucket, Object content){

    }

    public void deleteObject(String key, String bucket){
        
    }
}
