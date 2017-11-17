package com.example.river.uploadanddownload.server;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/11/17.
 */
public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "xue";
        //byte[] bytes = str.getBytes();
        char[] chars = str.toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; i++) {
            String newStr = "0" + Integer.toBinaryString(chars[i]);
            result += newStr;
        }
        int len = result.length() / 4;
        String newResult;
        String[] newArr = new String[4];
        for (int i = 0; i < 4; i++) {
            newResult = result.substring(len * i, len * (i + 1));
            newArr[i] = newResult;
            // System.out.println(BinstrToStr(newResult));
        }
        char[] tempChar = new char[newArr.length];
        StringBuffer sbu = new StringBuffer();
        for (int j = 0; j < newArr.length; j++) {
            System.out.println(newArr[j]);
//            tempChar[j] = BinstrToChar(newArr[j]);
            int sum= BinstrToChar(newArr[j]);
            sbu.append((char) sum);
        }
        System.out.println(sbu.toString());



    }

//    private static String BinstrToStr(String binStr) {
//        String[] tempStr=StrToStrArray(binStr);
//        char[] tempChar=new char[tempStr.length];
//        for(int i=0;i<tempStr.length;i++) {
//            tempChar[i]=BinstrToChar(tempStr[i]);
//        }
//        return String.valueOf(tempChar);
//    }

    public static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;

        }
        return result;
    }

    //将二进制转换成字符
    public static int BinstrToChar(String binStr) {

        int[] temp = BinstrToIntArray(binStr);

        int sum = 0;
        for (int i = 0; i < temp.length; i++) {

            sum += temp[temp.length - 1 - i] << i;
        }

        return sum;
    }
}
