package xin.futureme.letter.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Sunkey. Multi-Thread Supported.
 * https://github.com/s614053620/ConfigManager
 * Support：like :{u.getMe()},{u.getMe().me.getU()},{excep:error}
 * 
 */

public class Template implements Serializable {

  private static final long serialVersionUID = 153558874770994255L;

  public static final String USE_PARAM_NAME = "%$PARAM%$";

  private final String expression;
  private final String[] templateStrings;

  private static final LinkedList<ValueResolver> resolvers = new LinkedList<>();
  
  static {
    resolvers.add(new BaseValueResolver());
    resolvers.add(new ReflectValueResolver());
    resolvers.add(new FunctionValueResolver());
    Collections.sort(resolvers, new Comparator<ValueResolver>() {
      public int compare(ValueResolver o1, ValueResolver o2) {
        return o1.getOrder() - o2.getOrder();
      }
    });
  }
  
  public Template(String expression) {
    this.expression = expression;
    List<String> list = Expressions.explainExpression(expression);
    templateStrings = list.toArray(new String[list.size()]);
    if (templateStrings == null || templateStrings.length == 0)
      throw new IllegalArgumentException();
  }

  public String[] getParams() {
    return templateStrings;
  }

  public String render(Map<String, ?> datas) {
    return render(datas, (NullHolder) null);
  }

  protected Object resolveValue(Map<String, ?> datas, String key) {
    Object result = null;
    for(ValueResolver resolver : resolvers){
      result = resolver.resolveValue(datas, key);
      if(result != null){
        return result;
      }
    }
    return result;
  }

  public String render(Map<String, ?> datas, NullHolder holder) {
    if (templateStrings.length == 1) return templateStrings[0];
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < templateStrings.length; i++) {
      if (i % 2 == 0) {
        // is a template string.
        sb.append(templateStrings[i]);
      } else {
        // is a parameter string.
        Object data = resolveValue(datas, templateStrings[i]);
        if (data != null) {
          // if not null append to string.
          sb.append(data);
        } else {
          // else append default (null is "")
          if (holder != null) {
            String result = holder.get(templateStrings[i]);
            if (result != null) sb.append(result);
          } else {
            // there not do anything.
            // sb.append("");
          }
        }
      }
    }
    return sb.toString();
  }

  public String render(Map<String, ?> datas, String def) {
    if (templateStrings.length == 1) return templateStrings[0];
    // cache the $param placeholder index before execute.
    int index = def == null ? -1 : def.indexOf(USE_PARAM_NAME);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < templateStrings.length; i++) {
      if (i % 2 == 0) {
        // is a template string.
        sb.append(templateStrings[i]);
      } else {
        // is a parameter string.
        Object data = resolveValue(datas, templateStrings[i]);
        if (data != null) {
          // if not null append to string.
          sb.append(data);
        } else {
          // else append default (null is "")
          if (def != null) {
            if (index > -1) {
              /**
               * support expression: if null: "*** {paramName} ***".render(data, "${" +
               * Template.USE_PARAM_NAME + "}") display : *** ${paramName} ***
               */
              sb.append(def.substring(0, index)).append(templateStrings[i])
                  .append(def.substring(index + USE_PARAM_NAME.length()));
            } else {
              sb.append(def);
            }
          } else {
            // there not do anything.
            // sb.append("");
          }
        }
      }
    }
    return sb.toString();
  }

  public String getExpression() {
    return expression;
  }

  @Override
  public String toString() {
    return "Template [expression=" + expression + "]";
  }


  /***************** STATIC USE ******************/

  private static final HashMap<String, Template> templates = new HashMap<String, Template>();

  public static final Template forName(String template) {
    Template tpl = templates.get(template);
    if (tpl == null) {
      tpl = new Template(template);
      templates.put(template, tpl);
    }
    return tpl;
  }

  public static String f(String fmt, Map<String, Object> datas) {
    return forName(fmt).render(datas);
  }

  public static String f(String fmt, Object... params) {
    Template tpl = forName(fmt);
    HashMap<String, Object> datas = new HashMap<String, Object>();
    for (int i = 0; i < params.length; i++) {
      datas.put(i + 1 + "", params[i]);
    }
    return tpl.render(datas);
  }

  public static interface NullHolder {
    /**
     * @return null : notAppend "" : append("") "null" : append("null")
     */
    public String get(String key);
  }

  public static class Expressions {

    /**
     * can write-custom it.
     */
    public static final char REPLACE_PREFIX = '\\';
    public static final char SPLIT_EXP_PREFIX = '{';
    public static final char SPLIT_EXP_SUFFIX = '}';

    public static final List<String> explainExpression(String exp) {
      char[] chars = exp.toCharArray();
      List<String> temps = new ArrayList<String>();
      // use StringBuilder to use least memory and fast.
      StringBuilder curStr = new StringBuilder("");
      for (int i = 0; i < chars.length; i++) {
        switch (chars[i]) {
          /**
           * support show '{' and '}': "*** \\{ \\}" = "*** { }"
           */
          case REPLACE_PREFIX:
            if (i < chars.length - 1) {
              if (chars[i + 1] == SPLIT_EXP_PREFIX || chars[i + 1] == SPLIT_EXP_SUFFIX) {
                curStr.append(chars[i + 1]);
                i++;
              } else {
                curStr.append(REPLACE_PREFIX);
              }
            } else {
              curStr.append(REPLACE_PREFIX);
            }
            break;
          case SPLIT_EXP_PREFIX:
          case SPLIT_EXP_SUFFIX:
            temps.add(curStr.toString());
            curStr = new StringBuilder();
            break;
          default:
            curStr.append(chars[i]);
            break;
        }
      }
      temps.add(curStr.toString());
      return temps;
    }
  }

  public static interface ValueResolver {
    Object resolveValue(Map<String, ?> context, String key);
    int getOrder();// MAX will be first
  }

  public static class BaseValueResolver implements ValueResolver {
    public Object resolveValue(Map<String, ?> context, String key) {
      if (context != null) {
        return context.get(key);
      }
      return null;
    }

    public int getOrder() {
      return Integer.MIN_VALUE;
    }
  }

  public static class FunctionValueResolver implements ValueResolver {
    
    private static final Map<String, Function> functions = new HashMap<>();
    
    static {
      addFunction(new PrintExceptionFunction());
    }

    public static void addFunction(Function f){
      functions.put(f.getName(), f);
    }
    
    public Object resolveValue(Map<String, ?> context, String key) {
      if(key == null || context == null){
        return null;
      }
      int index = key.indexOf(':');
      if(index != -1){
        String func = key.substring(0, index);
        String param = key.substring(index + 1);
        Function f = functions.get(func);
        if(f != null){
          return f.explain(param, context);
        }else{
          return null;
        }
      }else{
        return null;
      }
    }

    public int getOrder() {
      return 100;
    }
  }
  
  public static interface Function{
    String getName();
    String explain(String param, Map<String, ?> context);
  }
  
  public static class PrintExceptionFunction implements Function{
    public String getName() {
      return "excep";
    }
    public String explain(String param, Map<String, ?> context) {
      Object t = context.get(param);
      if(t != null && t instanceof Throwable)
        return ExceptionUtils.getStackTrace((Throwable)t);
      else
        return null;
    }
  }

  public static class ReflectValueResolver implements ValueResolver {
    public Object resolveValue(Map<String, ?> context, String key) {
      if (context != null) {
        String[] expSplit = StringUtils.tokenizeToStringArray(key, ".");
        if(expSplit.length == 1){
          return null;
        }
        return getNextValue(context.get(expSplit[0]), expSplit);
      }
      return null;
    }

    protected Object getNextValue(Object target, String[] expSplit){
      for(int i=1;i<expSplit.length;i++){
        String exp = expSplit[i];
        if(target == null){
          return null;
        }
        if(exp == null || exp.trim().isEmpty()){
          return target;
        }
        try {
          target = getObjectValue(target, exp);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return target;
    }

    public int getOrder() {
      return 1000;
    }

    // only support non-param method
    protected static Object getObjectValue(Object target, String expression) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException {
      if (expression.endsWith("()")) {
        // METHOD
        String methodName = expression.substring(0, expression.length() - 2);
        String cacheName = target.getClass().getName() + "." + methodName;
        Method m = methods.get(cacheName);
        if (m == null) {
          m = findMethod(target.getClass(), methodName);
          if (m == null) {
            methods.put(cacheName, (Method) EMPTY_);
            return null;
          } else {
            methods.put(cacheName, m);
            return m.invoke(target);
          }
        } else {
          if (m != EMPTY_) {
            return m.invoke(target);
          } else {
            return null;
          }
        }
      } else {
        // FIELD
        String fieldName = expression;
        String cacheName = target.getClass().getName() + "." + fieldName;
        Field f = fields.get(cacheName);
        if (f == null) {
          f = target.getClass().getField(fieldName);
          fields.put(cacheName, f);
        }
        return f.get(target);
      }

    }

    private static final Object EMPTY_ = new Object();

    private static Method findMethod(Class<?> type, String method, Class<?>... paramTypes) {
      for (Method m : type.getMethods()) {
        if (m.getName().equals(method) && m.getParameterTypes().length == paramTypes.length) {
          for (int i = 0; i < paramTypes.length; i++) {
            if (!m.getParameterTypes()[i].isAssignableFrom(paramTypes[i])) {
              continue;
            }
          }
          return m;
        }
      }
      return null;
    }

    private static final Map<String, Method> methods = new HashMap<>();
    private static final Map<String, Field> fields = new HashMap<>();
  }

}
