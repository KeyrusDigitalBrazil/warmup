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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BOrderThresholdEvaluationStrategyTest
{
	private CurrencyModel testCurrency;
	private final DefaultB2BOrderThresholdEvaluationStrategy defaultB2BOrderThresholdEvaluationStrategy = new DefaultB2BOrderThresholdEvaluationStrategy();
	private final AbstractOrderModel order = new OrderModel();
	private final Set<B2BOrderThresholdPermissionModel> permissions = new HashSet<>();
	private final B2BOrderThresholdPermissionModel b2bPermissionModel1 = new B2BOrderThresholdPermissionModel();
	private final B2BOrderThresholdPermissionModel b2bPermissionModel2 = new B2BOrderThresholdPermissionModel();
	private final B2BOrderThresholdPermissionModel b2bPermissionModel3 = new B2BOrderThresholdPermissionModel();
	private final B2BOrderThresholdPermissionModel b2bPermissionModel4 = new B2BOrderThresholdPermissionModel();
	private final B2BOrderThresholdTimespanPermissionModel b2bPermissionModel5 = new B2BOrderThresholdTimespanPermissionModel();

	@Mock
	private ModelService modelService;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultB2BOrderThresholdEvaluationStrategy.setModelService(modelService);
		testCurrency = new CurrencyModel();
		testCurrency.setIsocode("Test");
		order.setCurrency(testCurrency);

		b2bPermissionModel1.setThreshold(Double.valueOf("10.25"));
		b2bPermissionModel1.setCurrency(testCurrency);
		b2bPermissionModel1.setActive(Boolean.TRUE);
		b2bPermissionModel2.setThreshold(Double.valueOf("10.45"));
		final CurrencyModel otherCurrency = new CurrencyModel();
		otherCurrency.setIsocode("other");
		b2bPermissionModel2.setCurrency(otherCurrency); // Different currency from order
		b2bPermissionModel2.setActive(Boolean.TRUE);
		b2bPermissionModel3.setThreshold(Double.valueOf("10.35")); // Highest threshold. Should be returned
		b2bPermissionModel3.setCurrency(testCurrency);
		b2bPermissionModel3.setActive(Boolean.TRUE);
		b2bPermissionModel4.setThreshold(Double.valueOf("10.15"));
		b2bPermissionModel4.setCurrency(testCurrency);
		b2bPermissionModel4.setActive(Boolean.TRUE);
		b2bPermissionModel5.setThreshold(Double.valueOf("10.45")); // Threshold is higher but not B2BOrderThresholdPermissionModel
		b2bPermissionModel5.setCurrency(testCurrency);
		b2bPermissionModel5.setActive(Boolean.TRUE);

		permissions.add(b2bPermissionModel1);
		permissions.add(b2bPermissionModel2);
		permissions.add(b2bPermissionModel3);
		permissions.add(b2bPermissionModel4);
		permissions.add(b2bPermissionModel5);
	}

	@Test
	public void shouldGetPermissionToEvaluate()
	{

		final B2BOrderThresholdPermissionModel permissionToEvaluate = defaultB2BOrderThresholdEvaluationStrategy
				.getPermissionToEvaluate(permissions, order);
		Assert.assertEquals("permissionToEvaluate should be b2bPermissionModel3, but it's not. Tts threshold is"
				+ permissionToEvaluate.getThreshold(), b2bPermissionModel3, permissionToEvaluate);
	}

	@Test
	public void shouldEvaluatePermission()
	{
		final B2BCustomerModel employee = new B2BCustomerModel();
		employee.setUid("uid");
		final B2BUserGroupModel b2bUserGroupModel = new B2BUserGroupModel();
		b2bUserGroupModel.setPermissions(new ArrayList<B2BPermissionModel>(permissions));
		final Set<B2BUserGroupModel> userPermissionGroups = new HashSet<>(1);
		userPermissionGroups.add(b2bUserGroupModel);
		employee.setPermissionGroups(userPermissionGroups);
		employee.setGroups(new HashSet<>());
		employee.setPermissions(new HashSet<>());
		BDDMockito.given(modelService.create(B2BPermissionResultModel.class)).willReturn(new B2BPermissionResultModel());

		final B2BPermissionResultModel permissionResult = defaultB2BOrderThresholdEvaluationStrategy.evaluate(order, employee);
		Assert.assertNotNull("permissionResult should not be null", permissionResult);
		Assert.assertEquals("permission result approver don't match", employee, permissionResult.getApprover());
		Assert.assertEquals("permission result should be pending approval", PermissionStatus.PENDING_APPROVAL,
				permissionResult.getStatus());
		Assert.assertEquals("permission result PermissionTypeCode don't match", B2BOrderThresholdPermissionModel._TYPECODE,
				permissionResult.getPermissionTypeCode());
		Assert.assertEquals("permission result Permission don't match", b2bPermissionModel3, permissionResult.getPermission());
	}

	/**
	 * Permission result status should be open and permission should be null since there is no permission to evaluate
	 */
	@Test
	public void shouldEvaluatePermissionAndResultStatusOpen()
	{
		final B2BCustomerModel employee = new B2BCustomerModel();
		employee.setUid("uid");
		employee.setPermissionGroups(new HashSet<>());
		employee.setGroups(new HashSet<>());
		employee.setPermissions(new HashSet<>());
		BDDMockito.given(modelService.create(B2BPermissionResultModel.class)).willReturn(new B2BPermissionResultModel());

		final B2BPermissionResultModel permissionResult = defaultB2BOrderThresholdEvaluationStrategy.evaluate(order, employee);
		Assert.assertNotNull("permissionResult should not be null", permissionResult);
		Assert.assertEquals("permission result approver don't match", employee, permissionResult.getApprover());
		Assert.assertEquals("permission result should be pending approval", PermissionStatus.OPEN, permissionResult.getStatus());
		Assert.assertEquals("permission result PermissionTypeCode don't match", B2BOrderThresholdPermissionModel._TYPECODE,
				permissionResult.getPermissionTypeCode());
		Assert.assertNull("permission result Permission should be null", permissionResult.getPermission());
	}
}
