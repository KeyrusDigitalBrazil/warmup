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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext;
import de.hybris.platform.adaptivesearch.data.AsCategoryAwareSearchProfile;
import de.hybris.platform.adaptivesearch.data.AsConfigurableSearchConfiguration;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.strategies.AsMergeStrategy;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileResultFactory;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.PK;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AsCategoryAwareSearchProfileCalculationStrategyTest
{
	private AsCategoryAwareSearchProfileCalculationStrategy strategy;

	@Mock
	private AsSearchProfileResultFactory asSearchProfileResultFactory;

	@Mock
	private AsMergeStrategy asMergeStrategy;

	@Mock
	private AsSearchProfileContext context;

	@Mock
	private final CategoryModel category10 = new CategoryModel();

	@Mock
	private final CategoryModel category20 = new CategoryModel();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		when(category10.getPk()).thenReturn(PK.fromLong(1));
		when(category10.getCode()).thenReturn("cat10");

		when(category20.getPk()).thenReturn(PK.fromLong(2));
		when(category20.getCode()).thenReturn("cat20");

		strategy = new AsCategoryAwareSearchProfileCalculationStrategy();
		strategy.setAsSearchProfileResultFactory(asSearchProfileResultFactory);
		strategy.setAsMergeStrategy(asMergeStrategy);
	}

	@Test
	public void calculate()
	{
		//given
		final AsCategoryAwareSearchProfile searchProfile = new AsCategoryAwareSearchProfile();
		final AsConfigurableSearchConfiguration globalCS = new AsConfigurableSearchConfiguration();
		final AsConfigurableSearchConfiguration cat10CS = new AsConfigurableSearchConfiguration();
		final AsConfigurableSearchConfiguration cat20CS = new AsConfigurableSearchConfiguration();

		final Map<PK, AsConfigurableSearchConfiguration> searchConfigurations = new HashMap<>();
		searchConfigurations.put(null, globalCS);
		searchConfigurations.put(category10.getPk(), cat10CS);
		searchConfigurations.put(category20.getPk(), cat20CS);

		searchProfile.setSearchConfigurations(searchConfigurations);

		final AsSearchProfileResult globalResult = new AsSearchProfileResult();
		final AsSearchProfileResult cat10Result = new AsSearchProfileResult();
		final AsSearchProfileResult cat20Result = new AsSearchProfileResult();

		when(context.getCategoryPath()).thenReturn(Arrays.asList(category10, category20));
		when(asSearchProfileResultFactory.createResultFromSearchConfiguration(globalCS)).thenReturn(globalResult);
		when(asSearchProfileResultFactory.createResultFromSearchConfiguration(cat10CS)).thenReturn(cat10Result);
		when(asSearchProfileResultFactory.createResultFromSearchConfiguration(cat20CS)).thenReturn(cat20Result);

		final List<AsSearchProfileResult> results = new ArrayList<>();
		results.add(globalResult);
		results.add(cat10Result);
		results.add(cat20Result);

		when(asMergeStrategy.merge(context, results, null)).thenReturn(new AsSearchProfileResult());

		//when
		strategy.calculate(context, searchProfile);

		//then
		verify(asMergeStrategy).merge(context, results, null);
	}
}
