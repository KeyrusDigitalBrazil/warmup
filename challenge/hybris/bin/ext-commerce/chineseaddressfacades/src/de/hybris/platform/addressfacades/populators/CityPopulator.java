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
package de.hybris.platform.addressfacades.populators;

import de.hybris.platform.addressfacades.data.CityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.C2LItemModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import javax.annotation.Resource;

import org.springframework.util.Assert;


public class CityPopulator implements Populator<C2LItemModel, CityData>
{
	@Resource(name = "i18NService")
	private I18NService i18NService;

	@Override
	public void populate(final C2LItemModel source, final CityData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getIsocode());
		target.setName(source.getName(i18NService.getCurrentLocale()));
	}

}
