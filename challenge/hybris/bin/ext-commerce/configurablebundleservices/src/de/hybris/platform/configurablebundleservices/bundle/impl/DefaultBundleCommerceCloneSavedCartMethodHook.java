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

package de.hybris.platform.configurablebundleservices.bundle.impl;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceCloneSavedCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Bundle specific hook for cloning saved carts
 */
public class DefaultBundleCommerceCloneSavedCartMethodHook implements CommerceCloneSavedCartMethodHook
{

	private ModelService modelService;

	@Override
	public void beforeCloneSavedCart(final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		return; //NOPMD
	}

	@Override
	public void afterCloneSavedCart(final CommerceSaveCartParameter parameters, final CommerceSaveCartResult cloneCartResult)
			throws CommerceSaveCartException
	{
		if (cloneCartResult != null && cloneCartResult.getSavedCart() != null &&
				CollectionUtils.isNotEmpty(cloneCartResult.getSavedCart().getLastModifiedEntries()))
		{
			// Clear last modified entries for cloned cart
			final CartModel clonedSavedCart = cloneCartResult.getSavedCart();
			for (final AbstractOrderEntryModel entry : clonedSavedCart.getEntries())
			{
				((CartEntryModel) entry).setLastModifiedMasterCart(null);
			}
			getModelService().saveAll(clonedSavedCart.getEntries());
			getModelService().refresh(clonedSavedCart);
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