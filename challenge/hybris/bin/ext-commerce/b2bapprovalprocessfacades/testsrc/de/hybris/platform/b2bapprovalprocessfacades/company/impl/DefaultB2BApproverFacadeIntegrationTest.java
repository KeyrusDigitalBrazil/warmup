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

import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.isUserIncluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.b2bcommercefacades.testframework.AbstractCommerceOrgIntegrationTest;
import de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BApproverFacadeIntegrationTest extends AbstractCommerceOrgIntegrationTest
{
	private static final String UNEXPECTED_VALUE_FOR_NAME_MSG = "Unexpected value for name.";

	private static final String DC_SALES = "DC Sales";
	private static final String DC_SALES_US = "DC Sales US";
	private static final String DC_SALES_DETROIT = "DC Sales Detroit";
	private static final String DC_SALES_BOSS = "DC Sales Boss";
	private static final String DC_SALES_US_BOSS = "DC Sales US Boss";
	private static final String DC_S_DET = "DC S Det";

	private static final String EXPECTED_APPROVER_NOT_SELECTED = "Expected approver not selected.";
	private static final String SELECTION_DATA_IS_NULL = "SelecetionData is null.";
	private static final String UNEXPECTED_APPROVER_SELECTED = "Unexpected approver selected.";
	private static final String UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED = "Unexpected number of approvers selected.";
	private static final String UNEXPECTED_SELECTION_DATA_ID = "Unexpected SelectionData id.";

	/**
	 * {@link PageableData} object, getting the <i>first result page</i> with <i>page size 10</i> and <i>sort by
	 * name</i>.
	 */
	private static final PageableData DEFAULT_PAGEABLE_DATA = B2BCommerceTestUtils.createPageableData(0, 20, "byName");
	private static final PageableData PAGEABLE_DATA_0_20_BY_UNIT_NAME = B2BCommerceTestUtils.createPageableData(0, 20,
			"byUnitName");

	@Resource
	private DefaultB2BApproverFacade defaultB2BApproverFacade;

	@Resource
	private I18NService i18NService;

	@Test
	public void testGetPagedApproversForUnit()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade.getPagedApproversForUnit(DEFAULT_PAGEABLE_DATA,
				DC_SALES_US);

		assertSearchPageData(4, searchPageData);

		// assert correct sort order and selected flags

		final CustomerData user1 = searchPageData.getResults().get(0);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Big Cheese", user1.getName());
		assertFalse("User [" + user1.getName() + "] was selected.", user1.isSelected());

		final CustomerData user2 = searchPageData.getResults().get(1);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Bobby Bargain", user2.getName());
		assertFalse("User [" + user2.getName() + "] was selected.", user2.isSelected());

		// John Ford is the selected approver for DC Sales US
		final CustomerData user3 = searchPageData.getResults().get(2);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "John Ford", user3.getName());
		assertTrue("User [" + user3.getName() + "] was not selected.", user3.isSelected());

		final CustomerData user4 = searchPageData.getResults().get(3);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, "Otto Meier", user4.getName());
		assertFalse("User [" + user4.getName() + "] was selected.", user4.isSelected());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPagedApproversForUnitNullUid()
	{
		defaultB2BApproverFacade.getPagedApproversForUnit(DEFAULT_PAGEABLE_DATA, null);
	}

	@Test
	public void testAddApproverToUnit()
	{
		defaultB2BApproverFacade.addApproverToUnit(DC_SALES_DETROIT, DC_S_DET);
		final SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade.getPagedApproversForUnit(DEFAULT_PAGEABLE_DATA,
				DC_SALES_DETROIT);
		assertNotNull("Search page data is null.", searchPageData);
		assertNotNull("Search results are null.", searchPageData.getResults());

		// check if test user was added as an approver
		boolean approverWasAdded = false;
		for (final CustomerData user : searchPageData.getResults())
		{
			if (DC_S_DET.equals(user.getUid()))
			{
				approverWasAdded = true;
				break;
			}
		}
		assertTrue("Expected approver not found.", approverWasAdded);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddApproverToUnitNullUnitUid()
	{
		defaultB2BApproverFacade.addApproverToUnit(null, DC_S_DET);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddApproverToUnitNullApproverUid()
	{
		defaultB2BApproverFacade.addApproverToUnit(DC_SALES_DETROIT, null);
	}

	@Test
	public void testRemoveApproverFromUnit()
	{
		// make sure "DC Sales Boss" is selected as an approver
		SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade
				.getPagedApproversForUnit(DEFAULT_PAGEABLE_DATA, DC_SALES);
		assertSearchPageData(4, searchPageData);
		CustomerData user = searchPageData.getResults().get(0);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, DC_SALES_BOSS, user.getUid());
		assertTrue("User [" + user.getName() + "] was not selected.", user.isSelected());

		// deselect
		defaultB2BApproverFacade.removeApproverFromUnit(DC_SALES, DC_SALES_BOSS);

		// make sure "DC Sales Boss" is not selected as an approver anymore
		searchPageData = defaultB2BApproverFacade.getPagedApproversForUnit(DEFAULT_PAGEABLE_DATA, DC_SALES);
		assertSearchPageData(4, searchPageData);
		user = searchPageData.getResults().get(0);
		assertEquals(UNEXPECTED_VALUE_FOR_NAME_MSG, DC_SALES_BOSS, user.getUid());
		assertFalse("User [" + user.getName() + "] was selected.", user.isSelected());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveApproverFromUnitNullUnitUid()
	{
		defaultB2BApproverFacade.removeApproverFromUnit(null, DC_SALES_BOSS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveApproverFromUnitNullApproverUid()
	{
		defaultB2BApproverFacade.removeApproverFromUnit(DC_SALES, null);
	}

	@Test
	public void shouldGetPagedApproversForCustomer()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		final List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());

		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 1, selectedApprovers.size());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetPagedApproversForCustomer()
	{
		defaultB2BApproverFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotGetPagedApproversForCustomerWithEmptyCustomerUid()
	{
		defaultB2BApproverFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY);
	}

	@Test
	public void shouldAddApproverForCustomer()
	{
		SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 1, selectedApprovers.size());
		assertFalse(UNEXPECTED_APPROVER_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_BOSS));

		final B2BSelectionData selectionData = defaultB2BApproverFacade.addApproverForCustomer(DC_S_DET, DC_SALES_BOSS);
		assertNotNull(SELECTION_DATA_IS_NULL, selectionData);
		assertEquals(UNEXPECTED_SELECTION_DATA_ID, DC_SALES_BOSS, selectionData.getId());

		searchPageData = defaultB2BApproverFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertEquals(UNEXPECTED_NUMBER_OF_APPROVERS_SELECTED, 2, selectedApprovers.size());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddApproverForCustomer()
	{
		defaultB2BApproverFacade.addApproverForCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotAddApproverForCustomerWithEmptyCustomerUid()
	{
		defaultB2BApproverFacade.addApproverForCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Test
	public void shouldRemoveApproverFromCustomer()
	{
		SearchPageData<CustomerData> searchPageData = defaultB2BApproverFacade.getPagedApproversForCustomer(
				PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		List<CustomerData> selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertTrue(EXPECTED_APPROVER_NOT_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));

		final B2BSelectionData selectionData = defaultB2BApproverFacade.removeApproverFromCustomer(DC_S_DET, DC_SALES_US_BOSS);
		assertNotNull(SELECTION_DATA_IS_NULL, selectionData);
		assertEquals(UNEXPECTED_SELECTION_DATA_ID, DC_SALES_US_BOSS, selectionData.getId());

		searchPageData = defaultB2BApproverFacade.getPagedApproversForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_DET);
		assertSearchPageData(4, searchPageData);

		selectedApprovers = B2BCommercefacadesTestUtils.getSelectedUsers(searchPageData.getResults());
		assertFalse(UNEXPECTED_APPROVER_SELECTED, isUserIncluded(selectedApprovers, DC_SALES_US_BOSS));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotRemoveApproverFromCustomer()
	{
		defaultB2BApproverFacade.removeApproverFromCustomer(null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotRemoveApproverFromCustomerWithEmptyCustomerUid()
	{
		defaultB2BApproverFacade.removeApproverFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY);
	}

	@Override
	protected String getTestDataPath()
	{
		return "/b2bapprovalprocessfacades/test/testOrganizations.csv";
	}
}
