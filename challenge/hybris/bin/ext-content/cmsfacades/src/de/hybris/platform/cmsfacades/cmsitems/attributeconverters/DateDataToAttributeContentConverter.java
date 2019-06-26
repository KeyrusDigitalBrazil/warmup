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
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import de.hybris.platform.cms2.common.functions.Converter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import static java.util.Date.from;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.DATE_TIME_FORMAT;


/**
 * Attribute Converter for {@link java.util.Date}.
 * Converts the Date to its proper {@link java.time.Instant} object and formats the date from {@link ZoneOffset#UTC} zone.
 */
public class DateDataToAttributeContentConverter implements Converter<String, Date>
{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
	
	@Override
	public Date convert(final String source)
	{
		if (Objects.isNull(source))
		{
			return null;
		}
		return from(ZonedDateTime.parse(source, DATE_FORMATTER).toInstant());
	}
}
