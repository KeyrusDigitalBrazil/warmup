/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiadapter.clients;

import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOAuthResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import rx.Observable;

import com.hybris.charon.annotations.Http;


@Http(value = "oauth")
public interface SapCpiOAuthClient
{
	@POST
	@Path("/token?grant_type=client_credentials")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Observable<SapCpiOAuthResult> getToken(@HeaderParam("Authorization") String basicAuth);


}
