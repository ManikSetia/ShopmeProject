package com.shopme.admin;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//This class is for uploading the user's photos.
public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

        //First check the uploadDir exists or not. If not, create one.
        Path uploadPath= Paths.get(uploadDir);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        //try block with resources
        try(InputStream inputStream=multipartFile.getInputStream()){
            Path filePath=uploadPath.resolve(fileName);//create relative path for the file
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);//copying the file
        }
        catch(IOException ex){
            throw new IOException("Could not save file: "+ fileName, ex);
        }
    }

    public static void cleanup(String dir){
        Path dirPath=Paths.get(dir);

        try{
            Files.list(dirPath).forEach(file -> {
                if(!Files.isDirectory(file)){
                    try{
                        Files.delete(file);
                    }
                    catch (IOException ex){
                        System.out.println("Could not delete the file: "+file);
                    }
                }
            });
        }
        catch (IOException ex){
            System.out.println("Could not list directory: "+dirPath);
        }
    }
}
