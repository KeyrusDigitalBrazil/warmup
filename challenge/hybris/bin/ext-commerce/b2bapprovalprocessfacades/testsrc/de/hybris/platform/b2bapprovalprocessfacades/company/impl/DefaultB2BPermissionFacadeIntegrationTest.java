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
package de.hybris.platform.b2bapprovalprocessfacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.getSelectedPermissions;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.isPermissionIncluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.testframework.Transactional;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


@IntegrationTest
@Transactional
public class DefaultB2BPermissionFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String TEST_PERMISSION_CODE = "testPermission";
	private static final String DC_UNIT_UID = "DC";
	// failure messages
	private static final String CUSTOMER_IS_NULL = "Customer is null.";
	private static final String PERMISSIONS_ARE_NULL = "Permissions are null.";

	// b2b customers
	private static final String DC_SALES_US_BOSS = "DC Sales US Boss";
	private static final String DC_S_DET = "DC S Det";

	// permissions
	private static final String DC_10_000_USD = "DC 10,000 USD";
	private static final String DC_100000_TIMESPAN_USD = "DC 100000 TIMESPAN USD";
	private static final String DC_BUDGET = "DC BUDGET";

	// paging
	private static final String BY_UNIT_NAME = "byUnitName";
	private static final String BY_NAME = "byName";
	private static final PageableData PAGEABLE_DATA_0_20_BY_UNIT_NAME = createPageableData(0, 20, BY_UNIT_NAME);
	private static final PageableData PAGEABLE_DATA_0_20_BY_NAME = createPageableData(0, 20, BY_NAME);
	private static final PageableData PAGEABLE_DATA_0_5_BY_NAME = createPageableData(0, 5, BY_NAME);

	private static final String B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION
			.getCode();
	private static final String B2B_ORDER_THRESHOLD_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BORDERTHRESHOLDPERMISSION
			.getCode();
	private static final String B2B_ORDER_THRESHOLD_TIMESPAN_PERMISSION_TYPECODE = B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION
			.getCode();
	private static final String DC_BUDGET_PERMISSION_CODE = "DC BUDGET";
	private static final String DC_10000_TIMESPAN_EUR_PERMISSION_CODE = "DC 10000 TIMESPAN EUR";

	@Resource
	private DefaultB2BPermissionFacade defaultB2BPermissionFacade;

	@Resource
	private DefaultB2BPermissionService defaultB2BPermissionService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserDao userDao;

	@Resource
	private I18NService i18NService;

	@Resource
	private UserService userService;

	@Test
	public void testAddPermission()
	{
		final B2BUnitData unitData = new B2BUnitData();
		unitData.setUid(DC_UNIT_UID);
		final B2BPermissionData testPermissionData = new B2BPermissionData();
		testPermissionData.setCode(TEST_PERMISSION_CODE);
		testPermissionData.setB2BPermissionTypeData(defaultB2BPermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION));
		testPermissionData.setUnit(unitData);

		defaultB2BPermissionFacade.addPermission(testPermissionData);

		// use the service layer to assert the permission has been created
		// properly
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
		defaultB2BPermissionFacade.enableDisablePermission(DC_BUDGET_PERMISSION_CODE, false);
		permission = defaultB2BPermissionService.getB2BPermissionForCode(DC_BUDGET_PERMISSION_CODE);
		assertEquals("Permission was active", Boolean.FALSE, permission.getActive());

		// enable and assert that it's active again
		defaultB2BPermissionFacade.enableDisablePermission(DC_BUDGET_PERMISSION_CODE, true);
		permission = defaultB2BPermissionService.getB2BPermissionForCode(DC_BUDGET_PERMISSION_CODE);
		assertEquals("Permission was not active", Boolean.TRUE, permission.getActive());
	}

	@Test
	public void testGetPagedPermissions()
	{
		final SearchPageData<B2BPermissionData> pagedPermissions = defaultB2BPermissionFacade
				.getPagedPermissions(PAGEABLE_DATA_0_5_BY_NAME);

		assertNotNull(pagedPermissions.getResults());
		assertNotNull(pagedPermissions.getPagination());
		assertEquals("Unexpected number of results", 5, pagedPermissions.getResults().size());
		assertEquals("Unexpected current page", 0, pagedPermissions.getPagination().getCurrentPage());
		assertEquals("Unexpected number of pages", 4, pagedPermissions.getPagination().getNumberOfPages());
		assertEquals("Unexpected page size", 5, pagedPermissions.getPagination().getPageSize());
		assertEquals("Unexpected total number of results", 19, pagedPermissions.getPagination().getTotalNumberOfResults());
	}

	@Test
	public void testGetPermissionDetails()
	{
		final B2BPermissionData permission = defaultB2BPermissionFacade.getPermissionDetails(DC_BUDGET);

		assertNotNull("Permission was null", permission);
		assertNotNull("Permission permission type was null", permission.getB2BPermissionTypeData());
		assertNotNull("Permission unit was null", permission.getUnit());
		assertNull("Permission currency was not null", permission.getCurrency());
		assertNull("Permission value was not null", permission.getValue());
		assertNull("Permission time span was not null", permission.getTimeSpan());
		assertNull("Permission period range was not null", permission.getPeriodRange());
		assertEquals("Unexpected permission type", permission.getB2BPermissionTypeData().getCode(),
				B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE);
		assertEquals("Unexpected permission code", permission.getCode(), DC_BUDGET);
		assertEquals("Unexpected permission unit uid", permission.getUnit().getUid(), DC_UNIT_UID);
	}

	@Test
	public void testGetB2BPermissionTypes()
	{
		final List<B2BPermissionTypeData> permissionTypes = defaultB2BPermissionFacade.getB2BPermissionTypes();
		assertEquals("Unexpected number of B2B permission types", 3, permissionTypes.size());
	}

	@Test
	public void testUpdatePermissionDetails()
	{
		// assert preconditions
		final B2BPermissionData permissionData = defaultB2BPermissionFacade
				.getPermissionDetails(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		assertEquals("Unexpected permission period range", B2BPeriodRange.MONTH, permissionData.getPeriodRange());
		assertEquals("Unexpected permission value", Double.valueOf("10000"), permissionData.getValue());

		// do the update
		permissionData.setOriginalCode(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		permissionData.setPeriodRange(B2BPeriodRange.YEAR);
		permissionData.setValue(Double.valueOf("120000"));

		defaultB2BPermissionFacade.updatePermissionDetails(permissionData);

		// use service layer for assertions
		final B2BOrderThresholdTimespanPermissionModel permission = (B2BOrderThresholdTimespanPermissionModel) defaultB2BPermissionService
				.getB2BPermissionForCode(DC_10000_TIMESPAN_EUR_PERMISSION_CODE);
		assertEquals("Unexpected permission period range", B2BPeriodRange.YEAR, permission.getRange());
		assertEquals("Unexpected permission value", Double.valueOf("120000"), permission.getThreshold());
	}

	@Test
	public void testGetB2BPermissionTypeDataForPermission()
	{
		final B2BPermissionTypeData budgetExceededPermissionType = defaultB2BPermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BBUDGETEXCEEDEDPERMISSION);
		assertNotNull("Permission type was null", budgetExceededPermissionType);
		assertEquals("Unexpected permission type code", B2B_BUDGET_EXCEEDED_PERMISSION_TYPECODE,
				budgetExceededPermissionType.getCode());

		final B2BPermissionTypeData orderThresholdPermissionType = defaultB2BPermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BORDERTHRESHOLDPERMISSION);
		assertNotNull("Permission type was null", orderThresholdPermissionType);
		assertEquals("Unexpected permission type code", B2B_ORDER_THRESHOLD_PERMISSION_TYPECODE,
				orderThresholdPermissionType.getCode());

		final B2BPermissionTypeData orderThresholdTimeSpanPermissionType = defaultB2BPermissionFacade
				.getB2BPermissionTypeDataForPermission(B2BPermissionTypeEnum.B2BORDERTHRESHOLDTIMESPANPERMISSION);
		assertNotNull("Permission type was null", orderThresholdTimeSpanPermissionType);
		assertEquals("Unexpected permission type code", B2B_ORDER_THRESHOLD_TIMESPAN_PERMISSION_TYPECODE,
				orderThresholdTimeSpanPermissionType.getCode());
	}

	@Test
	public void shouldGetPagedPermissionsForCustomer()
	{
		final SearchPageData<B2BPermissionData> permissions = defaultB2BPermissionFacade.getPagedPermissionsForCustomer(
				PAGEABLE_DATA_0_20_BY_NAME, DC_SALES_US_BOSS);
		assertSearchPageData(19, permissions);
		final List<B2BPermissionData> selectedPermissions = getSelectedPermissions(permissions.getResults());
		assertEquals("Unexpected number of permissions selected.", 3, selectedPermissions.size());
		assertTrue("Expected permission " + DC_10_000_USD + " not selected.",
				isPermissionIncluded(selectedPermissions, DC_10_000_USD));
		assertTrue("Expected permission " + DC_100000_TIMESPAN_USD + " not selected.",
				isPermissionIncluded(selectedPermissions, DC_100000_TIMESPAN_USD));
		assertTrue("Expected permission " + DC_BUDGET + " not selected.", isPermissionIncluded(selectedPermissions, DC_BUDGET));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedPermissionsForCustomer()
	{
		defaultB2BPermissionFacade.getPagedPermissionsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotGetPagedPermissionsForCustomerWithEmptyCustomerUid()
	{
		defaultB2BPermissionFacade.getPagedPermissionsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddAndRemovePermissionToCustomer()
	{
		CustomerData customer = defaultB2BPermissionFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull("Permissions are null", customer.getPermissions());
		assertEquals(3, customer.getPermissions().size());

		// add permission
		defaultB2BPermissionFacade.addPermissionToCustomer(DC_S_DET, DC_10_000_USD);
		customer = defaultB2BPermissionFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull(PERMISSIONS_ARE_NULL, customer.getPermissions());
		assertEquals("Unexpected number of permissions.", 4, customer.getPermissions().size());
		assertTrue(DC_10_000_USD + " was not added to customer permissions.",
				isPermissionIncluded(customer.getPermissions(), DC_10_000_USD));

		// remove permission
		defaultB2BPermissionFacade.removePermissionFromCustomer(DC_S_DET, DC_10_000_USD);
		customer = defaultB2BPermissionFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull(PERMISSIONS_ARE_NULL, customer.getPermissions());
		assertEquals("Unexpected number of permissions.", 3, customer.getPermissions().size());
		assertFalse(DC_10_000_USD + " was not removed from customer permissions.",
				isPermissionIncluded(customer.getPermissions(), DC_10_000_USD));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddPermissionToCustomerNullCustomerUid()
	{
		defaultB2BPermissionFacade.addPermissionToCustomer(null, DC_BUDGET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddPermissionToCustomerNullPermissionId()
	{
		defaultB2BPermissionFacade.addPermissionToCustomer(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddPermissionToCustomerWithEmptyCustomerUid()
	{
		defaultB2BPermissionFacade.addPermissionToCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemovePermissionFromCustomerNullCustomerUid()
	{
		defaultB2BPermissionFacade.removePermissionFromCustomer(null, DC_BUDGET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemovePermissionFromCustomerNullPermissionId()
	{
		defaultB2BPermissionFacade.removePermissionFromCustomer(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemovePermissionFromCustomerWithEmptyCustomerUid()
	{
		defaultB2BPermissionFacade.removePermissionFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void testGetPagedPermissionsForUserGroup()
	{
		final SearchPageData<B2BPermissionData> searchPageData = defaultB2BPermissionFacade.getPagedPermissionsForUserGroup(
				PAGEABLE_DATA_0_20_BY_NAME, "DC_CEO_PERMISSIONS");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		final List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 7, selectedPermissions.size());
	}

	@Test
	public void testAddPermissionToUserGroup()
	{
		SearchPageData<B2BPermissionData> searchPageData = defaultB2BPermissionFacade.getPagedPermissionsForUserGroup(
				PAGEABLE_DATA_0_20_BY_NAME, "EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 2, selectedPermissions.size());
		assertFalse("Unexpected permission is selected.", isPermissionIncluded(selectedPermissions, "DC UNLIMITED TIMESPAN EUR"));

		defaultB2BPermissionFacade.addPermissionToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC UNLIMITED TIMESPAN EUR");

		searchPageData = defaultB2BPermissionFacade.getPagedPermissionsForUserGroup(PAGEABLE_DATA_0_20_BY_NAME,
				"EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 3, selectedPermissions.size());
		assertTrue("Expected permission is not selected.", isPermissionIncluded(selectedPermissions, "DC UNLIMITED TIMESPAN EUR"));
	}

	@Test
	public void testRemovePermissionFromUserGroup()
	{
		SearchPageData<B2BPermissionData> searchPageData = defaultB2BPermissionFacade.getPagedPermissionsForUserGroup(
				PAGEABLE_DATA_0_20_BY_NAME, "EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		List<B2BPermissionData> selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 2, selectedPermissions.size());
		assertTrue("Expected permission is not selected.", isPermissionIncluded(selectedPermissions, "DC 100000 TIMESPAN EUR"));

		defaultB2BPermissionFacade.removePermissionFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC 100000 TIMESPAN EUR");

		searchPageData = defaultB2BPermissionFacade.getPagedPermissionsForUserGroup(PAGEABLE_DATA_0_20_BY_NAME,
				"EUROPE_MANAGER_PERM_GROUP_DC");
		assertSearchPageData(19, searchPageData);
		assertNotNull("Search page data pagination is null.", searchPageData.getPagination());
		assertEquals("Unexpected total number of results.", 19, searchPageData.getPagination().getTotalNumberOfResults());
		selectedPermissions = getSelectedPermissions(searchPageData.getResults());
		assertNotNull("Selected permissions are null.", selectedPermissions);
		assertEquals("Unexpected number of selected permissions.", 1, selectedPermissions.size());
		assertFalse("Unexpected permission is selected.", isPermissionIncluded(selectedPermissions, "DC 100000 TIMESPAN EUR"));
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bapprovalprocessfacades/test/testOrganizations.csv";
	}

}
