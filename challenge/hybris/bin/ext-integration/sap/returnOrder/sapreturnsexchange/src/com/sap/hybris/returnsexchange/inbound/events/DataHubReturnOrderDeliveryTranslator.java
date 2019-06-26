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
package com.sap.hybris.returnsexchange.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;


/**
 *
 */
public class DataHubReturnOrderDeliveryTranslator extends DefaultSpecialValueTranslator
{
	@Override
	public void performImport(final String delivInfo, final Item processedItem) throws ImpExException
	{
		final String orderCode = getOrderCode(processedItem);
		getInboundHelper().processOrderDeliveryNotififcationFromDataHub(orderCode, delivInfo);
	}

}
