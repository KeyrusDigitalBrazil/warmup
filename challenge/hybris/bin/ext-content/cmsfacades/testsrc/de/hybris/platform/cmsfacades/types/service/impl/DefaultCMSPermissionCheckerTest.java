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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.types.service.CMSAttributeTypeService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSPermissionCheckerTest
{
	private static final String INVALID = "invalid";
	private static final String SOME_COMPOSED_TYPE = "someComposedType";

	@InjectMocks
	private DefaultCMSPermissionChecker cmsPermissionChecker;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private CMSAttributeTypeService cmsAttributeTypeService;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private AtomicTypeModel atomicType;

	@Test
	public void shouldHaveReadPermissionForComposedType()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canReadType(any(ComposedTypeModel.class))).thenReturn(true);
		when(composedType.getCode()).thenReturn(SOME_COMPOSED_TYPE);

		final boolean canReadType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.READ);

		verify(permissionCRUDService).canReadType(any(ComposedTypeModel.class));
		assertTrue(canReadType);
	}

	@Test
	public void shouldNotHaveReadPermissionForComposedTypeOfOptionalAttribute()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canReadType(any(ComposedTypeModel.class))).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(true);
		when(composedType.getCode()).thenReturn(SOME_COMPOSED_TYPE);

		final boolean canReadType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.READ);

		assertFalse(canReadType);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailGetReadPermissionForComposedTypeOfRequiredAttribute()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canReadType(any(ComposedTypeModel.class))).thenReturn(false);
		when(attributeDescriptor.getOptional()).thenReturn(false);
		when(attributeDescriptor.getEnclosingType()).thenReturn(composedType);
		when(composedType.getCode()).thenReturn("MockTypeCode");

		cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor, PermissionsConstants.READ);
	}

	@Test
	public void shouldHaveChangePermissionForComposedType()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canChangeType(any(ComposedTypeModel.class))).thenReturn(true);
		when(composedType.getCode()).thenReturn(SOME_COMPOSED_TYPE);

		final boolean canChangeType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.CHANGE);

		verify(permissionCRUDService).canChangeType(any(ComposedTypeModel.class));
		assertTrue(canChangeType);
	}

	@Test
	public void shouldHaveCreatePermissionForComposedType()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canCreateTypeInstance(any(ComposedTypeModel.class))).thenReturn(true);
		when(composedType.getCode()).thenReturn(SOME_COMPOSED_TYPE);

		final boolean canChangeType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.CREATE);

		verify(permissionCRUDService).canCreateTypeInstance(any(ComposedTypeModel.class));
		assertTrue(canChangeType);
	}

	@Test
	public void shouldHaveRemovePermissionForComposedType()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canRemoveTypeInstance(any(ComposedTypeModel.class))).thenReturn(true);
		when(composedType.getCode()).thenReturn(SOME_COMPOSED_TYPE);

		final boolean canChangeType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.REMOVE);

		verify(permissionCRUDService).canRemoveTypeInstance(any(ComposedTypeModel.class));
		assertTrue(canChangeType);
	}

	@Test
	public void shouldHavePermissionForAtomicTypeAndInvalidPermissionName()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(atomicType);

		final boolean canReadType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor, INVALID);

		verifyZeroInteractions(permissionCRUDService);
		assertTrue(canReadType);
	}

	@Test
	public void shouldHavePermissionForAtomicTypeAndNullPermissionName()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(atomicType);

		final boolean canReadType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor, null);

		verifyZeroInteractions(permissionCRUDService);
		assertTrue(canReadType);
	}

	@Test
	public void shouldHaveAllPermissionsForMediaType()
	{
		when(cmsAttributeTypeService.getAttributeContainedType(attributeDescriptor)).thenReturn(composedType);
		when(permissionCRUDService.canCreateTypeInstance(any(ComposedTypeModel.class))).thenReturn(true);
		when(composedType.getCode()).thenReturn(MediaModel._TYPECODE);

		final boolean canChangeType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.CHANGE);
		final boolean canReadType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.READ);
		final boolean canCreateType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.CREATE);
		final boolean canRemoveType = cmsPermissionChecker.hasPermissionForContainedType(attributeDescriptor,
				PermissionsConstants.REMOVE);

		assertTrue(canChangeType);
		assertTrue(canReadType);
		assertTrue(canCreateType);
		assertTrue(canRemoveType);
	}
}
