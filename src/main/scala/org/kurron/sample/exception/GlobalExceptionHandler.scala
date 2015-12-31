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

package org.kurron.sample.exception

import org.kurron.feedback.exceptions.AbstractError
import org.kurron.feedback.{FeedbackAware, FeedbackProvider}
import org.kurron.sample.feeback.LoggingContext
import org.kurron.sample.inbound.{ErrorBlock, HypermediaControl}
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.{ControllerAdvice, ExceptionHandler}
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

import scala.collection.JavaConversions._

/**
  * Global handling for REST exceptions.
  */
@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler with FeedbackAware {

  var feedbackProvider: FeedbackProvider = null

  override def setFeedbackProvider(aProvider: FeedbackProvider): Unit = {
      feedbackProvider = aProvider
  }

  override def getFeedbackProvider: FeedbackProvider = {
    feedbackProvider
  }

  override def handleExceptionInternal(e: Exception,
                                       body: scala.Any,
                                       headers: HttpHeaders,
                                       status: HttpStatus,
                                       request: WebRequest): ResponseEntity[HypermediaControl] = {
    // I couldn't get a handler specific to MethodArgumentNotValidException bound. Spring says it is ambiguous!
    e match {
      case validationException: MethodArgumentNotValidException =>
        handleValidationException(validationException)
      case _ =>
        getFeedbackProvider.sendFeedback(LoggingContext.GENERIC_ERROR, e.getMessage)
        val control = new HypermediaControl( status.value() )
        control.errorBlock = new ErrorBlock( LoggingContext.GENERIC_ERROR.getCode,
                                             e.getMessage,
                                            "Indicates that the exception was not handled explicitly and is being handled as a generic error")
        wrapInResponseEntity(control, status, headers)
    }
  }

  /**
    * Handles validation errors detected by Spring.
    * @param e the validation failure.
    * @return the constructed response entity, containing details about the error.
    */
  //    @ExceptionHandler( MethodArgumentNotValidException )
  def handleValidationException( e: MethodArgumentNotValidException  ) : ResponseEntity[HypermediaControl] = {
    val errors = e.getBindingResult.getFieldErrors
    val field = errors.head.getField
    val message = errors.head.getDefaultMessage
    getFeedbackProvider.sendFeedback( LoggingContext.INVALID_FIELD, field, message )
    val control = new HypermediaControl( HttpStatus.BAD_REQUEST.value() )
    control.errorBlock = new ErrorBlock( LoggingContext.INVALID_FIELD.getCode,
                                         "Request failed validation.",
                                         "The field ${field} ${message}. Please correct your request and try again." )
    wrapInResponseEntity( control, HttpStatus.BAD_REQUEST)
  }

  /**
    * Handles errors thrown by application itself.
    * @param e the error.
    * @return the constructed response entity, containing details about the error.
    */
  @ExceptionHandler( Array( classOf[AbstractError] ) )
  def handleApplicationException( e: AbstractError ) = {
    val control = new HypermediaControl( e.getHttpStatus.value )
    control.errorBlock = new ErrorBlock( e.getCode, e.getMessage, e.getDeveloperMessage )
    wrapInResponseEntity( control, e.getHttpStatus )
  }

  /**
    * Knows how to transform a non-application exception into a hypermedia control.
    * @param throwable non-application error.
    * @return control that contains as much data about the error that is available to us.
    */
  @ExceptionHandler( Array( classOf[Throwable] ) )
  def handleSystemException( throwable: Throwable ): ResponseEntity[HypermediaControl] = {
    val control = new HypermediaControl( HttpStatus.INTERNAL_SERVER_ERROR.value )
    control.errorBlock = new ErrorBlock( LoggingContext.GENERIC_ERROR.getCode,
                                         throwable.getMessage,
                                         "Indicates that the exception was not handled explicitly and is being handled as a generic error" )
    wrapInResponseEntity( control, HttpStatus.INTERNAL_SERVER_ERROR )
  }

  /**
    * Wraps the provided control in a response entity.
    * @param control the control to return in the body of the response.
    * @param status the HTTP status to return.
    * @param headers the HTTP headers to return. If provided, the existing headers are used, otherwise new headers are created.
    * @return the response entity.
    */
  def wrapInResponseEntity( control: HypermediaControl,
                            status:  HttpStatus,
                            headers: HttpHeaders = new HttpHeaders() ): ResponseEntity[HypermediaControl] = {
    new ResponseEntity( control, headers, status )
  }

}
