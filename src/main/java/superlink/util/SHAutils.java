package superlink.util;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @program: CSDN
 * @description: SHA工具类
 * @author: Alian
 * @create: 2021-06-01 15:20:10
 **/
public class SHAutils {

    public static final String MD_5 = "MD5";
    public static final String SHA_1 = "SHA-1";
    public static final String SHA_224 = "SHA-224";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_384 = "SHA-384";
    public static final String SHA_512 = "SHA-512";

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getMD5(String painText, boolean uppercase) {
        return getSha(painText, MD_5, uppercase);
    }

    public static String getSHA1(String painText, boolean uppercase) {
        return getSha(painText, SHA_1, uppercase);
    }

    public static String getSHA224(String painText, boolean uppercase) {
        return getSha(painText, SHA_224, uppercase);
    }

    public static String getSHA256(String painText, boolean uppercase) {
        return getSha(painText, SHA_256, uppercase);
    }

    public static String getSHA384(String painText, boolean uppercase) {
        return getSha(painText, SHA_384, uppercase);
    }

    public static String getSHA512(String painText, boolean uppercase) {
        return getSha(painText, SHA_512, uppercase);
    }

    /**
     * 利用Java原生摘要实现SHA加密(支持大小写，默认小写)
     *
     * @param plainText 要加密的数据
     * @param algorithm 要使用的算法（SHA-1,SHA-2224,SHA-256,SHA-384,SHA-512）
     * @param uppercase 是否转为大写
     * @return
     */
    private static String getSha(String plainText, String algorithm, boolean uppercase) {
        //输入的字符串转换成字节数组
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance(algorithm);
            //bytes是输入字符串转换得到的字节数组
            messageDigest.update(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA签名过程中出现错误,算法异常");
        }
        //转换并返回结果，也是字节数组，包含16个元素
        byte[] digest = messageDigest.digest();
        //字符数组转换成字符串返回
        String result = byteArrayToHexString(digest);
        //转换大写
        return uppercase ? result.toUpperCase() : result;
    }
    public static byte[] getSha(byte[] bytes, String algorithm) {
        //输入的字符串转换成字节数组
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance(algorithm);
            //bytes是输入字符串转换得到的字节数组
            messageDigest.update(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA签名过程中出现错误,算法异常");
        }
        //转换并返回结果，也是字节数组，包含16个元素
        byte[] digest = messageDigest.digest();
        return digest;
    }

    /**
     * 将字节数组转为16进制字符串
     * @param bytes 要转换的字节数组
     * @return
     */
    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            //java.lang.Integer.toHexString() 方法的参数是int(32位)类型，
            //如果输入一个byte(8位)类型的数字，这个方法会把这个数字的高24为也看作有效位，就会出现错误
            //如果使用& 0XFF操作，可以把高24位置0以避免这样错误
            String temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }
    public static int defautMemoryMean=1024*1024*16;
    public static String getShaFromFile(String filename, String algorithm, boolean uppercase) {
        File file=new File(filename);
        if (!file.exists()){
            return "";
        }
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        byte[] bytes=null;
        ByteBuffer byteBuffer=null;
        long i=0;
        while (byteBuffer==null){
            try {
                byteBuffer=ByteBuffer.allocate(defautMemoryMean);
            }catch (Exception e){
                i++;
                defautMemoryMean=defautMemoryMean/2;
                System.out.println(e.getMessage()+"  "+defautMemoryMean);
            }
            if (i>16){
                return null;
            }
        }


        i=file.length()/(defautMemoryMean);
        byte[] hash=new byte[length.valueOf(algorithm).Type()];

        try (FileInputStream filetream=new FileInputStream(file)) {
            FileChannel fileChannel=filetream.getChannel();
            while (i>=0){
                int len=fileChannel.read(byteBuffer);
                messageDigest.update(byteBuffer.array(),0,len);
//                bytes=getSha(byteBuffer.array(),algorithm);
//                for (int j = 0; j < bytes.length; j++) {
//                    hash[j]=(byte) (hash[j]^bytes[j]);
////                        hash[j]^=bytes[j];
//                }
                i--;
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound:  "+filename);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("fileLength<=0  "+e.fillInStackTrace());
        }

        String result = byteArrayToHexString(messageDigest.digest());
        //转换大写
        return uppercase ? result.toUpperCase() : result;
    }

    public static String getShaFromByte(byte[] bytes, String algorithm, boolean uppercase) {
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance(algorithm);
            //bytes是输入字符串转换得到的字节数组
            messageDigest.update(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA签名过程中出现错误,算法异常");
        }
        //转换并返回结果，也是字节数组，包含16个元素
        byte[] digest = messageDigest.digest();
        //字符数组转换成字符串返回
        String result = byteArrayToHexString(digest);
        //转换大写
        return uppercase ? result.toUpperCase() : result;
    }
    public static String getShaFromBytes(byte[][] bytess, String algorithm, boolean uppercase) {
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance(algorithm);
            //bytes是输入字符串转换得到的字节数组
            for (byte[] bytes:bytess){
                messageDigest.update(bytes);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA签名过程中出现错误,算法异常");
        }
        //转换并返回结果，也是字节数组，包含16个元素
        byte[] digest = messageDigest.digest();
        //字符数组转换成字符串返回
        String result = byteArrayToHexString(digest);
        //转换大写
        return uppercase ? result.toUpperCase() : result;
    }



    public enum length implements type{
        MD5(){
            @Override
            public Integer Type() {
                return new Integer(32);
            }
        },
        MD_5(){
            @Override
            public Integer Type() {
                return new Integer(32);
            }
        },
        SHA_1 (){
            public Integer Type() {
                return new Integer(40);
            }
        },
        SHA_224 (){
            public Integer Type() {
                return new Integer(56);
            }
        },
        SHA_256 (){
            public Integer Type() {
                return new Integer(64);
            }
        },
        SHA_384 (){
            public Integer Type() {
                return new Integer(96);
            }
        },
        SHA_512 (){
            public Integer Type() {
                return new Integer(128);
            }
        };
    }
    public interface type {
        abstract Integer Type();

    }
}
