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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BCustomerPermissionPopulatorTest
{
	private B2BCustomerPermissionPopulator b2BCustomerPermissionPopulator;
	private B2BCustomerModel source;
	private CustomerData target;

	@Mock
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BCustomerModel.class);
		target = new CustomerData();

		b2BCustomerPermissionPopulator = new B2BCustomerPermissionPopulator();
		b2BCustomerPermissionPopulator.setB2BPermissionConverter(b2BPermissionConverter);
	}

	@Test
	public void shouldPopulate()
	{
		Set<B2BPermissionModel> permissions = new HashSet<>();
		B2BPermissionModel permission = mock(B2BPermissionModel.class);
		permissions.add(permission);
		B2BPermissionData b2BPermissionData = mock(B2BPermissionData.class);
		given(b2BPermissionConverter.convert(permission)).willReturn(b2BPermissionData);
		given(source.getPermissions()).willReturn(permissions);

		b2BCustomerPermissionPopulator.populate(source, target);

		Assert.assertNotNull("target permissions should not be null", target.getPermissions());
		Assert.assertEquals("source and target permissions size should be 1", 1, target.getPermissions().size());
		Assert.assertEquals("source and target permissions should match", b2BPermissionData,
				target.getPermissions().iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCustomerPermissionPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCustomerPermissionPopulator.populate(source, null);
	}
}
