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
package de.hybris.platform.chinesepspalipayservices.dao.impl;

import de.hybris.platform.chinesepspalipayservices.dao.AlipayPaymentTransactionDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of {@link AlipayPaymentTransactionDao}
 */
public class DefaultAlipayPaymentTransactionDao extends DefaultGenericDao<AlipayPaymentTransactionModel>
		implements AlipayPaymentTransactionDao
{
	private static final Logger LOG = Logger.getLogger(DefaultAlipayPaymentTransactionDao.class);

	public DefaultAlipayPaymentTransactionDao()
	{
		super(AlipayPaymentTransactionModel._TYPECODE);
	}

	@Override
	public AlipayPaymentTransactionModel findTransactionByLatestRequestEntry(final OrderModel orderModel, final boolean limit)
	{
		final String order = orderModel.getPk().getLongValueAsString();

		final StringBuilder queryString = new StringBuilder();
		queryString.append("select {" + AlipayPaymentTransactionModel.PK + "} from {" + AlipayPaymentTransactionModel._TYPECODE
				+ "} where {" + AlipayPaymentTransactionModel.PK + "} in" + " ({{ select * from" + "({{ " + "select {"
				+ PaymentTransactionModel._TYPECODE + "} from {" + AlipayPaymentTransactionEntryModel._TYPECODE + " as entry join "
				+ PaymentTransactionType._TYPECODE + " as type on {entry.type}={type.pk} } where {type.code}='REQUEST'"
				+ " and {entry.paymenttransaction} in ({{" + "select {" + AlipayPaymentTransactionModel.PK + "} from {"
				+ AlipayPaymentTransactionModel._TYPECODE + " as t} where {t.order}=?order"
				+ "}} )  order by {entry.time} desc limit 1 }}) as temp }})");

		if (limit)
		{
			queryString.append(" and {pk} in ({{select {" + PaymentTransactionModel._TYPECODE + "} from {"
					+ AlipayPaymentTransactionEntryModel._TYPECODE + "} group by {" + PaymentTransactionModel._TYPECODE
					+ "}  having  count(*)=1 }})");
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());

		query.addQueryParameter("order", order);

		try
		{
			final AlipayPaymentTransactionModel result = getFlexibleSearchService().searchUnique(query);
			return result;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.info("No result for the given query for :" + order);
		}
		return null;

	}


	@Override
	public AlipayPaymentTransactionModel findTransactionByAlipayCode(final String alipayCode)
	{
		final String queryString = "select {pk} from {" + AlipayPaymentTransactionModel._TYPECODE + "} where {"
				+ AlipayPaymentTransactionModel.ALIPAYCODE + "}=?alipayCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);

		query.addQueryParameter("alipayCode", alipayCode);

		final List<AlipayPaymentTransactionModel> searchResult = getFlexibleSearchService()
				.<AlipayPaymentTransactionModel> search(query)
				.getResult();
		if (!searchResult.isEmpty())
		{
			return searchResult.get(0);
		}

		return null;

	}


}
