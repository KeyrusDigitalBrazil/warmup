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
package de.hybris.platform.cmsfacades.util.models;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.builder.MediaContainerModelBuilder;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.dao.MediaContainerDao;

import java.util.Arrays;
import java.util.List;


public class MediaContainerModelMother extends AbstractModelMother<MediaContainerModel>
{

	protected static final String MEDIA_CONTAINER_QUALIFIER = "simple-responsive-media-container";
	private MediaContainerDao mediaContainerDao;
	private MediaModelMother mediaModelMother;

	protected MediaContainerModel createMediaContainerModelWithQualifier(final CatalogVersionModel catalogVersion,
			final String qualifier, final List<MediaModel> mediaList)
	{
		return getFromCollectionOrSaveAndReturn(() -> getMediaContainerDao().findMediaContainersByQualifier(qualifier), () -> {
			return MediaContainerModelBuilder.aModel() //
					.withCatalogVersion(catalogVersion) //
					.withQualifier(qualifier) //
					.withMediaList(mediaList).build();
		});
	}

	public MediaContainerModel createEmptyMediaContainerModel(final CatalogVersionModel catalogVersion)
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getMediaContainerDao().findMediaContainersByQualifier(MEDIA_CONTAINER_QUALIFIER), () -> {
					return MediaContainerModelBuilder.aModel() //
							.withCatalogVersion(catalogVersion) //
							.withQualifier(MEDIA_CONTAINER_QUALIFIER) //
							.build();
				});
	}

	public MediaContainerModel createMediaContainerModelWithLogos(final CatalogVersionModel catalogVersion)
	{
		final MediaModel logoWidescreen = getMediaModelMother().createWidescreenLogoMediaModel(catalogVersion);
		final MediaModel logoMobile = getMediaModelMother().createMobileLogoMediaModel(catalogVersion);

		return createMediaContainerModelWithQualifier(catalogVersion, MEDIA_CONTAINER_QUALIFIER,
				Arrays.asList(logoWidescreen, logoMobile));
	}

	public MediaContainerDao getMediaContainerDao()
	{
		return mediaContainerDao;
	}

	public void setMediaContainerDao(final MediaContainerDao mediaContainerDao)
	{
		this.mediaContainerDao = mediaContainerDao;
	}

	public MediaModelMother getMediaModelMother()
	{
		return mediaModelMother;
	}

	public void setMediaModelMother(final MediaModelMother mediaModelMother)
	{
		this.mediaModelMother = mediaModelMother;
	}
}
