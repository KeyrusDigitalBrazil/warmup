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
package de.hybris.platform.cmsfacades.types.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.service.AttributeModePopulatorsProvider;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureService;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilter;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilterProvider;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_CACHE_EXPIRATION;
import static de.hybris.platform.cmsfacades.data.StructureTypeMode.ADD;
import static de.hybris.platform.cmsfacades.data.StructureTypeMode.DEFAULT;
import static de.hybris.platform.cmsfacades.types.service.impl.DefaultStructureTypeModeService.DEFAULT_EXPIRATION_TIME;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultStructureTypeModeServiceTest
{
	private static final String ITEM_TYPECODE = "itemType";
	private static final String ABSTRACT_TYPE_CODE = "abstract-type-code";
	
	private static final String QUALIFIER_1 = "qualifier1";
	private static final String QUALIFIER_2 = "qualifier2";
	private static final String QUALIFIER_3 = "qualifier3";


	@InjectMocks
	private DefaultStructureTypeModeService service;
	
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ComponentTypeStructureService componentTypeStructureService;
	@Mock
	private TypeService typeService;
	@Mock
	private AttributeModePopulatorsProvider attributeModePopulatorsProvider;
	@Mock
	private StructureTypeModeAttributeFilterProvider structureTypeModeAttributeFilterProvider; 

	@Mock
	private Configuration configuration;
	
	@Mock
	private ComponentTypeStructure componentTypeStructure;
	
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private AttributeDescriptorModel attributeDescriptor1;
	@Mock
	private AttributeDescriptorModel attributeDescriptor2;
	@Mock
	private AttributeDescriptorModel attributeDescriptor3;

	@Before
	public void setUp() throws Exception
	{
		when(configuration.getLong(TYPE_CACHE_EXPIRATION, DEFAULT_EXPIRATION_TIME)).thenReturn(360l);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(composedType.getCode()).thenReturn(ITEM_TYPECODE);

		when(typeService.getComposedTypeForCode(ITEM_TYPECODE)).thenReturn(composedType);
		when(composedType.getDeclaredattributedescriptors()).thenReturn(newArrayList(attributeDescriptor1, attributeDescriptor2, attributeDescriptor3));

		when(attributeDescriptor1.getQualifier()).thenReturn(QUALIFIER_1);
		when(attributeDescriptor2.getQualifier()).thenReturn(QUALIFIER_2);
		when(attributeDescriptor3.getQualifier()).thenReturn(QUALIFIER_3);

		final ComponentTypeAttributeStructure baseAttribute1 = getComponentTypeAttributeStructure(ITEM_TYPECODE, QUALIFIER_1);
		final ComponentTypeAttributeStructure baseAttribute2 = getComponentTypeAttributeStructure(ITEM_TYPECODE, QUALIFIER_2);
		final ComponentTypeAttributeStructure baseAttribute3 = getComponentTypeAttributeStructure(ITEM_TYPECODE, QUALIFIER_3);

		when(componentTypeStructure.getTypecode()).thenReturn(ITEM_TYPECODE);
		when(componentTypeStructure.getAttributes()).thenReturn(newHashSet(baseAttribute1, baseAttribute2, baseAttribute3));
		
		when(componentTypeStructureService.getComponentTypeStructure(ITEM_TYPECODE)).thenReturn(componentTypeStructure);
		when(componentTypeStructureService.getComponentTypeStructures()).thenReturn(newArrayList(componentTypeStructure));

		service.afterPropertiesSet();

	}

	protected ComponentTypeAttributeStructure getComponentTypeAttributeStructure(final String typeCode, final String qualifier)
	{
		final ComponentTypeAttributeStructure baseAttribute = new DefaultComponentTypeAttributeStructure();
		baseAttribute.setQualifier(qualifier);
		baseAttribute.setTypecode(typeCode);
		baseAttribute.setPopulators(newArrayList());
		return baseAttribute;
	}

	@Test
	public void shouldReturnAllAttributesForTypeAndMode() 
	{
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, DEFAULT);
		assertThat(componentTypeStructure.getAttributes(), iterableWithSize(3));
		assertThat(componentTypeStructure.getAttributes().stream()
				.map(ComponentTypeAttributeStructure::getQualifier)
				.collect(toList()), containsInAnyOrder(QUALIFIER_1, QUALIFIER_2, QUALIFIER_3));
	}

	@Test
	public void shouldFollowModeDefinitionInclusionExclusionAndOrder()
	{
		final StructureTypeModeAttributeFilter structureTypeModeAttributeFilter = mock(StructureTypeModeAttributeFilter.class);
		when(structureTypeModeAttributeFilter.getConstrainedBy()).thenReturn((a, b) -> true);
		when(structureTypeModeAttributeFilter.getIncludes()).thenReturn(asList());
		when(structureTypeModeAttributeFilter.getExcludes()).thenReturn(asList(QUALIFIER_3));
		when(structureTypeModeAttributeFilter.getOrder()).thenReturn(asList(QUALIFIER_2, QUALIFIER_1));
		when(structureTypeModeAttributeFilterProvider.getStructureTypeModeAttributeFilters(ITEM_TYPECODE, DEFAULT)).thenReturn(newArrayList(
				structureTypeModeAttributeFilter));

		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, DEFAULT);
		assertThat(componentTypeStructure.getAttributes(), iterableWithSize(2));
		assertThat(componentTypeStructure.getAttributes()
				.stream()
				.map(ComponentTypeAttributeStructure::getQualifier)
				.collect(toList()), contains(QUALIFIER_2, QUALIFIER_1));
		
	}

	@Test
	public void shouldFollowModeDefinitionInclusionExclusionAndOrderButInheritFromDefaultMode()
	{
		final StructureTypeModeAttributeFilter defaultStructureTypeModeAttributeFilter = mock(StructureTypeModeAttributeFilter.class);
		when(defaultStructureTypeModeAttributeFilter.getConstrainedBy()).thenReturn((a, mode) -> mode == DEFAULT);
		when(defaultStructureTypeModeAttributeFilter.getIncludes()).thenReturn(asList());
		when(defaultStructureTypeModeAttributeFilter.getExcludes()).thenReturn(asList(QUALIFIER_1, QUALIFIER_2, QUALIFIER_3));
		when(defaultStructureTypeModeAttributeFilter.getOrder()).thenReturn(asList(QUALIFIER_1, QUALIFIER_2, QUALIFIER_3));

		final StructureTypeModeAttributeFilter addStructureTypeModeAttributeFilter = mock(StructureTypeModeAttributeFilter.class);
		when(addStructureTypeModeAttributeFilter.getConstrainedBy()).thenReturn((a, mode) -> mode == ADD);
		when(addStructureTypeModeAttributeFilter.getIncludes()).thenReturn(asList(QUALIFIER_1, QUALIFIER_2));
		when(addStructureTypeModeAttributeFilter.getExcludes()).thenReturn(asList());
		when(addStructureTypeModeAttributeFilter.getOrder()).thenReturn(asList(QUALIFIER_2, QUALIFIER_1));

		when(structureTypeModeAttributeFilterProvider.getStructureTypeModeAttributeFilters(ITEM_TYPECODE, DEFAULT)).thenReturn(newArrayList(
				defaultStructureTypeModeAttributeFilter));
		when(structureTypeModeAttributeFilterProvider.getStructureTypeModeAttributeFilters(ITEM_TYPECODE, ADD)).thenReturn(newArrayList(
				addStructureTypeModeAttributeFilter));
		
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD);
		assertThat(componentTypeStructure.getAttributes(), iterableWithSize(2));
		assertThat(componentTypeStructure.getAttributes()
				.stream()
				.map(ComponentTypeAttributeStructure::getQualifier)
				.collect(toList()), contains(QUALIFIER_2, QUALIFIER_1));

	}


	@Test
	public void shouldAddExtraPopulatorsToAttributes()
	{
		final Populator<AttributeDescriptorModel, ComponentTypeAttributeData> populator1 = mock(Populator.class);
		when(attributeModePopulatorsProvider.getAttributePopulators(attributeDescriptor1, ADD)).thenReturn(newArrayList(populator1));
		
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD);
		assertThat(componentTypeStructure.getAttributes(), iterableWithSize(3));
		assertThat(componentTypeStructure.getAttributes()
				.stream()
				.map(ComponentTypeAttributeStructure::getQualifier)
				.collect(toList()), containsInAnyOrder(QUALIFIER_2, QUALIFIER_1, QUALIFIER_3));
		final ComponentTypeAttributeStructure attr1 = 
				componentTypeStructure.getAttributes().stream().filter(attr -> attr.getQualifier().equals(QUALIFIER_1)).findFirst().get();
		final ComponentTypeAttributeStructure attr2 =
				componentTypeStructure.getAttributes().stream().filter(attr -> attr.getQualifier().equals(QUALIFIER_2)).findFirst().get();
		final ComponentTypeAttributeStructure attr3 =
				componentTypeStructure.getAttributes().stream().filter(attr -> attr.getQualifier().equals(QUALIFIER_3)).findFirst().get();
		assertThat(attr1.getPopulators().size(), is(1));
		assertThat(attr2.getPopulators().size(), is(0));
		assertThat(attr3.getPopulators().size(), is(0));
	}


	@Test
	public void shouldFollowModeDefinitionInclusionExclusionAndOrderFromAbstractType()
	{
		final ComposedTypeModel abstractComposedType = mock(ComposedTypeModel.class);
		when(abstractComposedType.getCode()).thenReturn(ABSTRACT_TYPE_CODE);
		when(typeService.getComposedTypeForCode(ABSTRACT_TYPE_CODE)).thenReturn(abstractComposedType);
		
		final StructureTypeModeAttributeFilter defaultStructureTypeModeAttributeFilter = mock(StructureTypeModeAttributeFilter.class);
		when(defaultStructureTypeModeAttributeFilter.getConstrainedBy()).thenReturn((typeCode, mode) -> typeCode.equals(ABSTRACT_TYPE_CODE));
		when(defaultStructureTypeModeAttributeFilter.getIncludes()).thenReturn(asList());
		when(defaultStructureTypeModeAttributeFilter.getExcludes()).thenReturn(asList(QUALIFIER_2));
		when(defaultStructureTypeModeAttributeFilter.getOrder()).thenReturn(asList(QUALIFIER_3, QUALIFIER_1));

		when(structureTypeModeAttributeFilterProvider.getStructureTypeModeAttributeFilters(ITEM_TYPECODE, DEFAULT)).thenReturn(newArrayList(
				defaultStructureTypeModeAttributeFilter));

		final ComposedTypeModel superComposedType = mock(ComposedTypeModel.class);
		when(composedType.getSuperType()).thenReturn(superComposedType);
		when(superComposedType.getCode()).thenReturn(ABSTRACT_TYPE_CODE);

		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeByCodeAndMode(ITEM_TYPECODE, ADD);
		assertThat(componentTypeStructure.getAttributes(), iterableWithSize(2));
		assertThat(componentTypeStructure.getAttributes()
				.stream()
				.map(ComponentTypeAttributeStructure::getQualifier)
				.collect(toList()), contains(QUALIFIER_3, QUALIFIER_1));
	}
}
