/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.stocknotificationservices.dao;

import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;

import java.util.List;


/**
 * manipulate ProductInterests whose notification type is BACK_IN_STOCK
 */
public interface BackInStockProductInterestDao
{

	/**
	 * retrieve such ProductInterests whose notification type is BACK_IN_STOCK and not expired
	 *
	 * @return The list of ProductInterests to send BACK_IN_STOCK notification
	 *
	 */
	List<ProductInterestModel> findBackInStorkProductInterests();

}
