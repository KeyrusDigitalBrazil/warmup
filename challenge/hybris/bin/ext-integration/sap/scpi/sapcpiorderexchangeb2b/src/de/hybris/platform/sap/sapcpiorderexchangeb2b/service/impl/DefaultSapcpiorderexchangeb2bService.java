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
package de.hybris.platform.sap.sapcpiorderexchangeb2b.service.impl;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.InputStream;
import java.util.Optional;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.sap.sapcpiorderexchangeb2b.service.Sapcpiorderexchangeb2bService;

/**
 * DefaultSapcpiorderexchangeb2bService
 */
public class DefaultSapcpiorderexchangeb2bService implements Sapcpiorderexchangeb2bService
{
	private static final Logger LOG = Logger.getLogger(DefaultSapcpiorderexchangeb2bService.class);

	private MediaService mediaService;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	@Override
	public String getHybrisLogoUrl(final String logoCode)
	{
		final MediaModel media = mediaService.getMedia(logoCode);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Found media [code: " + media.getCode() + "]");
		}

		return media.getURL();
	}

	@Override
	public void createLogo(final String logoCode)
	{
		final Optional<CatalogUnawareMediaModel> existingLogo = findExistingLogo(logoCode);

		final CatalogUnawareMediaModel media = existingLogo.isPresent() ? existingLogo.get()
				: modelService.create(CatalogUnawareMediaModel.class);
		media.setCode(logoCode);
		media.setRealFileName("sap-hybris-platform.png");
		modelService.save(media);

		mediaService.setStreamForMedia(media, getImageStream());
	}

	private final static String FIND_LOGO_QUERY = "SELECT {" + CatalogUnawareMediaModel.PK + "} FROM {"
			+ CatalogUnawareMediaModel._TYPECODE + "} WHERE {" + CatalogUnawareMediaModel.CODE + "}=?code";

	private Optional<CatalogUnawareMediaModel> findExistingLogo(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_LOGO_QUERY);
		fQuery.addQueryParameter("code", logoCode);

		try
		{
			return Optional.of(flexibleSearchService.searchUnique(fQuery));
		}
		catch (final SystemException e)
		{
			LOG.warn(e);
			LOG.warn(e.getMessage());
			return Optional.empty();
		}
	}

	private InputStream getImageStream()
	{
		return DefaultSapcpiorderexchangeb2bService.class.getResourceAsStream("/sapcpiorderexchangeb2b/sap-hybris-platform.png");
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
