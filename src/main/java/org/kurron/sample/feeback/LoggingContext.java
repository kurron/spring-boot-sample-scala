/*
 * Copyright (c) 2016. Ronald D. Kurr kurr@jvmguy.com 
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

package org.kurron.sample.feeback;

import static org.kurron.feedback.Audience.QA;
import static org.kurron.feedback.FeedbackLevel.ERROR;
import static org.kurron.feedback.FeedbackLevel.WARN;
import org.kurron.feedback.Audience;
import org.kurron.feedback.FeedbackContext;
import org.kurron.feedback.FeedbackLevel;

/**
 * Message codes specific to this application. NOTE: Scala does not appear to support enums so I took one person's advice and
 * implemented it in Java.
 */
public enum LoggingContext implements FeedbackContext
{
    GENERIC_ERROR( 2000, "The following error has occurred and was caught by the global error handler: {}", ERROR, QA ),
    PRECONDITION_FAILED( 2001, "The required {} header was not found on an inbound REST request", ERROR, QA ),
    MISSING_CORRELATION_ID( 2002, "A correlation id was missing from a request, and an auto-generated id of {} will be used instead", WARN, QA ),
    INVALID_FIELD( 2003, "The field {} {}", ERROR, QA );

    /**
     * Unique context code for this instance.
     */
    private final int theCode;
;
    /**
     * Message format string for this instance.
     */
    private final String theFormatString;

    /**
     * Feedback level for this instance.
     */
    private final FeedbackLevel theFeedbackLevel;

    /**
     * The audience for this instance.
     */
    private final Audience theAudience;

    LoggingContext( int aCode, String aFormatString, FeedbackLevel aFeedbackLevel, Audience anAudience ) {
        theCode = aCode;
        theFormatString = aFormatString;
        theFeedbackLevel = aFeedbackLevel;
        theAudience = anAudience;
    }

    @Override
    public int getCode() {
        return theCode;
    }

    @Override
    public String getFormatString() {
        return theFormatString;
    }

    @Override
    public FeedbackLevel getFeedbackLevel() {
        return theFeedbackLevel;
    }

    @Override
    public Audience getAudience() {
        return theAudience;
    }
}
