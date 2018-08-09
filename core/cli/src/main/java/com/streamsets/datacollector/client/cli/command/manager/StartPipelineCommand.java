/*
 * Copyright 2017 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.datacollector.client.cli.command.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.streamsets.datacollector.client.ApiClient;
import com.streamsets.datacollector.client.JSON;
import com.streamsets.datacollector.client.TypeRef;
import com.streamsets.datacollector.client.api.ManagerApi;
import com.streamsets.datacollector.client.cli.command.BaseCommand;
import io.airlift.airline.Command;
import io.airlift.airline.Option;

import java.util.Map;

@Command(name = "start", description = "Start Pipeline")
public class StartPipelineCommand extends BaseCommand {
  @Option(
    name = {"-n", "--name"},
    description = "Pipeline ID",
    required = true
  )
  public String pipelineId;

  @Option(
    name = {"-r", "--revision"},
    description = "Pipeline Revision",
    required = false
  )
  public String pipelineRev;

  @Option(
      name = {"-R", "--runtimeParameters"},
      description = "Runtime Parameters",
      required = false
  )
  public String runtimeParametersString;

  @Override
  public void run() {
    if(pipelineRev == null) {
      pipelineRev = "0";
    }
    Map<String, Object> runtimeParameters = null;
    try {
      ApiClient apiClient = getApiClient();
      ManagerApi managerApi = new ManagerApi(apiClient);
      if (runtimeParametersString != null && runtimeParametersString.trim().length() > 0) {
        JSON json = apiClient.getJson();
        TypeRef returnType = new TypeRef<Map<String, Object>>() {};
        runtimeParameters = json.deserialize(runtimeParametersString, returnType);
      }
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
      System.out.println(mapper.writeValueAsString(
          managerApi.startPipeline(pipelineId, pipelineRev, runtimeParameters))
      );
    } catch (Exception ex) {
      if(printStackTrace) {
        ex.printStackTrace();
      } else {
        System.out.println(ex.getMessage());
      }
    }
  }
}