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
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import rx.Observable;


/**
 * Specifies REST APIs for CPS calls to retrieve the configuration master data
 */
public interface MasterDataClientBase
{
	/**
	 * Retrieves the configuration master data using query parameter defining requested optional data parts
	 *
	 * @param id
	 *           Knowledge base ID (DB key in the modeling system)
	 * @param lang
	 *           ISO language code
	 * @param select
	 *           query parameter defining required additional data parts
	 * @return Observable wrapper around KB data
	 */
	@GET
	@Produces("application/json")
	@Path("/knowledgebases/{id}")
	Observable<CPSMasterDataKnowledgeBase> getKnowledgebase(@PathParam("id") String id,
			@HeaderParam("Accept-Language") String lang, @QueryParam("$select") String select);
}
