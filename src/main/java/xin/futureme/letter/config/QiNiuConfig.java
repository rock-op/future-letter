package xin.futureme.letter.config;

import xin.futureme.letter.utils.Config;

/**
 * Created by rockOps on 2017-01-23.
 */
public class QiNiuConfig {
  private static final Config CFG_QINIU = Config.forName("cp:qiniu");

  public static final String QINIU_APP_KEY = CFG_QINIU.getString("qiniu.storage.access.key");
  public static final String QINIU_APP_SECRET = CFG_QINIU.getString("qiniu.storage.access.secret");
  public static final String QINIU_BUCKET_NAME = CFG_QINIU.getString("qiniu.storage.letter.bucket.name");
  public static final String QINIU_BUCKET_DOMAIN = CFG_QINIU.getString("qiniu.storage.letter.bucket.domain");
}
