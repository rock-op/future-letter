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
  public void upload() throws Exception {
    String fileName = "F:\\test.txt";
    String body = "hi rock, i'm you";
    FileWriter fileWriter = new FileWriter(fileName);
    fileWriter.write(body);
    fileWriter.close();

    String key = "test.txt";
    QiNiuStorageUtils.upload(fileName, key);
  }

  @Test
  public void uploadFile2InvalidBucket() throws Exception {

  }

  @Test
  public void getBucketKey() throws Exception {

  }

}