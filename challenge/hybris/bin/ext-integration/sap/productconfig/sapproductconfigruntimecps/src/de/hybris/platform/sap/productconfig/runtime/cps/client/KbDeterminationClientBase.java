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



import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import rx.Observable;


/**
 * Charon client: KB determination. Used to access list of knowledge bases for a product at a certain date
 */
public interface KbDeterminationClientBase
{

	/**
	 * Find knowledge bases for given product
	 *
	 * @param productId
	 *           Key or product according to modeling environment
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getKnowledgebases(@QueryParam("productid") String productId);

	/**
	 * Find knowledge bases for given product and date. Date format needs to be according ISO 8601
	 *
	 * @param productId
	 *           Key of product according to modeling environment
	 * @param date
	 *           Kb validity date in format YYYY-MM-DD
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getKnowledgebases(@QueryParam("productid") String productId,
			@QueryParam("date") String date);

	/**
	 * @return List of knowledge bases
	 */
	@GET
	@Produces("application/json")
	@Path("/kbdetermination")
	Observable<List<CPSMasterDataKBHeaderInfo>> getKnowledgebases();
}
