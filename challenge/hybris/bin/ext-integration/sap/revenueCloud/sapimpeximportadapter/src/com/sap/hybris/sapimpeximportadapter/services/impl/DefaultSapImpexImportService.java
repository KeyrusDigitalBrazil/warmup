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
package com.sap.hybris.sapimpeximportadapter.services.impl;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.MediaBasedImpExResource;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapimpeximportadapter.constants.SapimpeximportadapterConstants;
import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;


/**
 * Default implementation for {@link ImpexImportService}
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel} for
 *             data import
 */
@Deprecated
public class DefaultSapImpexImportService implements ImpexImportService
{

	private static final int SINGLE_THREAD = 1;
	private static final boolean SYNCHRONOUS = true;

	private final Logger LOG = LoggerFactory.getLogger(DefaultSapImpexImportService.class);

	private MediaService mediaService;
	private ModelService modelService;
	private ImportService importService;

	/**
	 * Creates the {@link ImpExMediaModel} from the {@link InputStream}
	 *
	 * @param impexStream
	 *           - impex stream input
	 *
	 * @return {@link ImpExMediaModel}
	 */
	@Override
	public ImpExMediaModel createImpexMedia(final InputStream impexStream)
	{
		return createImpexMedia(impexStream, UUID.randomUUID().toString());
	}

	/**
	 * Creates the {@link ImpExMediaModel} from the {@link InputStream}. the method adds a generated code for the
	 * {@link ImpExMediaModel}
	 *
	 * @param impexStream
	 *           - impex stream input
	 * @param code
	 *           - generated UUID code
	 *
	 * @return {@link ImpExMediaModel}
	 */
	private ImpExMediaModel createImpexMedia(final InputStream impexStream, final String code)
	{
		final ImpExMediaModel impexMediaModel = modelService.create(ImpExMediaModel.class);
		impexMediaModel.setMime(SapimpeximportadapterConstants.IMPEX_MIMETYPE);
		impexMediaModel.setCode(code);
		impexMediaModel.setFolder(mediaService.getFolder(SapimpeximportadapterConstants.IMPEX_FOLDER));
		modelService.save(impexMediaModel);
		mediaService.setStreamForMedia(impexMediaModel, impexStream);
		LOG.debug(String.format("Impex media created [%s] ", impexMediaModel.getCode()));
		return impexMediaModel;
	}

	/**
	 * Imports the impex media and returns the {@link ImportResult}
	 *
	 * @param impexMedia
	 *           - impex media input
	 *
	 * @return {@link ImportResult}
	 */
	@Override
	public ImportResult importMedia(final ImpExMediaModel impexMedia)
	{
		final ImportConfig importConfig = new ImportConfig();
		importConfig.setRemoveOnSuccess(impexMedia.isRemoveOnSuccess());
		importConfig.setMaxThreads(SINGLE_THREAD);
		importConfig.setSynchronous(SYNCHRONOUS);
		importConfig.setScript(new MediaBasedImpExResource(impexMedia));
		final ImportResult result = importService.importData(importConfig);
		return result;
	}

	/**
	 * @return the mediaService
	 */
	public MediaService getMediaService()
	{
		return mediaService;
	}

	/**
	 * @param mediaService
	 *           the mediaService to set
	 */
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the importService
	 */
	public ImportService getImportService()
	{
		return importService;
	}

	/**
	 * @param importService
	 *           the importService to set
	 */
	public void setImportService(final ImportService importService)
	{
		this.importService = importService;
	}


}
