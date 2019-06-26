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

import static org.mockito.Mockito.when;

import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileResultFactory;
import de.hybris.platform.adaptivesearch.util.ConfigurationUtils;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public abstract class AbstractAsBoostItemsMergeStrategyTest
{
	protected static final String UID_1 = "uid1";
	protected static final String UID_2 = "uid2";
	protected static final String UID_3 = "uid3";
	protected static final String UID_4 = "uid4";
	protected static final String UID_5 = "uid5";
	protected static final String UID_6 = "uid6";

	protected static final String INDEX_PROPERTY_1 = "property1";
	protected static final String INDEX_PROPERTY_2 = "property2";
	protected static final String INDEX_PROPERTY_3 = "property3";

	protected static final PK PK_1 = PK.parse("1");
	protected static final PK PK_2 = PK.parse("2");
	protected static final PK PK_3 = PK.parse("3");

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private DefaultAsSearchProfileResultFactory asSearchProfileResultFactory;

	private AsSearchProfileResult source;
	private AsSearchProfileResult target;

	@Before
	public void initalize()
	{
		MockitoAnnotations.initMocks(this);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(ConfigurationUtils.DEFAULT_FACETS_MERGE_MODE, AsFacetsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsFacetsMergeMode.ADD_AFTER.name());
		when(configuration.getString(ConfigurationUtils.DEFAULT_BOOST_ITEMS_MERGE_MODE, AsBoostItemsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsBoostItemsMergeMode.ADD_AFTER.name());
		when(configuration.getString(ConfigurationUtils.DEFAULT_BOOST_RULES_MERGE_MODE, AsBoostRulesMergeMode.ADD.name()))
				.thenReturn(AsBoostRulesMergeMode.ADD.name());

		asSearchProfileResultFactory = new DefaultAsSearchProfileResultFactory();
		asSearchProfileResultFactory.setConfigurationService(configurationService);

		source = createResult();
		target = createResult();
	}

	protected AsSearchProfileResultFactory getAsSearchProfileResultFactory()
	{
		return asSearchProfileResultFactory;
	}

	public AsSearchProfileResult getSource()
	{
		return source;
	}

	public AsSearchProfileResult getTarget()
	{
		return target;
	}

	protected AsSearchProfileResult createResult()
	{
		return asSearchProfileResultFactory.createResult();
	}

	protected <T, R> AsConfigurationHolder<T, R> createConfigurationHolder(final T configuration)
	{
		return asSearchProfileResultFactory.createConfigurationHolder(configuration);
	}
}
