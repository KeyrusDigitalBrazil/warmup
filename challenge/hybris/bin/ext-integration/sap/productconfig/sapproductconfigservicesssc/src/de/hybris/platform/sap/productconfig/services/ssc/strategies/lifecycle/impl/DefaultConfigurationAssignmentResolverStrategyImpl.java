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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAssignmentResolverStrategy;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class DefaultConfigurationAssignmentResolverStrategyImpl implements ConfigurationAssignmentResolverStrategy
{
	private SessionAccessService sessionAccessService;
	private FlexibleSearchService flexibleSearchService;

	@Override
	public ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final String configId)
	{
		return ProductConfigurationRelatedObjectType.UNKNOWN;
	}

	@Override
	public Date retrieveCreationDateForRelatedEntry(final String configId)
	{
		return null;
	}

	@Override
	public ProductConfigurationRelatedObjectType retrieveRelatedObjectType(final AbstractOrderModel order)
	{
		return ProductConfigurationRelatedObjectType.UNKNOWN;
	}

	@Override
	public String retrieveRelatedProductCode(final String configId)
	{
		String productCode = null;
		final AbstractOrderEntryModel entry = retrieveOrderEntry(configId);
		if (entry != null)
		{
			productCode = entry.getProduct().getCode();
		}
		else
		{
			productCode = getSessionAccessService().getProductForConfigId(configId);
		}
		return productCode;
	}

	protected AbstractOrderEntryModel retrieveOrderEntry(final String configId)
	{
		AbstractOrderEntryModel entry = null;
		String entryId = getSessionAccessService().getCartEntryForDraftConfigId(configId);
		if (StringUtils.isEmpty(entryId))
		{
			entryId = getSessionAccessService().getCartEntryForConfigId(configId);
		}
		if (StringUtils.isNotEmpty(entryId))
		{
			final String query = "SELECT {pk} from {AbstractOrderEntry} WHERE {pk} = ?entryId";
			final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query, Collections.singletonMap("entryId", entryId));
			final SearchResult<AbstractOrderEntryModel> searchResult = flexibleSearchService.search(searchQuery);
			if (searchResult != null && CollectionUtils.isNotEmpty(searchResult.getResult()))
			{
				entry = searchResult.getResult().get(0);
			}
		}
		return entry;
	}

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
