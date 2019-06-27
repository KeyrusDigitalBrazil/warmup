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
package de.hybris.platform.subscriptionfacades.order;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;

import javax.annotation.Nonnull;


/**
 * This interface declares methods for manipulating subscription products in the cart.
 *
 * @deprecated Since 6.4 - use {@link de.hybris.platform.commercefacades.order.impl.DefaultCartFacade}
 */
@Deprecated
public interface SubscriptionCartFacade extends CartFacade
{
	/**
	 * Refreshes the xml of a cart entry, if the currency has changed.
	 */
	void refreshProductXMLs();

	/**
	 * Method for upgrading a subscription product. It adds the new subscription product to the cart and sets a reference
	 * to the original subscription
	 * 
	 * @param code
	 *           code of product to add
	 * @param originalSubscriptionId
	 *           the id of the original subscription which is upgraded by this addToCart
	 * @param originalOrderCode
	 *           the code of the original order in which the original subscription was bought
	 * @param originalEntryNumber
	 * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
	 * @throws CommerceCartModificationException
	 *            if the cart cannot be modified
	 */
	@Nonnull
	CartModificationData addToCart(@Nonnull String code,@Nonnull final String originalSubscriptionId,@Nonnull final String originalOrderCode,
			final int originalEntryNumber) throws CommerceCartModificationException;

	/**
	 * Converts a product model into its xml representation.
	 *
	 * @param product
	 *           product to generate xml representation for
	 * @return xml representation of the product
	 *
	 */
	String getProductAsXML(final ProductModel product);
}
