package xin.futureme.letter.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import xin.futureme.letter.common.StorageConfig;

import static org.junit.Assert.*;

/**
 * Created by rockops on 2017-02-06.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class StorageServiceTest {
  @Autowired
  private StorageService storageService;

  @Test
  public void upload() throws Exception {
    String body = "你好，世界helloWorld!";
    String key = "test.txt";
    storageService.upload(body.getBytes(), StorageConfig.DEFAULT_BUCKET_NAME, key);
  }

  @Test
  public void getBucketKeyContent() throws Exception {
    String key = "test.txt";
    String content = storageService.getBucketKeyContent(StorageConfig.DEFAULT_BUCKET_NAME, key);
    System.err.println(content);
  }

}