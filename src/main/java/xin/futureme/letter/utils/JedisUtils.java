package xin.futureme.letter.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Jedis utility.
 *
 * @author subo
 */
public class JedisUtils {

  private static final Logger logger = LoggerFactory.getLogger(JedisUtils.class);

  private static JedisPool jedisPool = SpringContextHolder.getBean("jedisPool");

  /**
   * Returns all the keys matching the glob-style pattern as space separated strings.
   * 
   * @param pattern
   * @return
   */
  public static Set<String> keys(String pattern) {
    Set<String> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      value = jedis.keys(pattern);
      logger.debug("getSet {} = {}", pattern, value);
    } catch (Exception e) {
      logger.warn("keys {} = {}", pattern, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Delete all the keys of the currently selected DB. This command never fails.
   * 
   * @return
   */
  public static String flushDB() {
    String value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      value = jedis.flushDB();
      logger.debug("flushDB: {}", value);
    } catch (Exception e) {
      logger.warn("flushDB error: {}", e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }


  /**
   * Return the number of keys in the currently selected database.
   * 
   * @return
   */
  public static Long dbSize() {
    Long value = 0L;
    Jedis jedis = null;
    try {
      jedis = getResource();
      value = jedis.dbSize();
      logger.debug("flushDB: {}", value);
    } catch (Exception e) {
      logger.warn("flushDB error: {}", e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Get the value of the specified key. If the key does not exist null is returned.
   * 
   * @param key
   * @return
   */
  public static String get(String key) {
    String value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        value = jedis.get(key);
        value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
        logger.debug("get {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("get {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  public static long setnx(String key, String val){
    Long res = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      res = jedis.setnx(key, val);
    } catch (Exception e) {
      logger.warn("get {} = {}", key, res, e);
    } finally {
      returnResource(jedis);
    }
    return res == null ? 0 : res;
  }
  
  public static String getset(String key, String val){
    String value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      value = jedis.getSet(key, val);
    } catch (Exception e) {
      logger.warn("get {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }
  
  public static long expire(String key, int seconds){
    Long value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      value = jedis.expire(key, seconds);
    } catch (Exception e) {
      logger.warn("get {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value == null ? 0 : value;
  }
  
  /**
   * Get the object value of the specified key. If the key does not exist null is returned.
   * 
   * @param key
   * @return
   */
  public static Object getObject(String key) {
    Object value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        value = toObject(jedis.get(getBytesKey(key)));
        logger.debug("getObject {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getObject {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Set the string value as value of the key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static String set(String key, String value, int cacheSeconds) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.set(key, value);
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("set {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("set {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Set the object value as value of the key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static String setObject(String key, Object value, int cacheSeconds) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.set(getBytesKey(key), toBytes(value));
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setObject {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setObject {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Return the specified elements of the list stored at the specified key.
   * 
   * @param key
   * @return
   */
  public static List<String> getList(String key) {
    List<String> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        value = jedis.lrange(key, 0, -1);
        logger.debug("getList {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getList {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Return the specified elements of the list stored at the specified key.
   * 
   * @param key
   * @return
   */
  public static List<Object> getObjectList(String key) {
    List<Object> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        List<byte[]> list = jedis.lrange(getBytesKey(key), 0, -1);
        value = Lists.newArrayList();
        for (byte[] bs : list) {
          value.add(toObject(bs));
        }
        logger.debug("getObjectList {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getObjectList {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static long setList(String key, List<String> value, int cacheSeconds) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        jedis.del(key);
      }
      result = jedis.lpush(key, (String[]) value.toArray());
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setList {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setList {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the object value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static long setObjectList(String key, List<Object> value, int cacheSeconds) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        jedis.del(key);
      }
      List<byte[]> list = Lists.newArrayList();
      for (Object o : value) {
        list.add(toBytes(o));
      }
      result = jedis.lpush(getBytesKey(key), (byte[][]) list.toArray());
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setObjectList {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setObjectList {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
   * 
   * @param key
   * @param value
   * @return
   */
  public static long listAdd(String key, String... value) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.lpush(key, value);
      logger.debug("listAdd {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("listAdd {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the object value to the head (LPUSH) or tail (RPUSH) of the list stored at key.
   * 
   * @param key
   * @param value
   * @return
   */
  public static long listObjectAdd(String key, Object... value) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      List<byte[]> list = Lists.newArrayList();
      for (Object o : value) {
        list.add(toBytes(o));
      }
      result = jedis.lpush(getBytesKey(key), (byte[][]) list.toArray());
      logger.debug("listObjectAdd {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("listObjectAdd {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Return all the members (elements) of the set value stored at key.
   * 
   * @param key
   * @return
   */
  public static Set<String> getSet(String key) {
    Set<String> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        value = jedis.smembers(key);
        logger.debug("getSet {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getSet {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Return all the members (elements) of the set value stored at key.
   * 
   * @param key
   * @return
   */
  public static Set<Object> getObjectSet(String key) {
    Set<Object> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        value = Sets.newHashSet();
        Set<byte[]> set = jedis.smembers(getBytesKey(key));
        for (byte[] bs : set) {
          value.add(toObject(bs));
        }
        logger.debug("getObjectSet {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getObjectSet {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Add the specified member to the set value stored at key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static long setSet(String key, Set<String> value, int cacheSeconds) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        jedis.del(key);
      }
      result = jedis.sadd(key, (String[]) value.toArray());
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setSet {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setSet {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the specified member to the set value stored at key.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static long setObjectSet(String key, Set<Object> value, int cacheSeconds) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        jedis.del(key);
      }
      Set<byte[]> set = Sets.newHashSet();
      for (Object o : value) {
        set.add(toBytes(o));
      }
      result = jedis.sadd(getBytesKey(key), (byte[][]) set.toArray());
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setObjectSet {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setObjectSet {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the specified member to the set value stored at key.
   * 
   * @param key
   * @param value
   * @return
   */
  public static long setSetAdd(String key, String... value) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.sadd(key, value);
      logger.debug("setSetAdd {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setSetAdd {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Add the specified member to the set value stored at key.
   * 
   * @param key
   * @param value
   * @return
   */
  public static long setSetObjectAdd(String key, Object... value) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      Set<byte[]> set = Sets.newHashSet();
      for (Object o : value) {
        set.add(toBytes(o));
      }
      result = jedis.lpush(getBytesKey(key), (byte[][]) set.toArray());
      logger.debug("setSetObjectAdd {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setSetObjectAdd {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Return all the fields and associated values in a hash.
   * 
   * @param key
   * @return
   */
  public static Map<String, String> getMap(String key) {
    Map<String, String> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        value = jedis.hgetAll(key);
        logger.debug("getMap {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getMap {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Return all the fields and associated values in a hash.
   * 
   * @param key
   * @return
   */
  public static Map<String, Object> getObjectMap(String key) {
    Map<String, Object> value = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        value = Maps.newHashMap();
        Map<byte[], byte[]> map = jedis.hgetAll(getBytesKey(key));
        for (Map.Entry<byte[], byte[]> e : map.entrySet()) {
          value.put(StringUtils.toEncodedString(e.getKey(), null), toObject(e.getValue()));
        }
        logger.debug("getObjectMap {} = {}", key, value);
      }
    } catch (Exception e) {
      logger.warn("getObjectMap {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return value;
  }

  /**
   * Set the respective fields to the respective values.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static String setMap(String key, Map<String, String> value, int cacheSeconds) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        jedis.del(key);
      }
      result = jedis.hmset(key, value);
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setMap {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setMap {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Set the respective fields to the respective values.
   * 
   * @param key
   * @param value
   * @param cacheSeconds expire time in seconds, no expire if 0
   * @return
   */
  public static String setObjectMap(String key, Map<String, Object> value, int cacheSeconds) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        jedis.del(key);
      }
      Map<byte[], byte[]> map = Maps.newHashMap();
      for (Map.Entry<String, Object> e : value.entrySet()) {
        map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
      }
      result = jedis.hmset(getBytesKey(key), map);
      if (cacheSeconds != 0) {
        jedis.expire(key, cacheSeconds);
      }
      logger.debug("setObjectMap {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("setObjectMap {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Set the respective fields to the respective values.
   * 
   * @param key
   * @param value
   * @return
   */
  public static String mapPut(String key, Map<String, String> value) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.hmset(key, value);
      logger.debug("mapPut {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("mapPut {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Set the respective fields to the respective values.
   * 
   * @param key
   * @param value
   * @return
   */
  public static String mapObjectPut(String key, Map<String, Object> value) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      Map<byte[], byte[]> map = Maps.newHashMap();
      for (Map.Entry<String, Object> e : value.entrySet()) {
        map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
      }
      result = jedis.hmset(getBytesKey(key), map);
      logger.debug("mapObjectPut {} = {}", key, value);
    } catch (Exception e) {
      logger.warn("mapObjectPut {} = {}", key, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Remove the specified field from an hash stored at key.
   * 
   * @param key
   * @param mapKey
   * @return
   */
  public static long mapRemove(String key, String mapKey) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.hdel(key, mapKey);
      logger.debug("mapRemove {}  {}", key, mapKey);
    } catch (Exception e) {
      logger.warn("mapRemove {}  {}", key, mapKey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Remove the specified field from an hash stored at key.
   * 
   * @param key
   * @param mapKey
   * @return
   */
  public static long mapObjectRemove(String key, String mapKey) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.hdel(getBytesKey(key), getBytesKey(mapKey));
      logger.debug("mapObjectRemove {}  {}", key, mapKey);
    } catch (Exception e) {
      logger.warn("mapObjectRemove {}  {}", key, mapKey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Test for existence of a specified field in a hash.
   * 
   * @param key
   * @param mapKey
   * @return
   */
  public static boolean mapExists(String key, String mapKey) {
    boolean result = false;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.hexists(key, mapKey);
      logger.debug("mapExists {}  {}", key, mapKey);
    } catch (Exception e) {
      logger.warn("mapExists {}  {}", key, mapKey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Test for existence of a specified field in a hash.
   * 
   * @param key
   * @param mapKey
   * @return
   */
  public static boolean mapObjectExists(String key, String mapKey) {
    boolean result = false;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.hexists(getBytesKey(key), getBytesKey(mapKey));
      logger.debug("mapObjectExists {}  {}", key, mapKey);
    } catch (Exception e) {
      logger.warn("mapObjectExists {}  {}", key, mapKey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Remove the specified key.
   *
   * @param key
   * @return
   */
  public static long del(String key) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(key)) {
        result = jedis.del(key);
        logger.debug("del {}", key);
      } else {
        logger.debug("del {} not exists", key);
      }
    } catch (Exception e) {
      logger.warn("del {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Remove the specified key.
   * 
   * @param key
   * @return
   */
  public static long delObject(String key) {
    long result = 0;
    Jedis jedis = null;
    try {
      jedis = getResource();
      if (jedis.exists(getBytesKey(key))) {
        result = jedis.del(getBytesKey(key));
        logger.debug("delObject {}", key);
      } else {
        logger.debug("delObject {} not exists", key);
      }
    } catch (Exception e) {
      logger.warn("delObject {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Test if the specified key exists.
   * 
   * @param key
   * @return
   */
  public static boolean exists(String key) {
    boolean result = false;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.exists(key);
      logger.debug("exists {}", key);
    } catch (Exception e) {
      logger.warn("exists {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Return the type of the value stored at key in form of a string.
   *
   * @param key
   * @return
   */
  public static String type(String key) {
    String result = null;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.type(getBytesKey(key));
      logger.debug("type {}", key);
    } catch (Exception e) {
      logger.warn("type {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Test if the specified key exists.
   * 
   * @param key
   * @return
   */
  public static boolean existsObject(String key) {
    boolean result = false;
    Jedis jedis = null;
    try {
      jedis = getResource();
      result = jedis.exists(getBytesKey(key));
      logger.debug("existsObject {}", key);
    } catch (Exception e) {
      logger.warn("existsObject {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * Fetch a jedis instance from pool.
   * 
   * @return
   * @throws JedisException
   */
  public static Jedis getResource() throws JedisException {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      // logger.debug("getResource.", jedis);
    } catch (JedisException e) {
      logger.warn("getResource.", e);
      // returnBrokenResource(jedis);
      throw e;
    }
    return jedis;
  }

  /**
   * Returns the jedis instance to pool.
   *
   * @param jedis
   * @param isBroken
   */
  public static void returnBrokenResource(Jedis jedis) {
    if (jedis != null) {
      jedisPool.close();
    }
  }

  /**
   * Returns the jedis instance to pool.
   *
   * @param jedis
   * @param isBroken
   */
  public static void returnResource(Jedis jedis) {
    if (jedis != null) {
      jedis.close();
    }
  }

  /**
   * Transform Object key to Bytes.
   *
   * @param key
   * @return
   */
  public static byte[] getBytesKey(Object object) {
    if (object instanceof String) {
      return ((String) object).getBytes();
    } else {
      return SerializationUtils.serialize((Serializable) object);
    }
  }

  /**
   * Serialize Object to Bytes.
   * 
   * @param object
   * @return
   */
  public static byte[] toBytes(Object object) {
    return SerializationUtils.serialize((Serializable) object);
  }

  /**
   * Serialize bytes to Object.
   * 
   * @param bytes
   * @return
   */
  public static Object toObject(byte[] bytes) {
    return SerializationUtils.deserialize(bytes);
  }

  public static String rpop(String key) {
    String vlaue = null;
    Jedis jedis = null;
    try {
      logger.debug("rpop 1");
      jedis = getResource();
      logger.debug("rpop 2 {}", jedis);
      vlaue = jedis.rpop(key);
      logger.debug("rpop 3 {}", vlaue);
    } catch (Exception e) {
      logger.warn("rpop = {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return vlaue;
  }

  /**
   * 在名称为key的list头添加一个值为value的元素
   * 
   * @param key
   * @param value
   * @return
   */
  public static long lpush(String key, String value) {
    Jedis jedis = null;
    long result = 0;
    try {
      jedis = jedisPool.getResource();
      result = jedis.lpush(key, value);
//      logger.debug("lpush {}", key);
    } catch (Exception e) {
      logger.warn("lpush = {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }


  /**
   * 在名称为key的list尾添加一个值为value的元素
   * 
   * @param key
   * @param value
   * @return
   */
  public static long rpush(String key, Object value) {
    Jedis jedis = null;
    long result = 0;
    try {
      jedis = jedisPool.getResource();
      result = jedis.rpush(getBytesKey(key), toBytes(value));
//      logger.debug("lpush {}", key);
    } catch (Exception e) {
      logger.warn("lpush = {}", key, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * 返回并删除名称为srckey的list的尾元素，并将该元素添加到名称为dstkey的list的头部
   * 
   * @param srckey
   * @param dstkey
   * @return
   */
  public static String rpoplpush(String srckey, String dstkey) {
    Jedis jedis = null;
    String result = "";
    try {
      jedis = jedisPool.getResource();
      result = jedis.rpoplpush(srckey, dstkey);
//      logger.debug("rpoplpush srckey={},dstkey={}", srckey, dstkey);
    } catch (Exception e) {
      logger.warn("rpoplpush srckey={},dstkey={}", srckey, dstkey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }
  
  /**
   * 从列表中弹出一个值，它推到另一个列表并返回它，或阻塞直到有可用
   * @param srckey
   * @param dstkey
   * @return
   */
  public static String brpoplpush(String srckey, String dstkey) {
    Jedis jedis = null;
    String result = "";
    try {
      jedis = jedisPool.getResource();
      result = jedis.brpoplpush(srckey, dstkey,0);
//      logger.debug("brpoplpush srckey={},dstkey={}", srckey, dstkey);
    } catch (Exception e) {
      logger.warn("brpoplpush srckey={},dstkey={}", srckey, dstkey, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  /**
   * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
   * 
   * @param key
   * @param count
   * @param value
   * @return
   */
  public static Long lrem(String key, long count, String value) {
    Jedis jedis = null;
    Long result = 0l;
    try {
      jedis = jedisPool.getResource();
      result = jedis.lrem(key, count, value);
//      logger.debug("lrem key={},count={},value={}", key, count, value);
    } catch (Exception e) {
      logger.warn("lrem key={},count={},value={}", key, count, value, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  public static Long publish(final String channel, final String message) {
    long result = 0L;
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      result = jedis.publish(channel, message);
//      logger.debug("publish {} {}", channel, message);
    } catch (Exception e) {
      logger.warn("publish = {} {}", channel, message, e);
    } finally {
      returnResource(jedis);
    }
    return result;
  }

  public static void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
    Jedis jedis = null;
    try {
      jedis = jedisPool.getResource();
      jedis.psubscribe(jedisPubSub, patterns);
//      logger.debug("psubscribe {}", Objects.toString(patterns));
    } catch (Exception e) {
      logger.warn("psubscribe = {}", Objects.toString(patterns));
    } finally {
      returnResource(jedis);
    }
  }
}
