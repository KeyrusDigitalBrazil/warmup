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
package de.hybris.platform.customerinterestsservices.hooks.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.daos.ProductInterestDao;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.service.hooks.CustomerSettingsChangedHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class ProductInterestCustomerSettingsChangedHook implements CustomerSettingsChangedHook
{
	private static final Logger LOG = Logger.getLogger(ProductInterestCustomerSettingsChangedHook.class);
	private ProductInterestDao productInterestDao;
	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;

	@Override
	public void afterUnbindMobileNumber(final CustomerModel customer)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();

		final List<ProductInterestModel> productInterests = getProductInterestDao().findProductInterestsByCustomer(customer,
				baseStoreModel, baseSiteModel);
		for(final ProductInterestModel productInterest : productInterests)
		{
			productInterest.setNotificationChannels(productInterest.getNotificationChannels().stream()
					.filter(c -> c != NotificationChannel.SMS)
					.collect(Collectors.toSet()));
			
			if (productInterest.getNotificationChannels().isEmpty())
			{
				getModelService().remove(productInterest);
				LOG.warn("Remove the productInterestData with only one SMS notification channel, because the mobile is unbound.");
			}
			else
			{
				getModelService().save(productInterest);
			}
		}
	}

	protected ProductInterestDao getProductInterestDao()
	{
		return productInterestDao;
	}

	@Required
	public void setProductInterestDao(final ProductInterestDao productInterestDao)
	{
		this.productInterestDao = productInterestDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}
