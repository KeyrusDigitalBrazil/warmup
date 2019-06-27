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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.Map;


/**
 * Helper Interface to Buffer Item data, typically used by ERP backend implementation.<br>
 *
 */
public interface ItemBuffer
{

	/**
	 * The list of items which represents the ERP status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 *
	 * @return list of items
	 */
	Map<String, Item> getItemsERPState();

	/**
	 * The list of items which represents the ERP status. We use it to compile a delta to the status we get from the BO
	 * layer. Aim is to optimise the LO-API call
	 *
	 * @param itemsERPState
	 *           list of items
	 */
	void setItemsERPState(Map<String, Item> itemsERPState);

	/**
	 * Removes item from ERP state map, together with sub items (free goods!)
	 *
	 * @param idAsString
	 *           item ID
	 */
	void removeItemERPState(String idAsString);

	/**
	 * Clears the buffer for the ERP state of the document.
	 */
	void clearERPBuffer();

}