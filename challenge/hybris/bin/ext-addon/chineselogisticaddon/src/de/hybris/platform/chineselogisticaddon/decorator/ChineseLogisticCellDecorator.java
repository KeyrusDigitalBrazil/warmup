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
package de.hybris.platform.chineselogisticaddon.decorator;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.CSVCellDecorator;
import de.hybris.platform.util.FlexibleSearchUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Intercept component to-be-inserted into order detail body content slot, put it into the desired position of the
 * existing component sequence
 *
 */
public class ChineseLogisticCellDecorator implements CSVCellDecorator
{
	private static final Logger LOG = Logger.getLogger(ChineseLogisticCellDecorator.class);

	private static final String FLEXIBLE_SEARCH_SERVICE_BEAN = "flexibleSearchService";
	private static final String CATALOG_VERSION_SERVICE_BEAN = "catalogVersionService";
	private static final String ORDER_DETAIL_BODY_SLOT = "BodyContent-orderdetail";

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return (FlexibleSearchService) Registry.getApplicationContext().getBean(FLEXIBLE_SEARCH_SERVICE_BEAN);
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return (CatalogVersionService) Registry.getApplicationContext().getBean(CATALOG_VERSION_SERVICE_BEAN);
	}

	@Override
	public String decorate(final int position, final Map<Integer, String> impexLine)
	{
		final String impexContent = impexLine.get(Integer.valueOf(position));
		final String[] components = StringUtils.split(impexContent, ",");
		final String newComponent = components[1], previousComponent = components[0];

		final StringBuilder queryBuilder = new StringBuilder();
		final Map<String, Object> queryParameters = new HashMap<>();
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<>();

		final CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion("electronicsContentCatalog",
				"Staged");
		catalogVersions.add(catalogVersionModel);
		queryBuilder.append("SELECT {pk} FROM {ContentSlot} WHERE {uid} =?uid AND ");
		queryBuilder.append(FlexibleSearchUtils.buildOracleCompatibleCollectionStatement("{catalogVersion} in (?catalogVersions)",
				"catalogVersions", "OR", catalogVersions,
				queryParameters));
		queryParameters.put("uid", ORDER_DETAIL_BODY_SLOT);
		final SearchResult<ContentSlotModel> result = getFlexibleSearchService().search(queryBuilder.toString(), queryParameters);
		final ContentSlotModel orderDetailBodyContentSlot = result.getResult().get(0);
		final List<String> orderDetailComponents = orderDetailBodyContentSlot.getCmsComponents().stream()
				.map(AbstractCMSComponentModel::getUid).collect(Collectors.toList());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Initialization:" + ChineseLogisticCellDecorator.class.getName() + " found components are "
					+ orderDetailComponents.toString());
		}
		orderDetailComponents.remove(newComponent);

		final int firstIndex = orderDetailComponents.indexOf(previousComponent);
		if (firstIndex == -1)
		{
			throw new IllegalArgumentException(ORDER_DETAIL_BODY_SLOT + " does not contain " + previousComponent);
		}
		orderDetailComponents.add(firstIndex + 1, newComponent);

		return StringUtils.join(orderDetailComponents, ",");
	}
}
