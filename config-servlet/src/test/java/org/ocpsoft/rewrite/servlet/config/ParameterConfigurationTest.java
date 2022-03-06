/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.servlet.config;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.rewrite.config.ConfigurationProvider;
import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ParameterConfigurationTest extends RewriteTest
{
   @Deployment(testable = false)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest
               .getDeployment()
               .addPackages(true, ConfigRoot.class.getPackage())
               .addAsServiceProvider(ConfigurationProvider.class, ParameterConfigurationProvider.class);
      return deployment;
   }

   @Test
   public void testPathParameterRequestBinding() throws Exception
   {
      HttpAction action = get("/lincoln/order/3");
      assertThat(action.getStatusCode()).isEqualTo(200);
      assertThat(action.getResponseHeaderValues("User-Name").get(0)).isEqualTo("lincoln");
      assertThat(action.getResponseHeaderValues("Order-ID").get(0)).isEqualTo("3");
   }

   @Test
   public void testTestPathParameterNotMatchingRegexes() throws Exception
   {
      HttpAction action = get("/lincoln3/order/z42");
      assertThat(action.getStatusCode()).isEqualTo(404);
   }

   @Test
   public void testTestPathAndForwardUseEvaluationContextByDefault() throws Exception
   {
      HttpAction action = get("/p/rewrite/story/50");
      assertThat(action.getStatusCode()).isEqualTo(200);
   }

   @Test
   public void testFailedBindingRaisesException() throws Exception
   {
      HttpAction action = get("/lincoln/profile");
      assertThat(action.getStatusCode()).isEqualTo(500);
   }
}
