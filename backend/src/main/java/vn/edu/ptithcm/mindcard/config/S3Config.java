package vn.edu.ptithcm.mindcard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import vn.edu.ptithcm.mindcard.config.properties.S3Properties;

import java.net.URI;

@Configuration
public class S3Config {
    @Autowired
    private S3Properties s3Properties;

    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials credentials = AwsBasicCredentials.create(s3Properties.accessKey(), s3Properties.secretKey());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        return S3Client.builder()
                .region(Region.of(s3Properties.region()))
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(s3Properties.endpoint()))
                .build();
    }
}
