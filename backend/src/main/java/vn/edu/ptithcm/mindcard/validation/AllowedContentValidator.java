package vn.edu.ptithcm.mindcard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class AllowedContentValidator implements ConstraintValidator<AllowedContentType, MultipartFile> {
    private Set<String> allowedTypes;
    private boolean allowEmpty;
    @Override
    public void initialize(AllowedContentType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        allowedTypes = Set.of(constraintAnnotation.types());
        allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()){
            if (allowEmpty){
                return true;
            }

            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("file can not be empty")
                    .addConstraintViolation();
            return false;
        }

        if(! allowedTypes.contains(file.getContentType())){
            String message = String.format(
                    "Invalid type '%s', available files : %s",
                    file.getContentType(),
                    allowedTypes.toString());

            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
