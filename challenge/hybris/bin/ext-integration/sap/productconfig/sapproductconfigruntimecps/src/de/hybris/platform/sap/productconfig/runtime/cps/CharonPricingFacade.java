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
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;


/**
 * Facilitates interaction with skywalker pricing service
 */
public interface CharonPricingFacade
{
	/**
	 * Create pricing document
	 *
	 * @param pricingInput
	 *           input document needed to call the pricing service
	 * @return result of the pricing call
	 * @throws PricingEngineException
	 *            indicates error during pricing engine call
	 */
	PricingDocumentResult createPricingDocument(PricingDocumentInput pricingInput) throws PricingEngineException;
}
