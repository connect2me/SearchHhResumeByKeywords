package ru.connect2me.util.hh.search.config;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Инициализация логирования, включения Saxon xslt-парсера  
 *
 * @author Зайнуллин Радик
 * @version 1.0
 * @since 2012.11.18
 */
public abstract class Module {
  protected static ClassLoader classLoader;
  protected static final Logger logger = LoggerFactory.getLogger(Module.class);
  protected static Properties props; // свойства из config.xml

  public Module(Configuration config) {
    init(config);
  }

  private void init(Configuration config) {
    classLoader = Thread.currentThread().getContextClassLoader();
    props = config.getProperties();
  }

  public Properties getProperties() { return props; }
}