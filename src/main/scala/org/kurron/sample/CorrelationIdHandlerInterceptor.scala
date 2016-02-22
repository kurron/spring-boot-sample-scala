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

import java.util.UUID
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.kurron.feedback.AbstractFeedbackAware
import org.kurron.feedback.exceptions.PreconditionFailedError
import org.kurron.sample.feeback.LoggingContext
import org.kurron.sample.inbound.CustomHttpHeaders.X_CORRELATION_ID
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.{HandlerInterceptor, ModelAndView}

/**
  * Intercepts each REST request and extracts the X-Correlation-Id header, which is added to the MDC logging context. If no header is
  * found, an error is thrown.
  */
@Component
class CorrelationIdHandlerInterceptor( @Autowired private val configuration: ApplicationProperties ) extends AbstractFeedbackAware with HandlerInterceptor  {
  /**
    * Correlation id key into the mapped diagnostic context.
    */
  val CORRELATION_ID: String = "correlation-id"

  override def afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: scala.Any, ex: Exception): Unit = {}

  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: scala.Any): Boolean = {
    var correlationId = request.getHeader( X_CORRELATION_ID )
    if ( null == correlationId ) {
      if ( configuration.requireCorrelationId ) {
          getFeedbackProvider.sendFeedback( LoggingContext.PRECONDITION_FAILED, X_CORRELATION_ID )
        throw new PreconditionFailedError( LoggingContext.PRECONDITION_FAILED, X_CORRELATION_ID )
      } else {
        correlationId = UUID.randomUUID().toString
        getFeedbackProvider.sendFeedback( LoggingContext.MISSING_CORRELATION_ID, correlationId )
      }
    }
    MDC.put( CORRELATION_ID, correlationId )
    true
  }

  override def postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: scala.Any, modelAndView: ModelAndView): Unit = {
    MDC.remove( CORRELATION_ID )
  }
}
