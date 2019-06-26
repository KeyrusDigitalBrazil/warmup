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
package de.hybris.platform.stocknotificationservices.email.processor.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.notificationservices.processor.Processor;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stocknotificationservices.constants.StocknotificationservicesConstants;
import de.hybris.platform.stocknotificationservices.model.StockNotificationProcessModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Processor to send back-in-stock notification
 */
public class DefaultStockNotificationEmailProcessor implements Processor
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public void process(final CustomerModel customer, final Map<String, ? extends ItemModel> dataMap)
	{
		final LanguageModel language = (LanguageModel) dataMap.get(StocknotificationservicesConstants.LANGUAGE);
		final ProductInterestModel productInterest = (ProductInterestModel) dataMap
				.get(StocknotificationservicesConstants.PRODUCT_INTEREST);

		final StockNotificationProcessModel stockNotificationProcessModel =  getBusinessProcessService()
				.createProcess("productBackInStockNotificationEmailProcess-" + customer.getUid() + "-" + System.currentTimeMillis()
						+ "-" + Thread.currentThread().getId(),
						"productBackInStockNotificationEmailProcess");
		stockNotificationProcessModel.setLanguage(language);
		stockNotificationProcessModel.setProductInterest(productInterest);
		getModelService().save(stockNotificationProcessModel);
		getBusinessProcessService().startProcess(stockNotificationProcessModel);
	}

}
