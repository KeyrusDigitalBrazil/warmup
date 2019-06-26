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
import de.hybris.platform.cmsfacades.util.builder.MediaModelBuilder;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.impl.MediaDao;


public class MediaModelMother extends AbstractModelMother<MediaModel>
{

	public enum MediaTemplate
	{
		LOGO("code-media-logo", "filename-media-logo", "mimetype-media-logo", "alt-text-media-logo", "description-media-logo",
				"url-media-logo"), //
		THUMBNAIL("code-media-thumbnail", "filename-media-thumbnail", "mimetype-media-thumbnail", "alt-text-media-thumbnail",
				"description-media-thumbnail", "url-media-thumbnail");

		private final String code;
		private final String filename;
		private final String mimetype;
		private final String altText;
		private final String description;
		private final String url;

		MediaTemplate(final String code, final String filename, final String mimetype, final String altText,
				final String description, final String url)
		{
			this.code = code;
			this.filename = filename;
			this.mimetype = mimetype;
			this.altText = altText;
			this.description = description;
			this.url = url;
		}

		public String getCode()
		{
			return code;
		}

		public String getFilename()
		{
			return filename;
		}

		public String getMimetype()
		{
			return mimetype;
		}

		public String getAltText()
		{
			return altText;
		}

		public String getDescription()
		{
			return description;
		}

		public String getUrl()
		{
			return url;
		}
	}

	private MediaDao mediaDao;
	private MediaFormatModelMother mediaFormatModelMother;

	public MediaModel createLogoMediaModel(final CatalogVersionModel catalogVersion)
	{
		return createMediaModel(catalogVersion, MediaTemplate.LOGO);
	}

	public MediaModel createThumbnailMediaModel(final CatalogVersionModel catalogVersion)
	{
		return createMediaModel(catalogVersion, MediaTemplate.THUMBNAIL);
	}

	public MediaModel createLogoMediaModelWithCode(final CatalogVersionModel catalogVersion, final String code)
	{
		return createMediaModelWithCode(catalogVersion, MediaTemplate.LOGO, code);
	}

	public MediaModel createMediaModel(final CatalogVersionModel catalogVersion, final MediaTemplate template)
	{
		return createMediaModelWithCode(catalogVersion, template, template.getCode());
	}

	protected MediaModel createMediaModelWithCode(final CatalogVersionModel catalogVersion, final MediaTemplate template,
			final String code)
	{
		return getFromCollectionOrSaveAndReturn(() -> getMediaDao().findMediaByCode(catalogVersion, code),
				() -> MediaModelBuilder.aModel() //
						.withCatalogVersion(catalogVersion) //
						.withCode(code) //
						.withRealFileName(template.getFilename()) //
						.withMimeType(template.getMimetype()) //
						.withInternalUrl(template.getUrl()) //
						.withAltText(template.getAltText()) //
						.withDescription(template.getDescription()) //
						.build());
	}

	public MediaModel createWidescreenLogoMediaModel(final CatalogVersionModel catalogVersion)
	{
		final MediaFormatModel widescreen = getMediaFormatModelMother().createWidescreenFormat();
		return createMediaModelWithFormat(catalogVersion, MediaTemplate.LOGO, widescreen);
	}

	public MediaModel createMobileLogoMediaModel(final CatalogVersionModel catalogVersion)
	{
		final MediaFormatModel mobile = getMediaFormatModelMother().createMobileFormat();
		return createMediaModelWithFormat(catalogVersion, MediaTemplate.LOGO, mobile);
	}

	protected MediaModel createMediaModelWithFormat(final CatalogVersionModel catalogVersion, final MediaTemplate template,
			final MediaFormatModel mediaFormat)
	{
		return getFromCollectionOrSaveAndReturn(
				() -> getMediaDao().findMediaByCode(catalogVersion, template.getCode() + "-" + mediaFormat.getQualifier()),
				() -> MediaModelBuilder.aModel() //
						.withCatalogVersion(catalogVersion) //
						.withCode(template.getCode() + "-" + mediaFormat.getQualifier()) //
						.withRealFileName(template.getFilename()) //
						.withMimeType(template.getMimetype()) //
						.withInternalUrl(template.getUrl()) //
						.withAltText(template.getAltText()) //
						.withDescription(template.getDescription()) //
						.withMediaFormat(mediaFormat).build());
	}

	public MediaDao getMediaDao()
	{
		return mediaDao;
	}

	public void setMediaDao(final MediaDao mediaDao)
	{
		this.mediaDao = mediaDao;
	}

	public MediaFormatModelMother getMediaFormatModelMother()
	{
		return mediaFormatModelMother;
	}

	public void setMediaFormatModelMother(final MediaFormatModelMother mediaFormatModelMother)
	{
		this.mediaFormatModelMother = mediaFormatModelMother;
	}
}
