package com.victor.test_demo.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
* @Time 2020-3-16 21:37
* @class md5 加密模块
* */
public class encryption {
    private String ENCRYPTION_STRING;

    public encryption(String parse) {
        this.ENCRYPTION_STRING = parse;
    }
    /*
    * @return 返回加密过的字符串
    * */
    public String convert(){
        String result = MD5(ENCRYPTION_STRING);
        return result;
    }

    /*
    * @param parse :需要加密的字符串
    * */
    private String MD5(String parse){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] input = parse.getBytes();
            messageDigest.update(input);
            byte[] result = messageDigest.digest();
            return ByteArray_To_Hex(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    * @param：byte[] byte 字节数组
    * @method：处理对应的字节，一个字节等于8bit，8位二进制，2^8等于16^2。
    *           利用其与0xf相与，得到对应的十六进制数，每四位对应一个char。
    * */
    private String ByteArray_To_Hex(byte[] bytes){
        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };
        char[] resultCharArray =new char[bytes.length * 2];
        int index = 0;
        for(byte b:bytes){
            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];         //处理高位
            resultCharArray[index++] = hexDigits[b& 0xf];               //处理低位
        }
        return new String(resultCharArray);
    }

}