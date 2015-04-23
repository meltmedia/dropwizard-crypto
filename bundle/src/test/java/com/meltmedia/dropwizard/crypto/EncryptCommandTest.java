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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.Callable;

import net.sourceforge.argparse4j.inf.Namespace;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.meltmedia.jackson.crypto.EncryptionService;
import com.meltmedia.jackson.crypto.Functions;

import static com.meltmedia.dropwizard.crypto.Mocks.*;

public class EncryptCommandTest {
  
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
  public void testEncryption() throws Exception {
    mockInput(namespace, "secret: value");
    Callable<String> output = mockOutput(namespace);
    mockPointer(namespace, "/secret");
    
    Commands.Encrypt encryptCommand = 
      new Commands.Encrypt("encrypt", "desc", service);
    
    encryptCommand.run(null, namespace);
    
    JsonNode result = yamlMapper.readValue(output.call(), JsonNode.class);
    assertThat(result.at("/secret/value").isMissingNode(), equalTo(false));
  }
  
  @Test
  public void testEncryptionAtDeepPath() throws Exception {
    mockInput(namespace, "secret: {nested: value}");
    Callable<String> output = mockOutput(namespace);
    mockPointer(namespace, "/secret/nested");
    
    Commands.Encrypt encryptCommand = 
      new Commands.Encrypt("encrypt", "desc", service);
    
    encryptCommand.run(null, namespace);
    
    JsonNode result = yamlMapper.readValue(output.call(), JsonNode.class);
    assertThat(result.at("/secret/nested/value").isMissingNode(), equalTo(false));
  }

}
