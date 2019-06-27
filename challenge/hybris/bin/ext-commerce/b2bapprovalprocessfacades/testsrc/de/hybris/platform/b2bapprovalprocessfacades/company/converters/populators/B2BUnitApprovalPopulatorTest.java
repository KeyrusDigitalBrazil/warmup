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
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.services.B2BApprovalProcessService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUnitApprovalPopulatorTest
{
	private B2BUnitApprovalPopulator b2BUnitApprovalPopulator;
	private B2BUnitModel source;
	private B2BUnitData target;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BApprovalProcessService b2BApprovalProcessService;

	@Mock
	private UserService userService;

	@Mock
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BUnitModel.class);
		target = new B2BUnitData();

		b2BUnitApprovalPopulator = new B2BUnitApprovalPopulator();
		b2BUnitApprovalPopulator.setB2BApprovalProcessService(b2BApprovalProcessService);
		b2BUnitApprovalPopulator.setUserService(userService);
		b2BUnitApprovalPopulator.setB2BCustomerConverter(b2BCustomerConverter);
	}

	@Test
	public void shouldPopulate()
	{
		String apprProcesCode = "apprProcesCode";
		String apprProcesName = "apprProcesName";
		given(source.getApprovalProcessCode()).willReturn(apprProcesCode);
		Map<String, String> process = new HashMap<>();
		process.put(apprProcesCode, apprProcesName);
		given(b2BApprovalProcessService.getProcesses(null)).willReturn(process);

		// approvers
		Set<B2BCustomerModel> approvers = new HashSet<>();
		B2BCustomerModel approver = mock(B2BCustomerModel.class);
		approvers.add(approver);
		given(b2BApprovalProcessService.getProcesses(null)).willReturn(process);
		UserGroupModel approverGroup = mock(UserGroupModel.class);
		given(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).willReturn(approverGroup);
		given(source.getApprovers()).willReturn(approvers);
		given(userService.isMemberOfGroup(approver, approverGroup)).willReturn(Boolean.TRUE);
		CustomerData approverData = mock(CustomerData.class);
		given(b2BCustomerConverter.convert(approver)).willReturn(approverData);

		b2BUnitApprovalPopulator.populate(source, target);

		Assert.assertEquals("source and target approvalProcessCode should match", source.getApprovalProcessCode(),
				target.getApprovalProcessCode());
		Assert.assertEquals("source and target approvalProcessCode should match", apprProcesName, target.getApprovalProcessName());
		Assert.assertNotNull("target Approvers should not be null", target.getApprovers());
		Assert.assertEquals("source and target Approvers size should be 1", 1, target.getApprovers().size());
		Assert.assertEquals("source and target Approvers should match", approverData, target.getApprovers().iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BUnitApprovalPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BUnitApprovalPopulator.populate(source, null);
	}

}
