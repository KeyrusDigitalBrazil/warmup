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
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BCustomerApproverPopulatorTest
{
	private B2BCustomerApproverPopulator b2BCustomerApproverPopulator;
	private B2BCustomerModel source;
	private CustomerData target;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Mock
	private LocaleProvider localeProvider;

	@Mock
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BCustomerModel.class);
		target = new CustomerData();

		b2BCustomerApproverPopulator = new B2BCustomerApproverPopulator();
		b2BCustomerApproverPopulator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
	}

	@Test
	public void shouldPopulate()
	{
		Set<B2BCustomerModel> approvers = new HashSet<>();
		B2BCustomerModel approver = mock(B2BCustomerModel.class);
		given(approver.getName()).willReturn("approverName");
		given(approver.getEmail()).willReturn("approverEmail");
		given(approver.getUid()).willReturn("approverUid");

		// roles
		Set<PrincipalGroupModel> roles = new HashSet<>();
		UserGroupModel roleGroup = mock(UserGroupModel.class);
		String roleUid = "usergroup";
		given(roleGroup.getUid()).willReturn(roleUid);
		roles.add(roleGroup);
		given(approver.getGroups()).willReturn(roles);
		approvers.add(approver);
		given(source.getApprovers()).willReturn(approvers);

		List<String> roleGroups = new ArrayList<>();
		roleGroups.add(roleUid);
		given(b2BUserGroupsLookUpStrategy.getUserGroups()).willReturn(roleGroups);

		b2BCustomerApproverPopulator.populate(source, target);

		// approvers
		Assert.assertNotNull("target approvers should not be null", target.getApprovers());
		Assert.assertEquals("source and target approver size should be 1", 1, target.getApprovers().size());
		CustomerData targetApprover = target.getApprovers().iterator().next();
		Assert.assertEquals("source and target approver uid should match", approver.getUid(), targetApprover.getUid());
		Assert.assertEquals("source and target approver name should match", approver.getName(), targetApprover.getName());
		Assert.assertEquals("source and target approver email should match", approver.getEmail(), targetApprover.getEmail());

		// approver roles
		Assert.assertEquals("source and target approver roles size should be 1", 1, targetApprover.getRoles().size());
		Assert.assertEquals("source and target approver roles should match", roleUid, targetApprover.getRoles().iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCustomerApproverPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCustomerApproverPopulator.populate(source, null);
	}

}
