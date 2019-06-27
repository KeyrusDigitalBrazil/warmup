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
package com.hybris.ymkt.common.interceptor;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Look into the user session for the soid and setYmktTrackingId on AbstractOrderModel
 */
public class SapOutboundIdPrepareInterceptor implements PrepareInterceptor<AbstractOrderModel>
{

	protected static final String SAP_OUTBOUND_ID = "soid";
	protected SessionService sessionService;

	@Override
	public void onPrepare(final AbstractOrderModel model, final InterceptorContext ctx) throws InterceptorException
	{
		Optional.ofNullable(this.sessionService.<String> getAttribute(SAP_OUTBOUND_ID)).ifPresent(model::setYmktTrackingId);
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
