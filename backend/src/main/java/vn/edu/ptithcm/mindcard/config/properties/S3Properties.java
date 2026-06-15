package vn.edu.ptithcm.mindcard.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String endpoint,
        String publicUrlOverride,
        String region,
        String accessKey,
        String secretKey,
        String defaultBucket
) {
    public S3Properties {
        Objects.requireNonNull(endpoint, "s3.endpoint is required");
        Objects.requireNonNull(accessKey,"s3.access-key is required");
        Objects.requireNonNull(secretKey,"s3.secret-key is required");
        Objects.requireNonNull(defaultBucket, "s3.default-bucket is required");

        if (publicUrlOverride == null || publicUrlOverride.isBlank()){
            publicUrlOverride = endpoint;
        }

        if (region == null || region.isBlank()){
            region = "auto";
        }
    }
}
