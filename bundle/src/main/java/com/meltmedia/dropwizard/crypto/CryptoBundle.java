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
package com.meltmedia.dropwizard.crypto;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meltmedia.jackson.crypto.CryptoModule;
import com.meltmedia.jackson.crypto.Defaults;
import com.meltmedia.jackson.crypto.EncryptedJson;
import com.meltmedia.jackson.crypto.EncryptionService;
import com.meltmedia.jackson.crypto.Functions;
import com.meltmedia.jackson.crypto.Salts;

public class CryptoBundle<T extends Configuration> implements ConfiguredBundle<T> {

  public static final String DEFAULT_ENVIRONMENT_VARIABLE = "DROPWIZARD_PASSPHRASE";
  public static final String CONFIG_SERVICE_NAME = "config";

  public static interface ConfigurationLocator {
    public EncryptionConfiguration locate(Configuration configuration);
  }

  public static interface Mixins {
    public void register(ObjectMapper mapper);
  }

  public static class NullConfigurationLocator implements ConfigurationLocator {
    @Override
    public EncryptionConfiguration locate(Configuration configuration) {
      return null;
    }
  }

  public static class NullMixins implements Mixins {
    @Override
    public void register(ObjectMapper mapper) {
    }
  }

  public static class Builder<T extends Configuration> {
    ConfigurationLocator locator = new NullConfigurationLocator();
    Mixins mixins = new NullMixins();
    String environmentVariable = DEFAULT_ENVIRONMENT_VARIABLE;

    public Builder<T> withConfigurationLocator(ConfigurationLocator locator) {
      this.locator = locator;
      return this;
    }

    public Builder<T> withMixins(Mixins mixins) {
      this.mixins = mixins;
      return this;
    }

    public Builder<T> withEnvironmentVariable(String environmentVariable) {
      this.environmentVariable = environmentVariable;
      return this;
    }

    public CryptoBundle<T> build() {
      return new CryptoBundle<T>(locator, mixins, environmentVariable);
    }
  }
  
  public static <T extends Configuration> Builder<T> builder() {
    return new Builder<T>();
  }

  ConfigurationLocator locator;
  Mixins mixins;
  String environmentVariable;
  EncryptionService<EncryptedJson> defaultService;
  EncryptionService<EncryptedJson> configService;
  CryptoModule module;

  CryptoBundle(ConfigurationLocator locator, Mixins mixins, String environmentVariable) {
    this.locator = locator;
    this.mixins = mixins;
    this.environmentVariable = environmentVariable;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    // create the static portion of the crypto service.
    defaultService =
        EncryptionService.builder().withName(Defaults.DEFAULT_NAME)
            .withObjectMapper(bootstrap.getObjectMapper())
            .withValidator(bootstrap.getValidatorFactory().getValidator())
            .withPassphraseLookup(Functions.passphraseFunction(environmentVariable))
            .withEncryptedJsonSupplier(Functions.encryptedJsonSupplier())
            .withSaltSupplier(Salts.saltSupplier()).build();

    // register the service with the object mapper.
    module = new CryptoModule().withSource(defaultService);
    bootstrap.getObjectMapper().registerModule(module);

    // add any mixins for the configuration file.
    mixins.register(bootstrap.getObjectMapper());
    
    bootstrap.addCommand(new Commands.Encrypt("encrypt", "Encrypt configuration", defaultService));
    bootstrap.addCommand(new Commands.Decrypt("decrypt", "Decrypt configuration", defaultService));
  }

  @Override
  public void run(T configuration, Environment environment) throws Exception {
    if (locator == null) {
      return;
    }
    EncryptionConfiguration dynamicConfiguration = locator.locate(configuration);
    if (dynamicConfiguration == null) {
      return;
    }

    configService =
        EncryptionService
            .builder()
            .withName(CONFIG_SERVICE_NAME)
            .withObjectMapper(environment.getObjectMapper())
            .withValidator(environment.getValidator())
            .withPassphraseLookup(Functions.passphraseFunction(dynamicConfiguration.getKeys()))
            .withEncryptedJsonSupplier(
                Functions.encryptedJsonSupplier(dynamicConfiguration.getCurrentKey()))
            .withSaltSupplier(Salts.saltSupplier(dynamicConfiguration.getSaltLength()))
            .withIterations(dynamicConfiguration.getIterations())
            .withKeyLength(dynamicConfiguration.getKeyLength()).build();

    module.withSource(configService);
  }

}
