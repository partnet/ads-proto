package com.partnet.config;

import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
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

//    Assert.assertEquals("foo", configuration);
  }

  private static class MockInjectionPoint implements InjectionPoint {

    private Member member = new MockMember();

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
    public Member getMember() {
      return member;
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

  private static class MockMember implements Member {

    @Override
    public Class<?> getDeclaringClass() {
      return SomeClass.class;
    }

    @Override
    public String getName() {
      return "someMethod";
    }

    @Override
    public int getModifiers() {
      return 0;
    }

    @Override
    public boolean isSynthetic() {
      return false;
    }
  }

  private static class SomeClass {

  }

}
