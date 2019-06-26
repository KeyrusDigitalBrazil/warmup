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
import de.hybris.platform.b2b.model.B2BOrderThresholdPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.strategies.impl.AbstractB2BOrderThresholdPermissionEvaluationStrategy.ThresholdComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@UnitTest
public class ThresholdComparatorTest
{
	private static final Double THRESHOLD_10_25 = Double.valueOf("10.25");
	private static final Double THRESHOLD_10_15 = Double.valueOf("10.15");
	private static final Double THRESHOLD_10_35 = Double.valueOf("10.35");

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	final B2BOrderThresholdPermissionModel permission1 = new B2BOrderThresholdPermissionModel();
	final B2BOrderThresholdPermissionModel permission2 = new B2BOrderThresholdPermissionModel();
	final B2BOrderThresholdPermissionModel permission3 = new B2BOrderThresholdPermissionModel();
	final List<B2BOrderThresholdPermissionModel> permissions = new ArrayList<>();

	@Before
	public void setup()
	{
		permission1.setCode("permission1");
		permission2.setCode("permission2");
		permission3.setCode("permission3");
		permission1.setThreshold(THRESHOLD_10_35);
		permission2.setThreshold(THRESHOLD_10_15);
		permission3.setThreshold(THRESHOLD_10_25);
	}

	@Test
	public void shouldThrowIllegalStateExceptionForNullPermission()
	{
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Permissions to compare may not be null");
		permissions.add(null);
		permissions.add(permission1);
		Collections.sort(permissions, new ThresholdComparator());
	}

	@Test
	public void shouldThrowIllegalStateExceptionForNullThresholdOfPermission()
	{
		final B2BOrderThresholdPermissionModel permissionNullThreshold = new B2BOrderThresholdPermissionModel();
		permissionNullThreshold.setThreshold(null);
		permissionNullThreshold.setCode("permissionNullThreshold");
		permissions.add(permissionNullThreshold);
		permissions.add(permission2);
		exception.expect(IllegalStateException.class);
		exception.expectMessage(
				"Permission thresholds must not be null; given permission2's Threshold is 10.15; permissionNullThreshold's Threshold is null");
		Collections.sort(permissions, new ThresholdComparator());
	}

	@Test
	public void shouldSortPermissionsForB2BOrderThresholdPermissionModel()
	{
		permissions.add(permission1);
		permissions.add(permission2);
		permissions.add(permission3);
		Collections.sort(permissions, new ThresholdComparator());

		Assert.assertTrue("Threshold of first permission should be 10.15",
				permissions.get(0).getThreshold().equals(THRESHOLD_10_15));
		Assert.assertTrue("Threshold of first permission should be 10.25",
				permissions.get(1).getThreshold().equals(THRESHOLD_10_25));
		Assert.assertTrue("Threshold of first permission should be 10.35",
				permissions.get(2).getThreshold().equals(THRESHOLD_10_35));
	}

	@Test
	public void shouldSortPermissionsForB2BOrderThresholdTimespanPermissionModel()
	{
		final B2BOrderThresholdTimespanPermissionModel timespanPermission1 = new B2BOrderThresholdTimespanPermissionModel();
		final B2BOrderThresholdTimespanPermissionModel timespanPermission2 = new B2BOrderThresholdTimespanPermissionModel();
		final B2BOrderThresholdTimespanPermissionModel timespanPermission3 = new B2BOrderThresholdTimespanPermissionModel();
		timespanPermission1.setThreshold(THRESHOLD_10_35);
		timespanPermission2.setThreshold(THRESHOLD_10_15);
		timespanPermission3.setThreshold(THRESHOLD_10_25);
		final List<B2BOrderThresholdTimespanPermissionModel> timespanPermissions = new ArrayList<>();
		timespanPermissions.add(timespanPermission1);
		timespanPermissions.add(timespanPermission2);
		timespanPermissions.add(timespanPermission3);
		Collections.sort(timespanPermissions, new ThresholdComparator());

		Assert.assertTrue("Threshold of first permission should be 10.15",
				timespanPermissions.get(0).getThreshold().equals(THRESHOLD_10_15));
		Assert.assertTrue("Threshold of first permission should be 10.25",
				timespanPermissions.get(1).getThreshold().equals(THRESHOLD_10_25));
		Assert.assertTrue("Threshold of first permission should be 10.35",
				timespanPermissions.get(2).getThreshold().equals(THRESHOLD_10_35));
	}

}
