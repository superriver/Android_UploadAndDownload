package com.example.river.multipletask;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/18.
 */

public class FileInfo implements Serializable{
  private int id;
  private String fileName;
  private String url;
  //文件的大小
  private int len;

  //文件的开始位置
  private int start;

  //文件当前位置
  private int finished;

  private boolean isPause;
  private boolean isDownloading;
  public FileInfo(){

  }
public FileInfo(int id,String fileName,String url){
  this.id = id;
    this.fileName = fileName;
    this.url = url;
}
  public boolean isPause() {
    return isPause;
  }

  public void setPause(boolean pause) {
    isPause = pause;
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

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
