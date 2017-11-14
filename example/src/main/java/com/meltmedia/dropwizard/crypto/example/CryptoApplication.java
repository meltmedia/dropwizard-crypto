/**
 * Copyright (C) 2014 meltmedia (christian.trimble@meltmedia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meltmedia.dropwizard.crypto.example;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.meltmedia.dropwizard.crypto.*;
import com.meltmedia.dropwizard.crypto.example.resources.RootResource;
import com.meltmedia.jackson.crypto.Encrypted;

public class CryptoApplication extends Application<CryptoConfiguration> {
  public static void main(String[] args) throws Exception {
    new CryptoApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<CryptoConfiguration> bootstrap) {
    bootstrap.addBundle(CryptoBundle.builder().withMixins(mapper -> {
      mapper.addMixInAnnotations(CryptoConfiguration.class, EncryptSecretMixin.class);
    })
      .withKeyLength(128)
      .build());
  }

  @Override
  public void run(CryptoConfiguration config, Environment env) throws Exception {
    env.jersey().register(new RootResource(config));
  }

  public static interface EncryptSecretMixin {
    @Encrypted
    public String getSecret();
  }
}
