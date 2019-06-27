/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingfacades.warehouse;

import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;


/**
 * Warehousing facade exposing CRUD operations on {@link de.hybris.platform.ordersplitting.model.WarehouseModel}
 */
public interface WarehousingWarehouseFacade
{
	/**
	 * API to get the warehouse for the code
	 *
	 * @param code
	 * 		the code of warehouse to search
	 * @return the warehouse {@link WarehouseData} for the passed code
	 */
	WarehouseData getWarehouseForCode(String code);
}
