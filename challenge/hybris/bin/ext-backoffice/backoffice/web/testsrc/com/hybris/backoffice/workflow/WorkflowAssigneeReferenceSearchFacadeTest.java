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
package com.hybris.backoffice.workflow;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.search.data.SearchQueryData;

import jersey.repackaged.com.google.common.collect.Sets;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowAssigneeReferenceSearchFacadeTest
{

	@InjectMocks
	private WorkflowAssigneeReferenceSearchFacade facade;
	@Mock
	private PermissionManagementService permissionManagementService;
	@Mock
	private TypeService typeService;

	@Test
	public void testGetAllPrincipals()
	{
		final ComposedTypeModel type = new ComposedTypeModel();

		final EmployeeModel excludedGroupEmployee = new EmployeeModel();
		final UserGroupModel excludedGroup = new UserGroupModel();
		excludedGroup.setMembers(Sets.newHashSet(excludedGroupEmployee));

		final EmployeeModel excludedEmployee = new EmployeeModel();

		final EmployeeModel employee = new EmployeeModel();

		final UserGroupModel subGroup = new UserGroupModel();
		final EmployeeModel subGroupEmployee = new EmployeeModel();
		subGroup.setMembers(Sets.newHashSet(subGroupEmployee));

		final UserGroupModel group = new UserGroupModel();
		group.setMembers(Sets.newHashSet(subGroup, employee, excludedEmployee, excludedGroup));

		final Collection<PermissionAssignment> permissions = Lists.newArrayList(
				new PermissionAssignment(PermissionsConstants.READ, group, false),
				new PermissionAssignment(PermissionsConstants.READ, excludedGroup, true),
				new PermissionAssignment(PermissionsConstants.READ, excludedEmployee, true));

		when(typeService.getComposedTypeForCode(WorkflowActionModel._TYPECODE)).thenReturn(type);
		when(permissionManagementService.getItemPermissionsForName(type, PermissionsConstants.READ)).thenReturn(permissions);

		final List<PrincipalModel> allPrincipals = facade.getAllPrincipals();
		assertThat(allPrincipals).containsOnly(group, subGroup, subGroupEmployee, employee);
		assertThat(allPrincipals.size()).isEqualTo(4);
	}

	@Test
	public void testSearch()
	{
		final SearchQueryData sampleSearchQueryData = mock(SearchQueryData.class);
		doReturn(Integer.valueOf(5)).when(sampleSearchQueryData).getPageSize();
		doReturn("test").when(sampleSearchQueryData).getSearchQueryText();

		final PrincipalModel principal1 = mock(PrincipalModel.class);
		when(principal1.getDisplayName()).thenReturn("TESTa");
		when(principal1.getUid()).thenReturn("");
		final PrincipalModel principal2 = mock(PrincipalModel.class);
		when(principal2.getDisplayName()).thenReturn("NOT TO BE FILTERED");
		when(principal2.getUid()).thenReturn("TESTc");
		final PrincipalModel principal3 = mock(PrincipalModel.class);
		when(principal3.getDisplayName()).thenReturn("to be filtered");
		when(principal3.getUid()).thenReturn("to be filtered");
		final PrincipalModel principal4 = mock(PrincipalModel.class);
		when(principal4.getDisplayName()).thenReturn("not to be filtered");
		when(principal4.getUid()).thenReturn("testb");
		final PrincipalModel principal5 = mock(PrincipalModel.class);
		when(principal5.getDisplayName()).thenReturn(null);
		when(principal5.getUid()).thenReturn("test");

		final WorkflowAssigneeReferenceSearchFacade facadeSpied = spy(facade);
		doReturn(Arrays.asList(principal1, principal2, principal3, principal4, principal5)).when(facadeSpied).getAllPrincipals();

		assertThat(facadeSpied.search(sampleSearchQueryData).getAllResults()).containsExactly(principal5, principal4, principal2,
				principal1);
	}

}
