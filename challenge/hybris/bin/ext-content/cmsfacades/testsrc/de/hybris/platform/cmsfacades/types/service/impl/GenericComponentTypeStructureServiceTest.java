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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.TYPE_CACHE_EXPIRATION;
import static de.hybris.platform.cmsfacades.types.service.impl.GenericComponentTypeStructureService.DEFAULT_EXPIRATION_TIME;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructureRegistry;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GenericComponentTypeStructureServiceTest
{
	private static final java.lang.String TYPE_CODE_1 = "TYPE_CODE_1";
	private static final java.lang.String TYPE_CODE_2 = "TYPE_CODE_2";
	private static final java.lang.String TYPE_CODE_3 = "TYPE_CODE_3";
	private static final java.lang.String TYPE_CODE_REGISTRY = "TYPE_CODE_REGISTRY";
	@Mock
	private ComponentTypeStructureRegistry componentTypeStructureRegistry;
	@Mock
	private TypeService typeService;
	@Mock
	private Set<String> typeBlacklistSet;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private Converter<ComposedTypeModel, ComponentTypeStructure> composedTypeToStructureConverter;

	@InjectMocks
	private GenericComponentTypeStructureService service;
	
	// fields
	@Mock
	private ComposedTypeModel cmsItemComposedType;
	@Mock
	private ComposedTypeModel subType1;
	@Mock
	private ComposedTypeModel subType2;
	@Mock
	private ComposedTypeModel subType3;
	@Mock
	private ComposedTypeModel typeRegistry;
	
	@Mock
	private ComponentTypeStructure cmsItemTypeStructure;
	@Mock
	private ComponentTypeStructure subTypeStructure1;
	@Mock
	private ComponentTypeStructure subTypeStructure2;
	@Mock
	private ComponentTypeStructure typeStructureRegistry1;
	@Mock
	private ComponentTypeStructure typeStructureRegistry2;
	@Mock
	private ComponentTypeAttributeStructure type1RegistryAttribute1;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> type1Populator1;
	
	private List<Populator<ComposedTypeModel, ComponentTypeData>> subTypeStructure1PopulatorList = new ArrayList<>();
	private Set<ComponentTypeAttributeStructure> subTypeStructure1AttributeList = new HashSet<>();


	@Before
	public void setup() throws Exception
	{
		when(configuration.getLong(TYPE_CACHE_EXPIRATION, DEFAULT_EXPIRATION_TIME)).thenReturn(360l);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(typeService.getComposedTypeForCode(CMSItemModel._TYPECODE)).thenReturn(cmsItemComposedType);
		when(typeService.getComposedTypeForCode(TYPE_CODE_1)).thenReturn(subType1);
		when(typeService.getComposedTypeForCode(TYPE_CODE_2)).thenReturn(subType2);
		when(typeService.getComposedTypeForCode(TYPE_CODE_3)).thenReturn(subType3);
		when(typeService.getComposedTypeForCode(TYPE_CODE_REGISTRY)).thenReturn(typeRegistry);
		
		when(composedTypeToStructureConverter.convert(cmsItemComposedType)).thenReturn(cmsItemTypeStructure);
		when(composedTypeToStructureConverter.convert(subType1)).thenReturn(subTypeStructure1);
		when(composedTypeToStructureConverter.convert(subType2)).thenReturn(subTypeStructure2);
		when(composedTypeToStructureConverter.convert(typeRegistry)).thenReturn(typeStructureRegistry1);
		
		
		when(cmsItemComposedType.getCode()).thenReturn(CMSItemModel._TYPECODE);
		when(cmsItemComposedType.getAllSubTypes()).thenReturn(Arrays.asList(subType1, subType2, subType3));
		
		when(subType1.getCode()).thenReturn(TYPE_CODE_1);
		when(subType1.getAllSuperTypes()).thenReturn(Arrays.asList(cmsItemComposedType));
		
		when(subType2.getCode()).thenReturn(TYPE_CODE_2);
		when(subType2.getAllSuperTypes()).thenReturn(Arrays.asList(cmsItemComposedType));
		
		when(subType3.getCode()).thenReturn(TYPE_CODE_3);
		when(subType3.getAllSuperTypes()).thenReturn(Arrays.asList(cmsItemComposedType));

		when(typeRegistry.getCode()).thenReturn(TYPE_CODE_REGISTRY);
		when(typeRegistry.getAllSuperTypes()).thenReturn(Arrays.asList());

		when(componentTypeStructureRegistry.getComponentTypeStructures()).thenReturn(Arrays.asList(typeStructureRegistry1, typeStructureRegistry2));
		when(componentTypeStructureRegistry.getComponentTypeStructure(TYPE_CODE_REGISTRY)).thenReturn(typeStructureRegistry1);
		when(componentTypeStructureRegistry.getComponentTypeStructure(TYPE_CODE_1)).thenReturn(typeStructureRegistry2);
		
		when(typeStructureRegistry1.getTypecode()).thenReturn(TYPE_CODE_REGISTRY);
		
		when(typeBlacklistSet.contains(TYPE_CODE_3)).thenReturn(true);
		
		when(cmsItemTypeStructure.getTypecode()).thenReturn(CMSItemModel._TYPECODE);
		
		when(subTypeStructure1.getTypecode()).thenReturn(TYPE_CODE_1);
		when(subTypeStructure1.getPopulators()).thenReturn(subTypeStructure1PopulatorList);
		when(subTypeStructure1.getAttributes()).thenReturn(subTypeStructure1AttributeList);
		
		when(subTypeStructure2.getTypecode()).thenReturn(TYPE_CODE_2);
		
		when(typeStructureRegistry1.getTypecode()).thenReturn(TYPE_CODE_REGISTRY);
		when(typeStructureRegistry2.getTypecode()).thenReturn(TYPE_CODE_1);
		when(typeStructureRegistry2.getAttributes()).thenReturn(Sets.newHashSet(type1RegistryAttribute1));
		when(typeStructureRegistry2.getPopulators()).thenReturn(Arrays.asList(type1Populator1));

		
		// initialize the service
		service.afterPropertiesSet();
		service.getComponentTypeStructures();
		
	}
	
	@Test
	public void testGetStructureTypeForCMSItem()
	{
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeStructure(CMSItemModel._TYPECODE);
		assertThat(componentTypeStructure, is(cmsItemTypeStructure)); 
		verify(componentTypeStructureRegistry, times(3))
				.getComponentTypeStructure(CMSItemModel._TYPECODE);
		verify(componentTypeStructureRegistry, times(3))
				.getComponentTypeStructure(TYPE_CODE_1);
		verify(componentTypeStructureRegistry, times(3))
				.getComponentTypeStructure(TYPE_CODE_2);
		verify(componentTypeStructureRegistry)
				.getComponentTypeStructure(TYPE_CODE_REGISTRY);
		
	}

	@Test
	public void testGetStructureTypeForSubtypes()
	{
		final ComponentTypeStructure componentTypeStructure1 = service.getComponentTypeStructure(TYPE_CODE_1);
		assertThat(componentTypeStructure1, is(subTypeStructure1));

		final ComponentTypeStructure componentTypeStructure2 = service.getComponentTypeStructure(TYPE_CODE_2);
		assertThat(componentTypeStructure2, is(subTypeStructure2));
	}

	@Test
	public void testGetStructureTypeRegistered()
	{
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeStructure(TYPE_CODE_REGISTRY);
		assertThat(componentTypeStructure, is(typeStructureRegistry1));
	}


	@Test
	public void testGetStructureTypeRegisteredWillAddTypePopulatorsToExistingType()
	{
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeStructure(TYPE_CODE_1);
		assertThat(componentTypeStructure, is(subTypeStructure1));
		assertThat(componentTypeStructure.getPopulators(), contains(type1Populator1));
	}
	
	@Test
	public void testGetStructureTypeRegisteredWillAddAttributesToExistingType()
	{
		final ComponentTypeStructure componentTypeStructure = service.getComponentTypeStructure(TYPE_CODE_1);
		assertThat(componentTypeStructure, is(subTypeStructure1));
		assertThat(componentTypeStructure.getAttributes(), contains(type1RegistryAttribute1));
	}
}
