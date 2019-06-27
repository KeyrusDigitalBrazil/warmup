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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Data Access Object for looking up items related to {@link AbstractOrderEntryModel}.
 * 
 * @spring.bean orderEntryDao
 */
public interface OrderEntryDao<O extends AbstractOrderModel, E extends AbstractOrderEntryModel>
{
	/**
	 * Find {@link AbstractOrderEntryModel}s that match the given <code>masterAbstractOrder</code> and
	 * <code>bundleNo</code>
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order of the {@link AbstractOrderEntryModel}.
	 * 
	 * @param bundleNo
	 *           the bundle number the {@link AbstractOrderEntryModel} belongs to
	 * 
	 * @return {@link List} of {@link AbstractOrderEntryModel}s or empty {@link List}.
	 */
	@Nonnull
	List<E> findEntriesByMasterCartAndBundleNo(@Nonnull final O masterAbstractOrder, int bundleNo);

	/**
	 * Find {@link AbstractOrderEntryModel}s that match the given <code>masterAbstractOrder</code> and
	 * <code>bundleNo</code> and <code>bundleTemplate</code>
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order of the {@link AbstractOrderEntryModel}.
	 * @param bundleNo
	 *           the bundle number the {@link AbstractOrderEntryModel} belongs to
	 * @param bundleTemplate
	 *           the bundle template based on which the {@link AbstractOrderEntryModel} was added to the cart
	 * 
	 * @return {@link List} of {@link AbstractOrderEntryModel}s or empty {@link List}.
	 */
	@Nonnull
	List<E> findEntriesByMasterCartAndBundleNoAndTemplate(@Nonnull final O masterAbstractOrder, int bundleNo,
														  @Nonnull BundleTemplateModel bundleTemplate);

	/**
	 * Find {@link AbstractOrderEntryModel}s that match the given <code>masterAbstractOrder</code> and
	 * <code>bundleNo</code> and <code>product</code>
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order of the {@link AbstractOrderEntryModel}.
	 * @param bundleNo
	 *           the bundle number the {@link AbstractOrderEntryModel} belongs to
	 * @param product
	 *           the product in the cart abstract order entry
	 * 
	 * @return {@link List} of {@link AbstractOrderEntryModel}s or empty {@link List}.
	 */
	@Nonnull
	List<E> findEntriesByMasterCartAndBundleNoAndProduct(@Nonnull final O masterAbstractOrder, int bundleNo,
														 @Nonnull ProductModel product);

}
