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
package com.sap.hybris.sapquoteintegration.outbound.service;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;

import de.hybris.platform.core.model.order.QuoteModel;


/**
 * Provides mapping from {@link QuoteModel} to {@link SAPCpiOutboundQuoteStatusModel}.
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public interface SapCpiQuoteStatusMapperService<SOURCE extends QuoteModel, TARGET extends SAPCpiOutboundQuoteStatusModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Model
	 * @param target
	 *           SAP CPI Outbound Quote Status Model
	 */
	void map(SOURCE source, TARGET target);

}