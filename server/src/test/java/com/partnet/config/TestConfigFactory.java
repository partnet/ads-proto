package com.partnet.config;

import com.partnet.es.ElasticSearchClient;
import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * @author jwhite
 */
public class TestConfigFactory {

  @Test
  public void testGetConfiguration() {
    final ConfigFactory configFactory = new ConfigFactory();
    final InjectionPoint injectionPoint = new MockInjectionPoint();
    final String configuration = configFactory.getConfiguration(injectionPoint);
    Assert.assertEquals("safetyreports", configuration);
  }

  private static class MockInjectionPoint implements InjectionPoint {

    @Override
    public Type getType() {
      return null;
    }

    @Override
    public Set<Annotation> getQualifiers() {
      return Collections.emptySet();
    }

    @Override
    public Bean<?> getBean() {
      return null;
    }

    @Override
    public Member getMember()
    {
      try {
        return ElasticSearchClient.class.getDeclaredField("indexName");
      } catch (NoSuchFieldException e) {
        return null;
      }
    }

    @Override
    public Annotated getAnnotated() {
      return null;
    }

    @Override
    public boolean isDelegate() {
      return false;
    }

    @Override
    public boolean isTransient() {
      return false;
    }
  }


}
