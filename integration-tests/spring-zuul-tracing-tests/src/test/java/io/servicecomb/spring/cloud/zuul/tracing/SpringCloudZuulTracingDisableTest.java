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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import io.servicecomb.foundation.common.utils.BeanUtils;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TracedZuulMain.class, webEnvironment = RANDOM_PORT)
public class SpringCloudZuulTracingDisableTest {

  @BeforeClass
  public static void init() throws Exception {
    System.setProperty(CONFIG_TRACING_ENABLED_KEY, "false");
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
