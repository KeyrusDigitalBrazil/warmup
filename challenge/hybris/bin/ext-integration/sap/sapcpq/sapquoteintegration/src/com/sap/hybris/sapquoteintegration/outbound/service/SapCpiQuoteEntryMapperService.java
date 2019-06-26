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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;


/**
 * Provides mapping from {@link AbstractOrderEntryModel} of Quote to {@link SAPCpiOutboundQuoteItemModel}.
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public interface SapCpiQuoteEntryMapperService<SOURCE extends AbstractOrderEntryModel, TARGET extends SAPCpiOutboundQuoteItemModel>
{
	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Entry Model
	 * @param target
	 *           SAP CPI Outbound Quote Entry Model
	 */
	void map(SOURCE source, TARGET target);

}