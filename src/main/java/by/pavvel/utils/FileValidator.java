package by.pavvel.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@Component
public class FileValidator implements Validator {

    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    private final long maxUploadSize = 5 * 1024 * 1024; // 5 MB

    @Override
    public boolean supports(Class<?> clazz) {
        return MultipartFile.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        MultipartFile[] files = (MultipartFile[]) target;

        Arrays.stream(files).forEach(file -> {
            boolean isRealFile = !Objects.requireNonNull(file.getOriginalFilename()).isEmpty();
            if (isRealFile) {
                if(file.isEmpty()){
                    errors.rejectValue(
                            "images",
                            "file.empty",
                            new Object[]{file.getOriginalFilename()},
                            "File cannot be empty"
                    );
                }
                if(!isValidFormat(file)) {
                    errors.rejectValue(
                            "images",
                            "file.format",
                            new Object[]{file.getOriginalFilename()},
                            "File must be in format jpeg, png or gif"
                    );
                }
                if(file.getSize() > maxUploadSize){
                    errors.rejectValue(
                            "images",
                            "file.size",
                            new Object[]{file.getOriginalFilename(), convertToMb()},
                            "File size cannot be more than 5 MB"
                    );
                }
            }
        });
    }

    private long convertToMb() {
        return Math.round((float) maxUploadSize / 1024 / 1024);
    }

    private boolean isValidFormat(MultipartFile file) {
        for (String allowedFileType : ALLOWED_FILE_TYPES) {
            if (Objects.equals(file.getContentType(), allowedFileType)) {
                return true;
            }
        }
        return false;
    }
}