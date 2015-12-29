/*
 * Copyright (c) 2015. Ronald D. Kurr kurr@jvmguy.com
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

package org.kurron.sample

import org.kurron.feedback.FeedbackAwareBeanPostProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.{AsyncRestTemplate, ResponseErrorHandler, RestTemplate}
/**
  * This is the main entry into the application. Running from the command-line using embedded Tomcat will invoke
  * the main() method.
  */
@SpringBootApplication
@EnableConfigurationProperties( value = Array( classOf[ApplicationProperties] ) )
class ApplicationContext {

  /**
    * Indicates the type of service emitting the messages.
    */
  @Value( "${info.app.name}" )
  var serviceCode: String = "bob"

  /**
    * Indicates the instance of the service emitting the messages.
    */
  @Value( "${PID}" )
  var serviceInstance: String = "bob"

  /**
    * Indicates the logical group of the service emitting the messages.
    */
  @Value( "${info.app.realm}" )
  var realm: String = "bob"

  @Bean
  def feedbackAwareBeanPostProcessor(): FeedbackAwareBeanPostProcessor = {
    new FeedbackAwareBeanPostProcessor( serviceCode, serviceInstance, realm )
  }

  @Bean
  def asyncRestTemplate() : AsyncRestTemplate = {
    def bean = new AsyncRestTemplate()
    bean.setErrorHandler( new NoOpErrorHandler() )
    bean
  }

  @Bean
  def restTemplate(): RestTemplate = {
    def bean = new RestTemplate()
    bean.setErrorHandler( new NoOpErrorHandler() )
    bean
  }
}

class NoOpErrorHandler extends ResponseErrorHandler {
  override def hasError(response: ClientHttpResponse): Boolean = false

  override def handleError(response: ClientHttpResponse): Unit = {}
}
