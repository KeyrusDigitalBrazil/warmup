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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {@link DefaultB2BUnitFacade}.
 */
@IntegrationTest
public class DefaultB2BUnitFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String UNEXPECTED_VALUE_FOR_NAME_MSG = "Unexpected value for name.";
	private static final String UNIT_IS_NOT_ACTIVE_MSG = "Unit is not active.";
	private static final String UNIT_IS_ACTIVE_MSG = "Unit is active.";
	private static final String UNIT_ADRESSES_ARE_NULL_MSG = "Unit adresses are null.";
	private static final String UNIT_ADRESSES_ARE_NOT_NULL_MSG = "Unit adresses are not null.";
	private static final String UNIT_IS_NULL_MSG = "Unit is null.";

	/**
	 * {@link PageableData} object, getting the <i>first result page</i> with <i>page size 10</i> and <i>sort by
	 * name</i>.
	 */
	private static final PageableData DEFAULT_PAGEABLE_DATA = B2BCommerceTestUtils.createPageableData(0, 10, "byName");

	@Resource
	private DefaultB2BUnitFacade b2bUnitFacade;

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private UserService userService;


	@Test
	public void testGetPagedCustomersForUnit()
	{
		final SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA,
				"DC Sales Detroit");

		assertSearchPageData(1, searchPageData);

		// assert correct sort order
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Ed Whitacre", searchPageData.getResults().get(0).getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedCustomersForUnitNullUid()
	{
		b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, null);
	}

	@Test
	public void testGetPagedAdministratorsForUnit()
	{
		// this returns all customers of the unit with the 'selected' flag set for admins of the unit
		final SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA,
				"DC");

		assertSearchPageData(2, searchPageData);

		// assert that the correct user is selected as admin
		final CustomerData user1 = searchPageData.getResults().get(0);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Bernie Big Boss", user1.getName());
		assertFalse("User [" + user1.getName() + "] was selected.", user1.isSelected());

		final CustomerData user2 = searchPageData.getResults().get(1);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Nimda Admin", user2.getName());
		assertTrue("User [" + user2.getName() + "] was not selected.", user2.isSelected());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedAdministratorsForUnitNullUid()
	{
		b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, null);
	}

	@Test
	public void testGetPagedManagersForUnit()
	{
		// this returns all customers of the unit with the 'selected' flag set for managers of the unit
		final SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, "DC");

		assertSearchPageData(2, searchPageData);

		final CustomerData user1 = searchPageData.getResults().get(0);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Bernie Big Boss", user1.getName());
		assertTrue("User [" + user1.getName() + "] was not selected.", user1.isSelected());

		final CustomerData user2 = searchPageData.getResults().get(1);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Nimda Admin", user2.getName());
		assertFalse("User [" + user2.getName() + "] was selected.", user2.isSelected());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedManagersForUnitNullUid()
	{
		b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, null);
	}

	@Test
	public void testDisableAndEnableUnit()
	{
		b2bUnitFacade.disableUnit("DC");

		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unitData);
		assertFalse(UNIT_IS_ACTIVE_MSG, unitData.isActive());

		b2bUnitFacade.enableUnit("DC");

		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unitData);
		assertTrue(UNIT_IS_NOT_ACTIVE_MSG, unitData.isActive());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDisableUnitNullUid()
	{
		b2bUnitFacade.disableUnit(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnableUnitNullUid()
	{
		b2bUnitFacade.enableUnit(null);
	}

	@Test
	public void testGetAllowedParentUnits()
	{
		final List<B2BUnitNodeData> allowedParentUnits = b2bUnitFacade.getAllowedParentUnits("DC Sales Detroit");

		assertNotNull("Allowed parent units are null.", allowedParentUnits);
		assertEquals("Unexpected number of allowed parent units.", 11, allowedParentUnits.size());
	}

	@Test
	public void testGetAllActiveUnitsOfOrganization()
	{
		// this will get results based on the current session user (DC Admin)
		final List<String> activeUnits = b2bUnitFacade.getAllActiveUnitsOfOrganization();
		assertNotNull(activeUnits);
		assertEquals("Unexpected number of active units", 12, activeUnits.size());
	}

	@Test
	public void testGetPagedUserDataForUnit()
	{
		SearchPageData<CustomerData> pageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC");
		assertSearchPageData(2, pageData);

		pageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC Sales Detroit");
		assertSearchPageData(1, pageData);
	}

	@Test
	public void testAddAddressToUnit()
	{
		// check the number of addresses before adding a new one
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unitData);
		assertNotNull(UNIT_ADRESSES_ARE_NULL_MSG, unitData.getAddresses());
		assertEquals("unexpected number of unit addresses.", 1, unitData.getAddresses().size());

		// add a new address
		b2bUnitFacade.addAddressToUnit(new AddressData(), "DC");

		// check the number of addresses after adding a new one
		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unitData);
		assertNotNull(UNIT_ADRESSES_ARE_NULL_MSG, unitData.getAddresses());
		assertEquals("unexpected number of unit addresses.", 2, unitData.getAddresses().size());
	}

	@Test
	public void testRemoveAddressFromUnit()
	{
		// check the number of addresses before removal
		B2BUnitData unit = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unit);
		assertNotNull(UNIT_ADRESSES_ARE_NULL_MSG, unit.getAddresses());
		assertEquals("Unexpected number of unit addresses before removal.", 1, unit.getAddresses().size());
		final AddressData address = unit.getAddresses().get(0);

		// remove an address
		b2bUnitFacade.removeAddressFromUnit("DC", address.getId());

		// check the number of addresses after removal
		unit = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unit);
		assertNull(UNIT_ADRESSES_ARE_NOT_NULL_MSG, unit.getAddresses()); // addresses are not populated if empty
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAddressFromUnitNullUnitUid()
	{
		b2bUnitFacade.removeAddressFromUnit(null, "addressId");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAddressFromUnitNullAddressId()
	{
		b2bUnitFacade.removeAddressFromUnit("DC", null);
	}

	@Test
	public void testEditAddressOfUnit()
	{
		// check the number of addresses before removal
		final B2BUnitData unit = b2bUnitFacade.getUnitForUid("DC");
		assertNotNull(UNIT_IS_NULL_MSG, unit);
		assertNotNull(UNIT_ADRESSES_ARE_NULL_MSG, unit.getAddresses());
		assertEquals("Unexpected number of unit addresses.", 1, unit.getAddresses().size());

		// edit line1 for the first address of the unit
		final AddressData address = unit.getAddresses().get(0);
		final String newLine1 = "New Line 1";
		assertNotEquals("Unexpected value for line1 of original address", newLine1, address.getLine1());
		address.setLine1(newLine1);

		b2bUnitFacade.editAddressOfUnit(address, "DC");

		// verify the address has been updated
		final AddressModel updatedAddress = b2bCommerceUnitService.getAddressForCode(b2bCommerceUnitService.getUnitForUid("DC"),
				address.getId());
		assertNotNull("Updated address is null", updatedAddress);
		assertEquals("Unexpected value for line1 of updated address", newLine1, updatedAddress.getLine1());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEditAddressOfUnitNullAddress()
	{
		b2bUnitFacade.editAddressOfUnit(null, "DC");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEditAddressOfUnitNullUnitUid()
	{
		b2bUnitFacade.editAddressOfUnit(new AddressData(), null);
	}

	@Test
	public void testUpdateOrCreateBusinessUnit() throws ImpExException
	{
		importCsv("/b2bcommercefacades/test/b2bUnitTestData_addPath.impex", "UTF-8");
		final B2BUnitData parentUnit = b2bUnitFacade.getUnitForUid("DC");

		// create a new business unit
		final B2BUnitData unit = new B2BUnitData();
		final String newUnitId = "DC New Unit";
		unit.setUid(newUnitId);
		unit.setName("New Unit");
		unit.setUnit(parentUnit);
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData newUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertNotNull("New unit has not been created.", newUnit);
		assertEquals("Unexpected unit name.", "New Unit", newUnit.getName());
		Assert.assertEquals("Unexpected path value.", "/DC/" + newUnitId, getUnitForUid(newUnitId).getPath());

		// update the unit
		unit.setName("Updated Unit");
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData updatedUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertNotNull("New unit has not been created.", updatedUnit);
		assertEquals("Unexpected unit name.", "Updated Unit", updatedUnit.getName());
	}

	protected OrgUnitModel getUnitForUid(final String uid)
	{
		final OrgUnitModel unit = userService.getUserGroupForUID(uid, OrgUnitModel.class);
		Assert.assertNotNull(String.format("Unit [%s] does not exist.", uid), unit);
		return unit;
	}

	public void testGetParentUnit()
	{
		final B2BUnitData parentUnit = b2bUnitFacade.getParentUnit();
		assertNotNull("Parent unit is null", parentUnit);
		assertEquals("Unexpected parent unit returned", "DC", parentUnit.getUid());
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bcommercefacades/test/testOrganizations.csv";
	}

}
