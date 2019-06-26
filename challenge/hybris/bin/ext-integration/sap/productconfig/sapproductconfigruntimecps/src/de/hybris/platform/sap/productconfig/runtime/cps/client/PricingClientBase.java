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

import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hybris.charon.annotations.OAuth;

import rx.Observable;


/**
 * Charon Client interface for the pricing endpoint.
 */
@OAuth
public interface PricingClientBase
{
	/**
	 * Create and retrieve the pricing document, which contains the pricing result.
	 *
	 * @param input
	 *           pricing input
	 * @return pricing document
	 */
	@POST
	@Produces("application/json")
	@Path("/statelesspricing")
	Observable<PricingDocumentResult> createPricingDocument(PricingDocumentInput input);
}
