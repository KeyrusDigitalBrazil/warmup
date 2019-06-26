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
package de.hybris.platform.sap.sapcarintegration.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarOrderHistoryData;
import de.hybris.platform.sap.sapcarintegration.services.CarDataProviderService;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryExtractorService;
import de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryService;
import de.hybris.platform.sap.sapcarintegration.utils.DateUtil;


/**
 * Default concrete implementation to provide business logic for
 * {@link de.hybris.platform.sap.sapcarintegration.services.CarOrderHistoryService}
 */
public class DefaultCarOrderHistoryService implements CarOrderHistoryService
{

	private CarDataProviderService carDataProviderService;

	private CarOrderHistoryExtractorService carOrderHistoryExtractorService;


	public CarOrderHistoryExtractorService getCarOrderHistoryExtractorService()
	{
		return carOrderHistoryExtractorService;
	}

	@Required
	public void setCarOrderHistoryExtractorService(CarOrderHistoryExtractorService carOrderHistoryExtractorService)
	{
		this.carOrderHistoryExtractorService = carOrderHistoryExtractorService;
	}

	public CarDataProviderService getCarDataProviderService()
	{
		return carDataProviderService;
	}

	@Required
	public void setCarDataProviderService(CarDataProviderService carDataProviderService)
	{
		this.carDataProviderService = carDataProviderService;
	}


	@Override
	public List<CarOrderHistoryData> readOrdersForCustomer(String customerNumber, PaginationData paginationData)
	{

		return getCarOrderHistoryExtractorService()
				.extractOrders(getCarDataProviderService().readHeaderFeed(customerNumber, paginationData), paginationData);

	}

	@Override
	public CarOrderHistoryData readOrderDetails(String businessDayDate, String storeId, Integer transactionIndex,
			String customerNumber)
	{
		// read and extract header data
		CarOrderHistoryData order = getCarOrderHistoryExtractorService()
				.extractOrder(getCarDataProviderService().readHeaderFeed(businessDayDate, storeId, transactionIndex, customerNumber));

		// read and extract item data
		getCarOrderHistoryExtractorService().extractOrderEntries(order,
				getCarDataProviderService().readItemFeed(businessDayDate, storeId, transactionIndex, customerNumber));

		// read store location
		order.getStore().setAddress(
				getCarOrderHistoryExtractorService().extractStoreLocation(getCarDataProviderService().readLocaltionFeed(storeId)));

		return order;
	}






}
