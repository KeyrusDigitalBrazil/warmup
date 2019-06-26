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
 */

package de.hybris.platform.yacceleratorordermanagement.events;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.AbstractSiteEventListener;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.warehousing.event.SendReturnLabelEvent;
import org.springframework.beans.factory.annotation.Required;


/**
 * listener to send return label to customer, after return request gets approved.
 */

public class SendReturnLabelEventListener extends AbstractSiteEventListener<SendReturnLabelEvent>
{
	private ModelService modelService;
	private BusinessProcessService businessProcessService;

	@Override
	protected void onSiteEvent(final SendReturnLabelEvent sendReturnLabelEvent)
	{
		final ReturnRequestModel returnRequest = sendReturnLabelEvent.getReturnRequest();
		final ReturnProcessModel returnProcessModel = (ReturnProcessModel) getBusinessProcessService().createProcess(
				"sendReturnLabelEmail-process-" + returnRequest.getCode() + "-" + System.currentTimeMillis(),
				"sendReturnLabelEmail-process");
		returnProcessModel.setReturnRequest(returnRequest);
		getModelService().save(returnProcessModel);
		getBusinessProcessService().startProcess(returnProcessModel);
	}

	@Override
	protected boolean shouldHandleEvent(final SendReturnLabelEvent event)
	{
		final ReturnRequestModel returnRequest = event.getReturnRequest();
		ServicesUtil.validateParameterNotNullStandardMessage("event.return", returnRequest);
		final BaseSiteModel site = returnRequest.getOrder().getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.return.site", site);
		return getSiteChannelForEvent(event).equals(site.getChannel());
	}

	protected SiteChannel getSiteChannelForEvent(final SendReturnLabelEvent event)
	{
		final OrderModel order = event.getReturnRequest().getOrder();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}

