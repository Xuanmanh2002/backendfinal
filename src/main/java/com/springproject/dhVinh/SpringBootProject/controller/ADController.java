//package com.springproject.dhVinh.SpringBootProject.controller;
//
//import com.springproject.dhVinh.SpringBootProject.response.ApplicationDocumentsResponse;
//import com.springproject.dhVinh.SpringBootProject.service.IADService;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/customer")
//public class ADController {
//    private final IADService adService;
//
//    @PostMapping(value = "/add-application-documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @SneakyThrows
//    public ResponseEntity<?> saveApplicationDocuments(@ModelAttribute(name = "ApplicationDocumentsResponse")ApplicationDocumentsResponse response,
//                                                        @RequestPart("cv") MultipartFile cv) {
//        try{
//            Path path = Paths.get("/home/suanmanh/Downloads/pictures" );
//            Files.createDirectories(path);
//            try{
//                InputStream inputStream = cv.getInputStream();
//                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
//        }
//    }
//}
