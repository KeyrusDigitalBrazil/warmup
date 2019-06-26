/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.saprevenuecloudproduct.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.saprevenuecloudproduct.dao.SapRevenueCloudProductDao;

/**
 * Default implementation for {@link SapRevenueCloudProductDao}
 */
public class DefaultSapSubscriptionProductDao extends DefaultGenericDao<SubscriptionPricePlanModel>
		implements SapRevenueCloudProductDao {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapSubscriptionProductDao.class);
	private static final String GET_SUBSCRIPTION_PRICE_PLAN_FOR_ID = "SELECT {" + SubscriptionPricePlanModel.PK
			+ "} FROM { " + SubscriptionPricePlanModel._TYPECODE + " AS sp} WHERE {sp."
			+ SubscriptionPricePlanModel.PRICEPLANID + "} = ?" + SubscriptionPricePlanModel.PRICEPLANID + " AND {"
			+ SubscriptionPricePlanModel.CATALOGVERSION + "} = ?" + SubscriptionPricePlanModel.CATALOGVERSION;

	private static final String GET_CRONJOBHISTORY_FOR_CODE = "SELECT { cjh." + CronJobHistoryModel.PK + "} FROM {"
			+ CronJobHistoryModel._TYPECODE + " AS cjh JOIN " + CronJobModel._TYPECODE + " AS cj ON {cjh."
			+ CronJobHistoryModel.CRONJOB + "} = {cj." + CronJobModel.PK + "} JOIN " + CronJobStatus._TYPECODE
			+ " AS cjs ON {cjh." + CronJobHistoryModel.STATUS + "} = {cjs." + CronJobModel.PK + "} JOIN "
			+ CronJobResult._TYPECODE + " AS cjr ON {cjh." + CronJobHistoryModel.RESULT + "} = {cjr." + CronJobModel.PK
			+ "}} WHERE {cj." + CronJobModel.CODE + "}  = ?" + CronJobModel.CODE +  " AND {cjs." + CronJobModel.CODE
			+ "} =  'FINISHED' AND {cjr." + CronJobModel.CODE + "} = 'SUCCESS' ORDER BY {cjh."
			+ CronJobHistoryModel.STARTTIME + "} DESC LIMIT 1";

	public DefaultSapSubscriptionProductDao() {
		super(SubscriptionPricePlanModel._TYPECODE);
	}

	/**
	 * Get the subscription price plan for for a specific price plan ID
	 *
	 * @param pricePlanId - price plan ID for the subscription price plan
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	@Override
	public Optional<SubscriptionPricePlanModel> getSubscriptionPricePlanForId(final String pricePlanId,
			final CatalogVersionModel catalogVersion) {
		validateParameterNotNullStandardMessage("pricePlanId", pricePlanId);
		validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_SUBSCRIPTION_PRICE_PLAN_FOR_ID);
		query.addQueryParameter(SubscriptionPricePlanModel.PRICEPLANID, pricePlanId);
		query.addQueryParameter(SubscriptionPricePlanModel.CATALOGVERSION, catalogVersion);
		try {
			final SubscriptionPricePlanModel pricePlan = getFlexibleSearchService().searchUnique(query);
			return Optional.of(pricePlan);
		} catch (ModelNotFoundException | AmbiguousIdentifierException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Error while fetching the SubscriptionPricePlan for price plan ID:" + pricePlanId + ".Error :"
						+ e);
			}
			LOG.error(String.format(
					"Error while fetching the SubscriptionPricePlan for price plan ID [%s] and Catalog Version [%s:%s] ",
					pricePlanId, catalogVersion.getCatalog().getId(), catalogVersion.getVersion()));
			return Optional.empty();
		}

	}

	/**
	 * Get the {@link CronJobHistoryModel} with status 'FINISHED' and result 'SUCCESS'
	 *
	 * @param code - code for {@link CronJobModel}
	 *
	 * @return {@link CronJobHistoryModel}
	 */

	@Override
	public CronJobHistoryModel getLastSuccessRunForCronjob(String code) {
		validateParameterNotNullStandardMessage("code", code);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_CRONJOBHISTORY_FOR_CODE);
		query.addQueryParameter(CronJobModel.CODE, code);
		try {
			final List<CronJobHistoryModel> cronJobHistoryList = getFlexibleSearchService().<CronJobHistoryModel>search(query).getResult();
			if(!CollectionUtils.isEmpty(cronJobHistoryList))
			{
				return cronJobHistoryList.get(0);
			}
		} catch (ModelNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Error while fetching the CronJobHistory for code:" + code + ".Error :"
						+ e);
			}
			LOG.error(String.format(
					"Error while fetching the CronJobHistory for code [%s] ", code));
		}
		return null;
	}

}
