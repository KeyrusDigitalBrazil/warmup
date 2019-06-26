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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.List;


/**
 * Overrides the {@link SubscriptionCommerceCartService} to handle products and subscription products that are sold as
 * part of a bundle as well as just standalone. It is mainly driven by a new <code>bundleNo</code> indicating the
 * specific bundle number for each cart entry and a <code>bundleTemplate</code> indicating the component - so the
 * detailed level of a package - to which the product has been added.
 * <p/>
 * On a method level it adds or overrides methods in order to pass that bundle information to add, update (incl. remove)
 * or calculate operations as well as additional checks have been implemented in order to verify if a certain
 * product/component is updateable or removable and it also allows for identifying problems in a bundle.
 * 
 * @spring.bean bundleCommerceCartService
 * @deprecated since 6.4. All bundle-specific functionality is provided via hooks,
 * {@link de.hybris.platform.commerceservices.order.CommerceCartService} and other services' methods.
 */
@Deprecated
public interface BundleCommerceCartService extends SubscriptionCommerceCartService
{
	/**
	 * Adds the (existing) {@link ProductModel} in the given {@link UnitModel} and with the given <code>quantity</code>
	 * to the (existing) {@link CartModel}. If an entry with the given product and given unit exists in the cart already,
	 * then the given <code>quantity</code> is added to the quantity of this cart entry unless <code>forceNewEntry</code>
	 * is set to true or a subscription product or a product, that is part of a bundle, is added. Dependent on the
	 * parameter <code>bundleNo</code> the product is added to an existing or new bundle or treated as a standalone
	 * product. In case a new bundle is created the method also adds auto-pick products to the cart if there are any
	 * setup for the bundle template. After this the multi-cart is calculated.
	 * 
	 * @param masterCartModel
	 *           the cart model. It must exist and it must be a master cart.
	 * @param productModel
	 *           the product model that will be added to the cart
	 * @param quantityToAdd
	 *           the quantity of the product
	 * @param unit
	 *           if <code>null</code> {@link ProductService#getOrderableUnit(ProductModel)} is used to determine the unit
	 * @param forceNewEntry
	 *           the force new entry if set to true, new cart entry will be created in any case. In case of subscription
	 *           products and products that are part of a bundle there will be always a new cart entry
	 * @param bundleNo
	 *           indicates to which bundle the product model shall be added (-1=create new bundle; 0=standalone
	 *           product/no bundle; >0=number of existing bundle)
	 * @param bundleTemplateModel
	 *           current version of the bundleTemplate model based on which the product is added to the cart. It will be
	 *           stored on the cart entry. If <code>null</code> the product will be treated as a standalone product and
	 *           the <code>bundleNo</code> must be 0 accordingly.
	 * @param removeCurrentProducts
	 *           whether to remove current products
	 * @return the list of cart modification data {@link CommerceCartModification} that includes a statusCode and the
	 *         actual quantity of the products added to the cart
	 * @throws CommerceCartModificationException
	 *            if the <code>product</code> is a base product OR the quantity is less than 1 or no usable unit was
	 *            found (only when given <code>unit</code> is also <code>null</code>) or any other reason the cart could
	 *            not be modified.
	 */
	@Nonnull
	List<CommerceCartModification> addToCart(@Nonnull CartModel masterCartModel, @Nonnull ProductModel productModel, long quantityToAdd,
		 	@Nullable final UnitModel unit, boolean forceNewEntry, int bundleNo, BundleTemplateModel bundleTemplateModel,
			boolean removeCurrentProducts) throws CommerceCartModificationException;

	/**
	 * @see BundleCommerceCartService#addToCart(CartModel, ProductModel, long, UnitModel, boolean, int,
	 *      BundleTemplateModel, boolean)
	 * 
	 * @param masterCartModel
	 * @param productModel
	 * @param quantity
	 * @param unit
	 * @param forceNewEntry
	 * @param bundleNo
	 * @param bundleTemplateModel
	 * @param removeCurrentProducts
	 * @param xmlProduct
	 * @return the list of cart modification data {@link CommerceCartModification} that includes a statusCode and the
	 *         actual quantity of the products added to the cart
	 * @throws CommerceCartModificationException
	 */
	@Nonnull
	List<CommerceCartModification> addToCart(@Nonnull CartModel masterCartModel,@Nonnull ProductModel productModel, long quantity,
                                             @Nullable UnitModel unit, boolean forceNewEntry, int bundleNo,
                                             @Nullable BundleTemplateModel bundleTemplateModel, boolean removeCurrentProducts,
			                                 @Nullable String xmlProduct)
            throws CommerceCartModificationException;

	/**
	 * Adds the (existing) {@link ProductModel}s <code>productModel1</code> and <code>productModel2</code> in the given
	 * {@link UnitModel} and with quantity=1 to the (existing) multi-cart {@link CartModel} as new cart entries. As this
	 * method works for bundles only, both products must have a bundle template and must be part of an existing bundle or
	 * a new bundle that is created during the addToCart. In case a new bundle is created the method also adds auto-pick
	 * products to the cart if there are any setup for the bundle template. After this the multi-cart is calculated.
	 * 
	 * @param masterCartModel
	 *           the cart model. It must exist and it must be a master cart.
	 * @param unit
	 *           if <code>null</code> {@link ProductService#getOrderableUnit(ProductModel)} is used to determine the unit
	 * @param bundleNo
	 *           indicates to which bundle the product model shall be added (-1=create new bundle; >0=number of existing
	 *           bundle; 0=standalone product/no bundle is not allowed here)
	 * @param productModel1
	 *           the first product model that will be added to the cart
	 * @param bundleTemplateModel1
	 *           current version of the bundleTemplate model based on which the first product is added to the cart. It
	 *           will be stored on the cart entry. The parameter cannot be <code>null</code>.
	 * @param productModel2
	 *           the second product model that will be added to the cart
	 * @param bundleTemplateModel2
	 *           current version of the bundleTemplate model based on which the second product is added to the cart. It
	 *           will be stored on the cart entry. The parameter cannot be <code>null</code>.
	 * @param xmlProduct1
	 * 			 first product in xml format
	 * @param xmlProduct2
	 * 			 second product in xml format
	 * @return the list of cart modification data {@link CommerceCartModification} that includes a statusCode and the
	 *         actual quantity of the products added to the cart
	 * @throws CommerceCartModificationException
	 *            if the <code>product</code> is a base product OR the quantity is less than 1 or no usable unit was
	 *            found (only when given <code>unit</code> is also <code>null</code>) or the bundle parameters were
	 *            empty/wrong or any other reason the cart could not be modified.
	 */
	List<CommerceCartModification> addToCart(@Nonnull final CartModel masterCartModel, final UnitModel unit, final int bundleNo,
											 @Nonnull final ProductModel productModel1,
											 @Nonnull final BundleTemplateModel bundleTemplateModel1,
											 @Nonnull final ProductModel productModel2,
											 @Nonnull final BundleTemplateModel bundleTemplateModel2,
											 @Nullable final String xmlProduct1,@Nullable final String xmlProduct2) throws CommerceCartModificationException;

	/**
	 * @see BundleCommerceCartService#addToCart(CartModel, UnitModel, int, ProductModel, BundleTemplateModel,
	 *      ProductModel, BundleTemplateModel, String, String)
	 *
	 * @param masterCartModel
	 * @param unit
	 * @param bundleNo
	 * @param productModel1
	 * @param bundleTemplateModel1
	 * @param productModel2
	 * @param bundleTemplateModel2
	 * @return {@link List} of {@link CommerceCartModification}s
	 * @throws CommerceCartModificationException
	 * 
	 * @deprecated Since ages (at least from 5.1)
	 */
	@Deprecated
	List<CommerceCartModification> addToCart(CartModel masterCartModel, UnitModel unit, int bundleNo, ProductModel productModel1,
			BundleTemplateModel bundleTemplateModel1, ProductModel productModel2, BundleTemplateModel bundleTemplateModel2)
			throws CommerceCartModificationException;


	/**
	 * Removes a bundle from the given {@link CartModel} and its child carts. The given {@link CartModel} must be a
	 * master cart. All entries within the multi-cart (master + child carts) that belong to the same bundle identified by
	 * the given <code>bundleNo</code> are removed from the multi cart.
	 * 
	 * @param masterCartModel
	 *           the {@link CartModel}. It must exist and it must be a master cart.
	 * @param bundleNo
	 *           the number of the bundle
	 * @throws CommerceCartModificationException
	 *            if the given {@link CartModel} is not a master cart
	 * @deprecated Since 6.4 - use {@link de.hybris.platform.commerceservices.order.CommerceCartService#removeEntryGroup(RemoveEntryGroupParameter)}
	 */
	@Deprecated
	void removeAllEntries(@Nonnull CartModel masterCartModel, int bundleNo) throws CommerceCartModificationException;

	/**
	 * Checks whether the given {@link CartEntryModel} could be removed from the bundle/cart (only a check, the cart is
	 * not modified!). The check is based on the selectionCritera of the given <code>cartEntry</code>'s component (bundle
	 * template). If with the removal of the given <code>cartEntry</code> the lower limit of required product selections
	 * would not be met any more, the check returns <code>false</code>, otherwise <code>true</code>.
	 * 
	 * @param cartEntry
	 *           cart entry that is checked whether it can be removed from the cart/bundle
	 * @return <code>true</code> if the given <code>cartEntry</code> can be removed from the cart, otherwise
	 *         <code>false</code>
	 */
	boolean checkIsEntryRemovable(@Nonnull CartEntryModel cartEntry);

	/**
	 * Checks whether the given {@link CartEntryModel} could be removed from the bundle/cart (only a check, the cart is
	 * not modified!) and returns the reason if the removal is not allowed. The check is based on the selectionCritera of
	 * the given <code>cartEntry</code>'s component (bundle template). If with the removal of the given
	 * <code>cartEntry</code> the lower limit of required product selections would not be met any more, the check returns
	 * the reason as {@link String}, otherwise <code>null</code>.
	 * 
	 * @param cartEntry
	 *           cart entry that is checked whether it can be removed from the cart/bundle
	 * @return the reason as {@link String} why the given <code>cartEntry</code> cannot be removed, otherwise
	 *         <code>null</code>
	 */
	@Nullable
	String checkAndGetReasonForNotRemovableEntry(@Nonnull CartEntryModel cartEntry);

	/**
	 * Checks whether the given <code>cartEntry</code> can be updated. Any cart entry {@link CartEntryModel} belonging to
	 * a bundle {@link BundleTemplateModel} cannot be updated.
	 * 
	 * @param cartEntry
	 *           cart Entry that is checked
	 * @return <code>true</code> if given <code>cartEntry</code> can be update, otherwise <code>false</code>
	 */
	boolean checkIsEntryUpdateable(@Nonnull CartEntryModel cartEntry);

	/**
	 * Checks if the given component <code>bundleTemplate</code> in given bundle <code>bundleNo</code> and
	 * <code>masterCart</code> can be edited. The check is based on the selection dependencies of the given
	 * <code>bundleTemplate</code>. In case the given <code>bundleTemplate</code>'s selection criteria is of type
	 * {@link AutoPickBundleSelectionCriteriaModel}, it is never editable.
	 * 
	 * @param masterCart
	 *           the master cart the bundle is in
	 * @param bundleTemplate
	 *           the component that is checked whether it can be edited
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> belongs to
	 * @return <code>true</code> if the selection dependencies are fulfilled and the component can be edited, otherwise
	 *         <code>false</code>
	 */
	boolean checkIsComponentEditable(@Nonnull CartModel masterCart,@Nullable final BundleTemplateModel bundleTemplate, final int bundleNo);

	/**
	 * Checks if the selection criteria of the given component <code>bundleTemplate</code> in given bundle
	 * <code>bundleNo</code> and <code>masterCart</code> is met.
	 * 
	 * @param masterCart
	 *           the master cart the bundle is in
	 * @param bundleTemplate
	 *           the component that is checked whether its selection criteria is met
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> belongs to
	 * 
	 * @return <code>true</code> if given <code>bundleTemplate's</code>'s selection criteria is met, otherwise
	 *         <code>false</code>
	 */
	boolean checkIsComponentSelectionCriteriaMet(@Nonnull CartModel masterCart,@Nullable BundleTemplateModel bundleTemplate, int bundleNo);

	/**
	 * Checks if the given <code>product</code> which is not yet added to the cart/bundle needs to be disabled within a
	 * component. A product needs to be disabled if a disable rule is applicable (see description of method
	 * isProductDisabledInBundle() above) and/or the selection criteria of the component is exceeded. If the given
	 * <code>product</code> needs to be disabled the reason is returned as String. If <code>ignoreCurrentProducts</code>
	 * is enabled, then all product of the current component will be ignored when finding disable rules.
	 * 
	 * @param masterCart
	 *           the master cart the bundle is in
	 * @param product
	 *           the product which is to be added to the given <code>bundleNo</code>
	 * @param bundleTemplate
	 *           the component to which the given <code>product</code> is linked
	 * @param bundleNo
	 *           the number of the bundle the given <code>bundleTemplate</code> and <code>product</code> belong to or
	 *           will be added
	 * @param ignoreCurrentProducts
	 *           if current products should be ignored
	 * 
	 * @return the reason as {@link String} why the given <code>product</code> needs to be disabled, otherwise
	 *         <code>null</code>
	 */
	@Nullable
	String checkAndGetReasonForDisabledProductInComponent(@Nonnull CartModel masterCart,@Nonnull ProductModel product,
						  @Nullable BundleTemplateModel bundleTemplate, int bundleNo, final boolean ignoreCurrentProducts);

	/**
	 * Gets the list of {@link CartEntryModel} for Products and bundleNo.
	 * 
	 * @param masterCart
	 *           the master cart the bundle is in
	 * @param product
	 *           the product for which the cartentries should be found
	 * @param bundleNo
	 *           the bundleNo for which the cartentries should be found
	 * @return List<CartEntryModel>
	 */
	@Nonnull
	List<CartEntryModel> getCartEntriesForProductInBundle(@Nonnull CartModel masterCart,@Nonnull  ProductModel product, int bundleNo);

	/**
	 * Gets the list of {@link CartEntryModel} for component and bundleNo.
	 * 
	 * @param masterCart
	 *           the master cart the bundle is in
	 * @param component
	 *           the component for which the cartentries should be found
	 * @param bundleNo
	 *           the bundleNo for which the cartentries should be found
	 * @return List<CartEntryModel>
	 */
	@Nonnull
	List<CartEntryModel> getCartEntriesForComponentInBundle(@Nonnull CartModel masterCart,@Nonnull BundleTemplateModel component, int bundleNo);

	/**
	 * Gets the list of {@link} for bundleNo.
	 *
	 * @param masterCart
	 * 			the master cart the bundle is in
	 * @param bundleNo
	 * 			the bundleNo for which the cartEntries should be found
	 * @return list of {@link CartEntryModel} with specific bundle number
	 */
	@Nonnull
	List<CartEntryModel> getCartEntriesForBundle(@Nonnull CartModel masterCart, int bundleNo);

	/**
	 * Get the master {@link BillingTimeModel}
	 * 
	 * @return {@link BillingTimeModel}
	 */
	BillingTimeModel getMasterBillingTime();

	/**
	 * Checks the given <code>masterCart</code> if there are invalid components in it.
	 * 
	 * @param masterCart
	 * @return the first invalid component {@link BundleTemplateModel} if there is one, otherwise <code>null</code>
	 */
	@Nullable
	BundleTemplateModel getFirstInvalidComponentInCart(@Nonnull CartModel masterCart);
}
