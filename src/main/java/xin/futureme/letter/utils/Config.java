package xin.futureme.letter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static xin.futureme.letter.utils.Template.f;

/**
 * 
 * @author Sunkey
 * @comment Simple Config Manager.
 * @date 2016/06/29
 * https://github.com/s614053620/ConfigManager
 *
 */
public final class Config {

	public static final String PREFIX_CLASSPATH = "classpath:";
	public static final String PREFIX_CLASSPATH_SHORT = "cp:";
	public static final String PROPERTIES_FORMAT = ".properties";
	
	private static final Logger loger = LoggerFactory.getLogger(Config.class);
	private static final Map<String, Config> configs = new HashMap<>();
	
	
	/**
	 * Cached Instances.
	 */
	public static final Config forName(String path){
		Config cfg = configs.get(path);
		if(cfg != null){
			return cfg;
		}
		String realPath = explainName(path);
		cfg = configs.get(realPath);
		if(cfg != null){
			configs.put(path, cfg);
			return cfg;
		}
		try {
			cfg = new Config(realPath);
			configs.put(realPath, cfg);
			configs.put(path, cfg);
			return cfg;
		} catch (IOException e) {
			loger.error("Resource {} not found!", path);
		}
		return null;
	}
	
	private final String path;
	
	private final Map<String, String> props = new HashMap<>();
	
	private Config(String path) throws IOException{
		this.path = path;
		initConfig();
	}
	
	private void initConfig() throws IOException{
		if(this.path == null || this.path.trim().isEmpty()){
			throw new IllegalArgumentException(f("for path {1}", String.valueOf(this.path)));
		}
		if(!this.path.endsWith(PROPERTIES_FORMAT)){
			throw new IllegalArgumentException(f("file name should endsWith '{1}' but {2}", PROPERTIES_FORMAT, String.valueOf(this.path)));
		}
		InputStream input = null;
		if(this.path.startsWith(PREFIX_CLASSPATH)){
			input = loadResourceFromClasspath(this.path.substring(PREFIX_CLASSPATH.length()));
		}else{
			input = loadResource(this.path);
		}
		if(input == null){
			throw new FileNotFoundException(this.path);
		}
		Properties prop = new Properties();
		prop.load(input);
		this.props.putAll(copyToMap(prop));
		closeQuietly(input);
	}
	
	/** Config Methods */
	
	public final String getString(String key) throws NullPointerException {
		String res = props.get(key);
		if(res == null)
			throw new NullPointerException(key);
		return res;
	}
	
	public final String getTemplateString(String key, Object ... params){
	  String tpl = getString(key);
	  return Template.f(tpl, params);
	}

	public final Template getTemplate(String key){
	  String tpl = getString(key);
	  return Template.forName(tpl);
	}

	public final String getString(String key, String def){
		String res = props.get(key);
		if(res == null){
			return def;
		}
		return res;
	}
	
	public final TimeParser.Range getTimeRange(String key, String def){
	  String exp = getString(key, def);
	  return TimeParser.range(exp);
	}

	public final TimeParser.Range getTimeRange(String key){
	  return TimeParser.range(getString(key));
	}

	public final Integer getInteger(String key){
		return Integer.valueOf(getString(key));
	}
	
	public final Integer getInteger(String key, Integer def){
		try{
			return getInteger(key);
		}catch (Exception e) {
			return def;
		}
	}
	
	public final BigDecimal getBigDecimal(String key){
		return new BigDecimal(getString(key));
	}
	
	public final BigDecimal getBigDecimal(String key, BigDecimal def){
		try{
			return getBigDecimal(key);
		}catch (Exception e) {
			return def;
		}
	}
	
	public final boolean getBoolean(String key){
		return Boolean.valueOf(getString(key));
	}
	
	public final boolean getBoolean(String key, boolean def){
		try{
			return getBoolean(key);
		}catch (Exception e) {
			return def;
		}
	}
	
	/** PUBLIC METHODS */
	
	public static final void closeQuietly(Closeable close){
		try {
			close.close();
		} catch (IOException e) {
			//ignore.
		}
	}
	
	public static final Map<String, String> copyToMap(Properties prop){
		Map<String, String> map = new HashMap<>();
		for(String key : prop.stringPropertyNames()){
			map.put(key, prop.getProperty(key));
		}
		return map;
	}
	
	public static final InputStream loadResource(String path) throws FileNotFoundException{
		return new FileInputStream(path);
	}
	
	public static final InputStream loadResourceFromClasspath(String classpath){
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath);
	}
	
	private static final String explainName(String path){
		if(path == null || path.trim().isEmpty()){
			return path;
		}
		if(path.startsWith(PREFIX_CLASSPATH_SHORT)){
			path = PREFIX_CLASSPATH + path.substring(PREFIX_CLASSPATH_SHORT.length());
		}
		if(!path.endsWith(PROPERTIES_FORMAT)){
			path = path + PROPERTIES_FORMAT;
		}
		return path;
	}

  @Override
  public String toString() {
    return "Config [path=" + path + ", props=" + props + "]";
  }
	
}
