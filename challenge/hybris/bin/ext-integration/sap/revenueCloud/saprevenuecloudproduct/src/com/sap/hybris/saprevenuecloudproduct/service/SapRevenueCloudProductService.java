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
package com.sap.hybris.saprevenuecloudproduct.service;

import java.util.Date;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;


/**
 * Sap Reveunue Cloud Product Service interface
 */
public interface SapRevenueCloudProductService
{

	/**
	 * get the Subscription price for a specific price plan ID
	 *
	 * @param pricePlanId
	 *           - price plan ID for the {@link SubscriptionPricePlanModel}
	 * @param catalogVersion
	 *           - catalog Version for the {@link SubscriptionPricePlanModel}          
	 *           
	 *
	 * @return {@link SubscriptionPricePlanModel}
	 */
	SubscriptionPricePlanModel getSubscriptionPricePlanForId(final String pricePlanId, CatalogVersionModel catalogVersion);
	
	/**
	 * Get the {@link SAPRatePlanElementModel} from {@code id}
	 * @param label
	 * 			- label of {@link SAPRatePlanElementModel }
	 * @return
	 * 		ratePlan element
	 */
	SAPRatePlanElementModel getRatePlanElementfromId(final String label);
	
	/**
	 * 
	 * Get {@link UsageUnitModel} from {@code id}
	 * @param id
	 * 			- id of {@link UsageUnitModel }
	 * @return
	 * 		usage unit
	 */
	UsageUnitModel getUsageUnitfromId(final String id);
	
	/**
	 * Get last success run date for Cronjob 
	 * @param code
	 * 			- code of {@link CronJobModel }
	 * @return
	 * 		{@link Date}
	 */
	
	Date getProductReplicationDateForCronjob(final String code);

}
