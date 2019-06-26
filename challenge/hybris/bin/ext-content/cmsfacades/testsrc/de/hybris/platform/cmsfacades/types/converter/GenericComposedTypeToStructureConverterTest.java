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
package de.hybris.platform.cmsfacades.types.converter;


import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.AttributePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenericComposedTypeToStructureConverterTest
{
	private static final String TYPE_CODE_1 = "TYPE_CODE_1";
	private static final String QUALIFIER_1 = "QUALIFIER_1";
	private static final String QUALIFIER_2_BLACKLISTED = "QUALIFIER_2_BLACKLISTED";

	@Mock
	private ObjectFactory<ComponentTypeStructure> componentTypeStructureObjectFactory;
	@Mock
	private ObjectFactory<ComponentTypeAttributeStructure> componentTypeAttributeStructureObjectFactory;

	@Mock
	private Function<ComposedTypeModel, Class> typeDataClassFunction;
	
	private Map<String, String> structureTypeBlacklistAttributeMap = new HashMap<>();

	@Mock
	private AttributePopulatorsProvider attributePopulatorProvider;
	
	@InjectMocks
	private GenericComposedTypeToStructureConverter converter;
	
	@Mock
	private ComponentTypeStructure componentTypeStructure;
	@Mock
	private ComponentTypeAttributeStructure componentTypeAttributeStructure;
	@Mock
	private AttributeDescriptorModel attributeDescriptor1;
	@Mock
	private AttributeDescriptorModel attributeDescriptor2Blacklisted;
	
	private List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> attributePopulatorList1 = new ArrayList<>();

	@Mock
	private ComposedTypeModel composedType;
	
	private Set<ComponentTypeAttributeStructure> structureAttributeList = new HashSet<>();

	@Before
	public void setup() throws Exception
	{
		when(composedType.getCode()).thenReturn(TYPE_CODE_1);
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Arrays.asList(attributeDescriptor1, attributeDescriptor2Blacklisted));
		
		when(attributeDescriptor1.getQualifier()).thenReturn(QUALIFIER_1);
		
		when(attributeDescriptor2Blacklisted.getQualifier()).thenReturn(QUALIFIER_2_BLACKLISTED);
		
		when(componentTypeStructure.getAttributes()).thenReturn(structureAttributeList);
		
		when(componentTypeStructureObjectFactory.getObject()).thenReturn(componentTypeStructure);
		when(componentTypeAttributeStructureObjectFactory.getObject()).thenReturn(componentTypeAttributeStructure);

		structureTypeBlacklistAttributeMap.put(TYPE_CODE_1, QUALIFIER_2_BLACKLISTED);
		converter.setStructureTypeBlacklistAttributeMap(structureTypeBlacklistAttributeMap);

		when(attributePopulatorProvider.getAttributePopulators(attributeDescriptor1)).thenReturn(attributePopulatorList1);

		converter.afterPropertiesSet();
	}
	
	@Test
	public void testConvertTypeWillSuccessfullyCollectAttributesFromGivenComposedType()
	{
		final ComponentTypeStructure componentTypeStructure = converter.convert(composedType);
		verify(componentTypeStructure).setTypecode(TYPE_CODE_1);
		assertThat(componentTypeStructure.getPopulators().size(), is(0));
		assertThat(componentTypeStructure.getAttributes().size(), is(1));
		final ComponentTypeAttributeStructure componentTypeAttributeStructure = componentTypeStructure.getAttributes().stream()
				.findFirst().get();
		assertThat(componentTypeAttributeStructure, is(this.componentTypeAttributeStructure));
		
		assertThat(componentTypeAttributeStructure.getPopulators(), is(attributePopulatorList1));
	}
	
}
