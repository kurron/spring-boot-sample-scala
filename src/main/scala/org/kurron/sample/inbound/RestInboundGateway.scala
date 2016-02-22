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
import java.util.concurrent.ThreadLocalRandom

import org.kurron.feedback.AbstractFeedbackAware
import org.kurron.stereotype.InboundRestGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.hateoas.{Link, UriTemplate}
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation.{RequestHeader, RequestMapping}
import org.springframework.web.util.UriComponentsBuilder

/**
  * Handles inbound REST requests.
  */
@InboundRestGateway
@RequestMapping( value = Array( "/hash-id" ) )
class RestInboundGateway( @Autowired val counterService: CounterService ) extends AbstractFeedbackAware {

  def apiDiscovery( @RequestHeader( "X-Correlation-Id" ) correlationID: Optional[String], builder: UriComponentsBuilder ): ResponseEntity[HypermediaControl] = {
    counterService.increment( "gateway.api-discovery" )
    def loggingID = correlationID.orElse( randomHexString() )
    def response = new HypermediaControl( httpCode = HttpStatus.OK.value() )
    injectLinks( builder, response )
    def headers = new HttpHeaders()
    headers.add( CustomHttpHeaders.X_CORRELATION_ID, loggingID )

    new ResponseEntity[HypermediaControl]( response, headers, HttpStatus.OK )
  }

  private def injectLinks( builder: UriComponentsBuilder,  response: HypermediaControl ) = {
    response.add( selfLink( builder ) )
    response.add( apiLink( builder ) )
    response.add( discoveryLink( builder ) )
  }

  private def selfLink(  builder: UriComponentsBuilder ) : Link = {
    def selfBuilder = UriComponentsBuilder.fromUri( builder.build().toUri )
    new Link( new UriTemplate( selfBuilder.path( "/hash-id" ) .build().toUriString ), "self" )
  }

  private def apiLink( builder: UriComponentsBuilder  ) : Link = {
    def docsBuilder = UriComponentsBuilder.fromUri( builder.build().toUri )
    new Link( new UriTemplate( docsBuilder.path( "/docs/index.html" ).build().toUriString ), "api-docs" )
  }

  private def discoveryLink( builder: UriComponentsBuilder  ) : Link = {
    def discoveryBuilder = UriComponentsBuilder.fromUri( builder.build().toUri )
    new Link( new UriTemplate( discoveryBuilder.path( "/hash-id" ) .build().toUriString ), "api-discovery" )
  }

  def randomHexString(): String = {
    Integer.toHexString( ThreadLocalRandom.current().nextInt( Integer.MAX_VALUE ) )
  }
}
