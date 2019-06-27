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

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Commerce service that exposes methods to find a matching rule for the product price
 * {@link ChangeProductPriceBundleRuleModel} or information about whether it is disabled
 * {@link DisableProductBundleRuleModel} when part of a bundle. The rules are depending on the other products in the
 * same bundle in the cart or newly products to add. These are the different ways on how to find a rule:
 * <ul>
 * <li>provisioning the rule based on two products that are not yet in the cart</li>
 * <li>finding the rule based only on products already in the cart in a bundle</li>
 * <li>combination of both: provisioning the rule based on an existing bundle in the cart and a new additional product</li>
 * </ul>
 */
public interface BundleRuleService
{
	/**
	 * Returns a {@link ChangeProductPriceBundleRuleModel} valid for the given <code>orderEntryModel</code>. The method
	 * calculates the lowest product price for the product in the given <code>orderEntryModel</code> which is added to a
	 * bundle. The calculation is based on the entry's bundle template and the other products in the same bundle
	 * (conditional products)
	 *
	 * @param orderEntryModel
	 *           entry that is added to the cart as part of a bundle
	 *
	 * @return a {@link ChangeProductPriceBundleRuleModel} if existing or null
	 */
	@Nullable
	ChangeProductPriceBundleRuleModel getChangePriceBundleRuleForOrderEntry(@Nonnull AbstractOrderEntryModel orderEntryModel);


	/**
	 * Returns a {@link ChangeProductPriceBundleRuleModel} valid for the given <code>targetProduct</code>. The method
	 * calculates (forecasts) the lowest product price for the given <code>targetProduct</code> which shall be added to a
	 * bundle based on the given bundle template (<code>targetComponent</code>) in combination with the given
	 * <code>conditionalProduct</code>. The bundle or a cart to which the given products should be added may or may not
	 * yet exist. The method just forecasts the possible price for the given combination of input parameters.
	 *
	 * @param targetComponent
	 *           bundle template based on which the given <code>targetProduct</code> is or shall be added to a bundle
	 * @param targetProduct
	 *           product that is added to a bundle and for which the product price is calculated
	 * @param conditionalProduct
	 *           second product that is added to the same bundle as the given <code>targetProduct</code> and that is the
	 *           condition for a {@link ChangeProductPriceBundleRuleModel} to be selected
	 * @param currency
	 *           the currency the {@link ChangeProductPriceBundleRuleModel} must match to be selected
	 *
	 * @return a {@link ChangeProductPriceBundleRuleModel} if existing or null
	 */
	@Nullable
	ChangeProductPriceBundleRuleModel getChangePriceBundleRule(@Nonnull BundleTemplateModel targetComponent,
			@Nonnull ProductModel targetProduct, @Nonnull ProductModel conditionalProduct, @Nonnull CurrencyModel currency);

	/**
	 * Returns a {@link DisableProductBundleRuleModel} valid for the given <code>product</code> ignoring products
	 * <code>ignoreProducts</code> in the cart. The method searches for {@link DisableProductBundleRuleModel}'s that can
	 * be applied if the given <code>product</code> is or would be added to the given <code>masterAbstractOrder</code> in
	 * the context of the given <code>bundleNo</code> and <code>bundleTemplate</code>.
	 *
	 * @param masterAbstractOrder
	 *           the master cart/order to which the given <code>product</code> is added
	 * @param product
	 *           product that is or shall be added to the bundle and for which applicable
	 *           {@link DisableProductBundleRuleModel}'s are searched
	 * @param bundleTemplate
	 *           bundle template based on which the given <code>product</code> is or shall be added to the bundle
	 * @param bundleNo
	 *           the number of the bundle to which the <code>product</code> is or shall be added to a bundle
	 * @param ignoreCurrentProducts
	 *           if the products in the current bundletemplate should be ignored
	 *
	 * @return the first {@link DisableProductBundleRuleModel} that is found which means that the given
	 *         <code>product</code> cannot be added to the bundle in the cart. <code>null</code> if no
	 *         {@link DisableProductBundleRuleModel} applies and the product can be added to the bundle.
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 */
	@Deprecated
	@Nullable
	DisableProductBundleRuleModel getDisableRuleForBundleProduct(@Nonnull final AbstractOrderModel masterAbstractOrder,
			 @Nonnull final ProductModel product,
			 @Nonnull final BundleTemplateModel bundleTemplate, final int bundleNo,
			 final boolean ignoreCurrentProducts);


	/**
	 * Returns a {@link ChangeProductPriceBundleRuleModel} valid for the given <code>targetProduct</code>. The method
	 * calculates (forecasts) the lowest product price for the given <code>targetProduct</code> which shall be added to a
	 * bundle based on the given bundle template (<code>targetComponent</code>). The price is retrieved from the
	 * {@link ChangeProductPriceBundleRuleModel}s that can be applied based on the other products in the same bundle. The
	 * method just forecasts the possible price for the given combination of input parameters.
	 *
	 * @param masterAbstractOrder
	 *           the master cart/order to which the given <code>targetProduct</code> shall be added
	 * @param targetProduct
	 *           product that shall be added to the bundle and for which applicable {@link DisableProductBundleRuleModel}
	 *           's are searched
	 * @param bundleTemplate
	 *           bundle template based on which the given <code>targetProduct</code> shall be added to the bundle
	 * @param bundleNo
	 *           the number of the bundle to which the <code>targetProduct</code> shall be added
	 *
	 * @return {@link ChangeProductPriceBundleRuleModel} if existing or null
	 * @deprecated since 6.5 - bundleNo parameter is deprecated, should be replaced with entry groups
	 *
	 */
	@Deprecated
	@Nullable
	ChangeProductPriceBundleRuleModel getChangePriceBundleRule(@Nonnull AbstractOrderModel masterAbstractOrder,
			@Nonnull BundleTemplateModel bundleTemplate, @Nonnull ProductModel targetProduct, int bundleNo);

	/**
	 * Returns a {@link DisableProductBundleRuleModel} which does not allow that the given products <code>product1</code>
	 * and <code>product2</code> are added together to a bundle. In case no such disable rule exists <code>null</code> is
	 * returned.
	 *
	 * @param bundleTemplate
	 *           bundle template based on which the given <code>targetProduct</code> and <code>conditionalProduct</code>
	 *           shall be added to the bundle
	 * @param product1
	 *           first product that shall be added to the bundle
	 * @param product2
	 *           second product that shall be added to the bundle
	 *
	 * @return the first {@link DisableProductBundleRuleModel} that is found which means that the given
	 *         <code>product1</code> and <code>product2</code> cannot be added together to the bundle in the cart.
	 *         <code>null</code> if no {@link DisableProductBundleRuleModel} applies and the products can be added to the
	 *         bundle.
	 */
	@Nullable
	DisableProductBundleRuleModel getDisableRuleForBundleProduct(@Nonnull final BundleTemplateModel bundleTemplate,
			 @Nonnull final ProductModel product1,@Nonnull final ProductModel product2);

	/**
	 * Searches for the lowest price {@link ChangeProductPriceBundleRuleModel} of the given <code>targetProduct</code> in
	 * any bundle package
	 *
	 * @param targetProduct
	 *           the product for which the lowest bundle price is searched
	 * @param currency
	 *           the currency the {@link ChangeProductPriceBundleRuleModel} must match to be selected
	 * @return {@link ChangeProductPriceBundleRuleModel} if existing or null
	 */
	@Nullable
	ChangeProductPriceBundleRuleModel getChangePriceBundleRuleWithLowestPrice(@Nonnull final ProductModel targetProduct,
			  @Nonnull final CurrencyModel currency);

	/**
	 * Generates the message explaining why given product is disabled with specific rule.
	 *
	 * @param disableRule
	 *           {@link DisableProductBundleRuleModel} which disabled given product for specific conditional products
	 * @param product
	 *           Disabled product
	 * @return Message about the disable rule for given product
	 */
	String createMessageForDisableRule(DisableProductBundleRuleModel disableRule, ProductModel product);


	/**
	 * Returns a list of {@link DisableProductBundleRuleModel} valid for the given <code>product</code> in the given
	 * <code>order<code>. The method searches for {@link DisableProductBundleRuleModel}s that can
	 * be applied if the given <code>product</code> is or would be added to the given <code>order</code> in the context
	 * of the given <code>entryGroup</code> .
	 *
	 * @param product
	 *           product that is or shall be added to the bundle and for which applicable
	 *           {@link DisableProductBundleRuleModel}s are searched
	 * @param entryGroup
	 *           entry group representing a bundle to which the <code>product</code> is or shall be added
	 * @param order
	 *           the cart/order to which the given <code>product</code> is added
	 * @return the list of {@link DisableProductBundleRuleModel} which are blocking adding <code>product</code> cannot be
	 *         added to the bundle in the cart.
	 */
	List<DisableProductBundleRuleModel> getDisableProductBundleRules(ProductModel product, EntryGroup entryGroup,
			AbstractOrderModel order);
}
