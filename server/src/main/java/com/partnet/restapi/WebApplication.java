package com.partnet.restapi;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest-api")
public class WebApplication extends Application {

  private Set<Class<?>> classes = new HashSet<>();

  public WebApplication() throws IOException {
    classes.add(FAERSResource.class);
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
