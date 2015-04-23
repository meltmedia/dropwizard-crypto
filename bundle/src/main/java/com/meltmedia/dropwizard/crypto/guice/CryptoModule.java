package com.meltmedia.dropwizard.crypto.guice;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.meltmedia.dropwizard.crypto.CryptoBundle;
import com.meltmedia.jackson.crypto.EncryptionService;

public class CryptoModule extends AbstractModule {
  
  public static final String ENVIRONMENT_SERVICE = "environment";
  public static final String CONFIGURATION_SERVICE = "configuration";
  
  CryptoBundle<?> bundle;

  public CryptoModule( CryptoBundle<?> bundle ) {
    this.bundle = bundle;
  }

  @Override
  protected void configure() {
    // all bindings in provider methods.
  }

  @Provides
  @Singleton
  @Named(ENVIRONMENT_SERVICE)
  public EncryptionService provideServiceFromEnvironment() {
    return bundle.getServiceFromEnvironment();
  }
  
  @Provides
  @Singleton
  @Named(CONFIGURATION_SERVICE)
  public EncryptionService provideServiceFromConfiguration() {
    return bundle.getServiceFromConfiguration();
  }
}
