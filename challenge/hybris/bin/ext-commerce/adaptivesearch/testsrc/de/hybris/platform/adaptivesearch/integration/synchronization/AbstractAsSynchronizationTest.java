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
package de.hybris.platform.adaptivesearch.integration.synchronization;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.adaptivesearch.model.AbstractAsConfigurationModel;
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel;
import de.hybris.platform.adaptivesearch.model.AsSearchProfileActivationSetModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractAsSynchronizationTest extends ServicelayerTest
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAsSynchronizationTest.class);

	@Resource
	private TypeService typeService;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;

	@Resource
	private SynchronizationStatusService synchronizationStatusService;

	protected void synchronize(final CatalogVersionModel source, final CatalogVersionModel target)
	{
		catalogSynchronizationService.synchronizeFully(source, target);
	}

	protected void assertSynchronized(final ItemModel source, final ItemModel target, final String... excludedAttributes)
	{
		final ComposedTypeModel composedType = typeService.getComposedTypeForCode(source.getItemtype());
		final Set<AttributeDescriptorModel> attributeDescriptors = typeService.getAttributeDescriptorsForType(composedType);

		for (final AttributeDescriptorModel attributeDescriptor : attributeDescriptors)
		{
			final TypeModel attributeType = attributeDescriptor.getAttributeType();
			final ComposedTypeModel attributeEnclosingType = attributeDescriptor.getDeclaringEnclosingType();

			if (typeService.isAssignableFrom(AbstractAsConfigurationModel._TYPECODE, attributeEnclosingType.getCode())
					|| typeService.isAssignableFrom(AbstractAsSearchProfileModel._TYPECODE, attributeEnclosingType.getCode())
					|| typeService.isAssignableFrom(AsSearchProfileActivationSetModel._TYPECODE, attributeEnclosingType.getCode()))
			{
				if (attributeType instanceof AtomicTypeModel
						&& !ArrayUtils.contains(excludedAttributes, attributeDescriptor.getQualifier()))
				{
					LOG.info("Checking attribute: {type={}, attribue={}}", composedType.getCode(), attributeDescriptor.getQualifier());

					final Object sourceValue = source.getProperty(attributeDescriptor.getQualifier());
					final Object targetValue = target.getProperty(attributeDescriptor.getQualifier());
					assertEquals(sourceValue, targetValue);
				}
				else
				{
					// YTODO: support other attribute types
				}
			}
		}
	}
}
