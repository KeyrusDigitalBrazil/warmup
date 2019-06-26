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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUnitPopulatorTest
{
	private static final Boolean IS_ACTIVE = Boolean.TRUE;
	private static final String P_UNIT_ID = "P_UNIT_ID";
	private static final String P_UNIT_NAME = "parentUnitName";
	private static final String CHILD_UID = "P_UNIT_NAME";
	private static final String CHILD_NAME = "CHILD_NAME";
	private static final String BUDGET_CODE = "BUDGET_CODE";
	private static final String COSTCENTER_CODE = "COSTCENTER_CODE";
	private static final String ADDRESS_ID = "ADDRESS_ID";
	private static final String CUSTOMER_UID = "CUSTOMER_UID";
	private static final String MANAGER_UID = "MANAGER_UID";
	private static final String ADMINISTRATOR_UID = "ADMINISTRATOR_UID";
	private static final String ACCOUNTMANAGER_UID = "ACCOUNTMANAGER_UID";

	private B2BUnitPopulator b2BUnitPopulator;
	private B2BUnitModel source;
	private B2BUnitData target;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Mock
	private UserService userService;

	@Mock
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2BCostCenterConverter;

	@Mock
	private Converter<B2BBudgetModel, B2BBudgetData> b2BBudgetConverter;

	@Mock
	private Converter<PrincipalModel, PrincipalData> principalConverter;

	@Mock
	private Converter<AddressModel, AddressData> addressConverter;

	@Mock
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BUnitModel.class);
		target = new B2BUnitData();

		b2BUnitPopulator = new B2BUnitPopulator();
		b2BUnitPopulator.setB2BUnitService(b2BUnitService);
		b2BUnitPopulator.setUserService(userService);
		b2BUnitPopulator.setAddressConverter(addressConverter);
		b2BUnitPopulator.setB2BCostCenterConverter(b2BCostCenterConverter);
		b2BUnitPopulator.setB2BBudgetConverter(b2BBudgetConverter);
		b2BUnitPopulator.setPrincipalConverter(principalConverter);
		b2BUnitPopulator.setB2BCustomerConverter(b2BCustomerConverter);
	}

	@Test
	public void shouldPopulate()
	{
		setupMockData();

		b2BUnitPopulator.populate(source, target);

		// unit
		assertUnit();

		// parent unit
		assertParentUnit();

		// child unit
		assertChildren();

		// budgets
		assertBudgets();

		// cost centers
		assertCostCenters();

		// addresses
		assertAddresses();

		// customers
		assertCustomers();

		// managers
		assertManagers();

		// Administrators
		assertAdmins();

		// AccountManagers
		assertAccountManagers();
	}

	private void assertAccountManagers()
	{
		Assert.assertNotNull("target accountManagers should not be null", target.getAccountManagers());
		Assert.assertEquals("target accountManagers size should be 1", 1, target.getAccountManagers().size());
		Assert.assertEquals("target accountManagers should match", ACCOUNTMANAGER_UID,
				target.getAccountManagers().iterator().next().getUid());
	}

	private void assertAdmins()
	{
		Assert.assertNotNull("target administrators should not be null", target.getAdministrators());
		Assert.assertEquals("target administrators size should be 1", 1, target.getAdministrators().size());
		Assert.assertEquals("target administrators should match", ADMINISTRATOR_UID,
				target.getAdministrators().iterator().next().getUid());
	}

	private void assertManagers()
	{
		Assert.assertNotNull("target managers should not be null", target.getManagers());
		Assert.assertEquals("target managers size should be 1", 1, target.getManagers().size());
		Assert.assertEquals("target managers should match", MANAGER_UID, target.getManagers().iterator().next().getUid());
	}

	private void assertCustomers()
	{
		Assert.assertNotNull("target customers should not be null", target.getCustomers());
		Assert.assertEquals("target customers size should be 1", 1, target.getCustomers().size());
		Assert.assertEquals("target customers should match", CUSTOMER_UID, target.getCustomers().iterator().next().getUid());
	}

	private void assertAddresses()
	{
		Assert.assertNotNull("target address should not be null", target.getAddresses());
		Assert.assertEquals("target address size should be 1", 1, target.getAddresses().size());
		Assert.assertEquals("target address should match", ADDRESS_ID, target.getAddresses().get(0).getId());
	}

	private void assertCostCenters()
	{
		Assert.assertNotNull("target costCenters should not be null", target.getCostCenters());
		Assert.assertEquals("target costCenters size should be 1", 1, target.getCostCenters().size());
		Assert.assertEquals("target costCenters should match", COSTCENTER_CODE, target.getCostCenters().get(0).getCode());
	}

	private void assertBudgets()
	{
		Assert.assertNotNull("target budget should not be null", target.getBudgets());
		Assert.assertEquals("target budgets size should be 1", 1, target.getBudgets().size());
		Assert.assertEquals("target budgets should match", BUDGET_CODE, target.getBudgets().get(0).getCode());
	}

	private void assertChildren()
	{
		Assert.assertNotNull("target Children should not be null", target.getChildren());
		Assert.assertEquals("target Children size should be 1", 1, target.getChildren().size());
		Assert.assertEquals("target childUid should match", CHILD_UID, target.getChildren().get(0).getUid());
		Assert.assertEquals("target childUid should match", CHILD_NAME, target.getChildren().get(0).getName());
		Assert.assertEquals("target childUid should match", IS_ACTIVE, Boolean.valueOf(target.getChildren().get(0).isActive()));
	}

	private void assertParentUnit()
	{
		final B2BUnitData parent = target.getUnit();
		Assert.assertNotNull("target parent should not be null", parent);
		Assert.assertEquals("source and target code should match", P_UNIT_ID, parent.getUid());
		Assert.assertEquals("source and target name should match", P_UNIT_NAME, parent.getName());
		Assert.assertEquals("source and target name should match", IS_ACTIVE, Boolean.valueOf(parent.isActive()));
	}

	private void assertUnit()
	{
		Assert.assertEquals("source and target code should match", source.getUid(), target.getUid());
		Assert.assertEquals("source and target name should match", source.getLocName(), target.getName());
		Assert.assertEquals("source and target name should match", source.getActive(), Boolean.valueOf(target.isActive()));
	}

	private void setupMockData()
	{
		// unit
		mockUnit();

		// parent unit
		mockParentUnit();

		// child unit
		mockChildren();

		// budget
		mockBudgets();

		// cost center
		mockCostCenters();

		// address
		mockAddresses();

		// customers
		mockCustomers();

		// managers
		mockManagers();

		// Administrators
		mockAdministrators();

		// AccountManagers
		mockAccountManagers();
	}

	private void mockAccountManagers()
	{
		final EmployeeModel accountManager = mock(EmployeeModel.class);
		given(source.getAccountManager()).willReturn(accountManager);
		final PrincipalData accountManagerData = new PrincipalData();
		accountManagerData.setUid(ACCOUNTMANAGER_UID);
		given(principalConverter.convert(accountManager)).willReturn(accountManagerData);
	}

	private void mockAdministrators()
	{
		final Collection<B2BCustomerModel> administrators = new ArrayList<>();
		final B2BCustomerModel administrator = mock(B2BCustomerModel.class);
		administrators.add(administrator);
		given(b2BUnitService.getUsersOfUserGroup(source, B2BConstants.B2BADMINGROUP, false)).willReturn(administrators);
		final CustomerData administratorData = new CustomerData();
		administratorData.setUid(ADMINISTRATOR_UID);
		given(b2BCustomerConverter.convert(administrator)).willReturn(administratorData);
	}

	private void mockManagers()
	{
		final Collection<B2BCustomerModel> managers = new ArrayList<>();
		final B2BCustomerModel manager = mock(B2BCustomerModel.class);
		managers.add(manager);
		given(b2BUnitService.getUsersOfUserGroup(source, B2BConstants.B2BMANAGERGROUP, false)).willReturn(managers);
		final CustomerData managerData = new CustomerData();
		managerData.setUid(MANAGER_UID);
		given(b2BCustomerConverter.convert(manager)).willReturn(managerData);
	}

	private void mockCustomers()
	{
		final Collection<B2BCustomerModel> customers = new ArrayList<>();
		final B2BCustomerModel customer = mock(B2BCustomerModel.class);
		customers.add(customer);
		given(b2BUnitService.getUsersOfUserGroup(source, B2BConstants.B2BCUSTOMERGROUP, false)).willReturn(customers);
		final CustomerData customerData = new CustomerData();
		customerData.setUid(CUSTOMER_UID);
		given(b2BCustomerConverter.convert(customer)).willReturn(customerData);
	}

	private void mockAddresses()
	{
		final List<AddressModel> addresses = new ArrayList<>();
		final AddressModel address = mock(AddressModel.class);
		addresses.add(address);
		given(source.getAddresses()).willReturn(addresses);
		final AddressData addressData = new AddressData();
		addressData.setId(ADDRESS_ID);
		given(addressConverter.convert(address)).willReturn(addressData);
	}

	private void mockCostCenters()
	{
		final List<B2BCostCenterModel> costCenters = new ArrayList<>();
		final B2BCostCenterModel costCenter = mock(B2BCostCenterModel.class);
		given(costCenter.getActive()).willReturn(Boolean.TRUE);
		costCenters.add(costCenter);
		given(source.getCostCenters()).willReturn(costCenters);
		final B2BCostCenterData costCenterData = new B2BCostCenterData();
		costCenterData.setCode(COSTCENTER_CODE);
		given(b2BCostCenterConverter.convert(costCenter)).willReturn(costCenterData);
	}

	private void mockBudgets()
	{
		final List<B2BBudgetModel> budgets = new ArrayList<>();
		final B2BBudgetModel budget = mock(B2BBudgetModel.class);
		budgets.add(budget);
		given(source.getBudgets()).willReturn(budgets);
		final B2BBudgetData budgetData = new B2BBudgetData();
		budgetData.setCode(BUDGET_CODE);
		given(b2BBudgetConverter.convert(budget)).willReturn(budgetData);
	}

	private void mockChildren()
	{
		final Set<PrincipalModel> members = new HashSet<>();
		final B2BUnitModel member = mock(B2BUnitModel.class);
		given(member.getUid()).willReturn(CHILD_UID);
		given(member.getLocName()).willReturn(CHILD_NAME);
		given(member.getActive()).willReturn(IS_ACTIVE);
		members.add(member);
		given(source.getMembers()).willReturn(members);
	}

	private void mockParentUnit()
	{
		given(b2BUnitService.getParent(source)).willReturn(testUnit);
		given(testUnit.getUid()).willReturn(P_UNIT_ID);
		given(testUnit.getLocName()).willReturn(P_UNIT_NAME);
		given(testUnit.getActive()).willReturn(IS_ACTIVE);
	}

	private void mockUnit()
	{
		given(source.getUid()).willReturn("uid");
		given(source.getLocName()).willReturn("name");
		given(source.getActive()).willReturn(IS_ACTIVE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullSource()
	{
		b2BUnitPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullTarget()
	{
		b2BUnitPopulator.populate(source, null);
	}

}
