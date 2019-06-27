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
package com.sap.hybris.saprevenuecloudproduct.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Date;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.sap.hybris.saprevenuecloudproduct.dao.SapRevenueCloudProductDao;
import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;


/**
 * Default implementation for {@link SapRevenueCloudProductService}
 */
public class DefaultSapRevenueCloudProductService implements SapRevenueCloudProductService
{

	private SapRevenueCloudProductDao sapRevenueCloudProductDao;
	private GenericDao<SAPRatePlanElementModel> sapRatePlanElementDao;
	private GenericDao<UsageUnitModel> usageUnitDao;

	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudProductService.class);

	/**
	 * Get the subscription price plan for a specific priceplan ID
	 *
	 * @param pricePlanId
	 *           - price plan ID
	 *
	 * @return SubscriptionPricePlanModel
	 */
	@Override
	public SubscriptionPricePlanModel getSubscriptionPricePlanForId(final String pricePlanId,
			final CatalogVersionModel catalogVersion)
	{
		validateParameterNotNull(pricePlanId, "Price plan cannot be null");
		validateParameterNotNull(catalogVersion, "Catalog Version cannot be null");
		Optional<SubscriptionPricePlanModel> pricePlanOpt = getSapRevenueCloudProductDao().getSubscriptionPricePlanForId(pricePlanId, catalogVersion);
		if(pricePlanOpt.isPresent())
		{
			return pricePlanOpt.get();
		}
		return null;

	}
	
	@Override
	public SAPRatePlanElementModel getRatePlanElementfromId(final String id)
	{
		return getSapRatePlanElementDao().find().stream()
					.filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public UsageUnitModel getUsageUnitfromId(String id) {
		return getUsageUnitDao().find().stream().filter(e->e.getId().equals(id)).findFirst().orElse(null);
	}
	
	@Override
	public Date getProductReplicationDateForCronjob(String code) {
		CronJobHistoryModel cronjobHistory = getSapRevenueCloudProductDao().getLastSuccessRunForCronjob(code);
		if(null != cronjobHistory)
		{
			LOG.info(String.format("Cronjob with code [%s] has run successfully for date [%s]", code, cronjobHistory.getStartTime()));
			return cronjobHistory.getStartTime();
		}
		return null;
	}

	
	/**
	 * @return the sapRevenueCloudProductDao
	 */
	public SapRevenueCloudProductDao getSapRevenueCloudProductDao()
	{
		return sapRevenueCloudProductDao;
	}

	/**
	 * @param sapRevenueCloudProductDao
	 *           the sapRevenueCloudProductDao to set
	 */
	public void setSapRevenueCloudProductDao(final SapRevenueCloudProductDao sapRevenueCloudProductDao)
	{
		this.sapRevenueCloudProductDao = sapRevenueCloudProductDao;
	}


	public GenericDao<SAPRatePlanElementModel> getSapRatePlanElementDao() {
		return sapRatePlanElementDao;
	}


	public void setSapRatePlanElementDao(GenericDao<SAPRatePlanElementModel> sapRatePlanElementDao) {
		this.sapRatePlanElementDao = sapRatePlanElementDao;
	}
	
	public GenericDao<UsageUnitModel> getUsageUnitDao() {
		return usageUnitDao;
	}

	public void setUsageUnitDao(GenericDao<UsageUnitModel> usageUnitDao) {
		this.usageUnitDao = usageUnitDao;
	}





}