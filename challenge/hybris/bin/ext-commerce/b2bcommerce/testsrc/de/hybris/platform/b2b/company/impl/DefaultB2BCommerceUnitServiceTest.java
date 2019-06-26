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
package de.hybris.platform.b2b.company.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BGroupCycleValidator;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultB2BCommerceUnitServiceTest
{
	private DefaultB2BCommerceUnitService b2bCommerceUnitService;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Mock
	private UserService userService;

	@Mock
	private B2BGroupCycleValidator b2BGroupCycleValidator;

	@Mock
	private SessionService sessionService;

	@Mock
	private SearchRestrictionService searchRestrictionService;

	@Mock
	private ModelService modelService;

	private static final String UNIT_ID = "unitId";
	private static final String APPROVER_ID = "approverId";

	private B2BUnitModel unit;
	private AddressModel address;


	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		b2bCommerceUnitService = new DefaultB2BCommerceUnitService();
		b2bCommerceUnitService.setB2BUnitService(b2BUnitService);
		b2bCommerceUnitService.setUserService(userService);
		b2bCommerceUnitService.setB2BGroupCycleValidator(b2BGroupCycleValidator);
		b2bCommerceUnitService.setSessionService(sessionService);
		b2bCommerceUnitService.setSearchRestrictionService(searchRestrictionService);
		b2bCommerceUnitService.setModelService(modelService);

		unit = mock(B2BUnitModel.class);
		address = mock(AddressModel.class);

		B2BCustomerModel approver = mock(B2BCustomerModel.class);
		Set<PrincipalGroupModel> groups = new HashSet<>();
		UserGroupModel b2bApproverGroup = mock(UserGroupModel.class);

		when(b2BUnitService.getUnitForUid(UNIT_ID)).thenReturn(unit);
		when(userService.getUserForUID(APPROVER_ID, B2BCustomerModel.class)).thenReturn(approver);
		when(approver.getGroups()).thenReturn(groups);
		when(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).thenReturn(b2bApproverGroup);
	}

	@Test
	public void shouldGetAllowedParentUnits()
	{
		final Set<B2BUnitModel> branch = mock(Set.class);
		final B2BUnitModel principalGroup = mock(B2BUnitModel.class);
		final B2BUnitModel parentUnit = mock(B2BUnitModel.class);
		final Iterator iter = mock(Iterator.class);
		when(branch.iterator()).thenReturn(iter);
		when(Boolean.valueOf(iter.hasNext())).thenReturn(Boolean.TRUE, Boolean.FALSE);
		when(iter.next()).thenReturn(principalGroup);
		when(sessionService.executeInLocalView(any(SessionExecutionBody.class))).thenReturn(branch);
		when(Boolean.valueOf(b2BGroupCycleValidator.validateGroups(unit, principalGroup))).thenReturn(Boolean.TRUE);
		when(b2BUnitService.getParent(unit)).thenReturn(parentUnit);

		final Collection<? extends B2BUnitModel> allowedParentUnits = b2bCommerceUnitService.getAllowedParentUnits(unit);
		Assert.assertNotNull(allowedParentUnits);
		Assert.assertEquals(2, allowedParentUnits.size());
		Assert.assertTrue(allowedParentUnits.contains(principalGroup));
		Assert.assertTrue(allowedParentUnits.contains(parentUnit));
	}

	@Test
	public void shouldSaveAddressEntry()
	{
		b2bCommerceUnitService.saveAddressEntry(unit, address);
		verify(address, times(1)).setOwner(unit);
		verify(modelService, times(1)).save(unit);
	}
}
