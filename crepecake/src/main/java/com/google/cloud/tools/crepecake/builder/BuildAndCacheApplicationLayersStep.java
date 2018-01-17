/*
 * Copyright 2018 Google Inc.
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

package com.google.cloud.tools.crepecake.builder;

import com.google.cloud.tools.crepecake.cache.Cache;
import com.google.cloud.tools.crepecake.cache.CacheChecker;
import com.google.cloud.tools.crepecake.cache.CacheMetadataCorruptedException;
import com.google.cloud.tools.crepecake.cache.CacheWriter;
import com.google.cloud.tools.crepecake.cache.CachedLayer;
import com.google.cloud.tools.crepecake.cache.CachedLayerType;
import com.google.cloud.tools.crepecake.image.DuplicateLayerException;
import com.google.cloud.tools.crepecake.image.ImageLayers;
import com.google.cloud.tools.crepecake.image.LayerBuilder;
import com.google.cloud.tools.crepecake.image.LayerPropertyNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

// TODO: Add unit test.
/** Builds and caches application layers. */
class BuildAndCacheApplicationLayersStep implements Step<Void, ImageLayers<CachedLayer>> {

  private final SourceFilesConfiguration sourceFilesConfiguration;
  private final Cache cache;

  BuildAndCacheApplicationLayersStep(
      SourceFilesConfiguration sourceFilesConfiguration, Cache cache) {
    this.sourceFilesConfiguration = sourceFilesConfiguration;
    this.cache = cache;
  }

  @Override
  public ImageLayers<CachedLayer> run(Void input)
      throws IOException, LayerPropertyNotFoundException, DuplicateLayerException,
          CacheMetadataCorruptedException {
    // TODO: Check if needs rebuilding.
    CachedLayer dependenciesLayer =
        buildAndCacheLayer(
            CachedLayerType.DEPENDENCIES,
            sourceFilesConfiguration.getDependenciesFiles(),
            sourceFilesConfiguration.getDependenciesExtractionPath());
    CachedLayer resourcesLayer =
        buildAndCacheLayer(
            CachedLayerType.RESOURCES,
            sourceFilesConfiguration.getResourcesFiles(),
            sourceFilesConfiguration.getResourcesExtractionPath());
    CachedLayer classesLayer =
        buildAndCacheLayer(
            CachedLayerType.CLASSES,
            sourceFilesConfiguration.getClassesFiles(),
            sourceFilesConfiguration.getClassesExtractionPath());

    return new ImageLayers<CachedLayer>()
        .add(dependenciesLayer)
        .add(resourcesLayer)
        .add(classesLayer);
  }

  private CachedLayer buildAndCacheLayer(
      CachedLayerType layerType, Set<Path> sourceFiles, Path extractionPath)
      throws IOException, LayerPropertyNotFoundException, DuplicateLayerException,
          CacheMetadataCorruptedException {
    // Don't build the layer if it exists already.
    CachedLayer cachedLayer = new CacheChecker(cache).getUpToDateLayerBySourceFiles(sourceFiles);
    if (cachedLayer != null) {
      return cachedLayer;
    }

    LayerBuilder layerBuilder = new LayerBuilder(sourceFiles, extractionPath);

    return new CacheWriter(cache).writeLayer(layerBuilder, layerType);
  }
}