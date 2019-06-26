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
package de.hybris.platform.customerinterestsservices.retention.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.retention.hook.ItemCleanupHook;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Hook to clean customer interests.
 */
public class CustomerInterestsCleanupHook implements ItemCleanupHook<CustomerModel>
{

	private ModelService modelService;

	@Override
	public void cleanupRelatedObjects(final CustomerModel customer)
	{
		validateParameterNotNullStandardMessage("customer", customer);

		final Collection<ProductInterestModel> interests = customer.getProductInterests();
		if (CollectionUtils.isNotEmpty(interests))
		{
			getModelService().removeAll(interests);
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

}
