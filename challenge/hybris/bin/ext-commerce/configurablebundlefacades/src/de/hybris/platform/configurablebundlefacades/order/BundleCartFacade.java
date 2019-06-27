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

package de.hybris.platform.configurablebundlefacades.order;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.List;


/**
 * Bundle Cart facade interface. Service is responsible for getting and updating all necessary information for a bundle
 * cart.
 */
public interface BundleCartFacade
{
	/**
	 * Start new bundle in cart based on the given bundle template and add the product to it.
	 *
	 * @see de.hybris.platform.core.order.EntryGroup
	 * @see de.hybris.platform.configurablebundleservices.jalo.BundleSelectionCriteria
	 *
	 * @param bundleTemplateId a component to add the product to. He whole bundle structure
	 *                          - starting from the root of the component - will be added to cart groups
	 * @param productCode a product which will be added to the component
	 * @param quantity quantity for the product. Is limited by selection criteria of the component
	 * @return information about the new cart entry
	 * @throws CommerceCartModificationException if the operation is not possible
	 */
	CartModificationData startBundle(@Nonnull String bundleTemplateId, @Nonnull String productCode, long quantity)
			throws CommerceCartModificationException;

	/**
	 * Add a product to an existing bundle.
	 *
	 * @see de.hybris.platform.core.order.EntryGroup
	 *
	 * @param productCode product to add
	 * @param quantity quantity of the product
	 * @param groupNumber entry group number, that defines the bundle and the component within the bundle
	 * @return information about the new cart entry
	 * @throws CommerceCartModificationException if the operation is not possible
	 */
	CartModificationData addToCart(@Nonnull String productCode, long quantity, int groupNumber)
			throws CommerceCartModificationException;

	/**
	 * Adds the product with the productId and with the given <code>quantity</code> to the cart. If an entry with the
	 * given product exists in the cart already, then the given <code>quantity</code> is added to the quantity of this
	 * cart entry. Dependent on the parameter <code>bundleNo</code> the product is added to an existing or new bundle or
	 * treated as a standalone product. In case a new bundle is created the method also adds auto-pick products to the
	 * cart if there are any setup for the bundle template. After this the multi-cart is calculated.
	 * 
	 * 
	 * @param productCode
	 *           the product id that will be added to the cart
	 * @param quantity
	 *           the quantity of the product
	 * @param bundleNo
	 *           indicates to which bundle the product shall be added (-1=create new bundle; 0=standalone product/no
	 *           bundle; >0=number of existing bundle)
	 * @param bundleTemplateId
	 *           The bundletemplate id to add the product to
	 * @param removeCurrentProducts
	 *           whether to remove existing products in that component
	 * @return List of CartModificationData {@link CartModificationData}
	 * @throws CommerceCartModificationException
	 * @deprecated Since 6.4 - Use {@link BundleCartFacade#startBundle(String, String, long)}
	 * 				or {@link BundleCartFacade#addToCart(String, long, int)}
	 */
	@Deprecated
	@Nonnull
	List<CartModificationData> addToCart(@Nonnull final String productCode, final long quantity, final int bundleNo,
										 @Nullable final String bundleTemplateId, final boolean removeCurrentProducts)
										throws CommerceCartModificationException;


	/**
	 * Method to add the <code>productCode1<code> and <code>productcode2</code> to multi-cart as new cart entries. As
	 * this method works for bundles only, both products must have a bundle template and must be part of an existing
	 * bundle or a new bundle that is created during the addToCart. In case a new bundle is created the method also adds
	 * auto-pick products to the cart if there are any setup for the bundle template. After this the multi-cart is
	 * calculated.
	 * 
	 * @param productCode1
	 *           the first product id that will be added to the cart
	 * @param bundleNo
	 *           indicates to which bundle the product model shall be added (-1=create new bundle; >0=number of existing
	 *           bundle; 0=standalone product/no bundle is not allowed here)
	 * @param bundleTemplateId1
	 *           current version of the bundleTemplate id based on which the first product is added to the cart. It will
	 *           be stored on the cart entry.
	 * @param productCode2
	 *           the second product id that will be added to the cart
	 * @param bundleTemplateId2
	 *           current version of the bundleTemplate id based on which the second product is added to the cart. It will
	 *           be stored on the cart entry.
	 * @return List the list of CartModificationData {@link CartModificationData}
	 * @throws CommerceCartModificationException
	 * @deprecated Since 6.4 - The products can be added in two separate calls.
	 */
	@Deprecated
	@Nonnull
	List<CartModificationData> addToCart(@Nonnull final String productCode1, final int bundleNo,@Nullable final String bundleTemplateId1,
										@Nonnull final String productCode2,@Nullable final String bundleTemplateId2) throws CommerceCartModificationException;

	/**
	 * Method to delete all cart entries of a particular bundle
	 * 
	 * @param bundleNo
	 *           bundleNo in cart
	 * 
	 * @throws CommerceCartModificationException
	 * @deprecated Since 6.4 - use {@link de.hybris.platform.commercefacades.order.CartFacade#removeEntryGroup(Integer)}
	 */
	@Deprecated
	void deleteCartBundle(final int bundleNo) throws CommerceCartModificationException;

	/**
	 * Checks if the session cart is valid (= does not contain any invalid components)
	 * 
	 * @return <code>true</code> if the session cart is valid, otherwise <code>false</code>
	 * @deprecated Since 6.4
	 */
	@Deprecated
	boolean isCartValid();

	/**
	 * Constructs pageable list of products available for entry group of type {@link de.hybris.platform.core.enums.GroupType#CONFIGURABLEBUNDLE}
	 *
	 * @param groupNumber
	 *           entry group number related to the component
	 * @param searchQuery
	 *           the search query
	 * @param pageableData
	 *           the page to return
	 * @return the search results
	 * @throws IllegalArgumentException 
	 *           if group is not found or group type is not {@link de.hybris.platform.core.enums.GroupType#CONFIGURABLEBUNDLE}
	 */
	@Nonnull
	ProductSearchPageData<SearchStateData, ProductData> getAllowedProducts(
			@Nonnull Integer groupNumber, String searchQuery, @Nonnull PageableData pageableData);
}
