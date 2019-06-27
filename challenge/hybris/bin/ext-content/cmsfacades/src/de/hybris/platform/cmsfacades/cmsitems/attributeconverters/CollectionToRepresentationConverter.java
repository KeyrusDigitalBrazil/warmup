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

import de.hybris.platform.cmsfacades.cmsitems.AttributeValueToRepresentationConverter;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CMSITEMS_INVALID_CONVERSION_ERROR;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * This {@link AttributeValueToRepresentationConverter} is invoked while converting a {@link de.hybris.platform.cms2.model.contents.CMSItemModel}
 * into its representation. It's used to convert a Collection of items into its representation (which is a collection of converted items
 * themselves).
 */
public class CollectionToRepresentationConverter implements AttributeValueToRepresentationConverter<Collection<Object>, Collection<Object>>
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionToRepresentationConverter.class);

	private ValidationErrorsProvider validationErrorsProvider;

	// --------------------------------------------------------------------------
	// Public API
	// --------------------------------------------------------------------------
	@Override
	public Collection<Object> convert(AttributeDescriptorModel attribute, Collection<Object> collection,
			Function<Object, Object> transformationFunction)
	{
		if( collection == null )
		{
			return null;
		}
		final AtomicInteger counter = new AtomicInteger(0);
		final Collection<Object> transformedCollection = newArrayList();
		collection.iterator().forEachRemaining(value -> {
			final Integer index = counter.getAndIncrement();
			try
			{
				Object convertedValue = transformationFunction.apply(value);
				if(convertedValue != null)
				{
					transformedCollection.add(convertedValue);
				}
			}
			catch (final ValidationException e)
			{
				getValidationErrorsProvider().collectValidationErrors(e, empty(), of(index));
			}
			catch (final ConversionException e)
			{
				LOGGER.info("Error converting attribute for [" + attribute.getQualifier() + "] with value [" + value + "]", e);
				getValidationErrorsProvider().getCurrentValidationErrors().add(
					newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.rejectedValue(value) //
							.position(index) //
							.errorCode(CMSITEMS_INVALID_CONVERSION_ERROR) //
							.exceptionMessage(e.getMessage()) //
							.build()
				);
			}
		});
		return transformedCollection;
	}

	// --------------------------------------------------------------------------
	// Getters/Setters
	// --------------------------------------------------------------------------
	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}
}
