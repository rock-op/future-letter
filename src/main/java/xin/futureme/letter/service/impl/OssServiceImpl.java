package xin.futureme.letter.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xin.futureme.letter.common.StorageConfig;
import xin.futureme.letter.service.StorageService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by rockops on 2017-02-06.
 */
@Service
public class OssServiceImpl implements StorageService{
  private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);

  @Override
  public void upload(byte[] data, String bucketName, String key) throws IOException {
    OSSClient ossClient = new OSSClient(StorageConfig.INTERNAL_ENDPOINT, StorageConfig.ACCESS_ID, StorageConfig.ACCESS_KEY);
    ossClient.putObject(bucketName, key, new ByteArrayInputStream(data));
    ossClient.shutdown();
  }

  @Override
  public String getBucketKeyContent(String bucketName, String key) throws IOException {
    OSSClient ossClient = new OSSClient(StorageConfig.INTERNAL_ENDPOINT, StorageConfig.ACCESS_ID, StorageConfig.ACCESS_KEY);
    OSSObject ossObject = ossClient.getObject(bucketName, key);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
    StringBuilder content = new StringBuilder();
    while (true) {
      String line = bufferedReader.readLine();
      if (line == null) {
        break;
      }
      content.append(line);
    }
    bufferedReader.close();
    ossClient.shutdown();
    return content.toString();
  }
}
