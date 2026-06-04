package vn.edu.ptithcm.mindcard.validation;

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortValidator implements ConstraintValidator<ValidSort, List<String>> {

    private Set<String> allowedFields;

    @Override
    public void initialize(ValidSort constraintAnnotation) {
        this.allowedFields = Set.of(constraintAnnotation.allowedFields());
    }

    @Override
    public boolean isValid(List<String> sortParams, ConstraintValidatorContext context) {
        if (sortParams == null || sortParams.isEmpty()) {
            return true;
        }

        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Sort parameter cannot be blank")
                        .addConstraintViolation();
                return false;
            }

            String[] parts = param.split(":");
            if (parts.length > 2) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Invalid sort syntax: " + param + ". Expected format: field:direction")
                        .addConstraintViolation();
                return false;
            }

            String field = parts[0].trim();
            if (!allowedFields.contains(field)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.format("Invalid sort field '%s'. Allowed fields: %s", field, allowedFields))
                        .addConstraintViolation();
                return false;
            }

            if (parts.length == 2) {
                String dir = parts[1].trim().toLowerCase();
                if (!dir.equals("asc") && !dir.equals("desc")) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(String.format("Invalid sort direction '%s'. Allowed values: asc, desc", dir))
                            .addConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }
}
