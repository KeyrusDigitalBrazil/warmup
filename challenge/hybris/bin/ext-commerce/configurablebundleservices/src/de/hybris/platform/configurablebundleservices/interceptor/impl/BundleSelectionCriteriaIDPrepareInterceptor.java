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
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Create a unique id for new {@link BundleSelectionCriteriaModel}s if not yet set
 */
public class BundleSelectionCriteriaIDPrepareInterceptor implements PrepareInterceptor
{

	private KeyGenerator bundleSelectionCriteriaIDGenerator;

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof BundleSelectionCriteriaModel)
		{
			final BundleSelectionCriteriaModel selectCriteria = (BundleSelectionCriteriaModel) model;
			final String id = selectCriteria.getId();
			if (StringUtils.isEmpty(id))
			{
				selectCriteria.setId((String) this.bundleSelectionCriteriaIDGenerator.generate());
			}
		}
	}

	@Required
	public void setBundleSelectionCriteriaIDGenerator(final KeyGenerator bundleSelectionCriteriaIDGenerator)
	{
		this.bundleSelectionCriteriaIDGenerator = bundleSelectionCriteriaIDGenerator;
	}

}
