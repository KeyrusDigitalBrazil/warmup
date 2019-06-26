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
package de.hybris.platform.commercefacades.consent.converters.populator;

import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.converters.Populator;


/**
 * Default populator that converts source {@link ConsentModel} to target {@link ConsentData}
 */
public class ConsentPopulator implements Populator<ConsentModel, ConsentData>
{
	@Override
	public void populate(final ConsentModel source, final ConsentData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setConsentWithdrawnDate(source.getConsentWithdrawnDate());
		target.setConsentGivenDate(source.getConsentGivenDate());
	}
}
