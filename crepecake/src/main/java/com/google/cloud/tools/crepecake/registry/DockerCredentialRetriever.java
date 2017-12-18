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

package com.google.cloud.tools.crepecake.registry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.cloud.tools.crepecake.http.Authorization;
import com.google.cloud.tools.crepecake.http.Authorizations;
import com.google.cloud.tools.crepecake.json.JsonHelper;
import com.google.cloud.tools.crepecake.json.JsonTemplate;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;

// TODO: Replace with non-CLI method.
/**
 * Retrieves Docker credentials with a Docker credential helper.
 *
 * @see <a
 *     href="https://github.com/docker/docker-credential-helpers">https://github.com/docker/docker-credential-helpers</a>
 */
public class DockerCredentialRetriever {

  private final String serverUrl;
  private final String credentialHelperCommand;

  /** Template for a Docker credential helper output. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class DockerCredentialsTemplate extends JsonTemplate {

    private String Username;
    private String Secret;
  }

  /**
   * @param serverUrl the server URL to pass into the credential helper
   * @param credentialHelperSuffix the credential helper CLI suffix
   */
  public DockerCredentialRetriever(String serverUrl, String credentialHelperSuffix) {
    this.serverUrl = serverUrl;
    credentialHelperCommand = "docker-credential-" + credentialHelperSuffix + " get";
  }

  /**
   * Retrieves the Docker credentials by calling the corresponding CLI.
   *
   * <p>The credential helper CLI is called in the form:
   *
   * <pre>{@code
   * echo -n <server URL> | docker-credential-<credential helper suffix> get
   * }</pre>
   */
  public Authorization retrieve() throws IOException {
    Process process = Runtime.getRuntime().exec(credentialHelperCommand);
    process.getOutputStream().write(serverUrl.getBytes(Charsets.UTF_8));
    process.getOutputStream().close();

    String output =
        CharStreams.toString(new InputStreamReader(process.getInputStream(), Charsets.UTF_8));

    DockerCredentialsTemplate dockerCredentials =
        JsonHelper.readJson(output, DockerCredentialsTemplate.class);

    return Authorizations.withBasicToken(dockerCredentials.Secret);
  }
}
