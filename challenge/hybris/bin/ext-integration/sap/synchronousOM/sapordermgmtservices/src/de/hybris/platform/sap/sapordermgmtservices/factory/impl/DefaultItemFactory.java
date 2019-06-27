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
package de.hybris.platform.sap.sapordermgmtservices.factory.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;


/* (non-Javadoc)
 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory
 */
public class DefaultItemFactory implements ItemFactory
{

	/* (non-Javadoc)
	 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
	 */
	@Override
	public Item createItem()
	{
		return new ItemSalesDoc();
	}

}
