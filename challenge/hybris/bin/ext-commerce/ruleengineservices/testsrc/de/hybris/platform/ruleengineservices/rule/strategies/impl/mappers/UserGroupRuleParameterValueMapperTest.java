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
package de.hybris.platform.ruleengineservices.rule.strategies.impl.mappers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class UserGroupRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private UserService userService;
	@Mock
	private UserGroupModel userGroup;

	@InjectMocks
	private final UserGroupRuleParameterValueMapper mapper = new UserGroupRuleParameterValueMapper();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void nullTestFromString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.fromString(null);
	}

	@Test
	public void nullTestToString()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		mapper.toString(null);
	}

	@Test
	public void noUserGroupFoundTest()
	{
		//given
		BDDMockito.given(userService.getUserGroupForUID(Mockito.anyString())).willReturn(null);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedProductTest()
	{
		//given
		BDDMockito.given(userService.getUserGroupForUID(Mockito.anyString())).willReturn(userGroup);

		//when
		final UserGroupModel mappedObject = mapper.fromString(ANY_STRING);

		//then
		Assert.assertEquals(userGroup, mappedObject);
	}


	@Test
	public void objectToStringTest()
	{
		//given
		BDDMockito.given(userGroup.getUid()).willReturn(ANY_STRING);

		//when
		final String stringRepresentation = mapper.toString(userGroup);

		//then
		Assert.assertEquals(ANY_STRING, stringRepresentation);
	}

}
