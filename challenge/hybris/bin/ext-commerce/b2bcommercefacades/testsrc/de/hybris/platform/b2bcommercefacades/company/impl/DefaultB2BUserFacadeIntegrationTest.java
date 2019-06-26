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
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isUserGroupIncluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCustomerService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BUserFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	// failure messages
	private static final String CUSTOMER_IS_ACTIVE = "Customer is active.";
	private static final String CUSTOMER_IS_NOT_ACTIVE = "Customer is not active.";
	private static final String CUSTOMER_IS_NULL = "Customer is null.";
	private static final String UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED = "Unexpected number of user groups selected.";

	// b2b customers
	private static final String DC_SALES_DE_BOSS = "DC Sales DE Boss";
	private static final String DC_S_DET = "DC S Det";
	private static final String DC_S_HH = "DC S HH";

	// b2b units
	private static final String DC_SALES_NOTTINGHAM = "DC Sales Nottingham";
	private static final String DC_SALES_DETROIT = "DC Sales Detroit";

	// b2b permission groups
	private static final String EUROPE_MANAGER_PERM_GROUP_DC = "EUROPE_MANAGER_PERM_GROUP_DC";


	// paging
	private static final String BY_UNIT_NAME = "byUnitName";
	private static final PageableData PAGEABLE_DATA_0_20_BY_UNIT_NAME = createPageableData(0, 20, BY_UNIT_NAME);

	@Resource
	private DefaultB2BUserFacade defaultB2BUserFacade;

	@Resource
	private DefaultB2BCustomerService defaultB2BCustomerService;

	@Resource
	private DefaultB2BUnitFacade defaultB2BUnitFacade;

	@Resource
	private UserService userService;

	@Test
	public void shouldGetPagedCustomers()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BUserFacade.getPagedCustomers(PAGEABLE_DATA_0_20_BY_UNIT_NAME);
		assertSearchPageData(9, searchPageData);
	}

	@Test
	public void shouldGetParentUnitForCustomer()
	{
		final B2BUnitData parentUnit = defaultB2BUserFacade.getParentUnitForCustomer(DC_S_DET);
		assertNotNull("Parent unit is null", parentUnit);
		assertEquals("Unexpected parent unit.", DC_SALES_DETROIT, parentUnit.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetParentUnitForCustomer()
	{
		defaultB2BUserFacade.getParentUnitForCustomer(null);
	}

	@Test
	public void shouldUpdateCustomerUpdateExistingCustomer()
	{
		final CustomerData customer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
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
		defaultB2BUserFacade.updateCustomer(customer);

		final CustomerData updatedCustomer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
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

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName("Customer");
		customer.setActive(true);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_DETROIT));
		final List<String> roles = new ArrayList<>();
		roles.add("b2bcustomergroup");
		customer.setRoles(roles);

		// create a new customer
		defaultB2BUserFacade.updateCustomer(customer);

		final CustomerData newCustomer = defaultB2BUserFacade.getCustomerForUid("DC S Det 2".toLowerCase()); // reverse populator makes uid lowercase
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
		defaultB2BUserFacade.updateCustomer(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerTitleCodeEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode(StringUtils.EMPTY);
		customer.setFirstName("New");
		customer.setLastName("Customer");

		defaultB2BUserFacade.updateCustomer(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerFirstNameEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName(StringUtils.EMPTY);
		customer.setLastName("Customer");

		defaultB2BUserFacade.updateCustomer(customer);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateCustomerLastNameEmpty()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName(StringUtils.EMPTY);

		defaultB2BUserFacade.updateCustomer(customer);
	}

	@Test
	public void shouldResetCustomerPassword()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertNotNull("Password is null.", customer.getEncodedPassword());

		final String oldEncodedPassword = customer.getEncodedPassword();

		defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, "updatedPassword");

		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotEquals("Password was not updated.", oldEncodedPassword, customer.getEncodedPassword());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotResetCustomerPassword()
	{
		defaultB2BUserFacade.resetCustomerPassword(null, "updatedPassword");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotResetCustomerPassword2()
	{
		defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, null);
	}

	@Test
	public void shouldDisableEnableCustomer()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertTrue(CUSTOMER_IS_NOT_ACTIVE, customer.getActive().booleanValue());

		// disable
		defaultB2BUserFacade.disableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertFalse(CUSTOMER_IS_ACTIVE, customer.getActive().booleanValue());

		// enable
		defaultB2BUserFacade.enableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertNotNull(CUSTOMER_IS_NULL, customer);
		assertTrue(CUSTOMER_IS_NOT_ACTIVE, customer.getActive().booleanValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDisableCustomer()
	{
		defaultB2BUserFacade.disableCustomer(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotDisableCustomerWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.disableCustomer(StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotEnableCustomer()
	{
		defaultB2BUserFacade.disableCustomer(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotEnableCustomerWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.disableCustomer(StringUtils.EMPTY);
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

		defaultB2BUserFacade.addUserRole(DC_S_DET, DC_SALES_NOTTINGHAM); // TODO: use actual role, not unit
		defaultB2BUserFacade.removeUserRole(DC_S_DET, DC_SALES_DETROIT);

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
		defaultB2BUserFacade.addUserRole(null, "b2bmanagergroup");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddUserRoleRoleUidNull()
	{
		defaultB2BUserFacade.addUserRole(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddUserRoleWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.addUserRole(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveUserRoleCustomerUidNull()
	{
		defaultB2BUserFacade.removeUserRole(null, "b2bcustomergroup");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveUserRoleRoleUidNull()
	{
		defaultB2BUserFacade.removeUserRole(DC_S_DET, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveUserRoleWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.removeUserRole(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldGetPagedB2BUserGroupsForCustomer()
	{
		final SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		final List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults()); // some customers are imported by system initialization
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertEquals("Expected user group not selected", EUROPE_MANAGER_PERM_GROUP_DC, selectedB2BUserGroups.get(0).getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedB2BUserGroupsForCustomer()
	{
		defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotGetPagedB2BUserGroupsForCustomerWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddB2BUserGroupToCustomerAndRemoveB2BUserGroupFromCustomerGroups()
	{
		SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());

		// add
		defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertEquals(EUROPE_MANAGER_PERM_GROUP_DC, selectedB2BUserGroups.get(0).getUid());

		// remove
		defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertSearchPageData(4, searchPageData);
		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddB2BUserGroupToCustomerNullCustomerUid()
	{
		defaultB2BUserFacade.addB2BUserGroupToCustomer(null, EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddB2BUserGroupToCustomerNullUserGroupUid()
	{
		defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddB2BUserGroupToCustomerWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.addB2BUserGroupToCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullCustomerUid()
	{
		defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(null, EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullUserGroupUid()
	{
		defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldDeselectB2BUserGroupFromCustomer()
	{
		SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		List<B2BUserGroupData> selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData
				.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 1, selectedB2BUserGroups.size());
		assertTrue("Expected user group not selected", isUserGroupIncluded(selectedB2BUserGroups, EUROPE_MANAGER_PERM_GROUP_DC));

		// deselect
		defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(DC_SALES_DE_BOSS, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertSearchPageData(4, searchPageData);

		selectedB2BUserGroups = B2BCommercefacadesTestUtils.getSelectedUserGroups(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_USER_GROUPS_SELECTED, 0, selectedB2BUserGroups.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotDeselectB2BUserGroupFromCustomer()
	{
		defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotDeselectB2BUserGroupFromCustomerWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldGetCustomerForUid()
	{
		final CustomerData customer = defaultB2BUserFacade.getCustomerForUid(DC_SALES_DE_BOSS);
		assertNotNull("Customer is null.", customer);
		assertEquals("Unexpected customer returned", DC_SALES_DE_BOSS, customer.getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetCustomerForUid()
	{
		defaultB2BUserFacade.getCustomerForUid(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotGetCustomerForUidWithEmptyCustomerUid()
	{
		defaultB2BUserFacade.getCustomerForUid(StringUtils.EMPTY);
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}
}
