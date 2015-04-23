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

import static com.meltmedia.dropwizard.crypto.Mocks.mockInput;
import static com.meltmedia.dropwizard.crypto.Mocks.mockOutput;
import static com.meltmedia.dropwizard.crypto.Mocks.mockPointer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Callable;

import net.sourceforge.argparse4j.inf.Namespace;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.meltmedia.jackson.crypto.EncryptionService;
import com.meltmedia.jackson.crypto.Functions;

public class DecryptCommandTest {
  
  Namespace namespace;
  EncryptionService service;
  ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  
  @Before
  public void setUp() {
    namespace = mock(Namespace.class);

    service = EncryptionService.builder()
      .withPassphraseLookup(Functions.constPassphraseFunction("correct horse battery staple"))
      .withObjectMapper(new ObjectMapper())
      .build();
  }
  
  @Test
  public void testDecryption() throws Exception {
    mockInput(namespace,
      "secret:\n"+
      "  salt: \"02n2NA==\"\n"+
      "  iv: \"WsiLMqSgKqTiURkqbuOnZw==\"\n"+
      "  value: \"1ww7k45AN+XvZHaEPrlztA==\"\n"+
      "  cipher: \"aes-256-cbc\"\n"+
      "  keyDerivation: \"pbkdf2\"\n"+
      "  keyLength: 256\n"+
      "  iterations: 2000\n"+
      "  encrypted: true\n");

    Callable<String> output = mockOutput(namespace);
    mockPointer(namespace, "/secret");
    
    Commands.Decrypt command = 
      new Commands.Decrypt("decrypt", "desc", service);
    
    command.run(null, namespace);

    JsonNode result = yamlMapper.readValue(output.call(), JsonNode.class);
    assertThat(result.at("/secret").asText(), equalTo("value"));
  }

  @Test
  public void testDecryptionWithOtherKeys() throws Exception {
    mockInput(namespace,
      "before: before value\n"+
      "secret:\n"+
      "  salt: \"02n2NA==\"\n"+
      "  iv: \"WsiLMqSgKqTiURkqbuOnZw==\"\n"+
      "  value: \"1ww7k45AN+XvZHaEPrlztA==\"\n"+
      "  cipher: \"aes-256-cbc\"\n"+
      "  keyDerivation: \"pbkdf2\"\n"+
      "  keyLength: 256\n"+
      "  iterations: 2000\n"+
      "  encrypted: true\n"+
      "after: after value\n");

    Callable<String> output = mockOutput(namespace);
    mockPointer(namespace, "/secret");
    
    Commands.Decrypt command = 
      new Commands.Decrypt("decrypt", "desc", service);
    
    command.run(null, namespace);

    JsonNode result = yamlMapper.readValue(output.call(), JsonNode.class);
    assertThat(result.at("/before").asText(), equalTo("before value"));
    assertThat(result.at("/secret").asText(), equalTo("value"));
    assertThat(result.at("/after").asText(), equalTo("after value"));
    
  }}
