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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapperException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Junit Test Suite for {@link DynamicEnumRuleParameterValueMapper}
 *
 */
@UnitTest
public class DynamicEnumRuleParameterValueMapperTest
{
	private static final String ANY_STRING = "anyString";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private TypeService typeService;

	@Mock
	private EnumerationValueModel enumerationValueModel;

	@InjectMocks
	private final DynamicEnumRuleParameterValueMapper mapper = new DynamicEnumRuleParameterValueMapper("test_enumeration_code");

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
	public void noEnumerationValueFoundTest()
	{
		//given
		when(typeService.getEnumerationValue(Matchers.anyString(), Matchers.anyString()))
				.thenThrow(UnknownIdentifierException.class);

		//expect
		expectedException.expect(RuleParameterValueMapperException.class);

		//when
		mapper.fromString(ANY_STRING);
	}

	@Test
	public void mappedEnumerationValueTest()
	{
		//given
		when(typeService.getEnumerationValue(Matchers.anyString(), Matchers.anyString())).thenReturn(enumerationValueModel);

		//when
		final EnumerationValueModel mappedEnumerationValue = mapper.fromString(ANY_STRING);

		//then
		assertEquals(enumerationValueModel, mappedEnumerationValue);
	}

	@Test
	public void objectToStringTest()
	{
		//given
		when(enumerationValueModel.getCode()).thenReturn(ANY_STRING);

		//when
		final String enumCode = mapper.toString(enumerationValueModel);

		//then
		assertEquals(ANY_STRING, enumCode);
	}
}
