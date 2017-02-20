package xin.futureme.letter.utils;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 
 * @author Sunkey
 *
 */
public class HttpUtils {

  private static final CloseableHttpClient httpClient = HttpClients.custom().build();
  public static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final Charset DEFAULT_CHARSET = UTF_8;
  private static final Logger loger = LoggerFactory.getLogger(HttpUtils.class);

  public static String IF_NULL = null;

  public static String get(String url) throws IOException {
    loger.debug("HTTP GET:{}", url);
    HttpGet get = new HttpGet(url);
    CloseableHttpResponse res = httpClient.execute(get);
    return EntityUtils.toString(res.getEntity(), DEFAULT_CHARSET);
  }

  public static String get(String url, Map<String, ?> params) throws IOException {
    String urlWithGetParams = generateGetParams(url, params, IF_NULL);
    return get(urlWithGetParams);
  }

  public static String post(String url, Map<String, ?> params, String body) throws IOException {
    boolean useParam = params != null;
    boolean useBody = body != null;
    boolean useAll = useBody && useParam;
    if (useAll) {
      String urlWithGetParams = generateGetParams(url, params, IF_NULL);
      return postBody(urlWithGetParams, body);
    }
    if (useParam) {
      return post(url, params);
    }
    if (useBody) {
      return postBody(url, body);
    }
    return post(url, params);
  }

  public static String postBody(String url, String body) throws IOException {
    loger.debug("HTTP POST:{}:{}", url, body);
    HttpPost post = new HttpPost(url);
    StringEntity entity = new StringEntity(body, DEFAULT_CHARSET);
    post.setEntity(entity);
    CloseableHttpResponse result = httpClient.execute(post);
    String res = EntityUtils.toString(result.getEntity(), DEFAULT_CHARSET);
    loger.debug("HttpRes:{}", res);
    return res;
  }

  public static String generateGetParams(String url, Map<String, ?> params, String ifNull) {
    StringBuilder sb = null;
    if (url != null)
      sb = new StringBuilder(url);
    else
      sb = new StringBuilder();
    if (params == null || params.isEmpty()) return sb.toString();
    boolean start = url.indexOf('?') > 0;
    for (Map.Entry<String, ?> entry : params.entrySet()) {
      String key = entry.getKey();
      Object val = entry.getValue();
      if (key == null) continue;
      String value = null;
      if (val == null) {
        if (ifNull == null) continue;
        value = ifNull;
      } else {
        value = String.valueOf(val);
      }
      if (!start) {
        sb.append('?');
        start = true;
      } else {
        sb.append('&');
      }
      sb.append(key).append('=').append(value);
    }
    return sb.toString();
  }

  public static class FilePart {
    private InputStream inputStream;
    private String name;

    public FilePart() {}

    public FilePart(String name, InputStream inputStream) {
      this.name = name;
      this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
      return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return "FilePart[" + name + "]";
    }
  }

  private static final ContentType DEF_ = ContentType.create("form-data", DEFAULT_CHARSET);

  public static String post(String url, Map<String, ?> params, List<FilePart> files)
      throws ClientProtocolException, IOException {
    loger.debug("HttpPostParams:{}:{}", url, params);
    HttpPost post = new HttpPost(url);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setCharset(DEFAULT_CHARSET);
    for (Map.Entry<String, ?> param : params.entrySet()) {
      if (param.getValue() != null) {
        builder.addPart(param.getKey(),
            new StringBody(URLEncoder.encode(param.getValue().toString(), "UTF-8"), DEF_));
      }
    }
    if (files != null) 
      for (FilePart fp : files) {
        builder.addPart(fp.getName(), new InputStreamBody(fp.getInputStream(), fp.getName()));
    }
    post.setEntity(builder.build());
    CloseableHttpResponse result = httpClient.execute(post);
    String res = EntityUtils.toString(result.getEntity(), DEFAULT_CHARSET);
    loger.debug("HttpPostParamsResult:{}", res);
    return res;
  }

  public static String post(String url, Map<String, ?> params) throws ParseException, IOException {
    loger.debug("HttpPostParams:{}:{}", url, params);
    HttpPost post = new HttpPost(url);
    List<NameValuePair> nvps = new ArrayList<>();
    if (params != null && !params.isEmpty()) {
      for (Map.Entry<String, ?> entry : params.entrySet()) {
        if (entry.getValue() != null) {
          nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
      }
    }
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, UTF_8);
    post.setEntity(entity);
    CloseableHttpResponse result = httpClient.execute(post);
    String res = EntityUtils.toString(result.getEntity(), DEFAULT_CHARSET);
    loger.debug("HttpPostParamsResult:{}", res);
    return res;
  }

  public static String readStreamAsString(HttpServletRequest req) {
    byte[] data = readStream(req);
    return new String(data, DEFAULT_CHARSET);
  }

  public static byte[] readStream(HttpServletRequest req) {
    try {
      InputStream in = req.getInputStream();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buf = new byte[256];
      int pos = -1;
      while ((pos = in.read(buf)) != -1) {
        baos.write(buf, 0, pos);
      }
      return baos.toByteArray();
    } catch (Throwable e) {
      loger.error(e.getMessage(), e);
    }
    return null;
  }

  public static Map<String, String> getParamMap(HttpServletRequest req) {
    Map<String, String> params = new HashMap<>();
    Enumeration<String> e = req.getParameterNames();
    while (e.hasMoreElements()) {
      String key = e.nextElement();
      params.put(key, req.getParameter(key));
    }
    return params;
  }

}
