package xin.futureme.letter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtils {

  public static final int TIMEOUT = 5 * 1000;
  public static final String METHOD = "GET";
  
  public static byte[] readData(InputStream in) throws IOException{
    return FileUtils.readFully(in);
  }
  
  public static InputStream downloadResource(String url) throws IOException{
    URL url_ = new URL(url);
    HttpURLConnection conn = (HttpURLConnection)url_.openConnection();
    conn.setConnectTimeout(TIMEOUT);
    conn.setRequestMethod(METHOD);
    return conn.getInputStream();
  }
  
  public static byte[] downloadToByteArray(String url) throws IOException{
    return readData(downloadResource(url));
  }
  
}
