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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BPermissionTypePopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private TypeService typeService;

	@Mock
	private EnumerationValueModel enumerationValueModel;

	private B2BPermissionTypeEnum b2bBudgetExceededPermission;
	private B2BPermissionTypeEnum b2bOrderThresholdPermission;
	private B2BPermissionTypeEnum b2bOrderThresholdTimeSpanPermission;

	private final B2BPermissionTypePopulator b2bPermissionTypePopulator = new B2BPermissionTypePopulator();

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		b2bBudgetExceededPermission = B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION;
		b2bOrderThresholdPermission = B2BPermissionTypeEnum.B2BORDERTHRESHOLDPERMISSION;
		b2bOrderThresholdTimeSpanPermission = B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION;
		b2bPermissionTypePopulator.setTypeService(typeService);

		BDDMockito.given(typeService.getEnumerationValue(b2bBudgetExceededPermission)).willReturn(enumerationValueModel);
		BDDMockito.given(typeService.getEnumerationValue(b2bOrderThresholdPermission)).willReturn(enumerationValueModel);
		BDDMockito.given(typeService.getEnumerationValue(b2bOrderThresholdTimeSpanPermission)).willReturn(enumerationValueModel);

		BDDMockito.given(enumerationValueModel.getName()).willReturn("don't care");
	}

	@Test
	public void testPopulate()
	{
		final B2BPermissionTypeData permissionTypeData = new B2BPermissionTypeData();

		b2bPermissionTypePopulator.populate(b2bBudgetExceededPermission, permissionTypeData);
		Assert.assertEquals(b2bBudgetExceededPermission.getCode(), permissionTypeData.getCode());
		Assert.assertEquals(enumerationValueModel.getName(), permissionTypeData.getName());

		b2bPermissionTypePopulator.populate(b2bOrderThresholdPermission, permissionTypeData);
		Assert.assertEquals(b2bOrderThresholdPermission.getCode(), permissionTypeData.getCode());
		Assert.assertEquals(enumerationValueModel.getName(), permissionTypeData.getName());

		b2bPermissionTypePopulator.populate(b2bOrderThresholdTimeSpanPermission, permissionTypeData);
		Assert.assertEquals(b2bOrderThresholdTimeSpanPermission.getCode(), permissionTypeData.getCode());
		Assert.assertEquals(enumerationValueModel.getName(), permissionTypeData.getName());
	}

	@Test
	public void testPopulateSourceNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("source");

		b2bPermissionTypePopulator.populate(null, new B2BPermissionTypeData());
	}

	@Test
	public void testPopulateTargetNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("target");

		b2bPermissionTypePopulator.populate(B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION, null);
	}

}
