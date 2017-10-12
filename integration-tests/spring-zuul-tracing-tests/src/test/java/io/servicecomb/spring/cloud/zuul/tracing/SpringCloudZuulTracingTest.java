/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.spring.cloud.zuul.tracing;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import io.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import io.servicecomb.tests.tracing.TracingTestBase;

public class SpringCloudZuulTracingTest extends TracingTestBase {

  private static ConfigurableApplicationContext context;

  private final static RestTemplate restTemplate = RestTemplateBuilder.create();

  private final static String GATEWAY_ADDRESS = "http://127.0.0.1:8081";

  @BeforeClass
  public static void init() {
    context = SpringApplication.run(TracedZuulMain.class);
    restTemplate.setErrorHandler(new ResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        return false;
      }

      @Override
      public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
      }
    });
  }

  @AfterClass
  public static void shutdown() {
    context.close();
  }

  @After
  public void tearDown() throws Exception {
    appender.clear();
  }

  @Test
  public void tracesCallsReceivedFromZuulToCalledService() throws InterruptedException {
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(GATEWAY_ADDRESS + "/dummy/rest/blah", String.class);

    assertThat(responseEntity.getStatusCode(), is(OK));
    assertThat(responseEntity.getBody(), is("blah"));

    TimeUnit.MILLISECONDS.sleep(1000);

    Collection<String> tracingMessages = appender.pollLogs(".*\\[\\w+/\\w+/\\w*\\]\\s+INFO.*(logged tracing|/blah).*");
    assertThat(tracingMessages.size(), greaterThanOrEqualTo(2));

    assertThatSpansReceivedByZipkin(tracingMessages, "/dummy/rest/blah", "/blah");
  }

  @Test
  public void tracesFailedCallsReceivedByZuul() throws InterruptedException {
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(GATEWAY_ADDRESS + "/dummy/rest/oops", String.class);

    assertThat(responseEntity.getStatusCode(), is(INTERNAL_SERVER_ERROR));

    TimeUnit.MILLISECONDS.sleep(1000);

    Collection<String> tracingMessages = appender.pollLogs(".*\\[\\w+/\\w+/\\w*\\]\\s+INFO.*(logged tracing|/oops).*");
    assertThat(tracingMessages.size(), greaterThanOrEqualTo(2));

    assertThatSpansReceivedByZipkin(tracingMessages, "/dummy/rest/oops", "500", "/oops", "590");
  }
}
