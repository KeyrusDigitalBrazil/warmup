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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;


/**
 * Utilities for ERP backend layer
 */
public interface BackendUtil
{


	/**
	 * Find an item via its POSNR
	 * 
	 * @param items
	 * @param posnr
	 * @return null if no item could be found
	 */
	Item findItem(ItemList items, String posnr);

}
