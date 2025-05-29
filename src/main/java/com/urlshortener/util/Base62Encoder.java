//package com.urlshortener;
//
//public class Base62Encoder {
//    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//
//    public static String encode(long value) {
//        StringBuilder sb = new StringBuilder();
//        while (value > 0) {
//            sb.append(BASE62.charAt((int) (value % 62)));
//            value /= 62;
//        }
//        return sb.reverse().toString();
//    }
//
//    public static String encodeFixedLength(long value, int length) {
//        String encoded = encode(value);
//        // 왼쪽을 0으로 채움
//        return String.format("%" + length + "s", encoded).replace(' ', '0');
//    }
//}
