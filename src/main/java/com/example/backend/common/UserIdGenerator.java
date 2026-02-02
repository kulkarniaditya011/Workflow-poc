package com.example.backend.common;

import java.util.UUID;

public class UserIdGenerator {

    public static void main(String[] args) {
        String id= generateUserId();
        System.out.println("userId: " + id);
    }
    public static String generateUserId() {
        return "usr_" +UUID.randomUUID();
    }
}
