package xin.futureme.letter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class FileUtils {

  private static final Logger loger = LoggerFactory.getLogger(FileUtils.class);
  
  public static String readGZipToString(InputStream in) throws FileNotFoundException, IOException{
    GZIPInputStream zipIn = new GZIPInputStream(in);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[2048];
    int pos = 0;
    while ((pos = zipIn.read(buf)) != -1){
      baos.write(buf, 0, pos);
    }
    zipIn.close();
    return new String(baos.toByteArray(), "UTF-8");
  }
  
  public static void closeQuietly(Closeable close){
    try {
      close.close();
    } catch (IOException e) {}
  }
  
  public static InputStream readByteArray(byte[] data){
    return new ByteArrayInputStream(data);
  }
  
  public static String readZipFromHttp(HttpServletRequest req) throws IOException{
    ZipInputStream in = new ZipInputStream(req.getInputStream());
    return new String(readFully(in));
  }
  
  public static byte[] readFromHttpRequest(HttpServletRequest req) throws IOException{
    return readFully(req.getInputStream());
  }
  
  public static byte[] readFully(InputStream in) throws IOException{
    byte[] buf = new byte[2048];
    int pos = 0;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    while((pos = in.read(buf)) != -1){
      baos.write(buf, 0, pos);
    }
    return baos.toByteArray();
  }
  
  public static byte[] readFully(InputStream in, int len) throws IOException{
    byte[] data = new byte[len];
    DataInputStream dis = new DataInputStream(in);
    dis.readFully(data, 0, len);
    return data;
  }
  
}
