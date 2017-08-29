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

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonPointerEditor {
  JsonNode root;
  private JsonPointer pointer;
  
  public JsonPointerEditor( JsonNode root, JsonPointer pointer ) {
    this.root = root;
    this.pointer = pointer;
  }
  
  public JsonNode getRoot() {
    return root;
  }
  
  public JsonNode getValue() {
    return root.at(pointer);
  }
  
  public JsonNode setValue( JsonNode value ) {
    JsonPointer currentPointer = pointer;
    JsonNode currentNode = root;
    JsonNode parentNode = null;
    String matchingProperty = null;
    int matchingIndex = -1;
    
    while( !"".equals(currentPointer.getMatchingProperty()) ) {
      parentNode = currentNode;
      matchingProperty = currentPointer.getMatchingProperty();
      matchingIndex = currentPointer.getMatchingIndex();
      if( currentNode instanceof ArrayNode ) {
        currentNode = currentNode.path(matchingIndex);
      } else {
        currentNode = currentNode.path(matchingProperty);
      }
      currentPointer = currentPointer.tail();
    }
    
    if( parentNode == null ) {
      root = value;
    }
    else if( parentNode instanceof ObjectNode ) {
      ((ObjectNode)parentNode).set(matchingProperty, value);
    }
    else if( parentNode instanceof ArrayNode ) {
      ((ArrayNode) parentNode).set(matchingIndex, value);
    }
    
    return value;
  }
}