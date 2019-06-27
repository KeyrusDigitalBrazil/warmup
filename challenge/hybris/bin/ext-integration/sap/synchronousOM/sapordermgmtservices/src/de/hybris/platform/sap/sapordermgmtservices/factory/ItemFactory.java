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
package de.hybris.platform.sap.sapordermgmtservices.factory;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;


/**
 * Factory to create new instance of item
 */
public interface ItemFactory
{
	/**
	 * Return new instance of item
	 *
	 * @return item
	 */
	Item createItem();
}
