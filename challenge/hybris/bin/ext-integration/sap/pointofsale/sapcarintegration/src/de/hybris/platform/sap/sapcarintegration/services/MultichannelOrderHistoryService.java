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
package de.hybris.platform.sap.sapcarintegration.services;

import java.util.List;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.data.CarMultichannelOrderHistoryData;


/**
 * Interface to provide Order History data from a Multichannel configured CAR instance
 */
public interface MultichannelOrderHistoryService extends CarOrderHistoryService
{

	/**
	 * reads the all transactions (hybris + pos + SD) for a given customer
	 * 
	 * @param customerNumber
	 * @param paginationData
	 * @return {@link List<CarMultichannelOrderHistoryData>}
	 */
	abstract List<CarMultichannelOrderHistoryData> readMultiChannelTransactionsForCustomer(String customerNumber,
			PaginationData paginationData);

	/**
	 * read detail for sales document
	 * 
	 * @param customerNumber
	 * @param transactionNumber
	 * @return {@link CarMultichannelOrderHistoryData}
	 */
	abstract CarMultichannelOrderHistoryData readSalesDocumentDetails(String customerNumber, String transactionNumber);
}
