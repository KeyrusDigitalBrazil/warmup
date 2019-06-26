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
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import de.hybris.platform.cissapdigitalpayment.facade.SapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;


/**
 *
 * Default implementation of {@link SapDigitalPaymentFacade}
 *
 */
public class DefaultSapDigitalPaymentFacade implements SapDigitalPaymentFacade
{


	private SapDigitalPaymentService sapDigitalPaymentService;


	@Override
	public String getCardRegistrationUrl()
	{
		return getSapDigitalPaymentService().getCardRegistrationUrl();
	}


	@Override
	public void createPollRegisteredCardProcess(final String sessionId)
	{
		getSapDigitalPaymentService().createPollRegisteredCardProcess(sessionId);

	}


	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}


	/**
	 * @param sapDigitalPaymentService
	 *           the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}





}
