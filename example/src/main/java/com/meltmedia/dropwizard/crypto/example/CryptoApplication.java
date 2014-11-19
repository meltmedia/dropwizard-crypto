package com.meltmedia.dropwizard.crypto.example;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.meltmedia.dropwizard.crypto.*;
import com.meltmedia.dropwizard.crypto.example.resources.RootResource;

public class CryptoApplication extends Application<CryptoConfiguration> {
  public static void main(String[] args) throws Exception {
    new CryptoApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<CryptoConfiguration> bootstrap) {
  }

  @Override
  public void run(CryptoConfiguration config, Environment env) throws Exception {
    env.jersey().register(new RootResource());
  }
}
