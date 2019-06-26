/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */

package de.hybris.platform.yacceleratorordermanagement.events;

import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.CreateReturnEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


/**
 * Listener for order submits.
 */
public class CreateReturnEventListener extends AbstractSiteEventListener<CreateReturnEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(CreateReturnEventListener.class);

	private BusinessProcessService businessProcessService;
	private BaseStoreService baseStoreService;
	private ModelService modelService;
	private Set<SiteChannel> supportedSiteChannels;

	@Override
	protected void onSiteEvent(final CreateReturnEvent event)
	{
		final ReturnRequestModel returnRequest = event.getReturnRequest();
		ServicesUtil.validateParameterNotNullStandardMessage("event.returnRequest", returnRequest);

		// Try the store set on the Order first, then fallback to the session
		BaseStoreModel store = returnRequest.getOrder().getStore();
		if (store == null)
		{
			store = getBaseStoreService().getCurrentBaseStore();
		}

		if (store == null)
		{
			LOG.info("Unable to start return process for return request [" + returnRequest.getCode()
					+ "]. Store not set on Order linked to the return request and no current base store defined in session.");
		}
		else
		{
			final String createReturnProcessDefinitionName = store.getCreateReturnProcessCode();
			if (createReturnProcessDefinitionName == null || createReturnProcessDefinitionName.isEmpty())
			{
				LOG.info("Unable to start return process for return request [" + returnRequest.getCode() + "]. Store ["
						+ store.getUid() + "] has missing CreateReturnProcessCode");
			}
			else
			{
				final String processCode = createReturnProcessDefinitionName + "-" + returnRequest.getCode() + "-"
						+ System.currentTimeMillis();
				final ReturnProcessModel businessProcessModel = getBusinessProcessService().createProcess(processCode,
						createReturnProcessDefinitionName);
				businessProcessModel.setReturnRequest(returnRequest);
				getModelService().save(businessProcessModel);
				getBusinessProcessService().startProcess(businessProcessModel);
				if (LOG.isInfoEnabled())
				{
					LOG.info(String.format("Started the process %s", processCode));
				}
			}
		}
	}

	@Override
	protected boolean shouldHandleEvent(final CreateReturnEvent event)
	{
		final ReturnRequestModel returnRequest = event.getReturnRequest();
		ServicesUtil.validateParameterNotNullStandardMessage("event.return", returnRequest);
		final BaseSiteModel site = returnRequest.getOrder().getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.return.site", site);
		return getSupportedSiteChannels().contains(site.getChannel());
	}
	
	/**
	 * @return the businessProcessService
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
	
	/**
	 * @return the set of supported site channels
	 */
	protected Set<SiteChannel> getSupportedSiteChannels()
	{
		return supportedSiteChannels;
	}

	/**
	 * @param supportedSiteChannels
	 *           the supported site channels to set
	 */
	@Required
	public void setSupportedSiteChannels(Set<SiteChannel> supportedSiteChannels)
	{
		this.supportedSiteChannels = supportedSiteChannels;
	}
}
