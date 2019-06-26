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
package de.hybris.platform.importcockpit.components.mappingview.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.cockpit.model.meta.ObjectType;
import de.hybris.platform.cockpit.model.meta.PropertyDescriptor;
import de.hybris.platform.cockpit.model.meta.impl.ItemAttributePropertyDescriptor;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.importcockpit.model.mappingview.mappingline.ComposedTypeMappingLine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;


public class ComposedTypeMappingLineConfigComponentTest
{
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String TYPE_CODE = "type_code";
	public static final String CODE_NAME = "code,name";

	@Mock
	private ComposedTypeMappingLineConfigComponent component;

	@Mock
	private ComposedTypeMappingLine composedTypeMappingLine;

	@Mock
	private ObjectType type;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;

	@Mock
	private TypeModel typeModel;

	@Before
	public void setup()
	{
		initMocks(this);
		when(type.getCode()).thenReturn(TYPE_CODE);
	}

	@Test
	public void getAdditionalUniquePropertyDescriptors()
	{
		final Set<PropertyDescriptor> descriptors = new HashSet<>();

		final ItemAttributePropertyDescriptor code = mock(ItemAttributePropertyDescriptor.class);
		when(code.getAttributeQualifier()).thenReturn(CODE);

		final ItemAttributePropertyDescriptor name = mock(ItemAttributePropertyDescriptor.class);
		when(name.getAttributeQualifier()).thenReturn(NAME);

		final ItemAttributePropertyDescriptor description = mock(ItemAttributePropertyDescriptor.class);
		when(description.getAttributeQualifier()).thenReturn(DESCRIPTION);

		final PropertyDescriptor nonItemAttributePropertyDescriptor = mock(PropertyDescriptor.class);

		descriptors.add(code);
		descriptors.add(description);
		descriptors.add(name);
		descriptors.add(nonItemAttributePropertyDescriptor);

		doReturn(CODE_NAME).when(component).getPlatformParameter(
				ComposedTypeMappingLineConfigComponent.MAPPING_LINE_ADDITIONAL_UNIQUE_ATTRIBUTES_PREFIX + TYPE_CODE);

		when(component.getAdditionalUniquePropertyDescriptors(any(), any())).thenCallRealMethod();

		final Collection<PropertyDescriptor> additionalUniquePropertyDescriptors = component
				.getAdditionalUniquePropertyDescriptors(descriptors, type);

		assertThat(additionalUniquePropertyDescriptors).containsOnly(code, name);
	}
}
