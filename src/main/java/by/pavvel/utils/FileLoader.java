package by.pavvel.utils;

import org.springframework.web.multipart.MultipartFile;

public interface FileLoader {
    String attachFile(MultipartFile file);
}
