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
package de.hybris.platform.marketplaceservices.vendor.impl;

import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.marketplaceservices.vendor.VendorUserService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation for {@link VendorUserService}.
 */
public class DefaultVendorUserService implements VendorUserService
{

	private ModelService modelService;

	@Override
	public void deactivateUser(VendorUserModel vendorUser)
	{
		vendorUser.setLoginDisabled(true);
		getModelService().save(vendorUser);
	}

	@Override
	public void activateUser(VendorUserModel vendorUser)
	{
		vendorUser.setLoginDisabled(false);
		getModelService().save(vendorUser);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

}
