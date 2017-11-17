package com.example.river.uploadanddownload.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * Created by Administrator on 2017/11/16.
 */
public class StreamTool {

    public static void save(File file,byte[] data) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);
        outputStream.close();
    }
    public static String readLine(PushbackInputStream pb) throws IOException {
        char buf[] =new char[128];
        int room= buf.length;
        int offset = 0;
        int c;
loop:        while (true){
            switch (c= pb.read()){
                case -1:
                case '\n':
                    break loop;
                case '\r':
                    int c2 = pb.read();
                    if((c2!='\n')&&(c2!=-1)) pb.unread(c2);
                    break loop;
                default:
                    if(--room<0){
                        char[] lineBuffer = buf;
                        buf = new char[offset+128];
                        room=buf.length-offset-1;
                        System.arraycopy(lineBuffer,0,buf,0,offset);
                    }
                    buf[offset++] = (char) c;
                    break ;
            }
        }
        if((c==-1)&&(offset==0)) return null;
        return String.copyValueOf(buf,0,offset);
    }
    public  static byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len=inputStream.read(buf))!=-1){
                byteArrayOutputStream.write(buf,0,len);
        }
        byteArrayOutputStream.close();
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    }
