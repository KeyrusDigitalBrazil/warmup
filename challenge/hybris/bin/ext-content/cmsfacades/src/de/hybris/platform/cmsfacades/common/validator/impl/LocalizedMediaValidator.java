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
package de.hybris.platform.cmsfacades.common.validator.impl;


import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.LocalizedTypeValidator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;


/**
 * Default validator to use for validating localized attributes of type media. This implementation uses the
 * {@link MediaService} and the {@link CMSAdminSiteService} to verify whether a given media is valid or not.
 */
public class LocalizedMediaValidator implements LocalizedTypeValidator
{
	private MediaService mediaService;
	private CMSAdminSiteService cmsAdminSiteService;

	/*
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exceptions) : It is
	 * perfectly acceptable not to handle "e" here
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public void validate(final String language, final String fieldName, final String mediaCode, final Errors errors)
	{
		try
		{
			if (!Objects.isNull(mediaCode))
			{
				final MediaModel media = getMediaService().getMedia(getCmsAdminSiteService().getActiveCatalogVersion(), mediaCode);

				if (Objects.isNull(media))
				{
					reject(language, fieldName, CmsfacadesConstants.INVALID_MEDIA_CODE_L10N, errors);
				}
			}
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			reject(language, fieldName, CmsfacadesConstants.INVALID_MEDIA_CODE_L10N, errors);
		}
	}

	@Override
	public void reject(final String language, final String fieldName, final String errorCode, final Errors errors)
	{
		errors.rejectValue(fieldName, errorCode, new Object[]
				{ language }, null);
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}
}
