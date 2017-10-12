/*
 *  Copyright 2017 Huawei Technologies Co., Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.servicecomb.spring.cloud.zuul.tracing;

import static io.servicecomb.foundation.common.base.ServiceCombConstants.CONFIG_TRACING_ENABLED_KEY;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import io.servicecomb.foundation.common.utils.BeanUtils;
import io.servicecomb.tests.tracing.TracingTestBase;


public class SpringCloudZuulTracingDisableTest {

  private static ConfigurableApplicationContext context;

  @BeforeClass
  public static void init() throws Exception {
    TracingTestBase.setUpLocalRegistry();
    System.setProperty(CONFIG_TRACING_ENABLED_KEY, "false");
    context = SpringApplication.run(TracedZuulMain.class);
  }

  @AfterClass
  public static void shutdown() throws Exception {
    context.close();
  }

  @Test
  public void ensureTracingDisable() {
    try {
      BeanUtils.getContext().getBean(SpringTracingConfiguration.class);
      fail("Expect to throw BeansException, but got none");
    } catch (BeansException ignored) {
    }
  }
}
