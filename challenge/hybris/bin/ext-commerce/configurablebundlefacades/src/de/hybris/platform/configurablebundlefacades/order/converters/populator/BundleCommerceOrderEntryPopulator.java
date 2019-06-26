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

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Converter for converting order / cart entries. It adds bundle specific data (bundle no, component) and flags the
 * entry as Updatable.
 *
 * @since 6.4
 * @deprecated Since 6.5: The populator fills in only deprecated fields, so it is deprecated, too.
 */
@Deprecated
public class BundleCommerceOrderEntryPopulator extends OrderEntryPopulator
{

	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	private BundleTemplateService bundleTemplateService;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);

		if (source.getOrder() == null)
		{
			return;
		}

		addCommon(source, target);
		target.setBundleNo(source.getBundleNo() == null ? 0 : source.getBundleNo().intValue());

		if (source.getBundleTemplate() != null)
		{
			target.setComponent(getBundleTemplateConverter().convert(source.getBundleTemplate()));
			if (source.getBundleTemplate().getParentTemplate() != null)
			{
				target.setRootBundleTemplate(getBundleTemplateConverter().convert(
						getBundleTemplateService().getRootBundleTemplate(
								source.getBundleTemplate().getParentTemplate()
						)
				));
			}
		}
	}

	protected Converter<BundleTemplateModel, BundleTemplateData> getBundleTemplateConverter()
	{
		return bundleTemplateConverter;
	}

	@Required
	public void setBundleTemplateConverter(final Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter)
	{
		this.bundleTemplateConverter = bundleTemplateConverter;
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
