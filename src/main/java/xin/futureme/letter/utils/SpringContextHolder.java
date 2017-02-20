package xin.futureme.letter.utils;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Spring ApplicationContext holder.
 *
 * @author subo
 */
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
  private static ApplicationContext sAppContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    sAppContext = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    checkApplicationContext();
    return sAppContext;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    checkApplicationContext();
    return (T) sAppContext.getBean(name);
  }

  public static <T> T getBean(Class<T> clazz) {
    checkApplicationContext();
    return sAppContext.getBean(clazz);
  }

  public static void cleanApplicationContext() {
    sAppContext = null;
  }

  private static void checkApplicationContext() {
    if (sAppContext == null) {
      throw new IllegalStateException("no SpringContextHolder found in applicationContext.xml");
    }
  }

  @Override
  public void destroy() throws Exception {
    cleanApplicationContext();
  }
}
