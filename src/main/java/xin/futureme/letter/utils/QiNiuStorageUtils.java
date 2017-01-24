package xin.futureme.letter.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import xin.futureme.letter.config.QiNiuConfig;

import java.io.IOException;

/**
 * Created by rockOps on 2017-01-23.
 */
public class QiNiuStorageUtils {
  private final static Logger logger = LoggerFactory.getLogger(QiNiuStorageUtils.class);

  private final static String APP_KEY = QiNiuConfig.QINIU_APP_KEY;
  private static final String APP_SECRET = QiNiuConfig.QINIU_APP_SECRET;
  private static final String DEFAULT_BUCKET_NAME = QiNiuConfig.QINIU_BUCKET_NAME;
  private final static String DEFAULT_BUCKET_DOMAIN = QiNiuConfig.QINIU_BUCKET_DOMAIN;
  private final static int DEFAULT_TIMEOUT = 3600;

  // key就是bucket中的文件名
  public static void upload(String fileName, String key) throws QiniuException {
    upload(fileName, DEFAULT_BUCKET_NAME, key);
  }

  public static void upload(String fileName, String bucketName, String key) throws QiniuException {
    Zone zone = Zone.autoZone();
    Configuration c = new Configuration(zone);
    UploadManager uploadManager = new UploadManager(c);
    String uploadToken = getUploadToken(bucketName, key);

    Response response = uploadManager.put(fileName, key, uploadToken);
    logger.info(response.bodyString());
  }

  public static void upload(byte[] data, String key) throws QiniuException {
    upload(data, DEFAULT_BUCKET_NAME, key);
  }

  public static void upload(byte[] data, String bucketName, String key) throws QiniuException {
    Zone zone = Zone.autoZone();
    Configuration c = new Configuration(zone);
    UploadManager uploadManager = new UploadManager(c);
    String uploadToken = getUploadToken(bucketName, key);

    Response response = uploadManager.put(data, key, uploadToken);
    logger.info(response.bodyString());
  }

  private static String getUploadToken(String bucketName, String key) {
    StringMap attributes = new StringMap();
    attributes.put("insertOnly", 1);
    int timeout = DEFAULT_TIMEOUT;
    return getUploadToken(bucketName, key, timeout, attributes);
  }

  private static String getUploadToken(String bucketName, String key, int timeout, StringMap attributes) {
    Auth auth = Auth.create(APP_KEY, APP_SECRET);
    return auth.uploadToken(bucketName, key, timeout, attributes);
  }

  public static byte[] getBucketKey(String bucketName, String key) throws IOException {
    return DownloadUtils.downloadToByteArray(getDownloadUrl(bucketName, key));
  }

  private static String getDownloadUrl(String bucketName, String key) throws IOException {
    String bucketDomain = getBucketDomainByName(bucketName);

    String Url = "http://" + bucketDomain + "/" + key;
    Auth auth = Auth.create(APP_KEY, APP_SECRET);
    String downloadUrl = auth.privateDownloadUrl(Url, DEFAULT_TIMEOUT);

    return downloadUrl;
  }

  private static String getBucketDomainByName(String bucketName) throws IOException {
    String bucketDomain = null;

    if (DEFAULT_BUCKET_NAME.equals(bucketName)) {
      bucketDomain = DEFAULT_BUCKET_DOMAIN;
    } else {
      logger.error("unknown bucketName:{}", bucketName);
      throw new IOException("Unknown bucket");
    }

    return bucketDomain;
  }
}
