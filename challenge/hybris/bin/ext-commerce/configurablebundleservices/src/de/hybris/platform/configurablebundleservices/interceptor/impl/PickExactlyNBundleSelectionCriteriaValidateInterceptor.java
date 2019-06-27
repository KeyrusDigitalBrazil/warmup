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

import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;


/**
 * Validate that the selection criteria has at least 1 selection
 */
public class PickExactlyNBundleSelectionCriteriaValidateInterceptor implements ValidateInterceptor
{

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model.getClass().equals(PickExactlyNBundleSelectionCriteriaModel.class))
		{
			final PickExactlyNBundleSelectionCriteriaModel pickExactlyNCriteria = (PickExactlyNBundleSelectionCriteriaModel) model;

			if (pickExactlyNCriteria.getN() != null && pickExactlyNCriteria.getN().intValue() < 1)
			{
				throw new InterceptorException("Selection Criteria " + pickExactlyNCriteria.getId()
						+ "pick number must be greater than or equal to 1");
			}
		}

	}

}
