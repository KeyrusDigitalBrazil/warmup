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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter.mapping.impl;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.mapping.ConverterMapping;


/**
 * Default converter mapping.
 */
public class DefaultConverterMapping implements ConverterMapping
{
	private String mapping;
	private ImpexConverter converter;

	@Override
	public ImpexConverter getConverter()
	{
		return converter;
	}

	@Override
	public String getMapping()
	{
		return mapping;
	}

	public void setMapping(final String mapping)
	{
		this.mapping = mapping;
	}

	public void setConverter(final ImpexConverter converter)
	{
		this.converter = converter;
	}

}
