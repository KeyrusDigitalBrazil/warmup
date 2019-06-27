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
package de.hybris.platform.sap.sapcarcommercefacades.order;

import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderEntryData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryBase;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;

import java.util.List;


/**
 * 
 */
public interface CarOrderConverter
{


	/**
	 * @param orderList
	 */
	void convertOrders(List<CarOrderHistoryData> orderList);



	/**
	 * @param order
	 */
	void convertOrder(CarOrderHistoryData order);

	/**
	 * @param orderEntries
	 */
	void convertOrderEntries(List<CarOrderEntryData> orderEntries);

	/**
	 * @param order
	 */
	void convertOrderBase(CarOrderHistoryBase order);


	/**
	 * @param orderList
	 */
	void convertOrdersBase(List<? extends CarOrderHistoryBase> orderList);

	/**
	 * @param order
	 */
	void convertOrder(CarMultichannelOrderHistoryData order);

}
