/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.TaxValue;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;


public class OrderModelBuilder
{
	private final OrderModel model;

	private OrderModelBuilder()
	{
		model = new OrderModel();
	}

	public static OrderModelBuilder aModel()
	{
		return new OrderModelBuilder();
	}

	private OrderModel getModel()
	{
		return this.model;
	}

	public OrderModel build()
	{
		return getModel();
	}

	public OrderModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public OrderModelBuilder withDeliveryMode(final DeliveryModeModel deliveryMode)
	{
		getModel().setDeliveryMode(deliveryMode);
		return this;
	}

	public OrderModelBuilder withCurrency(final CurrencyModel currency)
	{
		getModel().setCurrency(currency);
		return this;
	}

	public OrderModelBuilder withStore(final BaseStoreModel store)
	{
		getModel().setStore(store);
		return this;
	}

	public OrderModelBuilder withDeliveryAddress(final AddressModel deliveryAddress)
	{
		getModel().setDeliveryAddress(deliveryAddress);
		return this;
	}

	public OrderModelBuilder withDate(final Date date)
	{
		getModel().setDate(date);
		return this;
	}

	public OrderModelBuilder withUser(final UserModel user)
	{
		getModel().setUser(user);
		return this;
	}

	public OrderModelBuilder withCustomser(final CustomerModel customer)
	{
		getModel().setUser(customer);
		return this;
	}

	public OrderModelBuilder withBaseSite(final BaseSiteModel baseSite)
	{
		getModel().setSite(baseSite);

		return this;
	}

	public OrderModelBuilder withEntries(final AbstractOrderEntryModel... entries)
	{
		getModel().setEntries(Lists.newArrayList(entries));

		getModel().setTotalPrice(getModel().getEntries().stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum() + (
				getModel().getDeliveryCost() != null ?
						getModel().getDeliveryCost() :
						0));

		final double deliveryCostTax;
		if (getModel().getTotalTaxValues() != null && getModel().getTotalTaxValues().size() > 0)
		{
			deliveryCostTax = getModel().getTotalTaxValues().stream().findFirst().get().getValue();
		}
		else
		{
			deliveryCostTax = 0;
		}
		getModel().setTotalTax(
				getModel().getEntries().stream().flatMap(entry -> entry.getTaxValues().stream()).mapToDouble(TaxValue::getValue).sum()
						+ deliveryCostTax);

		return this;
	}

	public OrderModelBuilder withPaymentInfo(final PaymentInfoModel paymentInfo)
	{
		getModel().setPaymentInfo(paymentInfo);
		return this;
	}

	public OrderModelBuilder withPaymentTransactions(final List<PaymentTransactionModel> paymentTransactions)
	{
		getModel().setPaymentTransactions(paymentTransactions);
		return this;
	}
}
