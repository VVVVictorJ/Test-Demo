package com.victor.test_demo.utils.modules.encode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


public class base64_module {
    private String IMAGE_URL;
    static String STORAGE_URL = "/storage/emulated/0/DCIM/";//TODO 改路径

    public base64_module() {

    }

    public base64_module(String IMAGE_URL) {
        this.IMAGE_URL = IMAGE_URL;
    }
    /*
        将图片编码成 Base64字符串
     */
    public String Encode_To_Base64() throws Exception {
        File file = new File(IMAGE_URL);
        FileInputStream inputStream = new FileInputStream(IMAGE_URL);
        byte[] buffer =  new byte[(int)file.length()];
        //noinspection ResultOfMethodCallIgnored
        inputStream.read(buffer);
        inputStream.close();
        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(buffer);
    }

    public void  Decode_To_Image(String Base64,String Type){
        BASE64Decoder decoder = new BASE64Decoder();
        String random_file_name = UUID.randomUUID().toString();
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(STORAGE_URL+random_file_name+"."+Type));//随机uuid存储解码后的图片
            byte[] decodeBytes = decoder.decodeBuffer(Base64);
            outputStream.write(decodeBytes);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ...args)throws Exception{
        String string =  new base64_module("C:\\编程\\java\\okhttp-test\\src\\source\\test.PNG").Encode_To_Base64();
        new base64_module().Decode_To_Image(string,"PNG");

    }
}
