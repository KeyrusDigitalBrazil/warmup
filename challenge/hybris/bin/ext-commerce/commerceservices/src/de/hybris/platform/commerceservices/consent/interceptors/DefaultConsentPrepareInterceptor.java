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
package de.hybris.platform.commerceservices.consent.interceptors;

import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;

import org.springframework.beans.factory.annotation.Required;


/**
 * Sets default values for the following attributes of new {@link ConsentModel} instances:
 * <ul>
 * <li>{@link ConsentModel#CODE},</li>
 * </ul>
 */
public class DefaultConsentPrepareInterceptor implements PrepareInterceptor<ConsentModel>
{
	private KeyGenerator keyGenerator;

	@Override
	public void onPrepare(final ConsentModel consent, final InterceptorContext ctx) throws InterceptorException
	{
		if (consent.getCode() == null)
		{
			consent.setCode((String) getKeyGenerator().generate());
		}
	}

	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}
}
