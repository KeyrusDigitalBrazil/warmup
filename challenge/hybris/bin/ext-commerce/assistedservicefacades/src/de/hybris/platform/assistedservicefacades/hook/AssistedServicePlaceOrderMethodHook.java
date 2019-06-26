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
package de.hybris.platform.assistedservicefacades.hook;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Assisted service place order hook for setting up placedBy attribute for an order.
 */
public class AssistedServicePlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private AssistedServiceFacade assistedServiceFacade;
	private ModelService modelService;

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult orderModel)
			throws InvalidCartException
	{
		if (assistedServiceFacade.isAssistedServiceAgentLoggedIn())
		{
			orderModel.getOrder().setPlacedBy(assistedServiceFacade.getAsmSession().getAgent());
			modelService.save(orderModel.getOrder());
			modelService.refresh(orderModel.getOrder());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook#beforePlaceOrder(de.hybris.platform
	 * .commerceservices.service.data.CommerceCheckoutParameter)
	 */
	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		// Nothing done here

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook#beforeSubmitOrder(de.hybris.platform
	 * .commerceservices.service.data.CommerceCheckoutParameter,
	 * de.hybris.platform.commerceservices.service.data.CommerceOrderResult)
	 */
	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		// Nothing done here
	}

	/**
	 * @return the assistedServiceFacade
	 */
	public AssistedServiceFacade getAssistedServiceFacade()
	{
		return assistedServiceFacade;
	}

	/**
	 * @param assistedServiceFacade
	 *           the assistedServiceFacade to set
	 */
	public void setAssistedServiceFacade(final AssistedServiceFacade assistedServiceFacade)
	{
		this.assistedServiceFacade = assistedServiceFacade;
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
}