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

package de.hybris.platform.configurablebundleservices.daos;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Data Access Object for looking up items related to {@link ChangeProductPriceBundleRuleModel}.
 * 
 * @spring.bean changeProductPriceBundleRuleDao
 */
public interface ChangeProductPriceBundleRuleDao extends BundleRuleDao<ChangeProductPriceBundleRuleModel>
{
	/**
	 * Find all {@link ChangeProductPriceBundleRuleModel}s that have the given <code>targetProduct</code> as target
	 * product.
	 * 
	 * @param targetProduct
	 *           the target {@link ProductModel} of the bundle rule
	 * @param currency
	 *           the currency of the rule's price
	 * @return {@link List} of {@link ChangeProductPriceBundleRuleModel}s or empty {@link List}.
	 */
	@Nonnull
	List<ChangeProductPriceBundleRuleModel> findBundleRulesByTargetProductAndCurrency(ProductModel targetProduct,
			CurrencyModel currency);

	/**
	 * Find all {@link ChangeProductPriceBundleRuleModel}s that are assigned to the given <code>bundleTemplate</code> and
	 * have the given <code>targetProduct</code> as target product.
	 * 
	 * @param targetProduct
	 *           the target {@link ProductModel} of the bundle rule
	 * @param bundleTemplate
	 *           the {@link BundleTemplateModel} the bundle rule is assigned to
	 * @param currency
	 *           the currency of the rule's price
	 * @return {@link List} of {@link ChangeProductPriceBundleRuleModel}s or empty {@link List}.
	 */
	@Nonnull
	List<ChangeProductPriceBundleRuleModel> findBundleRulesByTargetProductAndTemplateAndCurrency(
			@Nonnull ProductModel targetProduct,
			@Nonnull BundleTemplateModel bundleTemplate,
			@Nonnull CurrencyModel currency);
}