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
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.function.Function;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CustomerRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Mock
	private CustomerModel customer;

	@Mock
	private Function<UserModel, String> userIdentifierProvider;

	@InjectMocks
	private CustomerRuleParameterValueMapper mapper;

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
	public void noCustomerFoundTest()
	{
		//given
		given(userService.getUserForUID(Mockito.anyString(), Mockito.eq(CustomerModel.class))).willReturn(null);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedCustomerTest()
	{
		//given
		given(userService.getUserForUID(Mockito.anyString(), Mockito.eq(CustomerModel.class))).willReturn(customer);

		//when
		final CustomerModel mappedCustomer = mapper.fromString(ANY_STRING);

		//then
		Assert.assertEquals(customer, mappedCustomer);
	}

	@Test
	public void anonymousUserTest()
	{
		//given
		given(userService.getAnonymousUser()).willReturn(customer);

		//when
		final CustomerModel mappedCustomer = mapper.fromString("anonymous");

		verify(userService).getAnonymousUser();

		//then
		Assert.assertEquals(customer, mappedCustomer);
	}

	@Test
	public void objectToStringTest()
	{
		given(userIdentifierProvider.apply(customer)).willReturn(ANY_STRING);

		//when
		final String stringRepresentation = mapper.toString(customer);

		//then
		Assert.assertEquals(ANY_STRING, stringRepresentation);
	}
}
