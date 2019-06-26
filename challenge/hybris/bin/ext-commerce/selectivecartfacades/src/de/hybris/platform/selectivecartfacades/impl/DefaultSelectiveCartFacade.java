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
package de.hybris.platform.selectivecartfacades.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;
import de.hybris.platform.selectivecartservices.SelectiveCartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;




/**
 * Default implementation of {@link SelectiveCartFacade}
 */
public class DefaultSelectiveCartFacade implements SelectiveCartFacade
{
	private SelectiveCartService selectiveCartService;
	private Converter<Wishlist2Model, Wishlist2Data> wishlistConverter;
	private ProductService productService;
	private CartFacade cartFacade;
	private Converter<Wishlist2EntryModel, OrderEntryData> wishlish2EntryModelToOrderEntryConverter;

	@Override
	public Wishlist2Data getWishlistForSelectiveCart()
	{
		final Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		if (wishlistForSelectiveCart != null)
		{
			return getWishlistConverter().convert(wishlistForSelectiveCart);
		}
		return null;
	}

	@Override
	public void removeWishlistEntryForProduct(final String productCode)
	{
		final Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		final ProductModel product = getProductService().getProductForCode(productCode);
		getSelectiveCartService().removeWishlistEntryForProduct(product, wishlistForSelectiveCart);
	}


	@Override
	public void updateCartFromWishlist() throws CommerceCartModificationException
	{
		final Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		boolean hasWishlist = false;
		List<Wishlist2EntryModel> wishlistEntries = null;
		if (wishlistForSelectiveCart != null)
		{
			wishlistEntries = wishlistForSelectiveCart.getEntries();
			hasWishlist = CollectionUtils.isNotEmpty(wishlistEntries);
		}

		final List<OrderEntryData> orderEntries = getCartFacade().getSessionCart().getEntries();
		final boolean hasCartData = CollectionUtils.isNotEmpty(orderEntries);

		if (hasWishlist && hasCartData)
		{
			for (final OrderEntryData orderEntry : orderEntries)
			{
				final String productCodeInCart = orderEntry.getProduct().getCode();
				final Optional<Wishlist2EntryModel> entry = wishlistEntries.stream()
						.filter(wishlistEntry -> productCodeInCart.equals(wishlistEntry.getProduct().getCode())).findFirst();
				if (entry.isPresent())
				{
					final Wishlist2EntryModel wishlistEntry = entry.get();
					removeWishlistEntryAndUpdateAddToCartTime(wishlistForSelectiveCart, wishlistEntry.getProduct(), wishlistEntry);
				}
			}
		}
	}

	@Override
	public void addToCartFromWishlist(final String productCode) throws CommerceCartModificationException
	{
		final Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		final ProductModel product = getProductService().getProductForCode(productCode);
		final Wishlist2EntryModel wishlistEntry = getSelectiveCartService().getWishlistEntryForProduct(product,
				wishlistForSelectiveCart);
		if (wishlistEntry != null)
		{
			removeWishlistEntryAndUpdateAddToCartTime(wishlistForSelectiveCart, product, wishlistEntry);
		}
	}

	protected void removeWishlistEntryAndUpdateAddToCartTime(final Wishlist2Model wishlistForSelectiveCart,
			final ProductModel product, final Wishlist2EntryModel wishlistEntry) throws CommerceCartModificationException
	{
		final int quatityInWishlist = wishlistEntry.getQuantity().intValue();
		final Date addTime = wishlistEntry.getAddToCartTime();
		getSelectiveCartService().removeWishlistEntryForProduct(product, wishlistForSelectiveCart);
		final CartModificationData modification = getCartFacade().addToCart(product.getCode(), quatityInWishlist);
		final String cartCode = modification.getCartCode();
		final int entryNumber = modification.getEntry().getEntryNumber() == null ? -1 : modification.getEntry().getEntryNumber();
		if (cartCode != null && entryNumber != -1 && addTime != null)
		{
			getSelectiveCartService().updateCartTimeForOrderEntry(cartCode, entryNumber, addTime);
		}
	}

	@Override
	public void addToWishlistFromCart(final OrderEntryData orderEntry) throws CommerceCartModificationException
	{
		Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		if (wishlistForSelectiveCart == null)
		{
			wishlistForSelectiveCart = getSelectiveCartService().createWishlist();
		}
		final ProductModel product = getProductService().getProductForCode(orderEntry.getProduct().getCode());
		final Wishlist2EntryModel wishlistEntry = getSelectiveCartService().saveWishlistEntryForProduct(product,
				wishlistForSelectiveCart, orderEntry.getAddToCartTime());
		final int totalQuatity = wishlistEntry.getQuantity().intValue() + orderEntry.getQuantity().intValue();
		getSelectiveCartService().updateQuantityForWishlistEntry(wishlistEntry, Integer.valueOf(totalQuatity));
		getCartFacade().updateCartEntry(orderEntry.getEntryNumber(), 0);

	}

	@Override
	public void addToWishlistFromCart(final Integer entryNumber) throws CommerceCartModificationException
	{
		final Optional<OrderEntryData> entryOptional = getCartFacade().getSessionCart().getEntries().stream()
				.filter(entry -> entry.getEntryNumber().equals(entryNumber)).findAny();

		if (entryOptional.isPresent())
		{
			addToWishlistFromCart(entryOptional.get());
		}

	}

	@Override
	public void addToWishlistFromCart(final List<String> productCodes) throws CommerceCartModificationException
	{
		for (final String productCode : productCodes)
		{
			final Optional<OrderEntryData> entryOptional = getCartFacade().getSessionCart().getEntries().stream()
					.filter(entry -> entry.getProduct().getCode().equals(productCode)).findAny();
			if (entryOptional.isPresent())
			{
				addToWishlistFromCart(entryOptional.get());
			}
		}
	}

	@Override
	public List<OrderEntryData> getWishlistOrdersForSelectiveCart()
	{
		final Wishlist2Model wishlistForSelectiveCart = getSelectiveCartService().getWishlistForSelectiveCart();
		if (wishlistForSelectiveCart != null && CollectionUtils.isNotEmpty(wishlistForSelectiveCart.getEntries()))
		{
			return getWishlish2EntryModelToOrderEntryConverter().convertAll(wishlistForSelectiveCart.getEntries());
		}
		return Collections.emptyList();
	}

	protected SelectiveCartService getSelectiveCartService()
	{
		return selectiveCartService;
	}

	@Required
	public void setSelectiveCartService(final SelectiveCartService selectiveCartService)
	{
		this.selectiveCartService = selectiveCartService;
	}

	protected Converter<Wishlist2Model, Wishlist2Data> getWishlistConverter()
	{
		return wishlistConverter;
	}

	@Required
	public void setWishlistConverter(final Converter<Wishlist2Model, Wishlist2Data> wishlistConverter)
	{
		this.wishlistConverter = wishlistConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	public Converter<Wishlist2EntryModel, OrderEntryData> getWishlish2EntryModelToOrderEntryConverter()
	{
		return wishlish2EntryModelToOrderEntryConverter;
	}

	@Required
	public void setWishlish2EntryModelToOrderEntryConverter(
			final Converter<Wishlist2EntryModel, OrderEntryData> wishlish2EntryModelToOrderEntryConverter)
	{
		this.wishlish2EntryModelToOrderEntryConverter = wishlish2EntryModelToOrderEntryConverter;
	}

}
