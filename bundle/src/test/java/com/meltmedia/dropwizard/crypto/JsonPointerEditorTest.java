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

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonPointerEditorTest {

  private ObjectMapper mapper;
  JsonNodeFactory factory;
  
  @Before
  public void setUp() {
    mapper = new ObjectMapper();
    factory = JsonNodeFactory.instance;
  }
  
  @Test
  public void shouldGetValue() throws Exception {
    JsonNode node = mapper.readValue("{\"key\": {\"sub\": \"value\"}}", JsonNode.class);
    JsonPointer pointer = JsonPointer.compile("/key/sub");
    
    JsonPointerEditor editor = new JsonPointerEditor(node, pointer);
    assertThat("value", equalTo(editor.getValue().asText()));
  }
  
  @Test
  public void shouldSetValue() throws Exception {
    JsonNode node = mapper.readValue("{\"key\": {\"sub\": \"value\"}}", JsonNode.class);
    JsonPointer pointer = JsonPointer.compile("/key/sub");
    
    JsonPointerEditor editor = new JsonPointerEditor(node, pointer);
    editor.setValue(JsonNodeFactory.instance.textNode("updated"));
    assertThat("updated", equalTo(editor.getValue().asText()));
  }
  
  @Test
  public void shouldUpdateArrayValue() throws Exception {
    JsonNode node = mapper.readValue("{\"key\": {\"sub\": [\"value1\", \"value2\", \"value3\"]}}", JsonNode.class);
    JsonPointer pointer = JsonPointer.compile("/key/sub/2");
    
    JsonPointerEditor editor = new JsonPointerEditor(node, pointer);
    editor.setValue(JsonNodeFactory.instance.textNode("updated"));
    assertThat("updated", equalTo(editor.getValue().asText()));    
  }
  
  @Test
  public void shouldAllowNodeTypeChange() throws Exception {
    JsonNode node = mapper.readValue("{\"key\": {\"sub\": \"value\"}}", JsonNode.class);
    JsonPointer pointer = JsonPointer.compile("/key/sub");
    
    JsonPointerEditor editor = new JsonPointerEditor(node, pointer);
    editor.setValue(factory.objectNode().set("subsub", factory.textNode("nested")));
    assertThat("nested", equalTo(node.at("/key/sub/subsub").asText()));
  }
  
  @Test
  public void shouldAllowRootNode() throws Exception {
    JsonNode node = mapper.readValue("{\"key\": {\"sub\": \"value\"}}", JsonNode.class);
    JsonPointer pointer = JsonPointer.compile("/").tail();
    JsonNode root = node.at(pointer);
    
    JsonPointerEditor editor = new JsonPointerEditor(node, pointer);
    editor.setValue(factory.objectNode().set("value", factory.textNode("text")));
    assertThat(editor.getRoot().at("/value").asText(), equalTo("text"));
    
    assertThat(editor.getRoot(), sameInstance(editor.getValue()));
  }

  
}
