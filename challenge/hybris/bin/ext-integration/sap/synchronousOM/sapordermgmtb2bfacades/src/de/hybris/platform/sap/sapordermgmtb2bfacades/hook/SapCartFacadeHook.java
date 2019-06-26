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
package de.hybris.platform.sap.sapordermgmtb2bfacades.hook;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;

import java.util.List;


/**
 * Hook interface for SapCartFacade
 */
public interface SapCartFacadeHook
{

	/**
	 * @param quantity
	 * @param entryNumber
	 * @param entries
	 */
	void beforeCartEntryUpdate(long quantity, long entryNumber, List<OrderEntryData> entries);

}
