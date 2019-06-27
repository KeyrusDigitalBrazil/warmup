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

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.providers.RAOFactsExtractor;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;


@UnitTest
public class AbstractExpandedRAOProviderUnitTest
{
	private static final String VALID_OPTION1 = "test_option1";
	private static final String VALID_OPTION2 = "test_option2";
	private static final String VALID_OPTION_EXTRACTOR1 = "test_option_extractor1";


	private static final Object MODEL_FACT = new NameAwareFact("MODEL_FACT");
	private static final Object modelRAO = new NameAwareFact("modelRAO");
	private static final Object fact1 = new NameAwareFact("fact1");
	private static final Object fact2 = new NameAwareFact("fact2");

	private AbstractExpandedRAOProvider raoProvider;

	@Before
	public void setUp() throws Exception
	{
		raoProvider = new MockExpandedRAOProvider();
		raoProvider.afterPropertiesSet();
		raoProvider = spy(raoProvider);
	}

	@Test
	public void testExpandFactModelNoRaoExtractor()
	{
		final Set facts = raoProvider.expandFactModel(MODEL_FACT);
		assertThat(facts).containsOnly(fact1, fact2);
	}

	@Test
	public void testExpandFactModelWithRaoExtractor() throws Exception
	{
		final Object extractedFact1 = new NameAwareFact("extractedFact1");
		final Object extractedFact2 = new NameAwareFact("extractedFact2");

		final RAOFactsExtractor raoFactsExtractor = new MockRAOFactsExtractor(true, true, VALID_OPTION_EXTRACTOR1,
				ImmutableSet.of(extractedFact1, extractedFact2));
		raoProvider.setFactExtractorList(asList(raoFactsExtractor));
		raoProvider.afterPropertiesSet();

		final Set facts = raoProvider.expandFactModel(MODEL_FACT);
		assertThat(facts).containsOnly(fact1, fact2, extractedFact1, extractedFact2);
	}

	public static class MockExpandedRAOProvider extends AbstractExpandedRAOProvider<Object, Object>
	{

		public MockExpandedRAOProvider()
		{
			this.defaultOptions = Arrays.asList(VALID_OPTION1, VALID_OPTION2);
			this.validOptions = Arrays.asList(VALID_OPTION1, VALID_OPTION2);
			this.minOptions = Arrays.asList(VALID_OPTION1);
		}

		@Override
		protected Object createRAO(final Object modelFact)
		{
			if (modelFact.equals(MODEL_FACT))
			{
				return modelRAO;
			}
			return null;
		}

		@Override
		public void afterPropertiesSet() throws Exception
		{
			super.afterPropertiesSet();
			final Map<String, BiConsumer<Set<Object>, Object>> consumerMap = getConsumerMap();
			consumerMap.putAll(ImmutableMap.of(VALID_OPTION1, (f, o) -> f.add(fact1), VALID_OPTION2, (f, o) -> f.add(fact2)));
		}


	}

	private static class MockRAOFactsExtractor implements RAOFactsExtractor
	{
		private final boolean isMin;
		private final boolean isDefault;
		private final String triggeringOption;
		private final Set facts;

		MockRAOFactsExtractor(final boolean isMin, final boolean isDefault, final String triggeringOption, final Set facts)
		{
			this.isMin = isMin;
			this.isDefault = isDefault;
			this.triggeringOption = triggeringOption;
			this.facts = facts;
		}

		@Override
		public boolean isMinOption()
		{
			return isMin;
		}

		@Override
		public boolean isDefault()
		{
			return isDefault;
		}

		@Override
		public String getTriggeringOption()
		{
			return triggeringOption;
		}

		@Override
		public Set expandFact(final Object raoFact)
		{
			return facts;
		}
	}

	private static class NameAwareFact
	{
		private final String name;

		NameAwareFact(final String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return "NameAware [name=" + name + "]";
		}

	}
}
