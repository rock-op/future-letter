package xin.futureme.letter.service;


import java.io.IOException;

/**
 * Created by rockops on 2017-02-06.
 */
public interface StorageService {
  void upload(byte[] data, String bucketName, String key) throws IOException;
  String getBucketKeyContent(String bucketName, String key) throws IOException;
}
