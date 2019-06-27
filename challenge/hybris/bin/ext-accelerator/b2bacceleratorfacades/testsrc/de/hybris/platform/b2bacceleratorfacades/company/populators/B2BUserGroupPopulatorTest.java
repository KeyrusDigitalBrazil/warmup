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
package de.hybris.platform.b2bacceleratorfacades.company.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("deprecation")
@UnitTest
public class B2BUserGroupPopulatorTest
{
	private static final String USER_GROUP_MODEL_UID = "testUserGroupId";
	private static final String USER_GROUP_MODEL_NAME = "testUserGroupName";

	private B2BUserGroupModel source;
	private B2BUserGroupData target;
	private CustomerModel customerModel1;
	private CustomerModel customerModel2;
	private final B2BUserGroupPopulator b2bUserGroupPopulator = new B2BUserGroupPopulator();

	@Mock
	private Converter<CustomerModel, CustomerData> b2BCustomerConverter;
	@Mock
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;
	@Mock
	private B2BUnitModel parentUnit;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		b2bUserGroupPopulator.setB2BCustomerConverter(b2BCustomerConverter);
		b2bUserGroupPopulator.setB2BPermissionConverter(b2BPermissionConverter);

		final List<B2BPermissionModel> permissions = new ArrayList<B2BPermissionModel>();
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>();

		// Initializing 'source' B2BUserGroupModel and 'target' B2BUserGroupData
		source = new B2BUserGroupModel();
		target = new B2BUserGroupData();

		source.setUid(USER_GROUP_MODEL_UID);
		source.setName(USER_GROUP_MODEL_NAME);

		// Initializing Parent
		source.setUnit(parentUnit);

		// Initializing Permissions
		permissions.add(new B2BPermissionModel());
		permissions.add(new B2BPermissionModel());
		source.setPermissions(permissions);

		// Initializing members
		customerModel1 = new CustomerModel();
		customerModel2 = new CustomerModel();
		final PrincipalModel principalModel1 = new PrincipalModel();
		final PrincipalModel principalModel2 = new PrincipalModel();

		members.add(customerModel1);
		members.add(customerModel2);
		members.add(principalModel1);
		members.add(principalModel2);
		source.setMembers(members);
	}

	@Test
	public void testShouldPopulateB2BUserGroupData()
	{
		Mockito.when(parentUnit.getUid()).thenReturn("testParentId1");
		Mockito.when(parentUnit.getName()).thenReturn("testParentName1");
		Mockito.when(parentUnit.getActive()).thenReturn(Boolean.TRUE);
		Mockito.when(parentUnit.getLocName()).thenReturn("locName");

		Mockito.when(b2BCustomerConverter.convert(customerModel1)).thenReturn(new CustomerData());
		Mockito.when(b2BCustomerConverter.convert(customerModel2)).thenReturn(new CustomerData());

		Mockito.when(b2BPermissionConverter.convert(Mockito.any(B2BPermissionModel.class))).thenReturn(new B2BPermissionData());

		b2bUserGroupPopulator.populate(source, target);

		Assert.assertEquals("Unexpected value for uid", source.getUid(), target.getUid());
		Assert.assertEquals("Unexpected value for name", source.getName(), target.getName());
		Assert.assertNotNull("Unit is null", target.getUnit());
		Assert.assertEquals("Unexpected value for unit.uid", source.getUnit().getUid(), target.getUnit().getUid());
		Assert.assertEquals("Unexpected value for unit.active", source.getUnit().getActive(),
				Boolean.valueOf(target.getUnit().isActive()));
		Assert.assertEquals("Unexpected value for unit.name", source.getUnit().getLocName(), target.getUnit().getName());
		Assert.assertNotNull("Members are null", target.getMembers());
		Assert.assertEquals("Unexpected number of members", 2, target.getMembers().size());
		Assert.assertNotNull("Permissions are null", target.getPermissions());
		Assert.assertEquals("Unexpected number of permissions", 2, target.getPermissions().size());
	}
}
