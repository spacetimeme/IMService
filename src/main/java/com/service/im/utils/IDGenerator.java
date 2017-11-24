package com.service.im.utils;

import java.util.UUID;

public class IDGenerator {

    public static String getGeneratorID() {
        UUID uuid = UUID.randomUUID();
        return Long.toString(uuid.getLeastSignificantBits(), 16).replace("-", "");
//        String id= Long.toString(uuid.getMostSignificantBits(), 32).replace("-", "");
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static void main(String[] args) {
        System.out.println(IDGenerator.getGeneratorID());
    }

}
