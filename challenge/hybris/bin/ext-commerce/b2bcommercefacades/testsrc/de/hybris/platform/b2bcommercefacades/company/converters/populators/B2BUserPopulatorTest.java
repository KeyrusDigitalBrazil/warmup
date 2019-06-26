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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;


@UnitTest
public class B2BUserPopulatorTest
{

	// Defining and Initializing test variables
	private B2BCustomerModel source;
	private CustomerData target;
	private List<String> userGroupsLookUpSrategyResults;

	private final B2BUserPopulator b2bUserPopulator = new B2BUserPopulator();

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	@Mock
	private UserService userService;
	@Mock
	private MessageSource messageSource;
	@Mock
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;
	@Mock
	private I18NService i18nService;
	@Mock
	private B2BUnitModel parentUnit;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		b2bUserPopulator.setB2BUnitService(b2BUnitService);
		b2bUserPopulator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
		b2bUserPopulator.setMessageSource(messageSource);
		b2bUserPopulator.setUserService(userService);
		b2bUserPopulator.setI18nService(i18nService);

		// Initializing 'source' B2BCusomerModel and 'target' CustomerData
		target = new CustomerData();
		source = new B2BCustomerModel();

		// Initializing 'source' attributes
		source.setUid("sourceUid");
		source.setName("sourceName");
		source.setActive(Boolean.TRUE);

		// Initializing RoleModels for 'source'
		final Set<PrincipalGroupModel> roleModels = new HashSet<>();
		final B2BUnitModel roleModel1 = new B2BUnitModel();
		roleModel1.setUid("roleModel1");
		final B2BUserGroupModel roleModel2 = new B2BUserGroupModel();
		roleModel2.setUid("roleModel2");
		final PrincipalGroupModel roleModel3 = new PrincipalGroupModel();
		roleModel3.setUid("roleModel3");
		final PrincipalGroupModel roleModel4 = new PrincipalGroupModel();
		roleModel4.setUid("roleModel4");
		roleModels.add(roleModel2);
		roleModels.add(roleModel1);
		roleModels.add(roleModel3);
		roleModels.add(roleModel4);
		source.setGroups(roleModels);

		// Initializing userGroupsLookUpSrategyResults results
		userGroupsLookUpSrategyResults = new ArrayList<String>();
		userGroupsLookUpSrategyResults.add("roleModel3");
		userGroupsLookUpSrategyResults.add("roleModel4");

	}

	@Test
	public void testShouldPopulateRolesInUserData()
	{
		Mockito.when(parentUnit.getUid()).thenReturn("parentUnitId");
		Mockito.when(parentUnit.getName()).thenReturn("parentUnitName");
		Mockito.when(parentUnit.getActive()).thenReturn(Boolean.TRUE);
		Mockito.when(parentUnit.getLocName()).thenReturn("locName");
		Mockito.when(b2BUserGroupsLookUpStrategy.getUserGroups()).thenReturn(userGroupsLookUpSrategyResults);
		Mockito.when(b2BUnitService.getParent(source)).thenReturn(parentUnit);

		b2bUserPopulator.populate(source, target);

		Assert.assertEquals("Unexpected value for active", source.getActive(), Boolean.valueOf(target.isActive()));
		Assert.assertEquals("Unexpected value for name", source.getName(), target.getName());
		Assert.assertEquals("Unexpected value for uid", source.getUid(), target.getUid());
		Assert.assertEquals("Unexpected value for normalized uid", source.getUid().replaceAll("\\W", "_"),
				target.getNormalizedUid());
		Assert.assertNotNull("Roles are null", target.getRoles());
		Assert.assertEquals("Unexpected number of roles", 2, target.getRoles().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BCustomerModel()
	{
		b2bUserPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullUserData()
	{
		b2bUserPopulator.populate(source, null);
	}
}
