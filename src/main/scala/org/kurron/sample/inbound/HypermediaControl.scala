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

import javax.validation.Valid

import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}
import org.springframework.http.MediaType

import scala.beans.BeanProperty

/**
  * The hypermedia REST control for the HID resource.  Can be serialized into JSON.
  */
@JsonInclude( JsonInclude.Include.NON_NULL )
class HypermediaControl( @JsonProperty( "http-code" ) val httpCode: Int ) {

  /**
    * Required block that contains both request and response information.
    */
  @Valid
  @JsonProperty( "items" )
  var items:List[Data]

  /**
    * An optional block that is only populated after an error occurs.
    */
  @JsonProperty( "error" )
  @BeanProperty
  var errorBlock: ErrorBlock

}

object HypermediaControl {
  val JSON_MIME_TYPE: String = "application/json;type=hash-id;version=1.0.0"
  val JSON_MEDIA_TYPE: MediaType = MediaType.parseMediaType( JSON_MIME_TYPE )
}