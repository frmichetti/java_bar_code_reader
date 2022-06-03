package com.spatialarchitects.zxing_api.controllers;

import com.google.zxing.*;
import com.spatialarchitects.zxing_api.dto.input.TestDTO;
import com.spatialarchitects.zxing_api.dto.response.CodeDTO;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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
    public ResponseEntity<CodeDTO> receiveFile (@RequestPart("file") MultipartFile mf) {
        String barcode;
        try {

            Map<DecodeHintType, Boolean> decodeHintMap = new HashMap<>();
            decodeHintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            decodeHintMap.put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);
            decodeHintMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
            barcode = readCode(mf.getInputStream(), "UTF-8", decodeHintMap);
            System.out.println(barcode);
        } catch (IOException | ChangeSetPersister.NotFoundException | NotFoundException  e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(new CodeDTO(barcode));
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

    public static String readCode(InputStream inputStream, String charset, Map hintMap)
            throws FileNotFoundException, IOException, ChangeSetPersister.NotFoundException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(ImageIO.read(inputStream))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
        return qrCodeResult.getText();
    }

    public static void main(String[] args) throws WriterException, IOException,
            NotFoundException {
        String qrCodeData = "Hello World!";
        String filePath = "5.png";
        String charset = "UTF-8"; // or "ISO-8859-1"

        Map<EncodeHintType, ErrorCorrectionLevel> encodeHintMap = new HashMap<>();
        encodeHintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        Map<DecodeHintType, Boolean> decodeHintMap = new HashMap<>();
        decodeHintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        decodeHintMap.put(DecodeHintType.ALSO_INVERTED, Boolean.TRUE);
        decodeHintMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);

        /*createQRCode(qrCodeData, filePath, charset, encodeHintMap, 200, 200);
        System.out.println("QR Code image created successfully!");*/

        try {
            System.out.println("Data read from QR or BAR Code: "
                    + readCode(filePath, charset, decodeHintMap));
        } catch (ChangeSetPersister.NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
