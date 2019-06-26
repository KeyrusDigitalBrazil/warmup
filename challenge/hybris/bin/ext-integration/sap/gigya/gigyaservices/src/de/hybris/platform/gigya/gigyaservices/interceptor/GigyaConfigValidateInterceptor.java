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
package de.hybris.platform.gigya.gigyaservices.interceptor;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.commons.lang3.StringUtils;


/**
 * Validates mandatory information of gigya config exists
 */
public class GigyaConfigValidateInterceptor implements ValidateInterceptor<GigyaConfigModel>
{

	@Override
	public void onValidate(final GigyaConfigModel gigyaConfig, final InterceptorContext context) throws InterceptorException
	{
		if (StringUtils.isEmpty(gigyaConfig.getGigyaApiKey()))
		{
			throw new InterceptorException("Gigya API key missing.");
		}

		if (!(StringUtils.isNotEmpty(gigyaConfig.getGigyaUserKey()) && StringUtils.isNotEmpty(gigyaConfig.getGigyaUserSecret())
				|| StringUtils.isNotEmpty(gigyaConfig.getGigyaSiteSecret())))
		{
			throw new InterceptorException("Either gigya site secret or gigya user key and gigya user secret must exist.");
		}
	}

}
