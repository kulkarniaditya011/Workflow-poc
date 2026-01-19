package com.example.backend.utilService;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class EncryptionUtil {

    public static String encode(String plaintext){
        try{
            return URLEncoder.encode(plaintext, StandardCharsets.UTF_8);
        }catch (Exception e){
            return plaintext;
        }
    }

    public static String decode(String encString){
        try{
            return URLDecoder.decode(encString, StandardCharsets.UTF_8);
        }catch (Exception e){
            return encString;
        }
    }

}
