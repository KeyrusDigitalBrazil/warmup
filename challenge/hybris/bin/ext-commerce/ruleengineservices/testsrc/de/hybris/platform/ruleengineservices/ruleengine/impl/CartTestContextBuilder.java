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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.PaymentModeRAO;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.util.Collections.emptyMap;


public class CartTestContextBuilder
{
	private static final String CURRENCY_ISO_CODE = "USD";

	private final CartModel cartModel;
	private final CartRAO cartRAO;
	private PaymentModeModel paymentModeModel;
	private PaymentModeRAO paymentModeRAO;

	public CartTestContextBuilder()
	{
		cartModel = new CartModel();
		cartModel.setCode(UUID.randomUUID().toString());
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(CURRENCY_ISO_CODE);
		cartModel.setCurrency(currencyModel);

		cartRAO = new CartRAO();
	}

	public CartModel getCartModel()
	{
		return cartModel;
	}

	public CartRAO getCartRAO()
	{
		return cartRAO;
	}

	public PaymentModeModel getPaymentModeModel()
	{
		return paymentModeModel;
	}

	public PaymentModeRAO getPaymentModeRAO()
	{
		return paymentModeRAO;
	}

	public CartTestContextBuilder withPaymentModeModel(final String code)
	{
		paymentModeModel = new PaymentModeModel();
		paymentModeModel.setCode(code);
		cartModel.setPaymentMode(paymentModeModel);
		return this;
	}

	public CartTestContextBuilder withPaymentModeRAO(final String code)
	{
		paymentModeRAO = new PaymentModeRAO();
		paymentModeRAO.setCode(code);
		cartRAO.setPaymentMode(paymentModeRAO);
		return this;
	}

	public CartTestContextBuilder withEntries(final List<AbstractOrderEntryModel> entries)
	{
		cartModel.setEntries(entries);
		return this;
	}

	public CartTestContextBuilder addEntry(final AbstractOrderEntryModel entry)
	{
		entry.setOrder(cartModel);
		List<AbstractOrderEntryModel> entries = cartModel.getEntries();
		if (CollectionUtils.isEmpty(entries))
		{
			entries = new ArrayList<>();
			cartModel.setEntries(entries);
		}
		entries.add(entry);
		return this;
	}

	public CartTestContextBuilder addNewEntry()
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		return addEntry(entry);
	}

	public CartTestContextBuilder addNewEntry(final ProductModel product)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		entry.setProduct(product);
		return addEntry(entry);
	}

	public CartTestContextBuilder addNewEntry(final CategoryModel... categoryModels)
	{
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final ProductModel product = new ProductModel();
		entry.setProduct(product);
		if (ArrayUtils.isNotEmpty(categoryModels))
		{
			product.setSupercategories(Arrays.asList(categoryModels));
		}
		return addEntry(entry);
	}

	public CartTestContextBuilder withDiscounts(final List<DiscountModel> discounts)
	{
		cartModel.setDiscounts(discounts);
		return this;
	}

	public CartTestContextBuilder addDiscount(final DiscountModel discount)
	{
		List<DiscountModel> discounts = cartModel.getDiscounts();
		if (CollectionUtils.isEmpty(discounts))
		{
			discounts = new ArrayList<>();
			cartModel.setDiscounts(discounts);
		}
		discounts.add(discount);
		return this;
	}

	public CartTestContextBuilder withUser(final String userId)
	{
		final ItemContextBuilder customerModelBuilder = ItemContextBuilder.createMockContextBuilder(CustomerModel.class, null,
				Locale.ENGLISH, emptyMap());
		final UserModel userModel = new UserModel(customerModelBuilder.build());
		userModel.setUid(userId);
		return withUser(userModel);
	}

	public CartTestContextBuilder withUserGroups(final String userId, final PrincipalGroupModel... groups)
	{
		final ItemContextBuilder customerModelBuilder = ItemContextBuilder.createMockContextBuilder(CustomerModel.class, null,
				Locale.ENGLISH, emptyMap());
		final UserModel userModel = new UserModel(customerModelBuilder.build());
		userModel.setUid(userId);
		userModel.setGroups(new HashSet<>(Arrays.asList(groups)));
		return withUser(userModel);
	}

	public CartTestContextBuilder withUser(final UserModel user)
	{
		cartModel.setUser(user);
		return this;
	}

	public CartTestContextBuilder withTotalPrice(final Double totalPrice)
	{
		cartModel.setTotalPrice(totalPrice);
		return this;
	}

	public CartTestContextBuilder withSubtotal(final Double subTotal)
	{
		cartModel.setSubtotal(subTotal);
		return this;
	}

	public CartTestContextBuilder withDeliveryCost(final Double deliveryCost)
	{
		cartModel.setDeliveryCost(deliveryCost);
		return this;
	}

	public CartTestContextBuilder withPaymentCost(final Double paymentCost)
	{
		cartModel.setPaymentCost(paymentCost);
		return this;
	}

	public CartTestContextBuilder withCurrency(final String currencyIsoCode)
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(currencyIsoCode);
		cartModel.setCurrency(currencyModel);
		return this;
	}

}
