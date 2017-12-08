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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents an image. */
public class Image {

  /** The layers of the image, in the order in which they are applied. */
  private final ImageLayers layers = new ImageLayers();

  /** Environment variables for running the image. Maps from variable name to its value. */
  private final Map<String, String> environmentMap = new HashMap<>();

  /** Initial command to run when running the image. */
  private List<String> entrypoint;

  public Map<String, String> getEnvironmentMap() {
    return ImmutableMap.copyOf(environmentMap);
  }

  public void setEnvironmentVariable(String name, String value) {
    environmentMap.put(name, value);
  }

  public List<String> getEntrypoint() {
    return ImmutableList.copyOf(entrypoint);
  }

  public void setEntrypoint(List<String> entrypoint) {
    this.entrypoint = entrypoint;
  }

  public List<Layer> getLayers() {
    return layers.asList();
  }

  public void addLayer(Layer layer) throws ImageException, LayerException {
    layers.add(layer);
  }
}
