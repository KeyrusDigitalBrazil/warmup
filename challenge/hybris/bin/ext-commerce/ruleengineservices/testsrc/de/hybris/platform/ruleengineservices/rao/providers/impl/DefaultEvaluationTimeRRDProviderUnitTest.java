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
package de.hybris.platform.ruleengineservices.rao.providers.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rrd.EvaluationTimeRRD;

import java.util.Date;
import java.util.Set;

import org.fest.assertions.Condition;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultEvaluationTimeRRDProviderUnitTest
{
	private DefaultEvaluationTimeRRDProvider evaluationTimeRRDProvider;

	@Before
	public void setUp() throws Exception
	{
		this.evaluationTimeRRDProvider = new DefaultEvaluationTimeRRDProvider();
	}

	@Test
	public void testEvaluationTimeRRD()
	{
		final Date dateFact = new Date();
		final Set facts = this.evaluationTimeRRDProvider.expandFactModel(dateFact);
		assertThat(facts).hasSize(1);
		final Object theOnlyFact = facts.iterator().next();
		assertThat(theOnlyFact).isInstanceOf(EvaluationTimeRRD.class).is(new Condition<Object>()
		{
			@Override
			public boolean matches(final Object factObject)
			{
				return (((EvaluationTimeRRD) factObject).getEvaluationTime()).equals(Long.valueOf(dateFact.getTime()));
			}
		});
	}

	@Test
	public void testTryExpandInvalidFact()
	{
		final String stringFact = "who is here?";
		final Set facts = this.evaluationTimeRRDProvider.expandFactModel(stringFact);
		assertThat(facts).isEmpty();
	}
}
