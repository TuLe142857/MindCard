package vn.edu.ptithcm.mindcard.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPublicProfileResponse;
import vn.edu.ptithcm.mindcard.dto.response.user.UserPrivateProfileResponse;
import vn.edu.ptithcm.mindcard.entity.User;
import vn.edu.ptithcm.mindcard.exception.AppException;
import vn.edu.ptithcm.mindcard.exception.ErrorCode;
import vn.edu.ptithcm.mindcard.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;

    /**
     * Retrieves the private profile of the currently authenticated user,
     * including sensitive information such as email.
     *
     * @param username the username of the authenticated user.
     * @return a {@link UserPrivateProfileResponse} containing private profile details.
     * @throws AppException if any business validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#USER_NOT_FOUND} - if the user does not exist in the database.</li>
     * </ul>
     */
    public UserPrivateProfileResponse getSelfProfile(String username) throws AppException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with name: " + username));

        String avatarUrl = resolveAvatarUrl(user);

        return UserPrivateProfileResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(avatarUrl)
                .build();
    }

    /**
     * Retrieves the public profile of a user by their username.
     * Does NOT expose sensitive information such as email.
     *
     * @param username the username of the user to look up.
     * @return a {@link UserPublicProfileResponse} containing public profile details.
     * @throws AppException if any business validation fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#USER_NOT_FOUND} - if the user does not exist in the database.</li>
     * </ul>
     */
    public UserPublicProfileResponse getPublicProfile(String username) throws AppException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with name: " + username));

        String avatarUrl = resolveAvatarUrl(user);

        return UserPublicProfileResponse.builder()
                .username(user.getUsername())
                .avatarUrl(avatarUrl)
                .build();
    }

    /**
     * Resolves the presigned URL for a user's avatar if an object key is set.
     *
     * @param user the user entity.
     * @return the presigned avatar URL, or {@code null} if no avatar is set.
     */
    private String resolveAvatarUrl(User user) {
        if (user.getAvatarObjectKey() != null && !user.getAvatarObjectKey().isBlank()) {
            return storageService.generatePresignedUrl(user.getAvatarObjectKey(), Duration.ofMinutes(15));
        }
        return null;
    }

    /**
     * Updates the avatar of the currently logged-in user. Deletes the old
     * avatar file from storage after update success if it exists.
     *
     * @param userId the ID of the user whose avatar is to be updated.
     * @param file the new avatar file.
     *
     * @return the presigned URL of the newly uploaded avatar.
     *
     * @throws AppException if validation fails or file upload encounters
     * errors, specifically:
     * <ul>
     * <li>{@link ErrorCode#USER_NOT_FOUND} - if the user does not exist in the
     * database.</li>
     * <li>{@link ErrorCode#FILE_UPLOAD_FAILED} - if the avatar file fails to
     * upload to storage.</li>
     * </ul>
     */
    @Transactional
    public String updateAvatar(int userId, MultipartFile file) throws AppException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "User not found with id: " + userId));

        // Get old key here to delete after update success
        String oldKey = user.getAvatarObjectKey();

        // Upload new avatar file
        String newKey = uploadAvatar(file);

        // Update user entity
        user.setAvatarObjectKey(newKey);
        userRepository.save(user);

        // After update success, delete old avatar
        if (oldKey != null && !oldKey.isBlank()) {
            try {
                storageService.deleteFile(oldKey);
            } catch (Exception e) {
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED, "Some thing went wrong, please try again");
            }
        }

        // Generate presigned URL for response
        return storageService.generatePresignedUrl(newKey, Duration.ofMinutes(15));
    }

    /**
     * Uploads avatar file to storage and returns the generated object key.
     *
     * @param file the file to upload.
     * @return the generated object key, required not {@code blank}(not {@code null} or empty)
     * @throws AppException if file upload fails, specifically:
     * <ul>
     * <li>{@link ErrorCode#FILE_UPLOAD_FAILED} - if file is {@code blank}({@code null} or emtpy) or the S3 upload encounters IO
     * issues.</li>
     * </ul>
     */
    private String uploadAvatar(MultipartFile file) throws AppException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        String key = "avatar_" + UUID.randomUUID().toString();
        try {
            storageService.uploadFile(
                    key,
                    new BufferedInputStream(file.getInputStream()),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (IOException ioException) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return key;
    }
}
