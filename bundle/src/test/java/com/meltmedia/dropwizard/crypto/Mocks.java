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

import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonPointer;

public class Mocks {
  public static void mockInput(Namespace namespace, String value) throws IOException {
    when(namespace.get(Commands.INFILE))
      .thenReturn(IOUtils.toInputStream(value, "UTF-8"));  
  }
  
  public static Callable<String> mockOutput(Namespace namespace) throws UnsupportedEncodingException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    when(namespace.get(Commands.OUTFILE))
      .thenReturn(new PrintStream(out, true, "UTF-8"));
    return new Callable<String>() {
      @Override public String call() throws Exception {
        return out.toString("UTF-8");
      }
    };
  }
  
  public static void mockPointer(Namespace namespace, String pointer) {
    when(namespace.get(Commands.POINTER))
      .thenReturn(JsonPointer.compile(pointer));
  }

}
