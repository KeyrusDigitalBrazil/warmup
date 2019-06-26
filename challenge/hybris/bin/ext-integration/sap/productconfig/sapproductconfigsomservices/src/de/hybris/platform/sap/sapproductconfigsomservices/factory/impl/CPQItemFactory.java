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
package de.hybris.platform.sap.sapproductconfigsomservices.factory.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;


/* (non-Javadoc)
 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
 */
public class CPQItemFactory implements ItemFactory
{
	/*
	 * (non-Javadoc)
	 * @see de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory#createItem()
	 */
	@Override
	public Item createItem()
	{
		return new CPQItemSalesDoc();
	}

}
