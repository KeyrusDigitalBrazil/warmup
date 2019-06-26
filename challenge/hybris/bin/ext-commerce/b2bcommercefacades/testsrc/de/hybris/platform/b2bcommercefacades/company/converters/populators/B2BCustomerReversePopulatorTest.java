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
import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BCustomerReversePopulatorTest
{
	private B2BCustomerReversePopulator b2BCustomerReversePopulator;
	private CustomerData source;
	private B2BCustomerModel target;

	private static final String EMAIL = "Email";
	private static final String DISPLAY_UID = "displayUid";

	@Mock
	private TitleModel titleModel;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BUnitData testUnitData;

	@Mock
	private B2BUnitModel oldDefaultUnit;

	@Mock
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Mock
	private UserService userService;

	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(CustomerData.class);
		target = new B2BCustomerModel();

		b2BCustomerReversePopulator = new B2BCustomerReversePopulator();
		b2BCustomerReversePopulator.setB2BCommerceB2BUserGroupService(b2BCommerceB2BUserGroupService);
		b2BCustomerReversePopulator.setCustomerNameStrategy(customerNameStrategy);
		b2BCustomerReversePopulator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
		b2BCustomerReversePopulator.setUserService(userService);
		b2BCustomerReversePopulator.setB2BUnitService(b2bUnitService);

		given(source.getEmail()).willReturn(EMAIL);
		given(customerNameStrategy.getName("firstName", "lastName")).willReturn("firstName lastName");
		given(source.getDisplayUid()).willReturn(DISPLAY_UID);
		final String titileCode = "titileCode";
		given(source.getTitleCode()).willReturn(titileCode);
		given(userService.getTitleForCode(titileCode)).willReturn(titleModel);
		given(source.getUnit()).willReturn(testUnitData);
		final String unitUid = "unitUid";
		given(testUnitData.getUid()).willReturn(unitUid);
		given(b2bUnitService.getUnitForUid(unitUid)).willReturn(testUnit);
		given(b2bUnitService.getParent(target)).willReturn(oldDefaultUnit);
		final HashSet<PrincipalGroupModel> group = new HashSet<PrincipalGroupModel>();
		group.add(oldDefaultUnit);
		target.setGroups(group);
		target.setDefaultB2BUnit(oldDefaultUnit);
	}

	@Test
	public void shouldUpdateDefaultUnitGroup()
	{
		Assert.assertTrue("target groups should contain old default unit before populate",
				target.getGroups().contains(oldDefaultUnit));

		b2BCustomerReversePopulator.populate(source, target);

		Assert.assertEquals("source and target email should match", source.getEmail(), target.getEmail());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source and target title should match", titleModel, target.getTitle());
		Assert.assertTrue("target groups should contain testunit", target.getGroups().contains(testUnit));
		Assert.assertFalse("target groups should not contain old default unit", target.getGroups().contains(oldDefaultUnit));
		Assert.assertEquals("source and target DefaultB2BUnit should match", testUnit, target.getDefaultB2BUnit());
	}

	@Test
	public void shouldPopulateWithDisplayUid()
	{
		b2BCustomerReversePopulator.populate(source, target);
		Assert.assertEquals("source and target email should match", source.getEmail(), target.getEmail());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source and target title should match", titleModel, target.getTitle());
		Assert.assertTrue("target groups should contain testunit", target.getGroups().contains(testUnit));
		Assert.assertEquals("source's display uid and target's original uid should match", DISPLAY_UID, target.getOriginalUid());
		Assert.assertEquals("source's display uid and target's uid should match", DISPLAY_UID.toLowerCase(), target.getUid());
	}

	@Test
	public void shouldPopulateWithEmail()
	{
		given(source.getDisplayUid()).willReturn(StringUtils.EMPTY);
		b2BCustomerReversePopulator.populate(source, target);
		Assert.assertEquals("source and target email should match", source.getEmail(), target.getEmail());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source and target title should match", titleModel, target.getTitle());
		Assert.assertTrue("target groups should contain testunit", target.getGroups().contains(testUnit));
		Assert.assertEquals("source's email and target's original uid should match", EMAIL, target.getOriginalUid());
		Assert.assertEquals("source's email and target's uid should match", EMAIL.toLowerCase(), target.getUid());
	}

	@Test
	public void shouldPopulateWithNullTitile()
	{
		given(source.getTitleCode()).willReturn(null);
		b2BCustomerReversePopulator.populate(source, target);
		Assert.assertEquals("source and target email should match", source.getEmail(), target.getEmail());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertNull("source and target title should match", target.getTitle());
		Assert.assertTrue("target groups should contain testunit", target.getGroups().contains(testUnit));
		Assert.assertEquals("source's display uid and target's original uid should match", DISPLAY_UID, target.getOriginalUid());
		Assert.assertEquals("source's display uid and target's uid should match", DISPLAY_UID.toLowerCase(), target.getUid());
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCustomerReversePopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCustomerReversePopulator.populate(source, null);
	}

}
