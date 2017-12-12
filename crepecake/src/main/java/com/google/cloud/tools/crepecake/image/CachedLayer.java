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

package com.google.cloud.tools.crepecake.image;

import com.google.cloud.tools.crepecake.blob.BlobDescriptor;
import com.google.cloud.tools.crepecake.blob.BlobStream;
import com.google.cloud.tools.crepecake.blob.BlobStreams;
import java.io.File;

/**
 * A {@link Layer} that has been written out (i.e. to a cache) and has its file-backed content BLOB,
 * digest, size, and diff ID.
 */
public class CachedLayer extends Layer {

  private final File contentFile;
  private final BlobDescriptor blobDescriptor;
  private final DescriptorDigest diffId;

  /**
   * Initializes the layer with its file-backed content BLOB, content descriptor (digest and size),
   * and diff ID. The {@code blobDescriptor} and {@code diffId} <b>must match</b> the BLOB stored in
   * the file - no checks are made at runtime.
   *
   * @param contentFile the file with the layer's content BLOB
   * @param blobDescriptor the content descriptor for the layer's content BLOB
   * @param diffId the diff ID for the layer
   * @see Layer
   */
  public CachedLayer(File contentFile, BlobDescriptor blobDescriptor, DescriptorDigest diffId) {
    this.contentFile = contentFile;
    this.blobDescriptor = blobDescriptor;
    this.diffId = diffId;
  }

  /** Gets a new {@link BlobStream} for the content of the cached layer. */
  public BlobStream getBlobStream() {
    return BlobStreams.from(contentFile);
  }

  @Override
  public LayerType getType() {
    return LayerType.CACHED;
  }

  public File getContentTarFile() {
    return contentFile;
  }

  @Override
  public BlobDescriptor getBlobDescriptor() {
    return blobDescriptor;
  }

  @Override
  public DescriptorDigest getDiffId() {
    return diffId;
  }
}
