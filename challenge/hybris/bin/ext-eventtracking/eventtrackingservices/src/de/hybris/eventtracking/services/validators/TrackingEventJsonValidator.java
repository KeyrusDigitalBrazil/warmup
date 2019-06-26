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
package de.hybris.eventtracking.services.validators;

import java.io.IOException;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;


/**
 * @author stevo.slavic
 *
 */
public class TrackingEventJsonValidator
{
	private final ObjectMapper mapper;

	private final JsonSchema eventTrackingSchema;

	public TrackingEventJsonValidator(final ObjectMapper mapper, final Resource eventTrackingSchema)
			throws ProcessingException, IOException
	{
		this.mapper = mapper;

		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		this.eventTrackingSchema = factory.getJsonSchema(mapper.readTree(eventTrackingSchema.getInputStream()));
	}

	public ProcessingReport validate(final String rawTrackingEvent) throws IOException,
			ProcessingException
	{
		final JsonNode instance = mapper.readTree(rawTrackingEvent);
		return eventTrackingSchema.validate(instance);
	}
}
