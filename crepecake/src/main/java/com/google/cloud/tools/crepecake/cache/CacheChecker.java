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

package com.google.cloud.tools.crepecake.cache;

import com.google.cloud.tools.crepecake.image.ImageLayers;
import com.google.cloud.tools.crepecake.image.ReferenceLayer;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/** Checks if cached data is outdated. */
public class CacheChecker {

  private final Cache cache;

  public CacheChecker(Cache cache) {
    this.cache = cache;
  }

  /** @return true if the base image layers exist in the cache; false otherwise */
  public boolean areBaseImageLayersCached(ImageLayers<ReferenceLayer> baseImageLayers)
      throws CacheMetadataCorruptedException {
    // Gets the base image layers.
    ImageLayers<CachedLayerWithMetadata> cachedLayers =
        cache.getMetadata().filterLayers().byType(CachedLayerType.BASE).filter();

    if (cachedLayers.size() < baseImageLayers.size()) {
      return false;
    }

    for (ReferenceLayer baseImageLayer : baseImageLayers) {
      if (!cachedLayers.has(baseImageLayer.getBlobDescriptor().getDigest())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks all cached layers built from the source files to see if the source files have been
   * modified since the newest layer build.
   *
   * @param sourceFiles the source files to check
   * @return true if no cached layer exists that are up-to-date with the source files; false
   *     otherwise.
   */
  public boolean areSourceFilesModified(Set<File> sourceFiles)
      throws IOException, CacheMetadataCorruptedException {
    ImageLayers<CachedLayerWithMetadata> cachedLayersWithSourceFiles =
        cache.getMetadata().filterLayers().bySourceFiles(sourceFiles).filter();

    for (CachedLayerWithMetadata cachedLayer : cachedLayersWithSourceFiles) {
      // Checks if the layer is outdated.
      long lastModifiedTime = cachedLayer.getMetadata().getLastModifiedTime();
      boolean hasOutdatedFile = false;
      for (File file : sourceFiles) {
        if (isFileModifiedRecursive(file, lastModifiedTime)) {
          hasOutdatedFile = true;
          break;
        }
      }
      if (hasOutdatedFile) {
        continue;
      }

      // This layer is an up-to-date layer.
      return false;
    }

    return true;
  }

  /**
   * Checks the file has been modified since the {@code lastModifiedTime}. Recursively checks all
   * subfiles if {@code file} is a directory.
   */
  private boolean isFileModifiedRecursive(File file, long lastModifiedTime) throws IOException {
    if (file.lastModified() > lastModifiedTime) {
      return true;
    }

    if (file.isDirectory()) {
      File[] subFiles = file.listFiles();
      if (null == subFiles) {
        throw new IOException("Failed to read directory: " + file.getAbsolutePath());
      }
      for (final File subFile : subFiles) {
        if (isFileModifiedRecursive(subFile, lastModifiedTime)) {
          return true;
        }
      }
    }

    return false;
  }
}