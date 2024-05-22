package by.pavvel.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Component("simpleLoader")
public class SimpleFileLoader implements FileLoader {

    @Value("${upload-path}")
    private String uploadPath;

    public String attachFile(MultipartFile file) {
        String resultFile;
        try {
            resultFile = transferFileToDirectory(file);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot attach file: " + file);
        }
        return resultFile;
    }

    private String transferFileToDirectory(MultipartFile file) throws IOException {
        File uploadDir = new File(uploadPath);
        checkDirectoryExistence(uploadDir);

        String uuid = UUID.randomUUID().toString();
        String resultFile = uuid + "." + file.getOriginalFilename();
        file.transferTo(Paths.get(uploadPath + "/" + resultFile));
        return resultFile;
    }

    private void checkDirectoryExistence(File uploadDir) {
        if (!uploadDir.exists())
            makeDirectory(uploadDir);
    }

    private void makeDirectory(File uploadDir) {
        uploadDir.mkdir();
    }
}
