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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.tools.crepecake.json.DescriptorDigestDeserializer;
import com.google.cloud.tools.crepecake.json.DescriptorDigestSerializer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a SHA-256 content descriptor digest as defined by the Registry HTTP API v2 reference.
 *
 * @see <a
 *     href="https://docs.docker.com/registry/spec/api/#content-digests">https://docs.docker.com/registry/spec/api/#content-digests</a>
 * @see <a href="https://github.com/opencontainers/image-spec/blob/master/descriptor.md#digests">OCI
 *     Content Descriptor Digest</a>
 */
@JsonSerialize(using = DescriptorDigestSerializer.class)
@JsonDeserialize(using = DescriptorDigestDeserializer.class)
public class DescriptorDigest {

  /** Pattern matches a SHA-256 hash - 32 bytes in lowercase hexadecimal. */
  private static final String HASH_REGEX = "[a-f0-9]{64}";

  /** Pattern matches a SHA-256 digest - a SHA-256 hash prefixed with "sha256:". */
  private static final String DIGEST_REGEX = "sha256:" + HASH_REGEX;

  private static final Pattern HASH_PATTERN = Pattern.compile(HASH_REGEX);

  private final String hash;

  /** Creates a new instance from a valid hash string. */
  public static DescriptorDigest fromHash(String hash) throws DescriptorDigestException {
    if (!hash.matches(HASH_REGEX)) {
      throw new DescriptorDigestException("Invalid hash: " + hash);
    }

    return new DescriptorDigest(hash);
  }

  /** Creates a new instance from a valid digest string. */
  public static DescriptorDigest fromDigest(String digest) throws DescriptorDigestException {
    if (!digest.matches(DIGEST_REGEX)) {
      throw new DescriptorDigestException("Invalid digest: " + digest);
    }

    // Extracts the hash portion of the digest.
    Matcher matcher = HASH_PATTERN.matcher(digest);
    matcher.find();
    String hash = matcher.group(0);
    return new DescriptorDigest(hash);
  }

  private DescriptorDigest(String hash) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  public String toString() {
    return "sha256:" + hash;
  }

  /** Pass-through hash code of the digest string. */
  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  /** Two digest objects are equal if their digest strings are equal. */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DescriptorDigest) {
      return hash.equals(((DescriptorDigest) obj).hash);
    }

    return false;
  }
}
