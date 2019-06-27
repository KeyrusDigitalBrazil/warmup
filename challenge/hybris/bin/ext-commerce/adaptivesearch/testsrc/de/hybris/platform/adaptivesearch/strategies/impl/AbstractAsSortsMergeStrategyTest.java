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

import de.hybris.platform.adaptivesearch.data.AbstractAsSortConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedSort;
import de.hybris.platform.adaptivesearch.data.AsPromotedSort;
import de.hybris.platform.adaptivesearch.data.AsSearchProfileResult;
import de.hybris.platform.adaptivesearch.data.AsSort;
import de.hybris.platform.adaptivesearch.data.AsSortExpression;
import de.hybris.platform.adaptivesearch.enums.AsBoostItemsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsBoostRulesMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsFacetsMergeMode;
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.enums.AsSortsMergeMode;
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileResultFactory;
import de.hybris.platform.adaptivesearch.util.ConfigurationUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public abstract class AbstractAsSortsMergeStrategyTest
{
	protected static final String UID_1 = "uid1";
	protected static final String UID_2 = "uid2";
	protected static final String UID_3 = "uid3";
	protected static final String UID_4 = "uid4";
	protected static final String UID_5 = "uid5";
	protected static final String UID_6 = "uid6";
	protected static final String UID_7 = "uid7";
	protected static final String UID_8 = "uid8";

	protected static final String SORT1_CODE = "code1";
	protected static final String SORT2_CODE = "code2";
	protected static final String SORT3_CODE = "code3";
	protected static final String SORT4_CODE = "code4";
	protected static final String SORT5_CODE = "code5";
	protected static final String SORT6_CODE = "code6";

	protected static final String INDEX_PROPERTY_1 = "property1";
	protected static final String INDEX_PROPERTY_2 = "property2";
	protected static final String INDEX_PROPERTY_3 = "property3";
	protected static final String INDEX_PROPERTY_4 = "property4";
	protected static final String INDEX_PROPERTY_5 = "property5";
	protected static final String INDEX_PROPERTY_6 = "property6";
	protected static final String INDEX_PROPERTY_7 = "property7";
	protected static final String INDEX_PROPERTY_8 = "property8";
	protected static final String INDEX_PROPERTY_9 = "property9";
	protected static final String INDEX_PROPERTY_10 = "property10";
	protected static final String INDEX_PROPERTY_11 = "property11";
	protected static final String INDEX_PROPERTY_12 = "property12";
	protected static final String INDEX_PROPERTY_13 = "property13";

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
		when(configuration.getString(ConfigurationUtils.DEFAULT_SORTS_MERGE_MODE, AsSortsMergeMode.ADD_AFTER.name()))
				.thenReturn(AsSortsMergeMode.ADD_AFTER.name());

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

	public static final class SortBuilder<T extends AbstractAsSortConfiguration>
	{

		private final T sortConfig;

		private SortBuilder(final Supplier<T> tSupplier)
		{
			this.sortConfig = tSupplier.get();
		}

		public static SortBuilder<AsSort> anAsSort()
		{
			return new SortBuilder(AsSort::new);
		}

		public static SortBuilder<AsExcludedSort> anAsExcludedSort()
		{
			return new SortBuilder(AsExcludedSort::new);
		}

		public static SortBuilder<AsPromotedSort> anAsPromotedSort()
		{
			return new SortBuilder(AsPromotedSort::new);
		}


		public SortBuilder<T> withCode(final String code)
		{
			sortConfig.setCode(code);
			return this;
		}

		public SortBuilder<T> withUid(final String uid)
		{
			sortConfig.setUid(uid);
			return this;
		}

		public SortBuilder<T> withExpressions(final List<AsSortExpression> expresions)
		{
			sortConfig.setExpressions(expresions);
			return this;
		}

		public T build()
		{
			return sortConfig;
		}
	}

	public static final class AsSortExpressionBuilder
	{
		private final AsSortExpression asSortExpression;

		private AsSortExpressionBuilder()
		{
			asSortExpression = new AsSortExpression();
		}

		public static AsSortExpressionBuilder anAsSortExpression()
		{
			return new AsSortExpressionBuilder();
		}

		public AsSortExpressionBuilder withUid(final String uid)
		{
			asSortExpression.setUid(uid);
			return this;
		}

		public AsSortExpressionBuilder withIndexProperty(final String indexProperty)
		{
			asSortExpression.setExpression(indexProperty);
			return this;
		}

		public AsSortExpressionBuilder withOrder(final AsSortOrder order)
		{
			asSortExpression.setOrder(order);
			return this;
		}


		public AsSortExpression build()
		{
			return asSortExpression;
		}
	}
}
