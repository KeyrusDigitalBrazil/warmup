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
package de.hybris.platform.cmsfacades.types.populator;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.OptionData;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate {@link ComponentTypeAttributeData#setOptions(List)} using the
 * {@link CMSMediaFormatService}.
 *
 * <p>
 * The option label in this case will have the following prefix <code>cms.media.format.</code> and terminate with the
 * qualifier of the media format in lower case. </br>
 * </br>
 *
 * Example: cms.media.format.mobile
 * </p>
 */
public class MediaFormatsComponentTypeAttributePopulator
implements Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{
	private TypeService typeService;
	private CMSMediaFormatService cmsMediaFormatService;

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target) throws ConversionException
	{
		final List<OptionData> options = new ArrayList<>();
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatService
				.getMediaFormatsByComponentType(typeService.getModelClass(source.getEnclosingType()));

		for (final MediaFormatModel mediaFormat : mediaFormats)
		{
			final OptionData option = new OptionData();
			option.setId(mediaFormat.getQualifier());
			option.setLabel("cms.media.format." + mediaFormat.getQualifier().toLowerCase());
			options.add(option);
		}
		target.setOptions(options);
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected CMSMediaFormatService getCmsMediaFormatService()
	{
		return cmsMediaFormatService;
	}

	@Required
	public void setCmsMediaFormatService(final CMSMediaFormatService cmsMediaFormatService)
	{
		this.cmsMediaFormatService = cmsMediaFormatService;
	}

}
