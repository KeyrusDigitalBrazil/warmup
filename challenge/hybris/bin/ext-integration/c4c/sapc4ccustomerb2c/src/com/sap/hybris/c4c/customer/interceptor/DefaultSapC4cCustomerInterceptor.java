/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.interceptor;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.List;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;


/**
 * Intercepts any changes in customer and creates customer update event if any replicated relavant fields are changed
 */
public class DefaultSapC4cCustomerInterceptor implements ValidateInterceptor<CustomerModel>
{

	private EventService eventService;
	private SapC4cCustomerUtils customerUtil;


	@Override
	public void onValidate(final CustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException
	{
		if ((customerModel.getClass() == CustomerModel.class) && !ctx.isNew(customerModel) && shouldReplicate(customerModel, ctx))
		{
			final List<AddressModel> addressModels = (List<AddressModel>) customerModel.getAddresses();

			final C4CCustomerData customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);

			final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
			event.setCustomerData(customerData);
			getEventService().publishEvent(event);
		}
	}

	/**
	 * Checks if any replicated relavant fields are modified and returns a boolean
	 */
	protected boolean shouldReplicate(final CustomerModel customerModel, final InterceptorContext ctx)
	{
		return (ctx.isModified(customerModel, CustomerModel.NAME) || ctx.isModified(customerModel, CustomerModel.UID)
				|| ctx.isModified(customerModel, CustomerModel.DEFAULTSHIPMENTADDRESS)
				|| ctx.isModified(customerModel, CustomerModel.DEFAULTPAYMENTADDRESS));
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the customerUtil
	 */
	public SapC4cCustomerUtils getCustomerUtil()
	{
		return customerUtil;
	}

	/**
	 * @param customerUtil
	 *           the customerUtil to set
	 */
	public void setCustomerUtil(final SapC4cCustomerUtils customerUtil)
	{
		this.customerUtil = customerUtil;
	}

}
