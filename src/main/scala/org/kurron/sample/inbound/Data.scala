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

import javax.validation.constraints.NotNull

import com.fasterxml.jackson.annotation.{JsonInclude, JsonProperty}

/**
  * Optional data section of the control.
  */
@JsonInclude( JsonInclude.Include.NON_NULL )
class Data( @NotNull @JsonProperty( "knownLanguage" ) var knownLanguage: String,
            @NotNull @JsonProperty( "learningLanguage" ) var learningLanguage: String,
            @NotNull @JsonProperty( "side1" ) var side1: String,
            @NotNull @JsonProperty( "side2" ) var side2: String ) {

  @JsonProperty( "hid" )
  var hid: String = null

}