package com.example.river.uploadanddownload.server;

import com.example.river.uploadanddownload.upload.StreamTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/11/16.
 */
public class SocketServer {
    private String uploadPath= "D://uploadFile/";
    private ExecutorService executorService;
    private ServerSocket ss = null;
    private int port;
    private boolean quit;
    private Map<Long,FileLog> datas = new HashMap<>();

    public SocketServer(int port){
        this.port=port;
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*50);
    }

    public static void main(String[] args) throws Exception {
        SocketServer server = new SocketServer(7878);
        server.start();
    }

    public void start() throws IOException {
        ss = new ServerSocket(port);
        while (!quit){
            Socket socket = ss.accept();
            executorService.execute(new SocketTask(socket));
        }
    }
    // 退出
    public void quit() {
        this.quit = true;
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class SocketTask implements Runnable{
        private Socket socket;
        public SocketTask(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            try{
                System.out.println("address-"+socket.getInetAddress()+" port-"+socket.getPort());
                PushbackInputStream inputStream = new PushbackInputStream(socket.getInputStream());
                String head = StreamTool.readLine(inputStream);
                if(head!=null){
                    String[] items = head.split(";");
                    String fileLen= items[0].substring(items[0].indexOf("=")+1);
                    String fileName = items[1].substring(items[1].indexOf("=")+1);
                    String sourceId = items[2].substring(items[2].indexOf("=")+1);
                    Long id = System.currentTimeMillis();
                    FileLog fileLog = null;
                    if(!sourceId.equals("")){
                        id=Long.valueOf(sourceId);
                        fileLog = find(id);
                    }
                    File file=null;
                    int position =0;
                    if(fileLog==null){
                        String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
                        File dir = new File(uploadPath+path);
                        if(!dir.exists()) dir.mkdirs();
                        file = new File(dir,fileName);
                        if(file.exists()){

                        }
                        save(id,file);
                    }else {
                        file = new File(fileLog.getPath());
                        if(file.exists()){
                            File logFile = new File(file.getParentFile(),file.getName()+".log");
                            Properties properties = new Properties();
                            properties.load(new FileInputStream(logFile));
                            position = Integer.valueOf(properties.getProperty("length"));
                        }
                    }

                    OutputStream outputStream = socket.getOutputStream();
                    String response = "sourceid="+id+";position="+position+"\r\n";
                    outputStream.write(response.getBytes());

                    RandomAccessFile fileOutStream = new RandomAccessFile(file,"rwd");
                    if(position==0) fileOutStream.setLength(Integer.valueOf(fileLen));
                    fileOutStream.seek(position);
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    int length = position;
                    while ((len=inputStream.read(buffer))!=-1){
                        fileOutStream.write(buffer,0,len);
                        length+=len;
                        Properties properties = new Properties();
                        properties.put("length",String.valueOf(length));
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(file.getParentFile(),file.getName()+".log"));
                        properties.store(fileOutputStream,null);
                       // fileLog.close()
                    }
                    if(length==fileOutStream.length()) delete(id);
                    fileOutStream.close();
                    inputStream.close();
                    outputStream.close();
                    file = null;
                }


            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    if(socket != null && !socket.isClosed()) socket.close();
                } catch (IOException e) {}
            }
        }
    }

    public FileLog find(Long sourceId){
        return datas.get(sourceId);
    }

    public void save(Long sourceId,File file){
        datas.put(sourceId,new FileLog(sourceId,file.getAbsolutePath()));
    }

    // 当文件上传完毕，删除记录
    public void delete(long sourceid) {
        if (datas.containsKey(sourceid))
            datas.remove(sourceid);
    }
    private class FileLog {
        private Long id;
        private String path;

        public FileLog(Long id, String path) {
            super();
            this.id = id;
            this.path = path;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

    }
}
