/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.tools.crepecake.blob;

import com.google.cloud.tools.crepecake.hash.CountingDigestOutputStream;
import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestException;

/** A {@link Blob} that holds a {@link String} and hashes the bytes when written out. */
class HashingStringBlob implements Blob {

  private final byte[] contentBytes;

  HashingStringBlob(String content) {
    contentBytes = content.getBytes(Charsets.UTF_8);
  }

  private void writeToWithHashing(CountingDigestOutputStream outputStream) throws IOException {
    outputStream.write(contentBytes);
  }

  @Override
  public BlobDescriptor writeTo(OutputStream outputStream) throws IOException {
    CountingDigestOutputStream.decoratedWrite(outputStream, this::writeToWithHashing);
  }
}
