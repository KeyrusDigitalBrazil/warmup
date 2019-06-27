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
package de.hybris.platform.acceleratorfacades.cartfileupload.impl;


import de.hybris.platform.acceleratorfacades.cartfileupload.SavedCartFileUploadFacade;
import de.hybris.platform.acceleratorservices.cartfileupload.events.SavedCartFileUploadEvent;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link SavedCartFileUploadFacade} that can trigger file upload process to create a savedcart.
 *
 */
public class DefaultSavedCartFileUploadFacade implements SavedCartFileUploadFacade
{
	private CommonI18NService commonI18NService;
	private BaseStoreService baseStoreService;
	private UserService userService;
	private EventService eventService;
	private MediaService mediaService;
	private ModelService modelService;
	private BaseSiteService baseSiteService;


	@Override
	public void createCartFromFileUpload(final InputStream fileInputStream, final String fileName, final String fileFormat)
	{
		final CatalogUnawareMediaModel mediaModel = getModelService().create(CatalogUnawareMediaModel.class);
		mediaModel.setCode(System.currentTimeMillis() + "_" + fileName);
		getModelService().save(mediaModel);
		getMediaService().setStreamForMedia(mediaModel, fileInputStream, fileName, fileFormat);
		getModelService().refresh(mediaModel);

		final SavedCartFileUploadEvent savedCartFileUploadEvent = new SavedCartFileUploadEvent();
		savedCartFileUploadEvent.setFileMedia(mediaModel);
		savedCartFileUploadEvent.setCurrency(getCommonI18NService().getCurrentCurrency());
		savedCartFileUploadEvent.setLanguage(getCommonI18NService().getCurrentLanguage());
		savedCartFileUploadEvent.setCustomer((CustomerModel) getUserService().getCurrentUser());
		savedCartFileUploadEvent.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		savedCartFileUploadEvent.setSite(getBaseSiteService().getCurrentBaseSite());
		getEventService().publishEvent(savedCartFileUploadEvent);
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
