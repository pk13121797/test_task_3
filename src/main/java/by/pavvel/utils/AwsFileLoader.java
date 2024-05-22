package by.pavvel.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component("awsLoader")
public class AwsFileLoader implements FileLoader {

    @Override
    public String attachFile(MultipartFile file) {
        return null;
    }
}
