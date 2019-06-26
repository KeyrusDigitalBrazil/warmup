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
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;
import rx.Observable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@OAuth
@Http
public interface ConsentServiceClient {

    /**
     * delete consent reference for user
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/${tenant}/consents/{consent-reference}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<RawResponse> deleteConsentReference(
            @PathParam("consent-reference") String consentReferenceId,
            @HeaderParam("X-B3-Sampled") String tracingEnabled);
}
