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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BOrderThresholdTimespanPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BPermissionService;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionTypeData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.testframework.Transactional;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@SuppressWarnings("deprecation")
@Transactional
@ContextConfiguration(locations =
{ "classpath:b2bacceleratorfacades/test/b2bacceleratorfacades-test-spring.xml" })
public class DefaultB2BCommercePermissionFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final int TEST_PAGE_SIZE = 5;
	private static final String DC_UNIT_UID = "DC";
	private static final String B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION
			.getCode();
	private static final String B2B_ORDER_THRESHOLD_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BORDERTHRESHOLDPERMISSION
			.getCode();
	private static final String B2B_ORDER_THRESHOLD_TIMESPAN_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION
			.getCode();
	private static final String DC_BUDGET_PERMISSION_CODE = "DC BUDGET";
	private static final String DC_10000_TIMESPAN_EUR_PERMISSION_CODE = "DC 10000 TIMESPAN EUR";
	private static final String TEST_PERMISSION_CODE = "testPermission";

	private static final Logger LOG = Logger.getLogger(DefaultB2BCommercePermissionFacadeIntegrationTest.class);

	@Resource
	private DefaultB2BCommercePermissionFacade legacyB2BCommercePermissionFacade;

	@Resource
	private DefaultB2BPermissionService defaultB2BPermissionService;

	@Before
	public void setUp() throws Exception
	{
		LOG.info("Creating data for DefaultB2BCommercePermissionFacadeIntegrationTest ..");
		final long startTime = System.currentTimeMillis();
		createCoreData();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");

		LOG.info("Finished creating data for DefaultB2BCommercePermissionFacadeIntegrationTest in "
				+ (System.currentTimeMillis() - startTime) + "ms");
	}

	@Test
	public void testAddPermission()
	{
		final B2BUnitData unitData = new B2BUnitData();
		unitData.setUid(DC_UNIT_UID);
		final B2BPermissionData testPermissionData = new B2BPermissionData();
		testPermissionData.setCode(TEST_PERMISSION_CODE);
		testPermissionData.setB2BPermissionTypeData(legacyB2BCommercePermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION));
		testPermissionData.setUnit(unitData);

		legacyB2BCommercePermissionFacade.addPermission(testPermissionData);

		// use the service layer to assert the permission has been created properly
		final B2BPermissionModel testPermission = defaultB2BPermissionService.getB2BPermissionForCode(TEST_PERMISSION_CODE);
		assertNotNull("Permission was not created", testPermission);
		assertNotNull("Permission unit was not set", testPermission.getUnit());
		assertTrue("Unexpexted permission type was created", testPermission instanceof B2BBudgetExceededPermissionModel);
		assertEquals("Unexpected permission code", TEST_PERMISSION_CODE, testPermission.getCode());
		assertEquals("Unexpected permission unit", DC_UNIT_UID, testPermission.getUnit().getUid());
	}

	@Test
	public void testEnableDisablePermission()
	{
		// load an active permission
		B2BPermissionModel permission = defaultB2BPermissionService.getB2BPermissionForCode(DC_BUDGET_PERMISSION_CODE);
		assertEquals("Permission was not active", Boolean.TRUE, permission.getActive());

		// disable and assert that it's not active
		legacyB2BCommercePermissionFacade.enableDisablePermission(DC_BUDGET_PERMISSION_CODE, false);
		permission = defaultB2BPermissionService.getB2BPermissionForCode(DC_BUDGET_PERMISSION_CODE);
		assertEquals("Permission was active", Boolean.FALSE, permission.getActive());

		// enable and assert that it's active again
		legacyB2BCommercePermissionFacade.enableDisablePermission(DC_BUDGET_PERMISSION_CODE, true);
		permission = defaultB2BPermissionService.getB2BPermissionForCode(DC_BUDGET_PERMISSION_CODE);
		assertEquals("Permission was not active", Boolean.TRUE, permission.getActive());
	}

	@Test
	public void testGetPagedPermissions()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(TEST_PAGE_SIZE);

		final SearchPageData<B2BPermissionData> pagedPermissions = legacyB2BCommercePermissionFacade
				.getPagedPermissions(pageableData);

		assertNotNull(pagedPermissions.getResults());
		assertNotNull(pagedPermissions.getPagination());
		assertEquals("Unexpected number of results", TEST_PAGE_SIZE, pagedPermissions.getResults().size());
		assertEquals("Unexpected current page", 0, pagedPermissions.getPagination().getCurrentPage());
		assertEquals("Unexpected number of pages", 4, pagedPermissions.getPagination().getNumberOfPages());
		assertEquals("Unexpected page size", TEST_PAGE_SIZE, pagedPermissions.getPagination().getPageSize());
		assertEquals("Unexpected total number of results", 19, pagedPermissions.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetPermissionDetails()
	{
		final B2BPermissionData permission = legacyB2BCommercePermissionFacade.getPermissionDetails(DC_BUDGET_PERMISSION_CODE);

		assertNotNull("Permission was null", permission);
		assertNotNull("Permission permission type was null", permission.getB2BPermissionTypeData());
		assertNotNull("Permission unit was null", permission.getUnit());
		assertNull("Permission currency was not null", permission.getCurrency());
		assertNull("Permission value was not null", permission.getValue());
		assertNull("Permission time span was not null", permission.getTimeSpan());
		assertNull("Permission period range was not null", permission.getPeriodRange());
		assertEquals("Unexpected permission type", permission.getB2BPermissionTypeData().getCode(),
				B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE);
		assertEquals("Unexpected permission code", permission.getCode(), DC_BUDGET_PERMISSION_CODE);
		assertEquals("Unexpected permission unit uid", permission.getUnit().getUid(), DC_UNIT_UID);
	}

	@Test
	public void testGetB2BPermissionTypes()
	{
		final List<B2BPermissionTypeData> permissionTypes = legacyB2BCommercePermissionFacade.getB2BPermissionTypes();
		assertEquals("Unexpected number of B2B permission types", 3, permissionTypes.size());
	}

	@Test
	public void testUpdatePermissionDetails()
	{
		// assert preconditions
		final B2BPermissionData permissionData = legacyB2BCommercePermissionFacade
				.getPermissionDetails(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		assertEquals("Unexpected permission period range", B2BPeriodRange.MONTH, permissionData.getPeriodRange());
		assertEquals("Unexpected permission value", Double.valueOf("10000"), permissionData.getValue());

		// do the update
		permissionData.setOriginalCode(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		permissionData.setPeriodRange(B2BPeriodRange.YEAR);
		permissionData.setValue(Double.valueOf("120000"));

		legacyB2BCommercePermissionFacade.updatePermissionDetails(permissionData);

		// use service layer for assertions
		final B2BOrderThresholdTimespanPermissionModel permission = (B2BOrderThresholdTimespanPermissionModel) defaultB2BPermissionService
				.getB2BPermissionForCode(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		assertEquals("Unexpected permission period range", B2BPeriodRange.YEAR, permission.getRange());
		assertEquals("Unexpected permission value", Double.valueOf("120000"), permission.getThreshold());
	}

	@Test
	public void testGetB2BPermissionTypeDataForPermission()
	{
		final B2BPermissionTypeData budgetExceededPermissionType = legacyB2BCommercePermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION);
		assertNotNull("Permission type was null", budgetExceededPermissionType);
		assertEquals("Unexpected permission type code", B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE,
				budgetExceededPermissionType.getCode());

		final B2BPermissionTypeData orderThresholdPermissionType = legacyB2BCommercePermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BORDERTHRESHOLDPERMISSION);
		assertNotNull("Permission type was null", orderThresholdPermissionType);
		assertEquals("Unexpected permission type code", B2B_ORDER_THRESHOLD_PERMISSION_TYPECODE,
				orderThresholdPermissionType.getCode());

		final B2BPermissionTypeData orderThresholdTimeSpanPermissionType = legacyB2BCommercePermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION);
		assertNotNull("Permission type was null", orderThresholdTimeSpanPermissionType);
		assertEquals("Unexpected permission type code", B2B_ORDER_THRESHOLD_TIMESPAN_PERMISSION_TYPECODE,
				orderThresholdTimeSpanPermissionType.getCode());
	}
}
