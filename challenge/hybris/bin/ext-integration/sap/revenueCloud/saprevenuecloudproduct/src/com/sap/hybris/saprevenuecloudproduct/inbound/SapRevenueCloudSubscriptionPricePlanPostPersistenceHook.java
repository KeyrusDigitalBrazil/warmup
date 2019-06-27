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
package com.sap.hybris.saprevenuecloudproduct.inbound;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

public class SapRevenueCloudSubscriptionPricePlanPostPersistenceHook implements  PostPersistHook{
	
	private ModelService modelService;
	private static final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudSubscriptionPricePlanPostPersistenceHook.class);

	@Override
	public void execute(ItemModel item) {
		LOG.info("The persistence hook  saprcSubscriptionPricePlanPostPersistenceHook is invoked");
		if(item instanceof SubscriptionPricePlanModel) {
			SubscriptionPricePlanModel pricePlan = (SubscriptionPricePlanModel)item;
			pricePlan.getProduct().getEurope1Prices().stream().filter(SubscriptionPricePlanModel.class::isInstance)
			.map(SubscriptionPricePlanModel.class::cast).filter(s -> !pricePlan.equals(s))
			.filter(s -> (s.getEndTime() != null && s.getEndTime().after(new Date()))).forEach(s -> {
				s.setEndTime(new Date());
				getModelService().save(s);
			});
		}
		
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
	
	

}
