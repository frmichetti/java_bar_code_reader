package com.spatialarchitects.zxing_api.controllers;

import com.spatialarchitects.zxing_api.dto.TestDTO;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


@RestController
public class DefaultController {
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public ResponseEntity<String> testRoute(@RequestBody TestDTO dto) {

        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
    }

    @RequestMapping(value = "/scan", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<String> receiveFile (@RequestPart("file") MultipartFile mf) {
        System.out.println(mf.getContentType());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public static void createQRCode(String qrCodeData, String filePath,
                                    String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(
                new String(qrCodeData.getBytes(charset), charset),
                BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath
                .lastIndexOf('.') + 1), new File(filePath));
    }

    public static String readCode(String filePath, String charset, Map hintMap)
            throws FileNotFoundException, IOException, ChangeSetPersister.NotFoundException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(new FileInputStream(filePath)))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap,
                hintMap);
        return qrCodeResult.getText();
    }

    public static void main(String[] args) throws WriterException, IOException,
            NotFoundException {
        String qrCodeData = "Hello World!";
        String filePath = "56eebb8a-1160-4488-9209-5e16fb24b8b8.png";
        String charset = "UTF-8"; // or "ISO-8859-1"
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        /*createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);
        System.out.println("QR Code image created successfully!");*/

        try {
            System.out.println("Data read from QR or BAR Code: "
                    + readCode(filePath, charset, hintMap));
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
