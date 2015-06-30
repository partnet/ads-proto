package com.partnet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.Set;

/**
 * Loads configuration properties from application.properties file and makes these available for injection via CDI
 *
 * Properties can be named with class name spaced keys, for example:
 *
 * mypackage.MyClass.property=value
 * or
 * MyClass.property=value
 * or simply:
 * property=value
 * for global properties.
 *
 * The property name can be explicity set if the field name is different than the property name by using the
 * propertyName attribute of the @Config annotation:
 * <code>
 * {@literal @}Config(propertyName = "surname")
 * private String lastName;
 * </code>
 *
 * Otherwise, this factory will look for a field name that matches the property name:
 * <code>
 * {@literal @}Config
 * private String surname;
 * </code>
 *
 * @author jwhite
 */
public class ConfigFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class);

  private volatile static Properties configProperties;
  public static final String propertiesFilePath = "application.properties";

  public synchronized static Properties getProperties() {

    if (configProperties == null) {
      configProperties = new Properties();
      try {
        final InputStream resourceAsStream = ConfigFactory.class.getClassLoader().getResourceAsStream(propertiesFilePath);
        configProperties.load(resourceAsStream);
      } catch (IOException ex) {
        LOG.error(ex.getMessage(), ex);
        throw new RuntimeException(ex);
      }
    }

    return configProperties;
  }

  @Produces
  @Config
  public String getConfiguration(InjectionPoint p) {

    String configKey = getPropertyName(p);
    Properties config = getProperties();
    if (configKey.trim().isEmpty() || config.getProperty(configKey) == null) {
      configKey = p.getMember().getDeclaringClass().getName() + "." + p.getMember().getName();

      if (config.getProperty(configKey) == null) {
        configKey = p.getMember().getDeclaringClass().getSimpleName() + "." + p.getMember().getName();
        if (config.getProperty(configKey) == null)
          configKey = p.getMember().getName();
      }
    }
    LOG.info("Config key: " + configKey + ", value: " + config.getProperty(configKey));

    return config.getProperty(configKey);
  }

  /**
   * Read the property name off of the Config Annotation if it has been set.
   *
   * @param p
   * @return  The property name given on the Config Annotation.  Empty String is expected as default if
   *          it hasn't been set.
   */
  private String getPropertyName(InjectionPoint p) {
    String propertyName = "";

    final Set<Annotation> annotations = p.getQualifiers();
    for (Annotation annotation : annotations) {
      if (annotation instanceof Config) {
        Config config = (Config) annotation;
        propertyName = config.propertyName();
        LOG.debug("propertyName: <" + propertyName + "> found for " + p.getMember().getDeclaringClass().getName() + "." + p.getMember().getName());
      }
    }
    return propertyName;
  }

  @Produces
  @Config
  public Integer getConfigurationInteger(InjectionPoint p) {
    return Integer.parseInt(getConfiguration(p));
  }

  @Produces
  @Config
  public Double getConfigurationDouble(InjectionPoint p) {
    return Double.parseDouble(getConfiguration(p));
  }

  @Produces
  @Config
  public URI getConfigurationURI(InjectionPoint p) throws URISyntaxException {
    return new URI(getConfiguration(p));
  }

  @Produces
  @Config
  public Long getConfigurationLong(InjectionPoint p) {
    return Long.parseLong(getConfiguration(p));
  }

  @Produces
  @Config
  public InternetAddress getConfigurationInternetAddress(InjectionPoint p) throws AddressException {
    return new InternetAddress(getConfiguration(p));
  }

}
