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

import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.springframework.util.StringUtils;

import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.sap.sapcarintegration.constants.SapcarintegrationConstants;
import de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService;


/**
 * Default concrete implementation to provide business logic for
 * {@link de.hybris.platform.sap.sapcarintegration.services.MultichannelDataProviderService}
 */
public class DefaultMultichannelDataProviderService extends DefaultCarDataProviderService
		implements MultichannelDataProviderService
{


	@Override
	public ODataFeed readMultiChannelTransactionsFeed(String customerNumber, PaginationData paginationData)
	{
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");

		queryFilter.append("&$skip").append("=").append(paginationData.getCurrentPage() * paginationData.getPageSize());
		queryFilter.append("&$top").append("=").append(paginationData.getPageSize());
		queryFilter.append("&$inlinecount").append("=").append("allpages");

		if (!StringUtils.isEmpty(paginationData.getSort()))
		{

			if (paginationData.getSort().contentEquals("byDate"))
			{

				queryFilter.append("&$orderby").append("=").append("CreationDate desc");

			}
			else if (paginationData.getSort().contentEquals("byOrderNumber"))
			{
				queryFilter.append("&$orderby").append("=").append("TransactionNumber desc");
			}

		}


		return readFeed(getServiceURI(), SapcarintegrationConstants.APPLICATION_JSON,
				SapcarintegrationConstants.MCSALES_ENTITYSETNAME_QUERY, SapcarintegrationConstants.MCSALES_ENTITYSETNAME_RESULT,
				SapcarintegrationConstants.SELECT_MCSALES_HEADER, queryFilter.toString(), null);
	}

	@Override
	public ODataFeed readSalesDocumentHeaderFeed(String customerNumber, String transactionNumber)
	{
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		queryFilter.append(" and ").append("TransactionNumber").append(" eq ").append("'").append(transactionNumber).append("'");

		return readFeed(getServiceURI(), SapcarintegrationConstants.APPLICATION_JSON,
				SapcarintegrationConstants.MCSALES_ENTITYSETNAME_QUERY, SapcarintegrationConstants.MCSALES_ENTITYSETNAME_RESULT,
				SapcarintegrationConstants.SELECT_MCSALES_HEADER, queryFilter.toString(), null);
	}

	@Override
	public ODataFeed readSalesDocumentItemFeed(String customerNumber, String transactionNumber)
	{
		StringBuilder queryFilter = new StringBuilder();
		queryFilter.append("CustomerNumber").append(" eq ").append("'").append(convertToInternalKey(customerNumber)).append("'");
		queryFilter.append(" and ").append("TransactionNumber").append(" eq ").append("'").append(transactionNumber).append("'");

		return readFeed(getServiceURI(), SapcarintegrationConstants.APPLICATION_JSON,
				SapcarintegrationConstants.MCSALES_ENTITYSETNAME_QUERY, SapcarintegrationConstants.MCSALES_ENTITYSETNAME_RESULT,
				SapcarintegrationConstants.SELECT_MCSALES_ITEM, queryFilter.toString(), null);
	}


}
