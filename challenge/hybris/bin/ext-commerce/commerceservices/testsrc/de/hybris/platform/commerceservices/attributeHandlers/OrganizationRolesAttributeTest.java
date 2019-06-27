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
package de.hybris.platform.commerceservices.attributeHandlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.OrganizationRolesAttribute;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * OrganizationRoleAttribute dynamic attribute handler unit test. This dynamic attribute is defined in Employee type.
 */
@UnitTest
public class OrganizationRolesAttributeTest
{
	private static final String ORG_EMPLOYEE = "salesemployee";

	private OrgUnitService orgUnitService;
	private OrganizationRolesAttribute organizationRolesAttribute;

	private EmployeeModel employeeModel;
	private PrincipalGroupModel principalGroupModel;
	private Set<PrincipalGroupModel> roles;


	@Before
	public void setUp()
	{
		orgUnitService = Mockito.mock(OrgUnitService.class);

		organizationRolesAttribute = new OrganizationRolesAttribute();
		organizationRolesAttribute.setOrgUnitService(orgUnitService);

		employeeModel = new EmployeeModel();
		principalGroupModel = new PrincipalGroupModel();
		principalGroupModel.setUid(ORG_EMPLOYEE);
		roles = new HashSet<PrincipalGroupModel>();
		roles.add(principalGroupModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAttributeHandlerForNull()
	{
		organizationRolesAttribute.get(null);
	}

	@Test
	public void testAttributeHandler()
	{
		Mockito.when(orgUnitService.getRolesForEmployee(employeeModel)).thenReturn(roles);
		Assert.assertEquals(principalGroupModel, organizationRolesAttribute.get(employeeModel).iterator().next());
	}

}