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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class B2BUserGroupPermissionPopulatorTest
{

	// Defining and Initializing test variables
	private B2BUserGroupModel source;
	private B2BUserGroupData target;
	private B2BPermissionModel b2bPermission1;
	private B2BPermissionModel b2bPermission2;

	private final B2BUserGroupPermissionPopulator b2bUserGroupPermissionPopulator = new B2BUserGroupPermissionPopulator();

	@Mock
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		b2bUserGroupPermissionPopulator.setB2BPermissionConverter(b2BPermissionConverter);

		// Initializing 'source' B2BUserGroupModel and 'target' B2BUserGroupData
		source = new B2BUserGroupModel();
		target = new B2BUserGroupData();

		b2bPermission1 = new B2BPermissionModel();
		b2bPermission2 = new B2BPermissionModel();

		source.setPermissions(new ArrayList<B2BPermissionModel>());
		source.getPermissions().add(b2bPermission1);
		source.getPermissions().add(b2bPermission2);
	}

	@Test
	public void testShouldPopulateB2BUserGroupDataPermissions()
	{
		b2bUserGroupPermissionPopulator.populate(source, target);
		Assert.assertTrue(target.getPermissions().size() == 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUserGroupModel()
	{
		b2bUserGroupPermissionPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUserGroupData()
	{
		b2bUserGroupPermissionPopulator.populate(source, null);
	}
}
