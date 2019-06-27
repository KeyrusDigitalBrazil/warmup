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
package de.hybris.platform.ruleengineservices.ruleengine.impl;

import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.ProductRAO;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;


/**
 * Builder to encapsulate logic for building CartRAO objects.
 */
public class CartRaoBuilder
{
	private CartRaoBuilder()
	{
		// no public constructors
	}

	public static CartRaoDraft newCart(final String code)
	{
		return new CartRaoDraft().setCode(code);
	}

	public static class CartRaoDraft
	{
		private final CartRAO cartRao = new CartRAO();
		{
			cartRao.setActions(new LinkedHashSet<>());
			cartRao.setEntries(new HashSet<>());
		}

		public CartRaoDraft setCode(final String code)
		{
			cartRao.setCode(code);
			return this;
		}

		public CartRaoDraft setCurrency(final String currencyIsoCode)
		{
			cartRao.setCurrencyIsoCode(currencyIsoCode);
			return this;
		}

		public CartRaoDraft setTotal(final BigDecimal total)
		{
			cartRao.setTotal(total);
			return this;
		}

		public CartRaoDraft setOriginalTotal(final BigDecimal total)
		{
			cartRao.setOriginalTotal(total);
			return this;
		}

		public CartRaoDraft addEntry(final ProductRAO product, final int quantity, final BigDecimal basePrice)
		{
			final OrderEntryRAO entry = new OrderEntryRAO();
			entry.setProduct(product);
			entry.setQuantity(quantity);
			entry.setOrder(cartRao);
			entry.setCurrencyIsoCode(cartRao.getCurrencyIsoCode());
			entry.setBasePrice(basePrice);
			entry.setPrice(basePrice);
			cartRao.getEntries().add(entry);
			return this;
		}

		public CartRAO getCart()
		{
			return cartRao;
		}
	}
}
