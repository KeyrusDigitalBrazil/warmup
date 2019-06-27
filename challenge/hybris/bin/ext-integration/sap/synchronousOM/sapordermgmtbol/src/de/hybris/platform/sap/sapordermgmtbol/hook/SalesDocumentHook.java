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
package de.hybris.platform.sap.sapordermgmtbol.hook;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import java.util.List;


/**
 * Hook interface for SalesDocument
 *
 */
public interface SalesDocumentHook
{

	/**
	 * @param techKey
	 */
	void afterDeleteItemInBackend(TechKey techKey);

	/**
	 * @param itemList
	 */
	void afterDeleteItemInBackend(ItemList itemList);

	/**
	 * @param itemList
	 */
	void afterDeleteItemInBackend(List<TechKey> itemsToDelete);

	/**
	 * @param itemsToDelete
	 */
	void afterUpdateItemInBackend(List<TechKey> itemsToDelete);

}
