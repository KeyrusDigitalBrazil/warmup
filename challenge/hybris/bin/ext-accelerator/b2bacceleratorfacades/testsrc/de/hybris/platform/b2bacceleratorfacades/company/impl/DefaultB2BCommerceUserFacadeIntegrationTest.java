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

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.getSelectedPermissions;
import static de.hybris.platform.b2bapprovalprocessfacades.util.B2BApprovalProcessUnitTestUtils.isPermissionIncluded;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isUserGroupIncluded;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isUserIncluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCustomerService;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@IntegrationTest
@SuppressWarnings("deprecation")
@Transactional
@ContextConfiguration(locations =
{ "classpath:b2bacceleratorfacades/test/b2bacceleratorfacades-test-spring.xml" })
public class DefaultB2BCommerceUserFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	// failure messages
	private static final String CUSTOMER_IS_ACTIVE = "Customer is active.";
	private static final String CUSTOMER_IS_NOT_ACTIVE = "Customer is not active.";
	private static final String CUSTOMER_IS_NULL = "Customer is null.";
	private static final String EXPECTED_APPROVER_NOT_SELECTED = "Expected approver not selected.";
	private static final String PERMISSIONS_ARE_NULL = "Permissions are null.";
	private static final String SELECTION_DATA_IS_NULL = "SelectionData is null.";
	private static final String UNEXPECTED_APPROVER_SELECTED = "Unexpected approver selected.";
	private static final String UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED = "Unexpected number of approvers selected.";
	private static final String UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED = "Unexpected number of user groups selected.";
	private static final String UNEXPECTED_SELECTION_DATA_ID = "Unexpected SelectionData id.";

	// b2b customers
	private static final String DC_SALES_BOSS = "DC Sales Boss";
	private static final String DC_SALES_US_BOSS = "DC Sales US Boss";
	private static final String DC_SALES_DE_BOSS = "DC Sales DE Boss";
	private static final String DC_S_DET = "DC S Det";
	private static final String DC_S_HH = "DC S HH";

	// b2b units
	private static final String DC_SALES_NOTTINGHAM = "DC Sales Nottingham";
	private static final String DC_SALES_DETROIT = "DC Sales Detroit";

	// b2b permission groups
	private static final String EUROPE_MANAGER_PERM_GROUP_DC = "EUROPE_MANAGER_PERM_GROUP_DC";

	// permissions
	private static final String DC_10_000_USD = "DC 10,000 USD";
	private static final String DC_100000_TIMESPAN_USD = "DC 100000 TIMESPAN USD";
	private static final String DC_BUDGET = "DC BUDGET";

	// paging
	private static final String BY_UNIT_NAME = "byUnitName";
	private static final String BY_NAME = "byName";
	private static final PageableData PAGEABLE_DATA_0_20_BY_UNIT_NAME = createPageableData(0, 20, BY_UNIT_NAME);
	private static final PageableData PAGEABLE_DATA_0_20_BY_NAME = createPageableData(0, 20, BY_NAME);

	@Resource
	private DefaultB2BCommerceUserFacade legacyB2BCommerceUserFacade;

	@Resource
	private DefaultB2BCustomerService defaultB2BCustomerService;

	@Resource
	private DefaultB2BUnitFacade defaultB2BUnitFacade;

	@Resource
	private UserService userService;

	@Test
	public void shouldGetPagedCustomers()
	{
		final SearchPageData<CustomerData> searchPageData = legacyB2BCommerceUserFacade
				.getPagedCustomers(PAGEABLE_DATA_0_20_BY_UNIT_NAME);
		assertSearchPageData(9, searchPageData);
	}

	@Test
	public void shouldGetPagedApproversForCustomer()
	{
		final SearchPageData<CustomerData> searchPageData = legacyB2BCommerceUserFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		final List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());

		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 1, selectedApprovers.size());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedApproversForCustomer()
	{
		legacyB2BCommerceUserFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedApproversForCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddApproverForCustomer()
	{

		SearchPageData<CustomerData> searchPageData = legacyB2BCommerceUserFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 1, selectedApprovers.size());
		assertFalse(UNEXPECTED_APPROVER_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_BOSS));

		final B2BSelectionData selectionData = legacyB2BCommerceUserFacade.addApproverForCustomer(DC_S_DET, DC_SALES_BOSS);
		assertNotNull(SELECTION_DATA_IS_NULL, selectionData);
		assertEquals(UNEXPECTED_SELECTION_DATA_ID, DC_SALES_BOSS, selectionData.getId());

		searchPageData = legacyB2BCommerceUserFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 2, selectedApprovers.size());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddApproverForCustomer()
	{
		legacyB2BCommerceUserFacade.addApproverForCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddApproverForCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.addApproverForCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldRemoveApproverFromCustomer()
	{
		SearchPageData<CustomerData> searchPageData = legacyB2BCommerceUserFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));

		final B2BSelectionData selectionData = legacyB2BCommerceUserFacade.removeApproverFromCustomer(DC_S_DET, DC_SALES_US_BOSS);
		assertNotNull(SELECTION_DATA_IS_NULL, selectionData);
		assertEquals(UNEXPECTED_SELECTION_DATA_ID, DC_SALES_US_BOSS, selectionData.getId());

		searchPageData = legacyB2BCommerceUserFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertFalse(UNEXPECTED_APPROVER_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveApproverFromCustomer()
	{
		legacyB2BCommerceUserFacade.removeApproverFromCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveApproverFromCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.removeApproverFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldGetParentUnitForCustomer()
	{
		final B2BUnitData parentUnit = legacyB2BCommerceUserFacade.getParentUnitForCustomer(DC_S_DET);
		assertNotNull("Parent unit is null", parentUnit);
		assertEquals("Unexpected parent unit.", DC_SALES_DETROIT, parentUnit.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetParentUnitForCustomer()
	{
		legacyB2BCommerceUserFacade.getParentUnitForCustomer(null);
	}

	@Test
	public void shouldUpdateCustomerUpdateExistingCustomer()
	{
		final CustomerData customer = legacyB2BCommerceUserFacade.getCustomerForUid(DC_S_DET);
		assertNotNull("Customer is null", customer);
		assertEquals("Unexpected customer title", "mr", customer.getTitleCode());
		assertEquals("Unexpected customer name", "Ed Whitacre", customer.getName());
		assertTrue("Customer is not active", customer.isActive());
		assertNotNull("Customer unit is null", customer.getUnit());
		assertEquals("Unexpexted customer unit", DC_SALES_DETROIT, customer.getUnit().getUid());
		assertNotNull("Customer roles are null", customer.getRoles());
		assertEquals("Unexpected number of customer roles", 1, customer.getRoles().size());
		assertTrue("Expexted customer role [b2bcustomergroup] not assigned", customer.getRoles().contains("b2bcustomergroup"));

		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det@gmail.com");
		customer.setDisplayUid(DC_S_DET);

		// update some fields
		customer.setFirstName("Edward");
		customer.setLastName("Whiteacre Jr.");
		customer.setActive(false);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales US"));
		customer.getRoles().add("b2bmanagergroup");

		// update customer
		legacyB2BCommerceUserFacade.updateCustomer(customer);

		final CustomerData updatedCustomer = legacyB2BCommerceUserFacade.getCustomerForUid(DC_S_DET);
		assertNotNull("Customer is null", updatedCustomer);
		assertEquals("Unexpected customer name", "Edward Whiteacre Jr.", updatedCustomer.getName());
		assertFalse("Customer is not active", customer.isActive());
		assertNotNull("Customer unit is null", updatedCustomer.getUnit());
		assertEquals("Unexpexted customer unit", "DC Sales US", updatedCustomer.getUnit().getUid());
		assertNotNull("Customer roles are null", updatedCustomer.getRoles());
		assertEquals("Unexpected number of customer roles", 2, updatedCustomer.getRoles().size());
		assertTrue("Expexted customer role [b2bcustomergroup] not assigned", updatedCustomer.getRoles()
				.contains("b2bcustomergroup"));
		assertTrue("Expexted customer role [b2bmanagergroup] not assigned", updatedCustomer.getRoles().contains("b2bmanagergroup"));
	}


	@Test
	public void shouldUpdateCustomerCreateNewCustomer()
	{
		final CustomerData customer = new CustomerData();
		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det.2@gmail.com");
		customer.setDisplayUid("DC S Det 2");

		// update some fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName("Customer");
		customer.setActive(true);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_DETROIT));
		final List<String> roles = new ArrayList<>();
		roles.add("b2bcustomergroup");
		customer.setRoles(roles);

		// update customer
		legacyB2BCommerceUserFacade.updateCustomer(customer);

		final CustomerData newCustomer = legacyB2BCommerceUserFacade.getCustomerForUid("DC S Det 2".toLowerCase()); // reverse populator makes uid lowercase
		assertNotNull("Customer is null", newCustomer);
		assertEquals("Unexpected customer name", "New Customer", newCustomer.getName());
		assertTrue("Customer is not active", customer.isActive());
		assertNotNull("Customer unit is null", newCustomer.getUnit());
		assertEquals("Unexpexted customer unit", DC_SALES_DETROIT, newCustomer.getUnit().getUid());
		assertNotNull("Customer roles are null", newCustomer.getRoles());
		assertEquals("Unexpected number of customer roles", 1, newCustomer.getRoles().size());
		assertTrue("Expexted customer role [b2bcustomergroup] not assigned", newCustomer.getRoles().contains("b2bcustomergroup"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerCustomerDataNull()
	{
		legacyB2BCommerceUserFacade.updateCustomer(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerTitleCodeEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode(StringUtils.EMPTY);
		customer.setFirstName("New");
		customer.setLastName("Customer");

		legacyB2BCommerceUserFacade.updateCustomer(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerFirstNameEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName(StringUtils.EMPTY);
		customer.setLastName("Customer");

		legacyB2BCommerceUserFacade.updateCustomer(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerLastNameEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName(StringUtils.EMPTY);

		legacyB2BCommerceUserFacade.updateCustomer(customer);
	}

	@Test
	public void shouldResetCustomerPassword()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull("Password is null.", customer.getEncodedPassword());

		final String oldEncodedPassword = customer.getEncodedPassword();

		legacyB2BCommerceUserFacade.resetCustomerPassword(DC_S_DET, "updatedPassword");

		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotEquals("Password was not updated.", oldEncodedPassword, customer.getEncodedPassword());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotResetCustomerPassword()
	{
		legacyB2BCommerceUserFacade.resetCustomerPassword(null, "updatedPassword");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotResetCustomerPassword2()
	{
		legacyB2BCommerceUserFacade.resetCustomerPassword(DC_S_DET, null);
	}

	@Test
	public void shouldDisableEnableCustomer()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertTrue(CUSTOMER_IS_NOT_ACTIVE, customer.getActive().booleanValue());

		// disable
		legacyB2BCommerceUserFacade.disableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertFalse(CUSTOMER_IS_ACTIVE, customer.getActive().booleanValue());

		// enable
		legacyB2BCommerceUserFacade.enableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertTrue(CUSTOMER_IS_NOT_ACTIVE, customer.getActive().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDisableCustomer()
	{
		legacyB2BCommerceUserFacade.disableCustomer(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotDisableCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.disableCustomer(StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotEnableCustomer()
	{
		legacyB2BCommerceUserFacade.disableCustomer(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotEnableCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.disableCustomer(StringUtils.EMPTY);
	}

	@Test
	public void shouldAddAndRemoveUserRole()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull("customer should not be null", customer);
		assertNotNull("customer's groups should not be null", customer.getGroups());
		assertEquals("size of customer.getGroups() should be 2", 2, customer.getGroups().size());
		assertTrue(customer.getGroups().contains(userService.getUserGroupForUID(DC_SALES_DETROIT)));
		assertFalse(customer.getGroups().contains(userService.getUserGroupForUID(DC_SALES_NOTTINGHAM)));

		legacyB2BCommerceUserFacade.addUserRole(DC_S_DET, DC_SALES_NOTTINGHAM); // TODO: use actual role, not unit
		legacyB2BCommerceUserFacade.removeUserRole(DC_S_DET, DC_SALES_DETROIT);

		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull("customer should not be null", customer);
		assertNotNull("customer's groups should not be null", customer.getGroups());
		assertEquals("size of customer.getGroups() should be 2", 2, customer.getGroups().size());
		assertFalse(customer.getGroups().contains(userService.getUserGroupForUID(DC_SALES_DETROIT)));
		assertTrue(customer.getGroups().contains(userService.getUserGroupForUID(DC_SALES_NOTTINGHAM)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddUserRoleCustomerUidNull()
	{
		legacyB2BCommerceUserFacade.addUserRole(null, "b2bmanagergroup");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddUserRoleRoleUidNull()
	{
		legacyB2BCommerceUserFacade.addUserRole(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddUserRoleWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.addUserRole(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveUserRoleCustomerUidNull()
	{
		legacyB2BCommerceUserFacade.removeUserRole(null, "b2bcustomergroup");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveUserRoleRoleUidNull()
	{
		legacyB2BCommerceUserFacade.removeUserRole(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveUserRoleWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.removeUserRole(StringUtils.EMPTY, StringUtils.EMPTY);
	}


	@Test
	public void shouldGetPagedPermissionsForCustomer()
	{
		final SearchPageData<B2BPermissionData> permissions = legacyB2BCommerceUserFacade.getPagedPermissionsForCustomer(
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
		legacyB2BCommerceUserFacade.getPagedPermissionsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedPermissionsForCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.getPagedPermissionsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddAndRemovePermissionToCustomer()
	{
		CustomerData customer = legacyB2BCommerceUserFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull("Permissions are null", customer.getPermissions());
		assertEquals(3, customer.getPermissions().size());

		// add permission
		legacyB2BCommerceUserFacade.addPermissionToCustomer(DC_S_DET, DC_10_000_USD);
		customer = legacyB2BCommerceUserFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull(PERMISSIONS_ARE_NULL, customer.getPermissions());
		assertEquals("Unexpected number of permissions.", 4, customer.getPermissions().size());
		assertTrue(DC_10_000_USD + " was not added to customer permissions.",
				isPermissionIncluded(customer.getPermissions(), DC_10_000_USD));

		// remove permission
		legacyB2BCommerceUserFacade.removePermissionFromCustomer(DC_S_DET, DC_10_000_USD);
		customer = legacyB2BCommerceUserFacade.getCustomerForUid(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull(PERMISSIONS_ARE_NULL, customer.getPermissions());
		assertEquals("Unexpected number of permissions.", 3, customer.getPermissions().size());
		assertFalse(DC_10_000_USD + " was not removed from customer permissions.",
				isPermissionIncluded(customer.getPermissions(), DC_10_000_USD));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddPermissionToCustomerNullCustomerUid()
	{
		legacyB2BCommerceUserFacade.addPermissionToCustomer(null, DC_BUDGET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddPermissionToCustomerNullPermissionId()
	{
		legacyB2BCommerceUserFacade.addPermissionToCustomer(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddPermissionToCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.addPermissionToCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemovePermissionFromCustomerNullCustomerUid()
	{
		legacyB2BCommerceUserFacade.removePermissionFromCustomer(null, DC_BUDGET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemovePermissionFromCustomerNullPermissionId()
	{
		legacyB2BCommerceUserFacade.removePermissionFromCustomer(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemovePermissionFromCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.removePermissionFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldGetPagedB2BUserGroupsForCustomer()
	{
		final SearchPageData<B2BUserGroupData> searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		final List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults()); // some customers are imported by system initialization
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertEquals("Expected user group not selected.", EUROPE_MANAGER_PERM_GROUP_DC, selectedB2BUserGroups.get(0).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedB2BUserGroupsForCustomer()
	{
		legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedB2BUserGroupsForCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddB2BUserGroupToCustomerAndRemoveB2BUserGroupFromCustomerGroups()
	{
		SearchPageData<B2BUserGroupData> searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());

		// add
		legacyB2BCommerceUserFacade.addB2BUserGroupToCustomer(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertEquals(EUROPE_MANAGER_PERM_GROUP_DC, selectedB2BUserGroups.get(0).getUid());

		// remove
		legacyB2BCommerceUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddB2BUserGroupToCustomerNullCustomerUid()
	{
		legacyB2BCommerceUserFacade.addB2BUserGroupToCustomer(null, EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddB2BUserGroupToCustomerNullUserGroupUid()
	{
		legacyB2BCommerceUserFacade.addB2BUserGroupToCustomer(DC_S_HH, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddB2BUserGroupToCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.addB2BUserGroupToCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullCustomerUid()
	{
		legacyB2BCommerceUserFacade.removeB2BUserGroupFromCustomerGroups(null, EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullUserGroupUid()
	{
		legacyB2BCommerceUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.removeB2BUserGroupFromCustomerGroups(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldDeselectB2BUserGroupFromCustomer()
	{
		SearchPageData<B2BUserGroupData> searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertTrue("Expected user group not selected", isUserGroupIncluded(selectedB2BUserGroups, EUROPE_MANAGER_PERM_GROUP_DC));

		// deselect
		legacyB2BCommerceUserFacade.deselectB2BUserGroupFromCustomer(DC_SALES_DE_BOSS, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = legacyB2BCommerceUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME,
				DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDeselectB2BUserGroupFromCustomer()
	{
		legacyB2BCommerceUserFacade.deselectB2BUserGroupFromCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotDeselectB2BUserGroupFromCustomerWithEmptyCustomerUid()
	{
		legacyB2BCommerceUserFacade.deselectB2BUserGroupFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bacceleratorfacades/test/testOrganizations.csv";
	}
}
