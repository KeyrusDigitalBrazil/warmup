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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.configurablebundleservices.daos.ChangeProductPriceBundleRuleDao;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation of the {@link ChangeProductPriceBundleRuleDao}
 */
public class DefaultChangeProductPriceBundleRuleDao extends AbstractBundleRuleDao<ChangeProductPriceBundleRuleModel> implements
		ChangeProductPriceBundleRuleDao
{

	private static final String FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_QUERY = "SELECT {rule:" + ChangeProductPriceBundleRuleModel.PK
			+ "} FROM {" + ChangeProductPriceBundleRuleModel._TYPECODE + " AS rule JOIN "
			+ ProductModel._ABSTRACTBUNDLERULESTARGETPRODUCTSRELATION + " AS targetRel ON {targetRel:source}={rule:"
			+ ChangeProductPriceBundleRuleModel.PK + "}} WHERE {targetRel:target}=?product";

	private static final String FIND_PRICE_RULES_BY_TARGET_PRODUCT_AND_CURRENCY_QUERY = FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_QUERY
			+ " AND {rule:" + ChangeProductPriceBundleRuleModel.CURRENCY + "}=?currency";

	private static final String FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_QUERY = FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_QUERY
			+ " AND {rule:" + ChangeProductPriceBundleRuleModel.BUNDLETEMPLATE + "}=?bundleTemplate";

	private static final String FIND_PRICE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_AND_CURRENCY_QUERY = FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_QUERY
			+ " AND {rule:" + ChangeProductPriceBundleRuleModel.CURRENCY + "}=?currency";

	private static final String FIND_BUNDLE_RULES_BY_PRODUCT_AND_ROOT_TEMPLATE_QUERY = "SELECT DISTINCT {rule:"
			+ ChangeProductPriceBundleRuleModel.PK + "} FROM {" + ChangeProductPriceBundleRuleModel._TYPECODE + " AS rule JOIN "
			+ BundleTemplateModel._TYPECODE + " AS templ ON {templ:PK}={rule:" + ChangeProductPriceBundleRuleModel.BUNDLETEMPLATE
			+ "} JOIN " + ProductModel._ABSTRACTBUNDLERULESTARGETPRODUCTSRELATION
			+ " AS targetRel ON {targetRel:source}={rule:PK} JOIN " + ProductModel._ABSTRACTBUNDLERULESCONDITIONALPRODUCTSRELATION
			+ " AS condRel ON {condRel:source}={rule:PK}}" + " WHERE {templ:parentTemplate}=?rootBundleTemplate AND "
			+ " ({targetRel:target}=?product OR {condRel:target}=?product)";

	@Override
	public String getFindBundleRulesByTargetProductQuery()
	{
		return FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_QUERY;
	}

	@Override
	public String getFindBundleRulesByTargetProductAndTemplateQuery()
	{
		return FIND_BUNDLE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_QUERY;
	}

	@Override
	public String getFindBundleRulesByProductAndRootTemplateQuery()
	{
		return FIND_BUNDLE_RULES_BY_PRODUCT_AND_ROOT_TEMPLATE_QUERY;
	}

	@Override
	@Nonnull
	public List<ChangeProductPriceBundleRuleModel> findBundleRulesByTargetProductAndCurrency(@Nonnull final ProductModel targetProduct,
																							 @Nonnull final CurrencyModel currency)
	{
		validateParameterNotNullStandardMessage("product", targetProduct);
		validateParameterNotNullStandardMessage("currency", currency);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("product", targetProduct);
		params.put("currency", currency);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_PRICE_RULES_BY_TARGET_PRODUCT_AND_CURRENCY_QUERY, params);

		final SearchResult<ChangeProductPriceBundleRuleModel> results = search(flexibleSearchQuery);
		return results.getResult();
	}

	@Override
	@Nonnull
	public List<ChangeProductPriceBundleRuleModel> findBundleRulesByTargetProductAndTemplateAndCurrency(
			@Nonnull final ProductModel targetProduct,
			@Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final CurrencyModel currency)
	{
		validateParameterNotNullStandardMessage("targetProduct", targetProduct);
		validateParameterNotNullStandardMessage("bundleTemplate", bundleTemplate);
		validateParameterNotNullStandardMessage("currency", currency);

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("bundleTemplate", bundleTemplate);
		params.put("product", targetProduct);
		params.put("currency", currency);

		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				FIND_PRICE_RULES_BY_TARGET_PRODUCT_AND_TEMPLATE_AND_CURRENCY_QUERY, params);

		final SearchResult<ChangeProductPriceBundleRuleModel> results = search(flexibleSearchQuery);
		return results.getResult();
	}

}
