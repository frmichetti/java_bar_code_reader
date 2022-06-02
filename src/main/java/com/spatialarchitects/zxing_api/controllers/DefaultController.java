package com.spatialarchitects.zxing_api.controllers;

import com.spatialarchitects.zxing_api.dto.TestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DefaultController {
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity<String> testRoute(@RequestBody TestDTO dto) {

        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
    }

    @RequestMapping(value = "/scan", method = RequestMethod.POST, consumes = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<String> receiveFile (@RequestPart("file") MultipartFile mf) {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
