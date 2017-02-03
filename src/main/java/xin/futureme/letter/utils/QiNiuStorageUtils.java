package xin.futureme.letter.utils;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xin.futureme.letter.common.QiNiuConfig;

import java.io.IOException;

/**
 * Created by rockOps on 2017-01-23.
 * 1. upload: 上传到七牛的bucket中，支持指定文件名上传文件名，支持直接写字节流
 * 2. getBucketKeyContent: 获取七牛中key的内容
 * 注意: 同一个文件名，不允许被覆盖写入。内容不一样的话，上传多次会报错，抛exception。
 * todo, 需要增加在七牛上的加密解密逻辑，对文件内容进行加密
 */
public class QiNiuStorageUtils {
  private final static Logger logger = LoggerFactory.getLogger(QiNiuStorageUtils.class);

  private final static String APP_KEY = QiNiuConfig.QINIU_APP_KEY;
  private static final String APP_SECRET = QiNiuConfig.QINIU_APP_SECRET;
  private static final String DEFAULT_BUCKET_NAME = QiNiuConfig.QINIU_BUCKET_NAME;
  private final static String DEFAULT_BUCKET_DOMAIN = QiNiuConfig.QINIU_BUCKET_DOMAIN;
  private final static int DEFAULT_TIMEOUT = 3600;

  // key就是bucket中的文件名
  public static void upload2DefaultBucket(String fileName, String key) throws IOException {
    upload(fileName, DEFAULT_BUCKET_NAME, key);
  }

  public static void upload(String fileName, String bucketName, String key) throws IOException {
    UploadManager uploadManager = getZone1UploadManager();
    String uploadToken = getUploadToken(bucketName, key);

    Response response = uploadManager.put(fileName, key, uploadToken);
    logger.info(response.bodyString());
  }

  private static UploadManager getZone1UploadManager() {
    Zone zone = Zone.zone1();
    Configuration c = new Configuration(zone);
    return new UploadManager(c);
  }

  public static void upload2DefaultBucket(byte[] data, final String key) throws IOException {
    upload(data, DEFAULT_BUCKET_NAME, key);
  }

  public static void upload(byte[] data, String bucketName, final String key) throws IOException {
    StringMap params = new StringMap();
    UploadManager uploadManager = getZone1UploadManager();
    String uploadToken = getUploadToken(bucketName, key);

    Response response = uploadManager.put(data, key, uploadToken, params, null, true);
    logger.info(response.bodyString());
  }

  private static String getUploadToken(String bucketName, String key) {
    StringMap attributes = new StringMap();
    // 在这里控制bucket:key的内容是否允许被覆盖
    attributes.put("insertOnly", 1);
    int timeout = DEFAULT_TIMEOUT;
    return getUploadToken(bucketName, key, timeout, attributes);
  }

  private static String getUploadToken(String bucketName, String key, int timeout, StringMap attributes) {
    Auth auth = Auth.create(APP_KEY, APP_SECRET);
    return auth.uploadToken(bucketName, key, timeout, attributes);
  }

  public static String getDefaultBucketKeyContent(final String key) throws IOException {
    return getBucketKeyContent(DEFAULT_BUCKET_NAME, key);
  }

  public static String getBucketKeyContent(String bucketName, final String key) throws IOException {
    byte[] content = DownloadUtils.downloadToByteArray(getDownloadUrl(bucketName, key));
    return new String(content);
  }

  private static String getDownloadUrl(String bucketName, final String key) throws IOException {
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
