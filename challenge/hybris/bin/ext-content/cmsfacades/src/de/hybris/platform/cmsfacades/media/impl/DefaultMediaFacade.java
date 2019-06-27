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
package de.hybris.platform.cmsfacades.media.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.InvalidNamedQueryException;
import de.hybris.platform.cms2.exceptions.SearchExecutionNamedQueryException;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;

import com.google.common.base.Preconditions;


/**
 * Default implementation of the media facade.
 */
public class DefaultMediaFacade implements MediaFacade
{
	private final Logger LOG = Logger.getLogger(DefaultMediaFacade.class);

	private MediaService mediaService;
	private ModelService modelService;
	private FacadeValidationService facadeValidationService;
	private NamedQueryService namedQueryService;
	private Validator namedQueryDataValidator;
	private Converter<MediaModel, MediaData> mediaModelConverter;
	private Converter<NamedQueryData, NamedQuery> mediaNamedQueryConverter;
	private CMSAdminSiteService adminSiteService;
	private Validator createMediaValidator;
	private Validator createMediaFileValidator;
	private Populator<MediaData, MediaModel> createMediaPopulator;
	private Populator<MediaFileDto, MediaModel> createMediaFilePopulator;
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public MediaData getMediaByUUID(final String uuid)
	{
		final MediaModel mediaModel = getUniqueItemIdentifierService().getItemModel(uuid, MediaModel.class)
				.orElseThrow(() -> new MediaNotFoundException("Media with uuid \"" + uuid + "\" not found."));

		return getMediaModelConverter().convert(mediaModel);
	}

	@Override
	public List<MediaData> getMediaByUUIDs(final List<String> uuids)
	{
		return uuids.stream().map(this::getMediaByUUID).collect(toList());
	}

	@Override
	public MediaData getMediaByCode(final String code)
	{
		final MediaModel media;
		try
		{
			final CatalogVersionModel catalogVersion = getAdminSiteService().getActiveCatalogVersion();

			media = getMediaService().getMedia(catalogVersion, code);

			return getMediaModelConverter().convert(media);

		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new MediaNotFoundException("Media with code \"" + code + "\" not found.", e);
		}
	}

	@Override
	public List<MediaData> getMediaByNamedQuery(final NamedQueryData namedQueryData)
	{
		Preconditions.checkArgument(namedQueryData != null);

		namedQueryData.setQueryType(MediaData.class);
		getFacadeValidationService().validate(getNamedQueryDataValidator(), namedQueryData);

		List<MediaModel> mediaModelList = new ArrayList<>();
		try
		{
			final NamedQuery namedQuery = getMediaNamedQueryConverter().convert(namedQueryData);
			mediaModelList = getNamedQueryService().search(namedQuery);
		}
		catch (ConversionException | InvalidNamedQueryException | SearchExecutionNamedQueryException e)
		{
			LOG.info("Unable to apply named query.", e);
		}

		return mediaModelList.stream() //
				.map(media -> getMediaModelConverter().convert(media)) //
				.collect(Collectors.toList());
	}

	@Override
	public MediaData addMedia(final MediaData media, final MediaFileDto mediaFile)
	{

		Preconditions.checkArgument(media != null);
		Preconditions.checkArgument(mediaFile != null);

		getFacadeValidationService().validate(getCreateMediaValidator(), media);
		getFacadeValidationService().validate(getCreateMediaFileValidator(), mediaFile);

		final MediaModel mediaModel = getModelService().create(MediaModel.class);
		getCreateMediaPopulator().populate(media, mediaModel);
		getCreateMediaFilePopulator().populate(mediaFile, mediaModel);
		getModelService().save(mediaModel);

		// can set a stream to an existing media model only
		populateStream(mediaFile, mediaModel);
		getModelService().save(mediaModel);

		return getMediaModelConverter().convert(mediaModel);
	}

	/**
	 * Populate the {@link MediaModel} with the InputStream provided by the source object.
	 *
	 * @param mediaFile
	 *           the {@link MediaFileDto} containing the file InputStream
	 * @param mediaModel
	 *           the {@link MediaModel} which InputStream is being set
	 */
	protected void populateStream(final MediaFileDto mediaFile, final MediaModel mediaModel)
	{
		try (InputStream inputStream = mediaFile.getInputStream())
		{
			getMediaService().setStreamForMedia(mediaModel, inputStream);
		}
		catch (final IOException e)
		{
			LOG.info(e);
		}
	}

	public MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected NamedQueryService getNamedQueryService()
	{
		return namedQueryService;
	}

	@Required
	public void setNamedQueryService(final NamedQueryService namedQueryService)
	{
		this.namedQueryService = namedQueryService;
	}

	protected Validator getNamedQueryDataValidator()
	{
		return namedQueryDataValidator;
	}

	@Required
	public void setNamedQueryDataValidator(final Validator namedQueryDataValidator)
	{
		this.namedQueryDataValidator = namedQueryDataValidator;
	}

	protected Converter<MediaModel, MediaData> getMediaModelConverter()
	{
		return mediaModelConverter;
	}

	@Required
	public void setMediaModelConverter(final Converter<MediaModel, MediaData> mediaModelConverter)
	{
		this.mediaModelConverter = mediaModelConverter;
	}

	protected Converter<NamedQueryData, NamedQuery> getMediaNamedQueryConverter()
	{
		return mediaNamedQueryConverter;
	}

	@Required
	public void setMediaNamedQueryConverter(final Converter<NamedQueryData, NamedQuery> mediaNamedQueryConverter)
	{
		this.mediaNamedQueryConverter = mediaNamedQueryConverter;
	}

	protected CMSAdminSiteService getAdminSiteService()
	{
		return adminSiteService;
	}

	@Required
	public void setAdminSiteService(final CMSAdminSiteService adminSiteService)
	{
		this.adminSiteService = adminSiteService;
	}

	protected Validator getCreateMediaValidator()
	{
		return createMediaValidator;
	}

	@Required
	public void setCreateMediaValidator(final Validator createMediaValidator)
	{
		this.createMediaValidator = createMediaValidator;
	}

	protected Validator getCreateMediaFileValidator()
	{
		return createMediaFileValidator;
	}

	@Required
	public void setCreateMediaFileValidator(final Validator createMediaFileValidator)
	{
		this.createMediaFileValidator = createMediaFileValidator;
	}

	protected Populator<MediaData, MediaModel> getCreateMediaPopulator()
	{
		return createMediaPopulator;
	}

	@Required
	public void setCreateMediaPopulator(final Populator<MediaData, MediaModel> createMediaPopulator)
	{
		this.createMediaPopulator = createMediaPopulator;
	}

	protected Populator<MediaFileDto, MediaModel> getCreateMediaFilePopulator()
	{
		return createMediaFilePopulator;
	}

	@Required
	public void setCreateMediaFilePopulator(final Populator<MediaFileDto, MediaModel> createMediaFilePopulator)
	{
		this.createMediaFilePopulator = createMediaFilePopulator;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}
}
