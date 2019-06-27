/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import static de.hybris.platform.odata2services.odata.EdmAnnotationUtils.isKeyProperty;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;
import de.hybris.platform.odata2services.odata.persistence.StorageRequest;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitivePropertyProcessor extends AbstractPropertyProcessor
{
	private static final Logger LOG = LoggerFactory.getLogger(PrimitivePropertyProcessor.class);

	@Override
	protected  boolean isApplicable(final TypeAttributeDescriptor typeAttributeDescriptor)
	{
		return typeAttributeDescriptor.isPrimitive() && !typeAttributeDescriptor.isCollection();
	}

	@Override
	protected void processItemInternal(final ItemModel item, final String entryPropertyName, final Object value,
			final StorageRequest request) throws EdmException
	{
		if (!getModelService().isNew(item)
				&& isKeyProperty(request.getEntityType().getProperty(entryPropertyName)))
		{
			return;
		}

		final String entityName = request.getEntityType().getName();
		LOG.debug("{}.{} set to '{}'", entityName, entryPropertyName, value);

		final AttributeDescriptorModel attributeDescriptor = getAttributeDescriptor(item, entryPropertyName, request);
		final String itemPropertyName = attributeDescriptor.getQualifier();

		if (value instanceof Calendar) // ECP does not handle Calendar
		{
			getModelService().setAttributeValue(item, itemPropertyName, ((Calendar) value).getTime());
		}
		else if (attributeDescriptor.getLocalized())
		{
			getModelService().setAttributeValue(item, itemPropertyName, Collections.singletonMap(request.getContentLocale(), value));
		}
		else
		{
			getModelService().setAttributeValue(item, itemPropertyName, value);
		}
	}

	@Override
	protected void processEntityInternal(final ODataEntry oDataEntry, final String propertyName, final Object value,
			final ItemConversionRequest request)
	{
		final Object propertyValue;
		if( value instanceof Date )
		{
			propertyValue = DateUtils.toCalendar((Date) value);
		}
		else
		{
			propertyValue = value;
		}
		oDataEntry.getProperties().putIfAbsent(propertyName, propertyValue);
	}

	@Override
	protected boolean shouldPropertyBeConverted(final ItemConversionRequest conversionRequest, final String propertyName)
	{
		return true;
	}
}