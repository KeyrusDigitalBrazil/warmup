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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;


import javax.annotation.Nonnull;
import java.util.List;


/**
 * Data Access Object for looking up items related to {@link AbstractBundleRuleModel}.
 * 
 * @spring.bean bundleRuleDao
 */
public interface BundleRuleDao<R extends AbstractBundleRuleModel>
{
	/**
	 * Find all {@link AbstractBundleRuleModel}s that have the given <code>targetProduct</code> as target product.
	 * 
	 * @param targetProduct
	 *           the target {@link ProductModel} of the bundle rule
	 * @return {@link List} of {@link AbstractBundleRuleModel}s or empty {@link List}.
	 */
	@Nonnull
	List<R> findBundleRulesByTargetProduct(@Nonnull ProductModel targetProduct);

	/**
	 * Find all {@link AbstractBundleRuleModel}s that are assigned to the given <code>bundleTemplate</code> and have the
	 * given <code>targetProduct</code> as target product.
	 * 
	 * @param targetProduct
	 *           the target {@link ProductModel} of the bundle rule
	 * @param bundleTemplate
	 *           the {@link BundleTemplateModel} the bundle rule is assigned to
	 * @return {@link List} of {@link AbstractBundleRuleModel}s or empty {@link List}.
	 */
	@Nonnull
	List<R> findBundleRulesByTargetProductAndTemplate(@Nonnull ProductModel targetProduct,@Nonnull BundleTemplateModel bundleTemplate);

	/**
	 * Find all {@link AbstractBundleRuleModel}s that are assigned to any of the given <code>rootBundleTemplate</code>'s
	 * child components and have the given <code>product</code> either as target product or as conditional product.
	 * 
	 * @param product
	 *           the {@link ProductModel} that is added to cart
	 * @param rootBundleTemplate
	 *           the root {@link BundleTemplateModel}. Only {@link DisableProductBundleRuleModel}s that are assigned to
	 *           any of the <code>rootBundleTemplate</code>'s child components, are retrieved.
	 * @return {@link List} of {@link AbstractBundleRuleModel}s or empty {@link List}.
	 */
	@Nonnull
	List<R> findBundleRulesByProductAndRootTemplate(@Nonnull final ProductModel product,@Nonnull final BundleTemplateModel rootBundleTemplate);
}
