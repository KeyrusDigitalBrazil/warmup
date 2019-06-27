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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.common.service.StringDecapitalizer;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.data.StructureTypeCategory;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeAttributeStructure;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ComponentTypeStructureConverterTest
{
	private static final String QUALIFIER1 = "qualifier1";
	private static final String QUALIFIER2 = "qualifier2";
	private static final String QUALIFIER3 = "qualifier3";
	private static final String QUALIFIER4 = "qualifier4";

	private static final String CODE = "code";
	private static final String ATTRIBUTE4_TYPECODE = "attribute4_code";

	@InjectMocks
	@Spy
	private ComponentTypeStructureConverter converter;

	@Mock
	private TypeService typeService;
	@Mock
	private StringDecapitalizer stringDecapitalizer;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private CMSPermissionChecker cmsPermissionChecker;
	@Mock
	private ComposedTypeModel composedType, attribute4ComposedType;
	@Mock
	private AttributeDescriptorModel attribute1, attribute2, attribute3;
	@Mock
	private ComponentTypeStructure structureType;
	@Mock
	private ComponentTypeAttributeStructure structureAttribute1, structureAttribute2, structureAttribute4;
	@Mock
	private ComponentTypeAttributeData componentTypeAttributeData;
	@Mock
	private Populator<ComposedTypeModel, ComponentTypeData> popType1, popType2, defaultPopType;
	@Mock
	private Populator<AttributeDescriptorModel, ComponentTypeAttributeData> popAttribute1, popAttribute2, popAttribute4;
	@Mock
	private ObjectFactory<ComponentTypeAttributeData> componentTypeAttributeDataFactory;

	private ComponentTypeData target;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp()
	{
		target = new ComponentTypeData();

		when(typeService.getComposedTypeForCode(CODE)).thenReturn(composedType);
		when(typeService.getComposedTypeForCode(ATTRIBUTE4_TYPECODE)).thenReturn(attribute4ComposedType);

		when(composedType.getCode()).thenReturn(CODE);
		when(composedType.getDeclaredattributedescriptors()).thenReturn(Sets.newHashSet(attribute1));
		when(composedType.getInheritedattributedescriptors()).thenReturn(Sets.newHashSet(attribute2, attribute3));

		when(attribute1.getQualifier()).thenReturn(QUALIFIER1);
		when(attribute1.getEnclosingType()).thenReturn(composedType);
		when(attribute2.getQualifier()).thenReturn(QUALIFIER2);
		when(attribute2.getEnclosingType()).thenReturn(composedType);
		when(attribute3.getQualifier()).thenReturn(QUALIFIER3);
		when(attribute3.getEnclosingType()).thenReturn(composedType);

		when(structureType.getTypecode()).thenReturn(CODE);
		when(structureType.getPopulators()).thenReturn(Lists.newArrayList(popType1, popType2));
		when(structureType.getAttributes())
				.thenReturn(Sets.newHashSet(structureAttribute1, structureAttribute2, structureAttribute4));
		when(structureType.getCategory()).thenReturn(StructureTypeCategory.COMPONENT);
		when(structureAttribute1.getQualifier()).thenReturn(QUALIFIER1);
		when(structureAttribute1.getPopulators()).thenReturn(Lists.newArrayList(popAttribute1, popAttribute2));
		when(structureAttribute2.getQualifier()).thenReturn(QUALIFIER2);
		when(structureAttribute2.getPopulators()).thenReturn(Lists.newArrayList(popAttribute1, popAttribute2));
		when(structureAttribute4.getQualifier()).thenReturn(QUALIFIER4);
		when(structureAttribute4.getTypecode()).thenReturn(ATTRIBUTE4_TYPECODE);
		when(structureAttribute4.getPopulators()).thenReturn(Lists.newArrayList(popAttribute4));

		configureComponentTypeAttributeDataFactory();

		when(stringDecapitalizer.decapitalize(any())).thenReturn(Optional.of("cmsParagraphComponentData"));

		// Permissions
		when(permissionCRUDService.canReadAttribute(any())).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(any())).thenReturn(true);
		when(permissionCRUDService.canReadType(CODE)).thenReturn(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldConvertWithDefault_NoStructureTypeFoundForCode()
	{
		when(structureType.getPopulators()).thenReturn(Lists.newArrayList(defaultPopType));

		converter.convert(structureType, target);

		verify(defaultPopType).populate(composedType, target);
	}

	@Test
	public void shouldPopulateComponentTypeProperties()
	{
		converter.convert(structureType, target);

		verify(popType1).populate(composedType, target);
		verify(popType2).populate(composedType, target);
	}

	@Test
	public void shouldConvertComponentTypeAttributes()
	{
		converter.convert(structureType, target);

		verify(popAttribute1).populate(Mockito.eq(attribute1), Mockito.any(ComponentTypeAttributeData.class));
		verify(popAttribute2).populate(Mockito.eq(attribute1), Mockito.any(ComponentTypeAttributeData.class));
		verify(popAttribute1).populate(Mockito.eq(attribute2), Mockito.any(ComponentTypeAttributeData.class));
		verify(popAttribute2).populate(Mockito.eq(attribute2), Mockito.any(ComponentTypeAttributeData.class));
	}

	@Test
	public void shouldConvertComponentTypeAttributesWithNoDescriptor()
	{
		final ArgumentCaptor<AttributeDescriptorModel> populatorArg = ArgumentCaptor.forClass(AttributeDescriptorModel.class);

		converter.convert(structureType, target);

		verify(popAttribute4).populate(populatorArg.capture(), Mockito.any(ComponentTypeAttributeData.class));
		assertThat(populatorArg.getValue().getQualifier(), equalTo(QUALIFIER4));
		assertThat(populatorArg.getValue().getAttributeType(), equalTo(attribute4ComposedType));
		assertThat(populatorArg.getValue().getEnclosingType(), equalTo(composedType));
	}

	@Test
	public void shouldNotConvertComponentTypeAttributesWithNoStructureAttributes()
	{
		converter.convert(structureType, target);

		verify(popAttribute1, times(0)).populate(Mockito.eq(attribute3), Mockito.any(ComponentTypeAttributeData.class));
		verify(popAttribute2, times(0)).populate(Mockito.eq(attribute3), Mockito.any(ComponentTypeAttributeData.class));
	}


	@Test
	public void shouldPopulateTypAttributeWithCorrectCMSParagraphComponentName()
	{
		converter.convert(structureType, target);
		verify(stringDecapitalizer).decapitalize(any(Class.class));
	}

	@Test
	public void shouldNotBeEditableWithReadOnlyAttributePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeAttribute(attribute2)).thenReturn(false);

		// WHEN
		converter.convert(structureType, target);

		// THEN
		assertTrue(target.getAttributes().stream().filter(attribute -> attribute.getQualifier().equals(QUALIFIER2))
				.noneMatch(ComponentTypeAttributeData::isEditable));
	}

	@Test
	public void shouldNotBeEditableWithNoReadAttributePermission()
	{
		// GIVEN
		when(permissionCRUDService.canReadAttribute(attribute2)).thenReturn(false);

		// WHEN
		converter.convert(structureType, target);

		// THEN
		assertTrue(target.getAttributes().stream().noneMatch(attribute -> attribute.getQualifier().equals(QUALIFIER2)));
	}

	@Test
	public void shouldIgnorePermissionForNoAttributeDescriptor()
	{
		// WHEN
		converter.convert(structureType, target);

		// THEN
		target.getAttributes().stream().filter(attribute -> attribute.getQualifier().equals(QUALIFIER4))
				.forEach(attribute -> assertTrue(attribute.isEditable()));
	}

	@Test
	public void shouldReturnEmptyStructureWithNoReadPermissionForRequiredAttribute()
	{
		// GIVEN
		when(permissionCRUDService.canReadAttribute(attribute2)).thenReturn(false);
		configurePopulatorToSetRequiredFlag(popAttribute2);

		// WHEN
		converter.convert(structureType, target);

		// THEN
		assertTrue(target.getAttributes().isEmpty());
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailWhenNoReadTypePermission()
	{
		// GIVEN
		doThrow(new TypePermissionException("")).when(converter).throwTypePermissionException(any(), any());
		when(permissionCRUDService.canReadType(CODE)).thenReturn(false);

		// WHEN
		converter.convert(structureType, target);
	}

	@Test
	public void shouldBeNullWhenNoReadPermissionForContainedType()
	{
		doReturn(attribute1).when(converter).getAttributeDescriptor(composedType, structureAttribute1);
		doReturn(componentTypeAttributeData).when(converter).convertAttribute(structureAttribute1, attribute1);
		doReturn(true).when(converter).enclosingTypeHasAttribute(composedType, QUALIFIER1);
		doReturn(true).when(converter).hasReadPermissionOnAttribute(attribute1);
		doReturn(false).when(cmsPermissionChecker).hasPermissionForContainedType(any(), any());

		final ComponentTypeAttributeData result = converter.convertAttributeAndEvaluatePermissions(composedType,
				structureAttribute1);

		assertThat(result, nullValue());
	}

	protected void configureComponentTypeAttributeDataFactory()
	{
		when(componentTypeAttributeDataFactory.getObject()).then(answer -> {
			final ComponentTypeAttributeData data = new ComponentTypeAttributeData();
			data.setEditable(true);
			data.setCmsStructureType("someStructureType");

			return data;
		});
	}

	protected void configurePopulatorToSetRequiredFlag(
			final Populator<AttributeDescriptorModel, ComponentTypeAttributeData> populator)
	{
		doAnswer(invocation -> {
			final ComponentTypeAttributeData attributeData = invocation.getArgumentAt(1, ComponentTypeAttributeData.class);
			attributeData.setRequired(true);

			return null;
		}).when(populator).populate(any(), any());
	}
}
