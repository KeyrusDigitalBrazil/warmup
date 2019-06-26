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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.hook.CommerceCartMetadataUpdateMethodHook;
import de.hybris.platform.commerceservices.order.strategies.CommerceCartMetadataUpdateStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.List;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CommerceCartMetadataUpdateStrategy}
 */
public class DefaultCommerceCartMetadataUpdateStrategy implements CommerceCartMetadataUpdateStrategy
{
	private List<CommerceCartMetadataUpdateMethodHook> commerceCartMetadataUpdateMethodHooks;
	private ModelService modelService;

	@Override
	public void updateCartMetadata(CommerceCartMetadataParameter parameter)
	{
		validateParameterNotNull(parameter, "parameter cannot be null");
		validateParameterNotNull(parameter.getCart(), "cart property cannot be null");

		beforeUpdateMetadata(parameter);

		final boolean shouldSaveCart = doMetadataUpdate(parameter);

		afterUpdateMetadata(parameter);

		if (shouldSaveCart)
		{
			getModelService().save(parameter.getCart());
		}
	}

	protected boolean doMetadataUpdate(final CommerceCartMetadataParameter parameter)
	{
		final CartModel cart = parameter.getCart();
		boolean shouldSaveCart = false;

		if (parameter.getName().isPresent())
		{
			final String name = parameter.getName().get();
			cart.setName(StringUtils.isEmpty(name) ? null : name);
			shouldSaveCart = true;
		}

		if (parameter.getDescription().isPresent())
		{
			final String description = parameter.getDescription().get();
			cart.setDescription(StringUtils.isEmpty(description) ? null : description);
			shouldSaveCart = true;
		}

		if (parameter.isRemoveExpirationTime())
		{
			cart.setExpirationTime(null);
			shouldSaveCart = true;
		}
		else
		{
			if (parameter.getExpirationTime().isPresent())
			{
				final Date expirationTime = parameter.getExpirationTime().get();
				cart.setExpirationTime(expirationTime);
				shouldSaveCart = true;
			}
		}

		return shouldSaveCart;
	}

	protected void beforeUpdateMetadata(final CommerceCartMetadataParameter parameter)
	{
		if (parameter.isEnableHooks())
		{
			for (final CommerceCartMetadataUpdateMethodHook metadataUpdateMethodHook : getCommerceCartMetadataUpdateMethodHooks())
			{
				metadataUpdateMethodHook.beforeMetadataUpdate(parameter);
			}
		}
	}

	protected void afterUpdateMetadata(final CommerceCartMetadataParameter parameter)
	{
		if (parameter.isEnableHooks())
		{
			for (final CommerceCartMetadataUpdateMethodHook metadataUpdateMethodHook : getCommerceCartMetadataUpdateMethodHooks())
			{
				metadataUpdateMethodHook.afterMetadataUpdate(parameter);
			}
		}
	}

	protected List<CommerceCartMetadataUpdateMethodHook> getCommerceCartMetadataUpdateMethodHooks()
	{
		return commerceCartMetadataUpdateMethodHooks;
	}

	@Required
	public void setCommerceCartMetadataUpdateMethodHooks(
			final List<CommerceCartMetadataUpdateMethodHook> commerceCartMetadataUpdateMethodHooks)
	{
		this.commerceCartMetadataUpdateMethodHooks = commerceCartMetadataUpdateMethodHooks;
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
