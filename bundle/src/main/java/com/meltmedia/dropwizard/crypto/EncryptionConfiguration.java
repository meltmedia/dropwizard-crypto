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

import java.util.LinkedHashMap;
import java.util.Map;

import com.meltmedia.jackson.crypto.Defaults;
import com.meltmedia.jackson.crypto.EncryptedJson;
import com.meltmedia.jackson.crypto.EncryptionException;
import com.meltmedia.jackson.crypto.EncryptionService;
import com.meltmedia.jackson.crypto.EncryptionService.Function;
import com.meltmedia.jackson.crypto.EncryptionService.Supplier;
import com.meltmedia.jackson.crypto.Salts;

/**
 * A configuration block for the DataEncryptionService.  This block should
 * be encrypted on disk and decrypted as it is read into memory.
 * 
 * @author Christian Trimble
 *
 */
public class EncryptionConfiguration {
  protected String currentKey;
  protected Map<String, char[]> keys = new LinkedHashMap<>();
  protected int saltLength = Defaults.SALT_LENGTH;
  protected int iterations = Defaults.KEY_STRETCH_ITERATIONS;
  protected int keyLength = Defaults.KEY_LENGTH;

  public String getCurrentKey() {
    return currentKey;
  }

  public void setCurrentKey(String currentCipher) {
    this.currentKey = currentCipher;
  }

  public Map<String, char[]> getKeys() {
    return keys;
  }

  public void setKeys(Map<String, char[]> keys) {
    this.keys = keys;
  }

  public int getIterations() {
    return iterations;
  }

  public int getSaltLength() {
    return saltLength;
  }

  public void setSaltLength(int saltLength) {
    this.saltLength = saltLength;
  }

  public int getKeyLength() {
    return keyLength;
  }

  public void setKeyLength(int keyLength) {
    this.keyLength = keyLength;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }
}
