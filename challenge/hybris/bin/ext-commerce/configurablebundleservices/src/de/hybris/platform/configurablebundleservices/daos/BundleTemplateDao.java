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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Data Access Object for looking up items related to {@link BundleTemplateModel}.
 * 
 * @spring.bean bundleTemplateDao
 */

public interface BundleTemplateDao
{

	/**
	 * This method returns the {@code BundleTemplateModel} corresponding to the bundleID
	 * 
	 * @param bundleId
	 * @return {@link BundleTemplateModel}
	 */
	@Nonnull
	BundleTemplateModel findBundleTemplateById(String bundleId);

	/**
	 * This method returns the {@code BundleTemplateModel} corresponding to the bundleID and version
	 * 
	 * @param bundleId
	 * @param version
	 *           Bundle Version
	 * @return {@link BundleTemplateModel}
	 */
	@Nonnull
	BundleTemplateModel findBundleTemplateByIdAndVersion(String bundleId, String version);

	/**
	 * Finds the {@link BundleTemplateModel}s for the given <code>productModel</code>. A {@link BundleTemplateModel} is
	 * selected if it is a child bundle template and the given <code>productModel</code> is in the bundle template's
	 * products list.
	 * 
	 * @param productModel
	 *           the product
	 * @return {@link List} of {@link BundleTemplateModel}s or empty {@link List}.
	 */
	@Nonnull
	List<BundleTemplateModel> findBundleTemplatesByProduct(ProductModel productModel);

	/**
	 * Finds the distinct {@link BundleTemplateModel}s in the order entries that match the given filter criteria
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order the bundle is in
	 * @param bundleNo
	 *           the number of the bundle to which the bundle templates belong
	 * @return {@link List} of {@link BundleTemplateModel}s or empty {@link List}.
	 */
	@Nonnull
	List<BundleTemplateModel> findTemplatesByMasterOrderAndBundleNo(AbstractOrderModel masterAbstractOrder, int bundleNo);

	/**
	 * Find all {@link BundleTemplateModel}s.
	 * 
	 * @param catalogVersion
	 *           the catalogversion the bundletemplate is in
	 * 
	 * @return {@link List} of {@link BundleTemplateModel}s
	 */
	@Nonnull
	List<BundleTemplateModel> findAllRootBundleTemplates(final CatalogVersionModel catalogVersion);

	/**
	 * Find all approved {@link BundleTemplateModel}s.
	 * 
	 * @param catalogVersion
	 *           the catalogversion the bundletemplate is in
	 * 
	 * @return {@link List} of {@link BundleTemplateModel}s
	 */
	@Nonnull
	List<BundleTemplateModel> findAllApprovedRootBundleTemplates(CatalogVersionModel catalogVersion);

	/**
	 * Find all {@link AbstractOrderEntryModel}s the given {@link BundleTemplateModel} is assigned to
	 * 
	 * @param bundleTemplate
	 *           the bundletemplate
	 * 
	 * @return {@link List} of {@link AbstractOrderEntryModel}s
	 */
	@Nonnull
	List<AbstractOrderEntryModel> findAbstractOrderEntriesByBundleTemplate(BundleTemplateModel bundleTemplate);
}
