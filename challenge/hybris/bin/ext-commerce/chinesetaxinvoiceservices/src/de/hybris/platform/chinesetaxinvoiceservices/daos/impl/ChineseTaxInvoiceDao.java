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
package de.hybris.platform.chinesetaxinvoiceservices.daos.impl;

import de.hybris.platform.chinesetaxinvoiceservices.daos.TaxInvoiceDao;
import de.hybris.platform.chinesetaxinvoiceservices.model.TaxInvoiceModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.ArrayList;
import java.util.List;


public class ChineseTaxInvoiceDao extends AbstractItemDao implements TaxInvoiceDao
{

	@Override
	public TaxInvoiceModel findInvoiceByCode(final String code)
	{

		final String fql = "SELECT {" + TaxInvoiceModel.PK + "} FROM {" + TaxInvoiceModel._TYPECODE + "} WHERE {"
				+ TaxInvoiceModel.PK + "} = ?code";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fql);
		query.addQueryParameter("code", code);

		final List<TaxInvoiceModel> taxInvoices = getFlexibleSearchService().<TaxInvoiceModel> search(query).getResult();

		if (taxInvoices != null && !taxInvoices.isEmpty())
		{
			return taxInvoices.get(0);
		}

		return null;
	}

	@Override
	public TaxInvoiceModel findInvoiceBySerialCode(final String serialCode)
	{

		final String fql = "SELECT {" + TaxInvoiceModel.PK + "} FROM {" + TaxInvoiceModel._TYPECODE + "} WHERE {"
				+ TaxInvoiceModel.SERIALCODE + "} = ?serialCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fql);
		query.addQueryParameter("serialCode", serialCode);

		final List<TaxInvoiceModel> taxInvoices = getFlexibleSearchService().<TaxInvoiceModel> search(query).getResult();

		if (taxInvoices != null && !taxInvoices.isEmpty())
		{
			return taxInvoices.get(0);
		}

		return null;
	}

	@Override
	public TaxInvoiceModel findInvoiceByOrder(final String orderCode)
	{

		final String fql = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE
				+ "} = ?orderCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fql);
		query.addQueryParameter("orderCode", orderCode);

		final List<OrderModel> orders = getFlexibleSearchService().<OrderModel> search(query).getResult();

		if (orders != null && !orders.isEmpty())
		{
			return orders.get(0).getTaxInvoice();
		}

		return null;
	}

	@Override
	public List<TaxInvoiceModel> findInvoicesByCustomer(final CustomerModel customer)
	{
		final String fql = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.USER
				+ "} = ?userId";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fql);
		query.addQueryParameter("userId", customer.getPk().toString());

		final List<OrderModel> orders = getFlexibleSearchService().<OrderModel> search(query).getResult();

		final List<TaxInvoiceModel> invoices = new ArrayList<>(0);
		if (orders != null && !orders.isEmpty())
		{
			for (final OrderModel order : orders)
			{
				invoices.add(order.getTaxInvoice());
			}
		}

		return invoices;
	}

}
