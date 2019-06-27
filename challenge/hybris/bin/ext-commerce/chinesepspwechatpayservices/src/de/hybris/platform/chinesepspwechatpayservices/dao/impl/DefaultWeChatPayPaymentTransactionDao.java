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
package de.hybris.platform.chinesepspwechatpayservices.dao.impl;

import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayPaymentTransactionDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;



public class DefaultWeChatPayPaymentTransactionDao extends DefaultGenericDao<WeChatPayPaymentTransactionModel>
		implements WeChatPayPaymentTransactionDao
{
	private static final Logger LOG = Logger.getLogger(DefaultWeChatPayPaymentTransactionDao.class);

	public DefaultWeChatPayPaymentTransactionDao()
	{
		super(WeChatPayPaymentTransactionModel._TYPECODE);
	}

	@Override
	public Optional<WeChatPayPaymentTransactionModel> findTransactionByLatestRequestEntry(final OrderModel orderModel,
			final boolean limit)
	{
		final String order = orderModel.getPk().getLongValueAsString();

		final StringBuilder queryString = new StringBuilder();
		queryString.append("select {" + WeChatPayPaymentTransactionModel.PK + "} from {"
				+ WeChatPayPaymentTransactionModel._TYPECODE + "} where {" + WeChatPayPaymentTransactionModel.PK + "} in"
				+ " ({{ select * from" + "({{ " + "select {" + PaymentTransactionModel._TYPECODE + "} from {"
				+ WeChatPayPaymentTransactionEntryModel._TYPECODE + " as entry join " + PaymentTransactionType._TYPECODE
				+ " as type on {entry.type}={type.pk} } where {type.code}='WECHAT_REQUEST'" + " and {entry.paymenttransaction} in ({{"
				+ "select {" + WeChatPayPaymentTransactionModel.PK + "} from {" + WeChatPayPaymentTransactionModel._TYPECODE
				+ " as t} where {t.order}=?order" + "}} )  order by {entry.time} desc limit 1 }}) as temp }})");

		if (limit)
		{
			queryString.append(" and {pk} in ({{select {" + PaymentTransactionModel._TYPECODE + "} from {"
					+ WeChatPayPaymentTransactionEntryModel._TYPECODE + "} group by {" + PaymentTransactionModel._TYPECODE
					+ "}  having  count(*)=1 }})");
		}
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());

		query.addQueryParameter("order", order);

		try
		{
			final WeChatPayPaymentTransactionModel result = getFlexibleSearchService().searchUnique(query);
			return Optional.ofNullable(result);
		}
		catch (final ModelNotFoundException e)//NOSONAR
		{
			LOG.info("No result for the given query for :" + order);
		}
		return Optional.empty();

	}


	@Override
	public Optional<WeChatPayPaymentTransactionModel> findTransactionByWeChatPayCode(final String weChatPayCode)
	{
		final String queryString = "select {pk} from {" + WeChatPayPaymentTransactionModel._TYPECODE + "} where {"
				+ WeChatPayPaymentTransactionModel.WECHATPAYCODE + "}=?weChatPayCode";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);

		query.addQueryParameter("weChatPayCode", weChatPayCode);

		final List<WeChatPayPaymentTransactionModel> searchResult = getFlexibleSearchService()
				.<WeChatPayPaymentTransactionModel> search(query).getResult();
		if (!searchResult.isEmpty())
		{
			return Optional.ofNullable(searchResult.get(0));
		}

		return Optional.empty();

	}


}
