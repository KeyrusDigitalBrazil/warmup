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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Hook interface for CartRestorationFacade
 *
 */
public interface CartRestorationFacadeHook
{

	/**
	 * @param entry
	 * @param entryModel
	 */
	void afterAddCartEntriesToStandardCart(OrderEntryData entry, AbstractOrderEntryModel entryModel);

}
