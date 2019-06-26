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
package de.hybris.platform.acceleratorservices.dataexport.generic.config;

import de.hybris.platform.acceleratorservices.dataexport.generic.query.ExportQuery;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.messaging.Message;


/**
 * Configuration used in exports.
 */
public class PipelineConfig
{
	private ExportQuery query;
	private Converter<Message<? extends ItemModel>, Object> itemConverter;
	private Converter<List<Object>, String> outputConverter;
	private String filename;

	public ExportQuery getQuery()
	{
		return query;
	}

	@Required
	public void setQuery(final ExportQuery query)
	{
		this.query = query;
	}

	// It is possible for this class to be extended or for methods to be used in other extensions - so no sonar added
	public Converter<Message<? extends ItemModel>, Object> getItemConverter() // NOSONAR
	{
		return itemConverter;
	}

	@Required
	public void setItemConverter(final Converter<Message<? extends ItemModel>, Object> itemConverter)
	{
		this.itemConverter = itemConverter;
	}

	public Converter<List<Object>, String> getOutputConverter()
	{
		return outputConverter;
	}

	@Required
	public void setOutputConverter(final Converter<List<Object>, String> outputConverter)
	{
		this.outputConverter = outputConverter;
	}

	public String getFilename()
	{
		return filename;
	}

	@Required
	public void setFilename(final String filename)
	{
		this.filename = filename;
	}
}
