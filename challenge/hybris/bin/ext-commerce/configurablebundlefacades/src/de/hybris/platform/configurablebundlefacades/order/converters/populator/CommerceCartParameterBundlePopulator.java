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
package de.hybris.platform.configurablebundlefacades.order.converters.populator;


import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Popualtes bundle-specific fields from {@link AddToCartParams}
 * into {@link CommerceCartParameter}.
 */
public class CommerceCartParameterBundlePopulator implements Populator<AddToCartParams, CommerceCartParameter>
{
	private static final Logger LOG = Logger.getLogger(CommerceCartParameterBundlePopulator.class);

	private BundleTemplateService bundleTemplateService;

	@Override
	public void populate(@Nonnull final AddToCartParams addToCartParams, @Nonnull final CommerceCartParameter parameter)
			throws ConversionException
	{
		validateParameterNotNullStandardMessage("addToCartParams", addToCartParams);
		validateParameterNotNullStandardMessage("parameter", parameter);
		
		if (StringUtils.isNotEmpty(addToCartParams.getBundleTemplateId()))
		{
			try
			{
				parameter
						.setBundleTemplate(getBundleTemplateService().getBundleTemplateForCode(addToCartParams.getBundleTemplateId()));
			}
			catch (final ModelNotFoundException e)
			{
				LOG.warn("Bundle template " + addToCartParams.getBundleTemplateId() + " was not found.", e);
			}
		}
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
