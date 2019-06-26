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
package com.hybris.backoffice.config.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.config.jaxb.wizard.PropertyType;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformConfigurableFlowConfigurationProcessorTest
{

	@Spy
	private DefaultPlatformConfigurableFlowConfigurationProcessor processor;

	@Mock
	private DataType typeWithOwnerAttribute;
	@Mock
	private DataAttribute ownerAttribute;
	@Mock
	private DataType ownerAttributeType;
	@Mock
	private DataAttribute catalogVersionAttribute;
	@Mock
	private DataType catalogVersionAttributeType;

	@Test
	public void retrieveMissingProperties()
	{

		//given
		final Set<PropertyType> missingAttributes = new HashSet<>();

		final PropertyType owner = prepareProperty(ItemModel.OWNER);
		final PropertyType catalogVersion = prepareProperty(ProductModel.CATALOGVERSION);
		missingAttributes.add(owner);
		missingAttributes.add(catalogVersion);

		doReturn(typeWithOwnerAttribute).when(processor).loadDataType(anyString());

		when(typeWithOwnerAttribute.getAttribute(ItemModel.OWNER)).thenReturn(ownerAttribute);
		when(typeWithOwnerAttribute.getAttribute(ProductModel.CATALOGVERSION)).thenReturn(catalogVersionAttribute);

		when(ownerAttribute.getValueType()).thenReturn(ownerAttributeType);
		when(catalogVersionAttribute.getValueType()).thenReturn(catalogVersionAttributeType);

		when(ownerAttributeType.getCode()).thenReturn(ItemModel._TYPECODE);
		when(catalogVersionAttributeType.getCode()).thenReturn(CatalogVersionModel._TYPECODE);

		//when
		final Set<PropertyType> result = processor.filterFields(null, missingAttributes);

		//then
		assertThat(result).hasSize(1);
		assertThat(result).contains(catalogVersion);
		assertThat(result).excludes(owner);

	}

	protected PropertyType prepareProperty(final String qualifier)
	{
		final PropertyType owner = new PropertyType();
		owner.setQualifier(qualifier);
		return owner;
	}

}
