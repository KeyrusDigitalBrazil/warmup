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
package de.hybris.platform.personalizationintegration.mapping.mapper.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;


/**
 * Test mapper that just adds a suffix to each input string value.
 *
 */
public class CxTestSuffixMapper implements Populator<List<String>, MappingData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final List<String> data, final MappingData target) throws ConversionException
	{
		data.stream().map(s -> s + "-test").forEach(s -> {
			final SegmentMappingData segment = new SegmentMappingData();
			segment.setCode(s);
			target.getSegments().add(segment);
		});
	}



}
