/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.yprofile.rest.clients;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Header;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;
import com.hybris.yprofile.dto.AbstractProfileEvent;
import org.springframework.http.HttpHeaders;
import rx.Observable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@OAuth
@Http
public interface ProfileClient {

    /**
     * Send tracking events, orders and users to yProfile
     * @deprecated please use ProfileTag instead
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/events")
    @Header(name = "hybris-tenant", val = "${tenant}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    @Deprecated
    Observable<ProfileResponse> sendEvent(
            @HeaderParam("event-type") String eventType,
            @HeaderParam("consent-reference") String consentReferenceId,
            @HeaderParam(HttpHeaders.USER_AGENT) String userAgent,
            @HeaderParam(HttpHeaders.ACCEPT) String accept,
            @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) String acceptLanguage,
            @HeaderParam(HttpHeaders.REFERER) String referer,
            AbstractProfileEvent event);

    /**
     * Send transactions (order events + login & registration events) to yProfile
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/events")
    @Header(name = "hybris-tenant", val = "${tenant}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<RawResponse> sendTransaction(
            @HeaderParam("event-type") String eventType,
            @HeaderParam("consent-reference") String consentReferenceId,
            @HeaderParam("X-B3-Sampled") String tracingEnabled,
            AbstractProfileEvent event);

    /**
     * Send slim events to yProfile
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/events")
    @Header(name = "hybris-tenant", val = "${tenant}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<RawResponse> sendSlimEvent(
            @HeaderParam("hybris-schema") String schema,
            @HeaderParam("consent-reference") String consentReferenceId,
            @HeaderParam("X-B3-Sampled") String tracingEnabled,
            AbstractProfileEvent event);
}
