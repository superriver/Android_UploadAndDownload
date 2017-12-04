package com.example.river.uploadanddownload.download;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/18.
 */

public class FileInfo implements Serializable{
  private String fileName;
  private String url;
  //文件的大小
  private int len;

  //文件结束位置
  private int finished;

  private boolean isDownloading;
  public FileInfo(){

  }
public FileInfo(String fileName,String url){
    this.fileName = fileName;
    this.url = url;
}

  public boolean isDownloading() {
    return isDownloading;
  }

  public void setDownloading(boolean downloading) {
    isDownloading = downloading;
  }


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }


  public int getFinished() {
    return finished;
  }

  public void setFinished(int finished) {
    this.finished = finished;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
}
