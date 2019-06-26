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

package de.hybris.platform.configurablebundleservices.bundle;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

/**
 * Checks if a bundle component ({@link BundleTemplateModel}) can be edited
 */
public interface AbstractBundleComponentEditableChecker<O extends AbstractOrderModel>
{
	/**
	 * Checks if the given component <code>bundleTemplate</code> in given bundle <code>bundleNo</code> and
	 * <code>masterAbstractOrder</code> can be edited. In case the given <code>bundleTemplate</code>'s selection criteria
	 * is of auto pick type, it is never editable. The check is based on the selection
	 * dependencies of the given <code>bundleTemplate</code>. Only if all requirements of those components, the given
	 * component is dependent on, are met, the given component should be open for modifications.
	 * 
	 * @param masterAbstractOrder
	 *           the master order/cart the bundle is in
	 * @param bundleTemplate
	 *           the component that is checked whether it can be edited
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> belongs to
	 * @return <code>true</code> if the selection dependencies are fulfilled and the component can be edited, otherwise
	 *         <code>false</code>
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	boolean canEdit(@Nonnull O masterAbstractOrder,@Nullable BundleTemplateModel bundleTemplate, int bundleNo);

	/**
	 * Checks if the selection dependency of the given component <code>bundleTemplate</code> in given bundle
	 * <code>bundleNo</code> and <code>masterAbstractOrder</code> is fulfilled.
	 * 
	 * @param masterAbstractOrder
	 *           the master order/cart the bundle is in
	 * @param bundleTemplate
	 *           the component whose selection dependency is checked
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> belongs to
	 * @throws CommerceCartModificationException
	 *            if the given <code>bundleTemplate</code>'s selection dependency is not met
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	void checkIsComponentDependencyMet(@Nonnull O masterAbstractOrder,@Nullable BundleTemplateModel bundleTemplate, int bundleNo)
			throws CommerceCartModificationException;

	/**
	 * Checks if the selection dependency of the given component <code>bundleTemplate</code> in given bundle
	 * <code>bundleNo</code> and <code>masterAbstractOrder</code> is fulfilled.
	 * 
	 * @param masterAbstractOrder
	 *           the master order/cart the bundle is in
	 * @param bundleTemplate
	 *           the component whose selection dependency is checked
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> belongs to
	 * @return <code>true</code> if selection dependency is met, otherwise <code>false</code>
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	boolean isComponentDependencyMet(@Nonnull O masterAbstractOrder,@Nullable BundleTemplateModel bundleTemplate, int bundleNo);

	/**
	 * Checks if the selection dependency of the given component <code>bundleTemplate</code> in given bundle
	 * <code>bundleTemplate</code> and <code>order</code> is fulfilled. Returns the result of that check instead
	 * of throwing an exception.
	 *
	 * @param order
	 *           the order specified bundle is a part of
	 * @param bundleTemplate
	 *           the component to check selection dependency for
	 * @param entryGroupNumber
	 *           number of the entry group created from bundle template
	 */
	boolean isRequiredDependencyMet(@Nonnull final O order, @Nonnull final BundleTemplateModel bundleTemplate,
			@Nonnull final Integer entryGroupNumber);

	/**
	 * Checks if {@link BundleTemplateModel} has a selection criteria of auto pick type.
	 * 
	 * @param bundleTemplate
	 * @return <code>true</code> if selection criteria has auto pick type, otherwise
	 *         <code>false</code>
	 * @deprecated since 1811 - auto pick components were moved to subscriptionbundles
	 */
	@Deprecated
	boolean isAutoPickComponent(@Nullable final BundleTemplateModel bundleTemplate);

	/**
	 * Checks if the given <code>bundleTemplate</code>'s selection criteria are fulfilled, neither exceeded nor under
	 * threshold. Throws an exception of selection criteria is not met.
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order
	 * @param bundleTemplate
	 *           the bundle template/component whose selection criteria are checked
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> is in
	 * @throws CommerceCartModificationException
	 *            if selection criteria is not fulfilled
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	void checkIsComponentSelectionCriteriaMet(@Nonnull O masterAbstractOrder, BundleTemplateModel bundleTemplate, int bundleNo)
			throws CommerceCartModificationException;

	/**
	 * Checks if the given <code>bundleTemplate</code>'s selection criteria are fulfilled, neither exceeded nor under
	 * threshold.
	 * 
	 * @param masterAbstractOrder
	 *           the master cart/order
	 * @param bundleTemplate
	 *           the bundle template/component whose selection criteria are checked
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> is in
	 * 
	 * @return <code>true</code> if selection criteria is met, otherwise <code>false</code>
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	boolean isComponentSelectionCriteriaMet(@Nonnull O masterAbstractOrder, BundleTemplateModel bundleTemplate, int bundleNo);
}
