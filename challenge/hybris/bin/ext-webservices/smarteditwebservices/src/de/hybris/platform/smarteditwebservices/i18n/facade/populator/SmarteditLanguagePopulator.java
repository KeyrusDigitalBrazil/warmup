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
package de.hybris.platform.smarteditwebservices.i18n.facade.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.smarteditwebservices.data.SmarteditLanguageData;

import java.util.Locale;



/**
 * Populator to convert {@link Locale} into {@link SmarteditLanguageData}
 */
public class SmarteditLanguagePopulator implements Populator<Locale, SmarteditLanguageData>
{

	@Override
	public void populate(final Locale source, final SmarteditLanguageData target) throws ConversionException
	{
		target.setName(source.getDisplayName(source));
		target.setIsoCode(source.toString());
	}
}
