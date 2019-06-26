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
package de.hybris.platform.customerinterestsservices.productinterest.daos.impl;

import static java.util.Objects.isNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.daos.ProductInterestDao;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

public class DefaultProductInterestDao extends DefaultGenericDao<ProductInterestModel> implements ProductInterestDao
{
	private PagedFlexibleSearchService pagedFlexibleSearchService;
	private static final Logger LOG = Logger.getLogger(DefaultProductInterestDao.class);
	private static final String CUSTOMER = "customer";
	private static final String BASESTORE = "baseStore";
	private static final String BASESITE = "baseSite";
	private static final String TODAY = "today";

	public DefaultProductInterestDao()
	{
		super(ProductInterestModel._TYPECODE);
	}

	@Override
	public List<ProductInterestModel> findProductInterestsByCustomer(final CustomerModel customerModel,
			final BaseStoreModel baseStore, final BaseSiteModel baseSite)
	{
		final String fsq = "SELECT {" + ProductInterestModel.PK + "} FROM {" + ProductInterestModel._TYPECODE + "} WHERE {"
				+ ProductInterestModel.CUSTOMER + "} = ?customer AND {" + ProductInterestModel.BASESTORE + "} = ?baseStore AND {"
				+ ProductInterestModel.BASESITE + "} = ?baseSite AND {" + ProductInterestModel.EXPIRYDATE + "} > ?today";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter(CUSTOMER, customerModel);
		query.addQueryParameter(BASESTORE, baseStore);
		query.addQueryParameter(BASESITE, baseSite);
		query.addQueryParameter(TODAY, new Date());
		final SearchResult<ProductInterestModel> result = getFlexibleSearchService().search(query);
		final List<ProductInterestModel> productInterestModels = result.getResult();
		if (productInterestModels != null && !productInterestModels.isEmpty())
		{
			return productInterestModels;
		}
		return Collections.emptyList();
	}

	@Override
	public Optional<ProductInterestModel> findProductInterest(final ProductModel productModel, final CustomerModel customerModel,
			final NotificationType notificationType, final BaseStoreModel baseStore, final BaseSiteModel baseSite)
	{
		final String fsq = "SELECT {pi.pk} FROM {ProductInterest as pi},{" + NotificationType._TYPECODE
				+ " as nt} WHERE {pi.notificationType} = {nt.pk} AND {pi.customer} = ?customer AND {pi.product} = ?product "
				+ "AND {nt.code} = ?notificationType AND {pi.baseStore} = ?baseStore AND {pi.baseSite} = ?baseSite "
				+ "AND {pi.expiryDate} > ?today";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter(CUSTOMER, customerModel);
		query.addQueryParameter("product", productModel);
		query.addQueryParameter("notificationType", notificationType.name());
		query.addQueryParameter(BASESTORE, baseStore);
		query.addQueryParameter(BASESITE, baseSite);
		query.addQueryParameter(TODAY, new Date());
		final SearchResult<ProductInterestModel> result = getFlexibleSearchService().search(query);
		final List<ProductInterestModel> productInterestModels = result.getResult();
		Assert.isTrue(productInterestModels.size() <= 1, "Product interest models more than one item.");
		if (!productInterestModels.isEmpty())
		{
			return Optional.of(productInterestModels.get(0));
		}
		return Optional.empty();
	}

	@Override
	public List<ProductInterestModel> findExpiredProductInterests()
	{
		final String fsq = "SELECT {" + ProductInterestModel.PK + "} FROM {" + ProductInterestModel._TYPECODE + "} WHERE {"
				+ ProductInterestModel.EXPIRYDATE + "} < ?today";
		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter(TODAY, new Date());
		final SearchResult<ProductInterestModel> result = getFlexibleSearchService().search(query);
		final List<ProductInterestModel> productInterestModels = result.getResult();
		if (productInterestModels != null && !productInterestModels.isEmpty())
		{
			return productInterestModels;
		}
		return Collections.emptyList();
	}

	@Override
	public Map<String, Map<String, String>> findProductsByCustomerInterests(final CustomerModel customerModel,
			final BaseStoreModel baseStore, final BaseSiteModel baseSite, final PageableData pageableData)
	{
		final FlexibleSearchQuery query = buildPagedProductInterestsQuery(customerModel, baseStore, baseSite, pageableData);
		final SearchResult<List<String>> result = getFlexibleSearchService().search(query);


		final List<List<String>> resultList = result.getResult();

		if (resultList == null || resultList.isEmpty())
		{

			return Collections.emptyMap();
		}
		final Map<String, Map<String, String>> productPKMap = new LinkedHashMap<>();
		resultList.stream().forEach(productNotification -> fillProductPKMap(productPKMap, productNotification));

		return productPKMap;
	}

	@Override
	public Map<String, Map<String, String>> findProductInterestRelationsByCustomer(final CustomerModel customerModel,
			final BaseStoreModel baseStore, final BaseSiteModel baseSite)
	{
		final FlexibleSearchQuery query = buildProductInterestsQuery(customerModel, baseStore, baseSite);
		final SearchResult<List<String>> result = getFlexibleSearchService().search(query);
		final List<List<String>> resultList = result.getResult();
		if (CollectionUtils.isEmpty(resultList))
		{
			return Collections.emptyMap();
		}

		final Map<String, Map<String, String>> productNotificationMap = new LinkedHashMap<>();
		resultList.forEach(productNotification -> fillProductNotificationMap(productNotificationMap, productNotification));
		return productNotificationMap;
	}

	@Override
	public int findProductsCountByCustomerInterests(final CustomerModel customerModel, final BaseStoreModel baseStore,
			final BaseSiteModel baseSite, final PageableData pageableData)
	{
		final FlexibleSearchQuery query = buildPagedProductInterestsQuery(customerModel, baseStore, baseSite, pageableData);
		final SearchResult<List<String>> result = getFlexibleSearchService().search(query);
		return result.getTotalCount();
	}

	protected FlexibleSearchQuery buildPagedProductInterestsQuery(final CustomerModel customerModel,
			final BaseStoreModel baseStore, final BaseSiteModel baseSite, final PageableData pageableData)
	{
		final String fsq = "SELECT {PI:" + ProductInterestModel.PRODUCT + "}" + ",GROUP_CONCAT({PI:"
				+ ProductInterestModel.NOTIFICATIONTYPE + "}),GROUP_CONCAT({PI:" + ProductInterestModel.CREATIONTIME
				+ "}) FROM {" + ProductInterestModel._TYPECODE
				+ " as PI } WHERE {" + ProductInterestModel.CUSTOMER + "} = ?customer AND {"
				+ ProductInterestModel.BASESTORE + "} = ?baseStore AND {" + ProductInterestModel.BASESITE + "} = ?baseSite AND {"
				+ ProductInterestModel.EXPIRYDATE + "} > ?today" + " GROUP BY {PI:" + ProductInterestModel.PRODUCT + "}";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter(CUSTOMER, customerModel);
		query.addQueryParameter(BASESTORE, baseStore);
		query.addQueryParameter(BASESITE, baseSite);
		query.addQueryParameter(TODAY, new Date());
		query.setNeedTotal(true);
		query.setStart(pageableData.getCurrentPage() * pageableData.getPageSize());
		query.setCount(pageableData.getPageSize());


		query.setResultClassList(Arrays.asList(String.class, String.class, String.class));
		return query;
	}

	protected FlexibleSearchQuery buildProductInterestsQuery(final CustomerModel customerModel, final BaseStoreModel baseStore,
			final BaseSiteModel baseSite)
	{
		final String fsq = "SELECT {PI:" + ProductInterestModel.PRODUCT + "}" + ",GROUP_CONCAT({PI:"
				+ ProductInterestModel.NOTIFICATIONTYPE + "}),GROUP_CONCAT({PI:" + ProductInterestModel.CREATIONTIME
				+ "}),GROUP_CONCAT({PI:" + ProductInterestModel.EXPIRYDATE + "}) FROM {" + ProductInterestModel._TYPECODE
				+ " as PI } WHERE {" + ProductInterestModel.CUSTOMER + "} = ?customer AND {"
				+ ProductInterestModel.BASESTORE + "} = ?baseStore AND {" + ProductInterestModel.BASESITE
				+ "} = ?baseSite GROUP BY {PI:" + ProductInterestModel.PRODUCT + "}";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(fsq);
		query.addQueryParameter(CUSTOMER, customerModel);
		query.addQueryParameter(BASESTORE, baseStore);
		query.addQueryParameter(BASESITE, baseSite);
		query.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class));
		return query;
	}

	protected void fillProductPKMap(final Map<String, Map<String, String>> productNotificationTypesMap,
			final List<String> productNotification)
	{
		final String productPK = productNotification.get(0);
		final String notificationCollection = productNotification.get(1);
		final String creationTimeCollection = productNotification.get(2);
		final List<String> notificationTypeList = Arrays.asList(notificationCollection.split(","));
		final List<String> creationTimeList = Arrays.asList(creationTimeCollection.split(","));
		final Map<String, String> interestCreationMap = new LinkedHashMap<>();

		for (int i = 0; i < notificationTypeList.size(); i++)
		{
			interestCreationMap.put(notificationTypeList.get(i), creationTimeList.get(i));

		}
		productNotificationTypesMap.put(productPK, interestCreationMap);
	}

	protected void fillProductNotificationMap(final Map<String, Map<String, String>> productNotificationTypesMap,
			final List<String> productNotification)
	{
		final String productPK = productNotification.get(0);
		final String notificationCollection = productNotification.get(1);
		final String creationTimeCollection = productNotification.get(2);
		final String expiryDateCollection = productNotification.get(3);
		final List<String> notificationTypeList = Arrays.asList(notificationCollection.split(","));
		final List<String> creationTimeList = Arrays.asList(creationTimeCollection.split(","));
		final List<String> expiryDateList = Arrays.asList(expiryDateCollection.split(","));

		final Map<String, String> interestCreationMap = new LinkedHashMap<>();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < notificationTypeList.size(); i++)
		{
			final String expiryTime = expiryDateList.get(i);
			final DateTime expiryDate = formatDate(expiryTime, sdf);
			if (expiryDate.isAfterNow())
			{
				interestCreationMap.put(notificationTypeList.get(i), creationTimeList.get(i));
				productNotificationTypesMap.put(productPK, interestCreationMap);
			}
		}

	}

	protected DateTime formatDate(final String expiryTime, final SimpleDateFormat sdf)
	{
		Date expiryDate = null;
		try
		{
			expiryDate = sdf.parse(expiryTime);
		}
		catch (final ParseException e)
		{
			LOG.error("Can't parse " + expiryTime + " Date format");
		}
		return isNull(expiryDate) ? DateTime.now() : new DateTime(expiryDate);

	}

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

}
