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

package de.hybris.platform.notificationservices.retention.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.service.SiteMessageService;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Hook to clean customer sitemessages.
 */
public class SiteMessageCleanupHook implements ItemCleanupHook<CustomerModel>
{

	private ModelService modelService;
	private SiteMessageService siteMessageService;

	@Override
	public void cleanupRelatedObjects(final CustomerModel customer)
	{
		validateParameterNotNullStandardMessage("customer", customer);
		final List<SiteMessageForCustomerModel> models = siteMessageService.getSiteMessagesForCustomer(customer);
		if (CollectionUtils.isNotEmpty(models))
		{
			modelService.removeAll(models);
		}
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

	protected SiteMessageService getSiteMessageService()
	{
		return siteMessageService;
	}

	@Required
	public void setSiteMessageService(final SiteMessageService siteMessageService)
	{
		this.siteMessageService = siteMessageService;
	}

}
