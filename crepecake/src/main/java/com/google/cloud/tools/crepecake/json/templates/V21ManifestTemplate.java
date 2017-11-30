package com.google.cloud.tools.crepecake.json.templates;

import com.google.cloud.tools.crepecake.image.Digest;
import com.google.cloud.tools.crepecake.json.JsonTemplate;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON template for Docker Manifest Schema V2.1
 *
 * This is only for parsing manifests in the older V2.1 schema. Generated manifests should be in the V2.2 schema using the {@link V22ManifestTemplate}.
 *
 * Example manifest JSON (only the {@code fsLayers} and {@code history} fields are relevant for parsing):
 *
 * <pre>{@code
 * {
 *   ...
 *   "fsLayers": {
 *     {
 *       "blobSum": "sha256:5f70bf18a086007016e948b04aed3b82103a36bea41755b6cddfaf10ace3c6ef"
 *     },
 *     {
 *       "blobSum": "sha256:5f70bf18a086007016e948b04aed3b82103a36bea41755b6cddfaf10ace3c6ef"
 *     }
 *   },
 *   "history": [
 *     {
 *       "v1Compatibility": "<some manifest V1 JSON object>"
 *     }
 *   ]
 *   ...
 * }
 *
 * @see <a href="https://docs.docker.com/registry/spec/manifest-v2-1/">Image Manifest Version 2,
 *     Schema 1</a>
 * }</pre>
 */
public class V21ManifestTemplate extends JsonTemplate {

  /** The list of layer references. */
  private final List<LayerObjectTemplate> fsLayers = new ArrayList<>();

  private final List<V1CompatibilityTemplate> history = new ArrayList<>();

  /**
   * Template for inner JSON object representing a layer as part of the list of layer references.
   */
  private static class LayerObjectTemplate extends JsonTemplate {

    private Digest blobSum;
  }

  /** Template for inner JSON object representing the V1-compatible format for a layer. */
  private static class V1CompatibilityTemplate extends JsonTemplate {

    // TODO: Change to its own JSON template that can extract the layer diff ID.
    private String v1Compatibility;
  }

  @VisibleForTesting
  Digest getLayerDigest(int index) {
    return fsLayers.get(index).blobSum;
  }

  @VisibleForTesting
  String getV1Compatibility(int index) {
    return history.get(index).v1Compatibility;
  }
}
