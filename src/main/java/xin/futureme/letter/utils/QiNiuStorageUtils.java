package xin.futureme.letter.utils;

import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import xin.futureme.letter.config.QiNiuConfig;

import java.io.IOException;

/**
 * Created by rockOps on 2017-01-23.
 */
public class QiNiuStorageUtils {
  private Logger logger = LoggerFactory.getLogger(QiNiuStorageUtils.class);

  private Auth auth = Auth.create(QiNiuConfig.QINIU_APP_KEY, QiNiuConfig.QINIU_APP_SECRET);
  private String bucketName = QiNiuConfig.QINIU_BUCKET_NAME;

  public String getUploadToken() {
    return "";
  }

  public void upload throws IOException {
    try {

    } catch () {
      
    }
  }
}
