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

package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Create a unique id for new {@link AbstractBundleRuleModel}s if not yet set
 */
public class AbstractBundleRuleIDPrepareInterceptor implements PrepareInterceptor
{

	private KeyGenerator abstractBundleRuleIDGenerator;

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{

		if (model instanceof AbstractBundleRuleModel)
		{
			final AbstractBundleRuleModel bundleRule = (AbstractBundleRuleModel) model;
			final String id = bundleRule.getId();
			if (StringUtils.isEmpty(id))
			{
				bundleRule.setId((String) this.abstractBundleRuleIDGenerator.generate());
			}
		}
	}

	@Required
	public void setAbstractBundleRuleIDGenerator(final KeyGenerator abstractBundleRuleIDGenerator)
	{
		this.abstractBundleRuleIDGenerator = abstractBundleRuleIDGenerator;
	}



}
