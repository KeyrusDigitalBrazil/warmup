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
package de.hybris.platform.sap.productconfig.runtime.pci.client;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;

import rx.Observable;


/**
 * Specifies REST API's for PCI calls
 */
@OAuth
@Control(timeout = "${sapproductconfigruntimepci.charon.timeout:15000}")
public interface PCIClient
{
	/**
	 * Determines popularity of possible values for a configuration
	 *
	 * @param input
	 * @return Analytics data (popularity of possible values)
	 */
	@POST
	@Produces("application/json")
	@Path("/findPopularity")
	Observable<AnalyticsDocument> createAnalyticsDocument(AnalyticsDocument input);

}
