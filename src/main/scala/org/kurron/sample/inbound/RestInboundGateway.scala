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

package org.kurron.sample.inbound

import java.util.Optional

import org.kurron.feedback.AbstractFeedbackAware
import org.kurron.stereotype.InboundRestGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.http.{HttpHeaders, ResponseEntity, HttpStatus}
import org.springframework.web.bind.annotation.{RequestMethod, RequestMapping}
import org.springframework.web.util.UriComponentsBuilder

/**
  * Handles inbound REST requests.
  */
@InboundRestGateway
@RequestMapping( value = Array( "/hash-id" ) )
class RestInboundGateway( @Autowired val counterService: CounterService ) extends AbstractFeedbackAware {

  @RequestMapping( method = Array( RequestMethod.GET ), produces = Array( HypermediaControl.JSON_MIME_TYPE ) )
  ResponseEntity[HypermediaControl] apiDiscovery( @RequestHeader( "X-Correlation-Id" ) Optional[String] correlationID,
  UriComponentsBuilder builder ) {
    counterService.increment( "gateway.api-discovery" )

    def loggingID = correlationID.orElse( randomHexString() )
    def response = new HypermediaControl( httpCode: HttpStatus.OK.value(  ) )
    injectLinks( builder, response )
    def headers = new HttpHeaders()
    headers.add( CustomHttpHeaders.X_CORRELATION_ID, loggingID )

    new ResponseEntity[HypermediaControl]( response, headers, HttpStatus.OK )
  }
}
