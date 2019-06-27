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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsBoostRuleConfiguration;
import de.hybris.platform.adaptivesearch.data.AsBoostRule;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class AsBoostRulesAddMergeStrategyTest extends AbstractAsBoostRulesMergeStrategyTest
{
	private AsBoostRulesAddMergeStrategy mergeStrategy;

	@Before
	public void createMergeStrategy()
	{
		mergeStrategy = new AsBoostRulesAddMergeStrategy();
		mergeStrategy.setAsSearchProfileResultFactory(getAsSearchProfileResultFactory());
	}

	@Test
	public void mergeBoostRules()
	{
		// given
		final AsBoostRule boostRule1 = new AsBoostRule();
		boostRule1.setIndexProperty(INDEX_PROPERTY_1);
		boostRule1.setUid(UID_1);

		final AsBoostRule boostRule2 = new AsBoostRule();
		boostRule2.setIndexProperty(INDEX_PROPERTY_2);
		boostRule2.setUid(UID_2);

		getTarget().getBoostRules().add(createConfigurationHolder(boostRule1));
		getSource().getBoostRules().add(createConfigurationHolder(boostRule2));

		// when
		mergeStrategy.mergeBoostRules(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getBoostRules().size());
		final Iterator<AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration>> boostRulesIterator = getTarget()
				.getBoostRules().iterator();

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule1Holder = boostRulesIterator.next();
		assertSame(boostRule1, boostRule1Holder.getConfiguration());

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule2Holder = boostRulesIterator.next();
		assertSame(boostRule2, boostRule2Holder.getConfiguration());
	}

	@Test
	public void mergeBoostRulesWithDuplicates()
	{
		// given
		final AsBoostRule boostRule1 = new AsBoostRule();
		boostRule1.setIndexProperty(INDEX_PROPERTY_1);
		boostRule1.setUid(UID_1);

		final AsBoostRule boostRule2 = new AsBoostRule();
		boostRule2.setIndexProperty(INDEX_PROPERTY_2);
		boostRule2.setUid(UID_2);

		final AsBoostRule boostRule3 = new AsBoostRule();
		boostRule3.setIndexProperty(INDEX_PROPERTY_2);
		boostRule3.setUid(UID_3);

		final AsBoostRule boostRule4 = new AsBoostRule();
		boostRule4.setIndexProperty(INDEX_PROPERTY_3);
		boostRule4.setUid(UID_4);

		getTarget().getBoostRules().add(createConfigurationHolder(boostRule1));
		getTarget().getBoostRules().add(createConfigurationHolder(boostRule2));

		getSource().getBoostRules().add(createConfigurationHolder(boostRule3));
		getSource().getBoostRules().add(createConfigurationHolder(boostRule4));

		// when
		mergeStrategy.mergeBoostRules(getSource(), getTarget());

		// then
		assertEquals(4, getTarget().getBoostRules().size());
		final Iterator<AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration>> boostRulesIterator = getTarget()
				.getBoostRules().iterator();

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule1Holder = boostRulesIterator.next();
		assertSame(boostRule1, boostRule1Holder.getConfiguration());

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule2Holder = boostRulesIterator.next();
		assertSame(boostRule2, boostRule2Holder.getConfiguration());

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule3Holder = boostRulesIterator.next();
		assertSame(boostRule3, boostRule3Holder.getConfiguration());

		final AsConfigurationHolder<AsBoostRule, AbstractAsBoostRuleConfiguration> boostRule4Holder = boostRulesIterator.next();
		assertSame(boostRule4, boostRule4Holder.getConfiguration());
	}
}