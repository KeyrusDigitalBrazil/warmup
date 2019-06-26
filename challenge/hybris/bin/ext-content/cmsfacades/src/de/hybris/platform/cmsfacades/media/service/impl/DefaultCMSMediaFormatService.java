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
package de.hybris.platform.cmsfacades.media.service.impl;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaFormatDao;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.core.model.media.MediaFormatModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of <code>CMSMediaFormatService</code> interface.
 */
public class DefaultCMSMediaFormatService implements CMSMediaFormatService
{
	private Map<Class<? extends AbstractCMSComponentModel>, Collection<String>> cmsComponentMediaFormats;
	private CMSMediaFormatDao cmsMediaFormatDao;

	@Override
	public Collection<MediaFormatModel> getMediaFormatsByComponentType(final Class<? extends AbstractCMSComponentModel> classType)
	{
		Collection<MediaFormatModel> mediaFormats;
		final Optional<Collection<String>> formatQualifiers = getMediaFormatsByType(classType);
		if (formatQualifiers.isPresent())
		{
			mediaFormats = getCmsMediaFormatDao().getMediaFormatsByQualifiers(formatQualifiers.get());
		}
		else
		{
			mediaFormats = Collections.emptyList();
		}
		return mediaFormats;
	}

	/**
	 * Get the format list by recursively walking up in the hierarchy class type, until it either finds the collection or
	 * reaches the upper level and returns and empty optional.
	 * 
	 * @param classType
	 *           the class of any type until Object
	 * @return the format collection or empty if it cannot find the configuration in the map.
	 */
	protected Optional<Collection<String>> getMediaFormatsByType(final Class classType)
	{
		if (classType == null)
		{
			return Optional.empty();
		}
		else if (getCmsComponentMediaFormats().containsKey(classType))
		{
			return Optional.of(getCmsComponentMediaFormats().get(classType));
		}
		else
		{
			return getMediaFormatsByType(classType.getSuperclass());
		}
	}

	protected Map<Class<? extends AbstractCMSComponentModel>, Collection<String>> getCmsComponentMediaFormats()
	{
		return cmsComponentMediaFormats;
	}

	@Required
	public void setCmsComponentMediaFormats(
			final Map<Class<? extends AbstractCMSComponentModel>, Collection<String>> cmsComponentMediaFormats)
	{
		this.cmsComponentMediaFormats = cmsComponentMediaFormats;
	}

	protected CMSMediaFormatDao getCmsMediaFormatDao()
	{
		return cmsMediaFormatDao;
	}

	@Required
	public void setCmsMediaFormatDao(final CMSMediaFormatDao cmsMediaFormatDao)
	{
		this.cmsMediaFormatDao = cmsMediaFormatDao;
	}


}
