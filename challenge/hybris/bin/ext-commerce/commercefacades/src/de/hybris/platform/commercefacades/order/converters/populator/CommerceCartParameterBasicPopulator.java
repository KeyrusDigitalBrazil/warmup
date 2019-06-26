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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates common data of add-to-cart creation to pass it from facade to service layer.
 */
public class CommerceCartParameterBasicPopulator implements Populator<AddToCartParams, CommerceCartParameter>
{
	private ProductService productService;
	private CartService cartService;
	private PointOfServiceService pointOfServiceService;

	@Override
	public void populate(final AddToCartParams addToCartParams, final CommerceCartParameter parameter) throws ConversionException
	{
		parameter.setEnableHooks(true);
		final CartModel cartModel = getCartService().getSessionCart();
		parameter.setCart(cartModel);
		if (StringUtils.isNotEmpty(addToCartParams.getStoreId()))
		{
			final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(
					addToCartParams.getStoreId());
			parameter.setPointOfService(pointOfServiceModel);
		}
		// Product code may be optional, e.g. when it's addressed by order entry.
		if (addToCartParams.getProductCode() != null)
		{
			final ProductModel product = getProductService().getProductForCode(addToCartParams.getProductCode());
			parameter.setProduct(product);
			parameter.setUnit(product.getUnit());
		}
		parameter.setQuantity(addToCartParams.getQuantity());
		parameter.setCreateNewEntry(false);
		parameter.setEntryGroupNumbers(addToCartParams.getEntryGroupNumbers());
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}
}
