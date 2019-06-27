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
package de.hybris.platform.ruleengineservices.rao.util;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.CategoryRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;


/**
 * Builder to encapsulate logic for building CartRAO objects.
 */
public class CartRAOBuilder
{
	public static final String DEFAULT_CURRENCY_ISO_CODE = "USD";
	private final CartRAO cartRAO;
	private ProductRAO lastProduct;
	private OrderEntryRAO lastOrderEntry;
	private static final DefaultRaoService raoService = new DefaultRaoService();


	public CartRAOBuilder()
	{
		this("" + Math.random());
	}

	public CartRAOBuilder(final String cartId)
	{
		this(cartId, DEFAULT_CURRENCY_ISO_CODE);
	}

	public CartRAOBuilder(final String cartId, final String currencyIsoCode)
	{
		this(raoService.createCart());
		cartRAO.setCode(cartId);
		//		cartRAO.setCode(cartId);
		cartRAO.setTotal(BigDecimal.ZERO);
		cartRAO.setActions(new LinkedHashSet<>());
		cartRAO.setEntries(new HashSet<>());
		cartRAO.setCurrencyIsoCode(currencyIsoCode);
	}

	/**
	 * @param cart
	 *           - a predefined cart.
	 */
	public CartRAOBuilder(final CartRAO cart)
	{
		this.cartRAO = cart;
	}

	public CartRAOBuilder addProductLine(final String productCode, final int quantity, final double price,
			final String... categories)
	{
		lastProduct = raoService.createProduct();
		lastProduct.setCode(productCode);

		for (final String category : categories)
		{
			final CategoryRAO categoryToAdd = new CategoryRAO();
			categoryToAdd.setCode(category);
			if (!lastProduct.getCategories().contains(categoryToAdd))
			{
				lastProduct.getCategories().add(categoryToAdd);
			}
		}
		return addProductQuantity(lastProduct, quantity, price);
	}

	/**
	 * @param product
	 * @param quantity
	 * @param price
	 * @return A valid Cart RAO Builder
	 */
	public CartRAOBuilder addProductQuantity(final ProductRAO product, final int quantity, final double price)
	{
		lastOrderEntry = raoService.createOrderEntry();
		lastOrderEntry.setProduct(product);
		lastOrderEntry.setQuantity(quantity);
		lastOrderEntry.setBasePrice(BigDecimal.valueOf(price));
		lastOrderEntry.setPrice(BigDecimal.valueOf(price));

		return addEntry(lastOrderEntry);
	}


	public CartRAOBuilder addCartDiscount(final boolean absolute, final double value)
	{
		final DiscountRAO discountRAO = createDiscount(value);
		if (absolute)
		{
			discountRAO.setCurrencyIsoCode(cartRAO.getCurrencyIsoCode());
		}
		cartRAO.getActions().add(discountRAO);
		return this;
	}

	/**
	 * @param value
	 * @return a DiscountRAO with absolute and value populated.
	 */
	private DiscountRAO createDiscount(final double value)
	{
		final DiscountRAO discountRAO = raoService.createDiscount();
		discountRAO.setValue(BigDecimal.valueOf(value));
		return discountRAO;
	}

	/**
	 * @param absolute
	 * @param value
	 * @return this
	 */
	public CartRAOBuilder addProductDiscount(final boolean absolute, final double value)
	{
		final DiscountRAO discountRAO = createDiscount(value);
		if (absolute)
		{
			discountRAO.setCurrencyIsoCode(cartRAO.getCurrencyIsoCode());
		}
		getLastOrderEntry().getActions().add(discountRAO);
		return this;
	}

	/**
	 * @param rao
	 */
	public CartRAOBuilder addEntry(final OrderEntryRAO rao)
	{
		cartRAO.getEntries().add(rao);
		return this;
	}

	public CartRAO toCart()
	{
		return cartRAO;
	}

	public OrderEntryRAO getLastOrderEntry()
	{
		return lastOrderEntry;
	}

	public ProductRAO getLastProduct()
	{
		return lastProduct;
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}



}
