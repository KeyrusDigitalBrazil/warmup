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
package de.hybris.platform.commerceservices.model;

import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;


public class ConsentActiveAttribute extends AbstractDynamicAttributeHandler<Boolean, ConsentModel>
{
	@Override
	public Boolean get(final ConsentModel model)
	{
		if (model == null)
		{
			throw new IllegalArgumentException("consent must not be null");
		}

		return Boolean.valueOf(model.getConsentWithdrawnDate() == null);
	}
}
