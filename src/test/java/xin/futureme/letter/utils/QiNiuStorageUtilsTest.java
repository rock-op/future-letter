package xin.futureme.letter.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileWriter;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * Created by rockOps on 2017-01-24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class QiNiuStorageUtilsTest {
  @Test
  public void uploadFromFile() throws Exception {
    String fileName = "F:\\test.txt";
    String body = "你好，世界。hello world ";
    FileWriter fileWriter = new FileWriter(fileName);
    fileWriter.write(body);
    fileWriter.close();

    String key = "test.txt";
    QiNiuStorageUtils.upload2DefaultBucket(fileName, key);
  }

  @Test
  public void uploadFromData() throws Exception {
    String body = "你好，世界。hello world #@!@$(";
    String key = "test1.txt";
    QiNiuStorageUtils.upload2DefaultBucket(body.getBytes(), key);
  }

  @Test
  public void uploadFile2InvalidBucket() throws Exception {

  }

  @Test
  public void getBucketKeyContent() throws Exception {
    String bucketName = "future-me-letters";
    String key = "test.txt";
    String content = QiNiuStorageUtils.getBucketKeyContent(bucketName, key);
    System.out.println(content);
  }

}