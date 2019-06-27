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
import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUserGroupEditPermissionsPopulatorTest
{
	private B2BUserGroupEditPermissionsPopulator b2BUserGroupEditPermissionsPopulator;
	private B2BCustomerModel source;
	private CustomerData target;

	@Mock
	private B2BCommerceB2BUserGroupService b2bCommerceB2bUserGroupService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BCustomerModel.class);
		target = new CustomerData();

		b2BUserGroupEditPermissionsPopulator = new B2BUserGroupEditPermissionsPopulator();
		b2BUserGroupEditPermissionsPopulator.setB2bCommerceB2bUserGroupService(b2bCommerceB2bUserGroupService);
	}

	@Test
	public void shouldPopulate()
	{
		String uid = "uid";
		List<B2BUserGroupData> customerPermissionGroups = new ArrayList<>();
		B2BUserGroupData userGroupData = new B2BUserGroupData();
		userGroupData.setUid(uid);
		customerPermissionGroups.add(userGroupData);
		target.setPermissionGroups(customerPermissionGroups);
		SearchPageData<B2BUserGroupModel> currentUserPermissionGroups = mock(SearchPageData.class);
		given(b2bCommerceB2bUserGroupService.getPagedB2BUserGroups(BDDMockito.any())).willReturn(currentUserPermissionGroups);
		List<B2BUserGroupModel> results = new ArrayList<>();
		B2BUserGroupModel result = mock(B2BUserGroupModel.class);
		given(result.getUid()).willReturn(uid);
		results.add(result);
		given(currentUserPermissionGroups.getResults()).willReturn(results);

		Assert.assertFalse("target userGroupData should not be editable", userGroupData.isEditable());
		b2BUserGroupEditPermissionsPopulator.populate(source, target);
		Assert.assertTrue("target userGroupData should be editable", userGroupData.isEditable());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BUserGroupEditPermissionsPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BUserGroupEditPermissionsPopulator.populate(source, null);
	}
}
