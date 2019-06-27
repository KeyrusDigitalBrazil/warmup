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
package de.hybris.platform.ruleengineservices.util;

import static java.util.Collections.singletonList;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;


/**
 * The class contains some common functionality for tests.
 */
public class TestUtil
{
	public static <SOURCE, TARGET> AbstractPopulatingConverter<SOURCE, TARGET> createNewConverter(final Class<TARGET> targetClass,
			final Populator<SOURCE, TARGET> populator)
	{
		final AbstractPopulatingConverter<SOURCE, TARGET> converter = new AbstractPopulatingConverter<>();
		converter.setTargetClass(targetClass);
		converter.setPopulators(singletonList(populator));
		return converter;
	}
}
