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

package de.hybris.platform.configurablebundleservices.daos.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.configurablebundleservices.daos.BundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Abstract implementation of the {@link BundleRuleDao}.
 */
public abstract class AbstractBundleRuleDao<R extends AbstractBundleRuleModel> extends AbstractItemDao implements
		BundleRuleDao<R>
{

	@Override
	@Nonnull
	public List<R> findBundleRulesByTargetProductAndTemplate(@Nonnull final ProductModel targetProduct,
															 @Nonnull final BundleTemplateModel bundleTemplate)
	{
		validateParameterNotNullStandardMessage("targetProduct", targetProduct);
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("bundleTemplate", bundleTemplate);
		params.put("product", targetProduct);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				getFindBundleRulesByTargetProductAndTemplateQuery(), params);

		final SearchResult<R> results = search(flexibleSearchQuery);
		return results.getResult();
	}

	@Override
	@Nonnull
	public List<R> findBundleRulesByProductAndRootTemplate(@Nonnull final ProductModel product,
														   @Nonnull final BundleTemplateModel rootBundleTemplate)

	{
		validateParameterNotNullStandardMessage("product", product);
		validateParameterNotNullStandardMessage("rootBundleTemplate", rootBundleTemplate);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("rootBundleTemplate", rootBundleTemplate);
		params.put("product", product);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(getFindBundleRulesByProductAndRootTemplateQuery(),
				params);

		final SearchResult<R> results = search(flexibleSearchQuery);
		return results.getResult();
	}

	@Override
	@Nonnull
	public List<R> findBundleRulesByTargetProduct(@Nonnull final ProductModel targetProduct)
	{
		validateParameterNotNullStandardMessage("product", targetProduct);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("product", targetProduct);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(getFindBundleRulesByTargetProductQuery(), params);

		final SearchResult<R> results = search(flexibleSearchQuery);
		return results.getResult();
	}

	abstract public String getFindBundleRulesByTargetProductQuery();

	abstract public String getFindBundleRulesByTargetProductAndTemplateQuery();

	abstract public String getFindBundleRulesByProductAndRootTemplateQuery();
}
