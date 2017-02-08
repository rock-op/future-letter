package xin.futureme.letter.common;

import xin.futureme.letter.utils.Config;

/**
 * Created by rockops on 2017-02-06.
 */
public class StorageConfig {
  private final static Config CFG_OSS = Config.forName("cp:oss");


  public static final String ACCESS_ID = CFG_OSS.getString("access_id");
  public static final String ACCESS_KEY = CFG_OSS.getString("access_key");
  public static final String DEFAULT_BUCKET_NAME = CFG_OSS.getString("letter_bucket_name");
  public static final String INTERNAL_ENDPOINT = CFG_OSS.getString("endpoint");
}
