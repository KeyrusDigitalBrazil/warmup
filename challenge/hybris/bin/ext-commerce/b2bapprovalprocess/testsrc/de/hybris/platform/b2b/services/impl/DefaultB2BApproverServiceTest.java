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
package de.hybris.platform.b2b.services.impl;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.SetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BApproverServiceTest
{
	private static final String USER_ID = "user";
	private static final String APPROVER_ID = "approver";
	private static final String UNIT_ID = "unitId";

	private final DefaultB2BApproverService defaultB2BApproverService = new DefaultB2BApproverService();

	private B2BCustomerModel user;
	private B2BCustomerModel approver;
	private B2BUserGroupModel approverGroup;
	private B2BUnitModel unit;
	private Set<B2BCustomerModel> approvers;
	private Set<PrincipalGroupModel> groups;

	@Mock
	private UserService userService;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		// Users
		approver = new B2BCustomerModel();
		approver.setUid(APPROVER_ID);
		approver.setGroups(new HashSet<PrincipalGroupModel>());

		user = new B2BCustomerModel();
		user.setUid(USER_ID);

		final Set<B2BCustomerModel> userApprovers = new HashSet<B2BCustomerModel>();
		userApprovers.add(approver);
		user.setApprovers(userApprovers);

		// Groups
		approverGroup = new B2BUserGroupModel();
		approverGroup.setUid(B2BConstants.B2BAPPROVERGROUP);

		approvers = new HashSet<>();

		// Unit
		unit = Mockito.mock(B2BUnitModel.class);

		// User service
		BDDMockito.given(userService.getUserForUID(USER_ID, B2BCustomerModel.class)).willReturn(user);
		BDDMockito.given(userService.getUserForUID(APPROVER_ID, B2BCustomerModel.class)).willReturn(approver);
		BDDMockito.given(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).willReturn(approverGroup);
		BDDMockito.given(b2bCommerceUnitService.getUnitForUid(UNIT_ID)).willReturn(unit);
		BDDMockito.given(unit.getApprovers()).willReturn(userApprovers);

		defaultB2BApproverService.setB2bUnitService(b2bUnitService);
		defaultB2BApproverService.setUserService(userService);
		defaultB2BApproverService.setModelService(modelService);
		defaultB2BApproverService.setB2bCommerceUnitService(b2bCommerceUnitService);
	}

	@Test
	public void testGetAllApprovers() throws Exception
	{
		final B2BCustomerModel mockB2BCustomerModel = Mockito.mock(B2BCustomerModel.class);
		final B2BUnitModel mockB2BUnitModel = Mockito.mock(B2BUnitModel.class);
		final B2BUserGroupModel mockB2BUserGroupModel = Mockito.mock(B2BUserGroupModel.class);
		final PrincipalGroupModel mockPrincipalGroupModel = Mockito.mock(PrincipalGroupModel.class);

		when(mockB2BCustomerModel.getCustomerID()).thenReturn("customer123");
		when(mockB2BUserGroupModel.getUid()).thenReturn(B2BConstants.B2BAPPROVERGROUP);
		when(mockB2BCustomerModel.getGroups()).thenReturn(Collections.singleton(mockPrincipalGroupModel));
		when(b2bUnitService.getParent(mockB2BCustomerModel)).thenReturn(mockB2BUnitModel);
		when(mockB2BCustomerModel.getApprovers()).thenReturn(Collections.singleton(mockB2BCustomerModel));
		when(mockB2BCustomerModel.getApproverGroups()).thenReturn(Collections.singleton(mockB2BUserGroupModel));
		when(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).thenReturn(mockB2BUserGroupModel);
		when(Boolean.valueOf(userService.isMemberOfGroup(mockB2BCustomerModel, mockB2BUserGroupModel))).thenReturn(Boolean.TRUE);

		//Only return active approvers
		when(mockB2BCustomerModel.getActive()).thenReturn(Boolean.TRUE);

		final List<B2BCustomerModel> allApprovers = defaultB2BApproverService.getAllApprovers(mockB2BCustomerModel);
		Assert.assertNotNull(allApprovers);
		Assert.assertTrue(CollectionUtils.isNotEmpty(allApprovers));
		Assert.assertEquals(allApprovers.get(0).getCustomerID(), mockB2BCustomerModel.getCustomerID());
	}

	@Test
	public void testShouldAddApproverToCustomer()
	{
		user.setApprovers(SetUtils.EMPTY_SET);
		approver.getGroups().add(approverGroup);

		final B2BCustomerModel returnedApprover = defaultB2BApproverService.addApproverToCustomer(USER_ID, APPROVER_ID);
		Assert.assertEquals("Unexpected approver returned", returnedApprover, approver);
		Assert.assertNotNull("Approver groups is null", returnedApprover.getGroups());
		Assert.assertTrue("Approver has to be in the approver group", returnedApprover.getGroups().contains(approverGroup));
		Assert.assertTrue("Approver was not added to user", user.getApprovers().contains(approver));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldNotAddApproverToCustomerNotInAppGrp()
	{
		defaultB2BApproverService.addApproverToCustomer(USER_ID, APPROVER_ID);
	}

	@Test
	public void testShouldRemoveApproverFromCustomer()
	{
		Assert.assertTrue(user.getApprovers().contains(approver));

		final B2BCustomerModel returnedApprover = defaultB2BApproverService.removeApproverFromCustomer(USER_ID, APPROVER_ID);
		Assert.assertEquals("Unexpected approver returned", returnedApprover, approver);
		Assert.assertFalse("Approver was not removed from user", user.getApprovers().contains(approver));
	}


	public void shouldAddApproverToUnit()
	{
		Assert.assertEquals(approver, defaultB2BApproverService.addApproverToUnit(UNIT_ID, APPROVER_ID));
		groups.add(approverGroup);
		approvers.add(approver);
		verify(approver, times(1)).setGroups(groups);
		verify(unit, times(1)).setApprovers(approvers);
		verify(modelService, times(1)).saveAll(approver, unit);
	}

	@Test
	public void shouldRemoveApproverFromUnit()
	{
		Assert.assertEquals(approver, defaultB2BApproverService.removeApproverFromUnit(UNIT_ID, APPROVER_ID));
		verify(unit, times(1)).setApprovers(approvers);
		verify(modelService, times(1)).saveAll(approver, unit);
	}

}
