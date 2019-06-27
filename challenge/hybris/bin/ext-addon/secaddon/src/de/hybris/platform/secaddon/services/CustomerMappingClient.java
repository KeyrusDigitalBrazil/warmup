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
package de.hybris.platform.secaddon.services;

import de.hybris.platform.secaddon.data.CustomerInfo;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;


@OAuth
@Http
public interface CustomerMappingClient
{
	@GET
	@Path("/${tenant}/customers/?expand=mixin:*&q={mixinQuery}:\"{id}\"")
	Observable<List<CustomerInfo>> getCustomer(@HeaderParam("hybris-languages") String lang,
			@PathParam("mixinQuery") String mixinQuery, @PathParam("id") String id);

}
