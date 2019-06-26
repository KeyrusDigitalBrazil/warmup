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
package de.hybris.platform.b2b.dao.impl;

import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link B2BOrderDao}
 *
 * @spring.bean b2bOrderDao
 */
public class DefaultB2BOrderDao extends DefaultGenericDao<OrderModel> implements B2BOrderDao
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BOrderDao.class);
	
	/**
	 * @deprecated Since 4.4.
	 */
	@Deprecated
	private BaseDao baseDao;
	private TypeService typeService;

	/**
	 * DefaultGenericDao is only usable when typecode is set.
	 */
	public DefaultB2BOrderDao()
	{
		super(OrderModel._TYPECODE);
	}

	@Override
	public List<OrderModel> findRejectedOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.REJECTED);
	}

	@Override
	public List<OrderModel> findApprovedOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.APPROVED);
	}

	@Override
	public List<OrderModel> findPendingApprovalOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.PENDING_APPROVAL);
	}

	@Override
	public <T extends OrderModel> T findOrderByCode(final String code)
	{

		final Map<String, Object> attr = new HashMap<String, Object>(1);
		attr.put(OrderModel.CODE, code);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {o:pk} from { ").append(OrderModel._TYPECODE).append(" as o} WHERE {o:code} = ?code ")
				.append("AND {o:" + OrderModel.VERSIONID + "} IS NULL");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);
		final List<OrderModel> orders = result.getResult();
		return orders.isEmpty() ? null : (T) orders.get(0);
	}

	@Override
	public List<OrderModel> findOrdersByStatus(final UserModel user, final OrderStatus status)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.STATUS, status);
		attr.put(OrderModel.USER, user);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {o:pk} from { ").append(OrderModel._TYPECODE).append(" as o} WHERE {o:user} = ?user ")
				.append(" and {o:status} = ?status AND {" + OrderModel.VERSIONID + "} IS NULL ORDER BY {o.date} DESC");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@Override
	public List<OrderModel> findOrdersByStatus(final UserModel user, final List<OrderStatus> status)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.STATUS, status);
		attr.put(OrderModel.USER, user);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {o:pk} from { ").append(OrderModel._TYPECODE).append(" as o} WHERE {o:user} = ?user ")
				.append(" and {o:status} in (?status) AND {" + OrderModel.VERSIONID + "} IS NULL ORDER BY {o.date} DESC");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}

	/**
	 * @deprecated Since 4.4. Use {@link #findOrdersApprovedByDateRange(UserModel,Date,Date)} instead
	 */
	@Deprecated
	@Override
	public List<OrderModel> findOrdersApprovedForDateRange(final UserModel user, final Date startDate, final Date endDate)
	{
		return findOrdersApprovedByDateRange(user, startDate, endDate);
	}

	@Override
	public List<OrderModel> findOrdersApprovedByDateRange(final UserModel user, final Date startDate, final Date endDate)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.STATUS, OrderStatus.APPROVED);
		attr.put(OrderModel.USER, user);
		attr.put("startDate", startDate);
		attr.put("endDate", endDate);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {o:pk} from { ")
				.append(OrderModel._TYPECODE)
				.append(" as o} WHERE {o:user} = ?user ")
				.append(
							" and {o:status} = ?status and {o:date} >= ?startDate and {o:date} <= ?endDate AND {" + OrderModel.VERSIONID
						+ "} IS NULL ORDER BY {o.date} DESC");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}

	@Override
	public List<OrderModel> findOrdersApprovedForDateRangeByCurrency(final B2BUnitModel unitModel, final Date startDate,
			final Date endDate, final CurrencyModel currency)
	{

		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.STATUS, OrderStatus.APPROVED);
		attr.put("startDate", startDate);
		attr.put("endDate", endDate);
		attr.put("unitUID", unitModel.getUid());
		attr.put("currency", currency.getIsocode());
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {o:pk} from { ")
				.append(OrderModel._TYPECODE)
				.append(" as o}, {B2BUnit as u} , {Currency as c} WHERE {o:unit} = {u:pk} and {u:uid}=?unitUID")
				.append(" and {o:status} = ?status and {o.currency}={c.pk} and {c.isocode}=?currency")
				.append(
							" and {o:date} >= ?startDate and {o:date} <= ?endDate AND {" + OrderModel.VERSIONID
						+ "} IS NULL ORDER BY {o.date} DESC");
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<OrderModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}

	/**
	 * Finds CartToOrderCronJob for a given user.
	 *
	 * @deprecated Since 6.0.  Use {@link DefaultCartToOrderCronJobModelDao#findCartToOrderCronJobs(UserModel)} instead
	 */
	@Deprecated
	@Override
	public List<CartToOrderCronJobModel> findCartToOrderCronJobs(final UserModel user)
	{
		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.USER, user);
		attr.put(CartToOrderCronJobModel.ACTIVE, Boolean.TRUE);
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {soj:pk} FROM { ")
				.append(CartToOrderCronJobModel._TYPECODE)
				.append(" as soj JOIN ")
				.append(CartModel._TYPECODE)
				.append(" as c ON {soj.cart} = {c:pk} } ")
				.append(
							" WHERE {soj:active} = ?active and {c:user} = ?user AND {" + OrderModel.VERSIONID
						+ "} IS NULL ORDER BY {c.date} DESC");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult<CartToOrderCronJobModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}

	/**
	 * @deprecated Since 6.0. Use {@link DefaultCartToOrderCronJobModelDao#findCartToOrderCronJob(String)} instead
	 */
	@Deprecated
	public CartToOrderCronJobModel findCartToOrderModel(final String code)
	{
		return baseDao.findFirstByAttribute(CartToOrderCronJobModel.CODE, code, CartToOrderCronJobModel.class);
	}

	@Override
	public List<OrderModel> findRejectedByMerchantOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.REJECTED_BY_MERCHANT);
	}

	@Override
	public List<OrderModel> findPendingApprovalOrdersFromMerchant(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.PENDING_APPROVAL_FROM_MERCHANT);
	}

	/**
	 * @deprecated Since 4.4. Use {@link #findOrderTotalsByDateRangeAndCurrency(B2BUnitModel,Date,Date,CurrencyModel)} instead
	 */
	@Deprecated
	@Override
	public Double findOrderTotalsForDateRangeByCurrency(final B2BUnitModel unitModel, final Date startDate, final Date endDate,
			final CurrencyModel currency)
	{
		return findOrderTotalsByDateRangeAndCurrency(unitModel, startDate, endDate, currency);
	}

	@Override
	public Double findOrderTotalsByDateRangeAndCurrency(final B2BUnitModel unitModel, final Date startDate, final Date endDate,
			final CurrencyModel currency)
	{
		Double total = new Double(0D);

		final Map<String, Object> attr = new HashMap<String, Object>();
		attr.put(OrderModel.STATUS, OrderStatus.APPROVED);
		attr.put("startDate", startDate);
		attr.put("endDate", endDate);
		attr.put("b2bUnit", unitModel);
		attr.put("currency", currency.getIsocode());
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT sum({o:totalprice} * {c2.conversion}/{c.conversion}) ").append(" FROM { ").append(OrderModel._TYPECODE)
				.append(" as o}, {Currency as c}, {Currency as c2} ").append(" WHERE {o:unit} = ?b2bUnit ")
				.append(" and {o:status} in (?status) and {o.currency}={c.pk} and {c2.isocode}=?currency")
				.append(" and {o:date} >= ?startDate and {o:date} <= ?endDate AND {" + OrderModel.VERSIONID + "} IS NULL ");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		query.setResultClassList(Collections.singletonList(Double.class));
		final SearchResult<Double> result = this.getFlexibleSearchService().search(query);

		if (result != null && !CollectionUtils.isEmpty(result.getResult()))
		{
			total = result.getResult().get(0);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Unit: " + unitModel.getUid() + " Total: " + total);
		}
		return (total == null ? new Double(0D) : total);
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> findPendingQuoteOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.PENDING_QUOTE);
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> findApprovedQuoteOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.APPROVED_QUOTE);
	}

	/**
	 * @deprecated Since 6.3.
	 */
	@Deprecated
	@Override
	public List<OrderModel> findRejectedQuoteOrders(final UserModel user)
	{
		return this.findOrdersByStatus(user, OrderStatus.REJECTED_QUOTE);
	}


	public BaseDao getBaseDao()
	{
		return baseDao;
	}

	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
