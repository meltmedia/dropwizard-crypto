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

import io.dropwizard.cli.Command;
import io.dropwizard.setup.Bootstrap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.meltmedia.jackson.crypto.EncryptedJson;
import com.meltmedia.jackson.crypto.EncryptionService;

public class Commands {
  public static final String INFILE = "infile";
  public static final String OUTFILE = "outfile";
  public static final String POINTER = "pointer";
  public static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());
  
  static abstract class AbstractCryptoCommand extends Command {
    EncryptionService service;
    
    protected AbstractCryptoCommand(String name, String description, EncryptionService service) {
      super(name, description);
      this.service = service;
    }
    
    @Override
    public void configure(Subparser subparser) {
      subparser.addArgument("-p", "--pointer")
        .required(false)
        .dest(POINTER)
        .type(new ArgumentType<JsonPointer>() {
          @Override
          public JsonPointer convert(ArgumentParser parser, Argument argument, String value)
              throws ArgumentParserException {
            if( StringUtils.isEmpty(value) ) {
              return JsonPointer.compile("/").tail();
            }
            return JsonPointer.compile(value);
          }
        }).setDefault(JsonPointer.compile("/").tail());
      subparser.addArgument(INFILE).nargs("?").type(FileInputStream.class).setDefault(System.in);
      subparser.addArgument(OUTFILE).nargs("?").type(PrintStream.class).setDefault(System.out);
    }
    
    protected JsonNode readInput( Namespace namespace ) {

      try (InputStream in = (InputStream)namespace.get(INFILE)) {
        return MAPPER.readValue(new InputStreamReader(in, "UTF-8"), JsonNode.class);
      }
      catch( Exception e ) {
        System.err.println("the supplied string is not valid json.  "+e.getMessage());
        System.err.println(e);
        System.exit(1);
        return null;
      }      
    }
    
    protected void writeOutput( Namespace namespace, JsonNode node ) throws JsonGenerationException, JsonMappingException, IOException {
      try (PrintStream out = (PrintStream)namespace.get(OUTFILE) ) {
        MAPPER.writeValue(out, node);
        out.format("%n");      
      }      
    }
    
    public JsonPointer getPointer( Namespace namespace ) {
      return (JsonPointer)namespace.get(POINTER);
    }
  }
  
  public static class Encrypt extends AbstractCryptoCommand {
    protected Encrypt(String name, String description, EncryptionService service) {
      super(name, description, service);
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
      JsonNode config = readInput(namespace);
      
      JsonPointer pointer = getPointer(namespace);
      JsonPointerEditor editor = new JsonPointerEditor(config, pointer);
      
      EncryptedJson encrypted = service.encryptValue(editor.getValue(), "UTF-8");
      JsonNode encryptedNode = MAPPER.convertValue(encrypted, ObjectNode.class);
      editor.setValue(encryptedNode);
      
      writeOutput(namespace, editor.getRoot());
    }
  }
  
  public static class Decrypt extends AbstractCryptoCommand {
    protected Decrypt(String name, String description, EncryptionService service) {
      super(name, description, service);
    }

    @Override
    public void run(Bootstrap<?> bootstrap, Namespace namespace) throws Exception {
      JsonNode config = readInput(namespace);
      
      JsonPointer pointer = getPointer(namespace);
      JsonPointerEditor editor = new JsonPointerEditor(config, pointer);
      
      EncryptedJson encrypted = MAPPER.convertValue(editor.getValue(), EncryptedJson.class);
      JsonNode decrypted = service.decryptAs(encrypted, "UTF-8", JsonNode.class);
      editor.setValue(decrypted);
      
      writeOutput(namespace, editor.getRoot());
    }
  }
}