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
package de.hybris.platform.chineseproductsharingaddon.decorator;

import de.hybris.platform.acceleratorcms.model.actions.AbstractCMSActionModel;
import de.hybris.platform.acceleratorcms.model.components.ProductAddToCartComponentModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
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
import org.springframework.util.CollectionUtils;

/**
 * Intercept component to-be-inserted into order detail body content slot, put it into the desired position of the
 * existing component sequence
 *
 */
public class MarketplaceProductSharingCellDecorator implements CSVCellDecorator
{
	private static final String JIATHIS_ACTION = "JiaThisAction";
	@Override
	public String decorate(final int position, final Map<Integer, String> impexLine)
	{

		final FlexibleSearchService flexibleSearchService = (FlexibleSearchService) Registry.getApplicationContext().getBean("flexibleSearchService");
		final CatalogVersionService catalogVersionService = (CatalogVersionService) Registry.getApplicationContext().getBean("catalogVersionService");

		final String impexContent = impexLine.get(Integer.valueOf(position));
		final String[] actions = StringUtils.split(impexContent, ",");
		final String oldSharingAction = actions[0];
		final String newSharingAction = actions[1];

		final StringBuilder fql = new StringBuilder();
		final Map<String, Object> params = new HashMap<String, Object>();
		final Collection<CatalogVersionModel> catalogVersions = new ArrayList<CatalogVersionModel>();

		CatalogVersionModel catalogVersionModel = null;
		try
		{
			catalogVersionModel = catalogVersionService.getCatalogVersion("marketplaceContentCatalog", "Staged");
		}
		catch (UnknownIdentifierException e)
		{
			return impexContent;
		}
		catalogVersions.add(catalogVersionModel);

		fql.append("SELECT {pk} FROM {ProductAddToCartComponent} WHERE {uid} =?uid AND ");
		fql.append(FlexibleSearchUtils.buildOracleCompatibleCollectionStatement("{catalogVersion} in (?catalogVersions)",
				"catalogVersions", "OR", catalogVersions, params));
		params.put("uid", "AddToCart");

		final SearchResult<ProductAddToCartComponentModel> result = flexibleSearchService.search(fql.toString(), params);
		if (!CollectionUtils.isEmpty(result.getResult()))
		{
			final ProductAddToCartComponentModel productAddToCartComponent = result.getResult().get(0);
			final List<String> addToCartActions = productAddToCartComponent.getActions().stream()
					.map(AbstractCMSActionModel::getUid).collect(Collectors.toList());

			addToCartActions.remove(oldSharingAction);
			addToCartActions.remove(JIATHIS_ACTION);
			if (!addToCartActions.contains(newSharingAction))
			{
				addToCartActions.add(newSharingAction);
			}

			return StringUtils.join(addToCartActions, ",");
		}

		return "";
	}
}
