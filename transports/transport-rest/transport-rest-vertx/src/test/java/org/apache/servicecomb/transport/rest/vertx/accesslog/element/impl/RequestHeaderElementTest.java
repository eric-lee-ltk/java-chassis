/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.transport.rest.vertx.accesslog.element.impl;

import static org.junit.Assert.assertEquals;

import org.apache.servicecomb.transport.rest.vertx.accesslog.AccessLogParam;
import org.junit.Test;
import org.mockito.Mockito;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.ext.web.RoutingContext;

public class RequestHeaderElementTest {

  private static final String HEADER_IDENTIFIER = "headerIdentifier";

  private static final RequestHeaderElement ELEMENT = new RequestHeaderElement(HEADER_IDENTIFIER);

  @Test
  public void getFormattedElement() {
    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    AccessLogParam param = new AccessLogParam().setRoutingContext(mockContext);
    HttpServerRequest mockRequest = Mockito.mock(HttpServerRequest.class);
    VertxHttpHeaders headers = new VertxHttpHeaders();
    String testValue = "testValue";
    headers.add(HEADER_IDENTIFIER, testValue);

    Mockito.when(mockContext.request()).thenReturn(mockRequest);
    Mockito.when(mockRequest.headers()).thenReturn(headers);

    String result = ELEMENT.getFormattedElement(param);

    assertEquals(testValue, result);
    assertEquals(ELEMENT.getIdentifier(), HEADER_IDENTIFIER);
  }

  @Test
  public void getFormattedElementIfHeaderIsNull() {
    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    AccessLogParam param = new AccessLogParam().setRoutingContext(mockContext);
    HttpServerRequest mockRequest = Mockito.mock(HttpServerRequest.class);

    Mockito.when(mockContext.request()).thenReturn(mockRequest);
    Mockito.when(mockRequest.headers()).thenReturn(null);

    String result = ELEMENT.getFormattedElement(param);

    assertEquals("-", result);
  }

  @Test
  public void getFormattedElementIfNotFound() {
    RoutingContext mockContext = Mockito.mock(RoutingContext.class);
    AccessLogParam param = new AccessLogParam().setRoutingContext(mockContext);
    HttpServerRequest mockRequest = Mockito.mock(HttpServerRequest.class);
    VertxHttpHeaders headers = new VertxHttpHeaders();
    String testValue = "testValue";
    headers.add("anotherHeader", testValue);

    Mockito.when(mockContext.request()).thenReturn(mockRequest);
    Mockito.when(mockRequest.headers()).thenReturn(headers);

    String result = ELEMENT.getFormattedElement(param);

    assertEquals("-", result);
  }
}
