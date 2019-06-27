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
package de.hybris.platform.acceleratorservices.cartfileupload.events;

import de.hybris.platform.acceleratorservices.enums.ImportStatus;
import de.hybris.platform.acceleratorservices.model.process.SavedCartFileUploadProcessModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SavedCartFileUploadEventListener extends AbstractEventListener<SavedCartFileUploadEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private MediaService mediaService;
	private TimeService timeService;
	private KeyGenerator guidKeyGenerator;
	private CartFactory cartFactory;
	private static final Logger LOG = Logger.getLogger(SavedCartFileUploadEventListener.class);

	@Override
	protected void onEvent(final SavedCartFileUploadEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received SavedCartFileUploadEvent..");
		}
		final SavedCartFileUploadProcessModel cartFileUploadProcessModel = (SavedCartFileUploadProcessModel) getBusinessProcessService()
				.createProcess("savedCartFileUploadProcess" + "-" + event.getBaseStore().getUid() + "-" + System.currentTimeMillis(),
						"savedCartFileUploadProcess");
		cartFileUploadProcessModel.setUploadedFile(event.getFileMedia());
		cartFileUploadProcessModel.setUser(event.getCustomer());
		cartFileUploadProcessModel.setStore(event.getBaseStore());
		cartFileUploadProcessModel.setCurrency(event.getCurrency());
		cartFileUploadProcessModel.setLanguage(event.getLanguage());
		cartFileUploadProcessModel.setSite(event.getSite());
		cartFileUploadProcessModel.setSavedCart(createSavedCartForProcess(event));
		getModelService().save(cartFileUploadProcessModel);
		getModelService().refresh(cartFileUploadProcessModel);
		getBusinessProcessService().startProcess(cartFileUploadProcessModel);
	}

	protected CartModel createSavedCartForProcess(final SavedCartFileUploadEvent event)
	{
		final CartModel cartModel = getCartFactory().createCart();
		cartModel.setSaveTime(getTimeService().getCurrentTime());
		cartModel.setName(String.valueOf(System.currentTimeMillis()));
		cartModel.setUser(event.getCustomer());
		cartModel.setCurrency(event.getCurrency());
		cartModel.setDate(getTimeService().getCurrentTime());
		cartModel.setSite(event.getSite());
		cartModel.setSavedBy(event.getCustomer());
		cartModel.setImportStatus(ImportStatus.PROCESSING);
		cartModel.setStore(event.getBaseStore());
		cartModel.setGuid(getGuidKeyGenerator().generate().toString());
		getModelService().save(cartModel);
		getModelService().refresh(cartModel);
		return cartModel;
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

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
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

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected KeyGenerator getGuidKeyGenerator()
	{
		return guidKeyGenerator;
	}

	@Required
	public void setGuidKeyGenerator(final KeyGenerator guidKeyGenerator)
	{
		this.guidKeyGenerator = guidKeyGenerator;
	}

	protected CartFactory getCartFactory()
	{
		return cartFactory;
	}

	@Required
	public void setCartFactory(final CartFactory cartFactory)
	{
		this.cartFactory = cartFactory;
	}

}
