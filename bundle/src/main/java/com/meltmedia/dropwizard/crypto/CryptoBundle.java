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
import com.meltmedia.jackson.crypto.EncryptionService;
import com.meltmedia.jackson.crypto.Functions;
import com.meltmedia.jackson.crypto.Salts;

public class CryptoBundle<T extends Configuration> implements ConfiguredBundle<T> {

  public static final String DEFAULT_ENVIRONMENT_VARIABLE = "DROPWIZARD_PASSPHRASE";
  public static final String CONFIG_SERVICE_NAME = "config";

  public static interface ConfigurationLocator<T extends Configuration> {
    public EncryptionConfiguration locate(T configuration);
  }

  public static interface Mixins {
    public void register(ObjectMapper mapper);
  }

  public static class NullConfigurationLocator<T extends Configuration> implements ConfigurationLocator<T> {
    @Override
    public EncryptionConfiguration locate(T configuration) {
      return null;
    }
  }

  public static class NullMixins implements Mixins {
    @Override
    public void register(ObjectMapper mapper) {
    }
  }

  public static class Builder<T extends Configuration> {
    ConfigurationLocator<T> locator = new NullConfigurationLocator<T>();
    Mixins mixins = new NullMixins();
    String environmentVariable = DEFAULT_ENVIRONMENT_VARIABLE;
    int keyStreachIterations = Defaults.KEY_STRETCH_ITERATIONS;
    int keyLength = Defaults.KEY_LENGTH;
    int saltLength = Defaults.SALT_LENGTH;
    
    public Builder<T> withConfigurationLocator(ConfigurationLocator<T> locator) {
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
    
    public Builder<T> withKeyStreachIterations( int keyStreachIterations ) {
      this.keyStreachIterations = keyStreachIterations;
      return this;
    }
    
    public Builder<T> withKeyLength( int keyLength ) {
      this.keyLength = keyLength;
      return this;
    }
    
    public Builder<T> withSaltLength( int saltLength ) {
      this.saltLength = saltLength;
      return this;
    }

    public CryptoBundle<T> build() {
      return new CryptoBundle<T>(locator, mixins, environmentVariable, keyStreachIterations, keyLength, saltLength);
    }
  }
  
  public static <T extends Configuration> Builder<T> builder() {
    return new Builder<T>();
  }

  ConfigurationLocator<T> locator;
  Mixins mixins;
  String environmentVariable;
  int keyStreachIterations;
  int keyLength;
  int saltLength;
  EncryptionService defaultService;
  EncryptionService configService;
  CryptoModule module;

  CryptoBundle(ConfigurationLocator<T> locator, Mixins mixins, String environmentVariable, int keyStreachIterations, int keyLength, int saltLength) {
    this.locator = locator;
    this.mixins = mixins;
    this.environmentVariable = environmentVariable;
    this.keyStreachIterations = keyStreachIterations;
    this.keyLength = keyLength;
    this.saltLength = saltLength;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    // create the static portion of the crypto service.
    defaultService =
        EncryptionService.builder().withName(Defaults.DEFAULT_NAME)
            .withObjectMapper(bootstrap.getObjectMapper())
            .withValidator(bootstrap.getValidatorFactory().getValidator())
            .withPassphraseLookup(Functions.passphraseFunction(environmentVariable))
            .withIterations(keyStreachIterations)
            .withKeyLength(keyLength)
            .withSaltSupplier(Salts.saltSupplier(saltLength))
            .build();

    // register the service with the object mapper.
    module = new CryptoModule().addSource(defaultService);
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
            .withCurrentKeyName(dynamicConfiguration.getCurrentKey())
            .withSaltSupplier(Salts.saltSupplier(dynamicConfiguration.getSaltLength()))
            .withIterations(dynamicConfiguration.getIterations())
            .withKeyLength(dynamicConfiguration.getKeyLength()).build();

    module.addSource(configService);
  }
  
  public EncryptionService getServiceFromEnvironment() {
    return defaultService;
  }
  
  public EncryptionService getServiceFromConfiguration() {
    return configService;
  }

}
