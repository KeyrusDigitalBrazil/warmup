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
package de.hybris.platform.sap.sapordermgmtservices.hook;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;


/**
 * Hook interface for CartRestorationService
 *
 */
public interface CartRestorationServiceHook
{

	/**
	 * @param orderEntry
	 * @param item
	 */
	void afterCreateItemHook(AbstractOrderEntryModel orderEntry, Item item);

}
