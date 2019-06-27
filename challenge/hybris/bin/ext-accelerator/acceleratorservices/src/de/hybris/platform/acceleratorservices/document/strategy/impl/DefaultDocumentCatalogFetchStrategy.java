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
package de.hybris.platform.acceleratorservices.document.strategy.impl;

import de.hybris.platform.acceleratorservices.document.strategy.DocumentCatalogFetchStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default strategy for fetching CatalogVersionModel for given businessProcessModel.
 */
public class DefaultDocumentCatalogFetchStrategy implements DocumentCatalogFetchStrategy
{

	@Override
	public CatalogVersionModel fetch(final BusinessProcessModel businessProcessModel)
	{
		validateParameterNotNull(businessProcessModel, "businessProcessModel must not be null");
		OrderModel order = null;

		if (businessProcessModel instanceof OrderProcessModel)
		{
			order = ((OrderProcessModel) businessProcessModel).getOrder();
		}
		else if (businessProcessModel instanceof ConsignmentProcessModel)
		{
			order = (OrderModel) ((ConsignmentProcessModel) businessProcessModel).getConsignment().getOrder();
		}
		else if (businessProcessModel instanceof ReturnProcessModel)
		{
			order = ((ReturnProcessModel) businessProcessModel).getReturnRequest().getOrder();
		}

		validateParameterNotNull(order, "Order cannot be null");
		Assert.isTrue(order.getSite() instanceof CMSSiteModel, "No CMSSite found for the order");	// NOSONAR
		final List<ContentCatalogModel> contentCatalogs = ((CMSSiteModel) order.getSite()).getContentCatalogs();
		Assert.isTrue(CollectionUtils.isNotEmpty(contentCatalogs), "Catalog Version cannot be found for the order");

		return contentCatalogs.get(0).getActiveCatalogVersion(); // NOSONAR
	}

}
