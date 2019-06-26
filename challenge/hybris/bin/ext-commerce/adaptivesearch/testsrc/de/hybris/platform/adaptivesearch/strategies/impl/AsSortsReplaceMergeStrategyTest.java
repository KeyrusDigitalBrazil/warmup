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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsSortConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedSort;
import de.hybris.platform.adaptivesearch.data.AsPromotedSort;
import de.hybris.platform.adaptivesearch.data.AsSort;
import de.hybris.platform.adaptivesearch.data.AsSortExpression;
import de.hybris.platform.adaptivesearch.enums.AsSortOrder;
import de.hybris.platform.adaptivesearch.util.MergeMap;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class AsSortsReplaceMergeStrategyTest extends AbstractAsSortsMergeStrategyTest
{
	private AsSortsReplaceMergeStrategy mergeStrategy;

	@Before
	public void createMergeStrategy()
	{
		mergeStrategy = new AsSortsReplaceMergeStrategy();
		mergeStrategy.setAsSearchProfileResultFactory(getAsSearchProfileResultFactory());
	}

	@Test
	public void mergePromotedSorts()
	{
		// given
		final AsSortExpression promotedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_1).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_2).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort1 = SortBuilder.anAsPromotedSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(promotedSort1Expression1, promotedSort1Expression2)).build();

		final AsSortExpression promotedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_4).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort2 = SortBuilder.anAsPromotedSort().withCode(SORT2_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(promotedSort2Expression1, promotedSort2Expression2)).build();

		getTarget().getPromotedSorts().put(promotedSort1.getCode(), createConfigurationHolder(promotedSort1));
		getSource().getPromotedSorts().put(promotedSort2.getCode(), createConfigurationHolder(promotedSort2));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getPromotedSorts().size());
		final List<AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration>> promotedSorts = ((MergeMap<String, AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration>>) getTarget()
				.getPromotedSorts()).orderedValues();

		final AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration> promotedSort2Holder = promotedSorts.get(0);
		assertSame(promotedSort2, promotedSort2Holder.getConfiguration());
		assertThat(promotedSort2Holder.getConfiguration().getExpressions()).containsExactly(promotedSort2Expression1,
				promotedSort2Expression2);
	}

	@Test
	public void mergePromotedSortsWithDuplicates()
	{
		// given
		final AsSortExpression promotedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_1).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_2).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort1 = SortBuilder.anAsPromotedSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(promotedSort1Expression1, promotedSort1Expression2)).build();

		final AsSortExpression promotedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_4).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort2 = SortBuilder.anAsPromotedSort().withCode(SORT2_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(promotedSort2Expression1, promotedSort2Expression2)).build();

		final AsSortExpression sort1Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_5)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort1Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_6)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort1 = SortBuilder.anAsSort().withCode(SORT1_CODE).withUid(UID_3)
				.withExpressions(Arrays.asList(sort1Expression1, sort1Expression2)).build();

		final AsSortExpression sort2Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_7)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort2Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_8)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort2 = SortBuilder.anAsSort().withCode(SORT3_CODE).withUid(UID_4)
				.withExpressions(Arrays.asList(sort2Expression1, sort2Expression2)).build();

		final AsSortExpression excludedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_9).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_10).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort1 = SortBuilder.anAsExcludedSort().withCode(SORT2_CODE).withUid(UID_5)
				.withExpressions(Arrays.asList(excludedSort1Expression1, excludedSort1Expression2)).build();

		final AsSortExpression excludedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_11).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_12).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort2 = SortBuilder.anAsExcludedSort().withCode(SORT4_CODE).withUid(UID_6)
				.withExpressions(Arrays.asList(excludedSort2Expression1, excludedSort2Expression2)).build();

		final AsSortExpression excludedSort3Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_13).withOrder(AsSortOrder.ASCENDING).build();
		final AsExcludedSort excludedSort3 = SortBuilder.anAsExcludedSort().withCode(SORT5_CODE).withUid(UID_7)
				.withExpressions(Arrays.asList(excludedSort3Expression1)).build();


		getTarget().getSorts().put(sort1.getCode(), createConfigurationHolder(sort1));
		getTarget().getSorts().put(sort2.getCode(), createConfigurationHolder(sort2));
		getTarget().getExcludedSorts().put(excludedSort1.getCode(), createConfigurationHolder(excludedSort1));
		getTarget().getExcludedSorts().put(excludedSort2.getCode(), createConfigurationHolder(excludedSort2));

		getSource().getPromotedSorts().put(promotedSort1.getCode(), createConfigurationHolder(promotedSort1));
		getSource().getPromotedSorts().put(promotedSort2.getCode(), createConfigurationHolder(promotedSort2));
		getSource().getExcludedSorts().put(excludedSort3.getCode(), createConfigurationHolder(excludedSort3));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getPromotedSorts().size());
		final List<AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration>> promotedSorts = ((MergeMap<String, AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration>>) getTarget()
				.getPromotedSorts()).orderedValues();

		final AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration> promotedSort1Holder = promotedSorts.get(0);
		assertSame(promotedSort1, promotedSort1Holder.getConfiguration());
		assertThat(promotedSort1Holder.getConfiguration().getExpressions()).containsExactly(promotedSort1Expression1,
				promotedSort1Expression2);

		final AsConfigurationHolder<AsPromotedSort, AbstractAsSortConfiguration> promotedSort2Holder = promotedSorts.get(1);
		assertSame(promotedSort2, promotedSort2Holder.getConfiguration());
		assertThat(promotedSort2Holder.getConfiguration().getExpressions()).containsExactly(promotedSort2Expression1,
				promotedSort2Expression2);

		assertEquals(0, getTarget().getSorts().size());

		assertEquals(1, getTarget().getExcludedSorts().size());
		final List<AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>> excludedSorts = ((MergeMap<String, AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>>) getTarget()
				.getExcludedSorts()).orderedValues();

		final AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration> excludedSort3Holder = excludedSorts.get(0);
		assertSame(excludedSort3, excludedSort3Holder.getConfiguration());
		assertThat(excludedSort3Holder.getConfiguration().getExpressions()).containsExactly(excludedSort3Expression1);
	}

	@Test
	public void mergeSorts()
	{
		// given
		final AsSortExpression sort1Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_1)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort1Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_2)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort1 = SortBuilder.anAsSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(sort1Expression1, sort1Expression2)).build();

		final AsSortExpression sort2Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_3)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort2Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_4)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort2 = SortBuilder.anAsSort().withCode(SORT2_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(sort2Expression1, sort2Expression2)).build();

		getTarget().getSorts().put(sort1.getCode(), createConfigurationHolder(sort1));
		getSource().getSorts().put(sort2.getCode(), createConfigurationHolder(sort2));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getSorts().size());
		final List<AsConfigurationHolder<AsSort, AbstractAsSortConfiguration>> sorts = ((MergeMap<String, AsConfigurationHolder<AsSort, AbstractAsSortConfiguration>>) getTarget()
				.getSorts()).orderedValues();

		final AsConfigurationHolder<AsSort, AbstractAsSortConfiguration> sort2Holder = sorts.get(0);
		assertSame(sort2, sort2Holder.getConfiguration());
		assertThat(sort2Holder.getConfiguration().getExpressions()).contains(sort2Expression1, sort2Expression2);
	}

	@Test
	public void mergeSortsWithDuplicates()
	{
		// given
		final AsSortExpression promotedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_1).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_2).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort1 = SortBuilder.anAsPromotedSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(promotedSort1Expression1, promotedSort1Expression2)).build();

		final AsSortExpression promotedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_4).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort2 = SortBuilder.anAsPromotedSort().withCode(SORT3_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(promotedSort2Expression1, promotedSort2Expression2)).build();

		final AsSortExpression sort1Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_5)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSort sort1 = SortBuilder.anAsSort().withCode(SORT1_CODE).withUid(UID_3)
				.withExpressions(Arrays.asList(sort1Expression1)).build();

		final AsSortExpression sort2Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_6)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort2Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_7)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort2 = SortBuilder.anAsSort().withCode(SORT2_CODE).withUid(UID_4)
				.withExpressions(Arrays.asList(sort2Expression1, sort2Expression2)).build();

		final AsSortExpression sort3Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_6)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSort sort3 = SortBuilder.anAsSort().withCode(SORT5_CODE).withUid(UID_5)
				.withExpressions(Arrays.asList(sort3Expression1)).build();

		final AsSortExpression excludedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_8).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_9).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort1 = SortBuilder.anAsExcludedSort().withCode(SORT2_CODE).withUid(UID_6)
				.withExpressions(Arrays.asList(excludedSort1Expression1, excludedSort1Expression2)).build();

		final AsSortExpression excludedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_10).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_11).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort2 = SortBuilder.anAsExcludedSort().withCode(SORT4_CODE).withUid(UID_7)
				.withExpressions(Arrays.asList(excludedSort2Expression1, excludedSort2Expression2)).build();

		getTarget().getPromotedSorts().put(promotedSort1.getCode(), createConfigurationHolder(promotedSort1));
		getTarget().getPromotedSorts().put(promotedSort2.getCode(), createConfigurationHolder(promotedSort2));
		getTarget().getExcludedSorts().put(excludedSort1.getCode(), createConfigurationHolder(excludedSort1));
		getTarget().getExcludedSorts().put(excludedSort2.getCode(), createConfigurationHolder(excludedSort2));
		getTarget().getSorts().put(sort3.getCode(), createConfigurationHolder(sort3));

		getSource().getSorts().put(sort1.getCode(), createConfigurationHolder(sort1));
		getSource().getSorts().put(sort2.getCode(), createConfigurationHolder(sort2));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(0, getTarget().getPromotedSorts().size());

		assertEquals(2, getTarget().getSorts().size());
		final List<AsConfigurationHolder<AsSort, AbstractAsSortConfiguration>> sorts = ((MergeMap<String, AsConfigurationHolder<AsSort, AbstractAsSortConfiguration>>) getTarget()
				.getSorts()).orderedValues();

		final AsConfigurationHolder<AsSort, AbstractAsSortConfiguration> sort1Holder = sorts.get(0);
		assertSame(sort1, sort1Holder.getConfiguration());
		assertThat(sort1Holder.getConfiguration().getExpressions()).containsExactly(sort1Expression1);

		final AsConfigurationHolder<AsSort, AbstractAsSortConfiguration> sort2Holder = sorts.get(1);
		assertSame(sort2, sort2Holder.getConfiguration());
		assertThat(sort2Holder.getConfiguration().getExpressions()).containsExactly(sort2Expression1, sort2Expression2);

		assertEquals(0, getTarget().getExcludedSorts().size());
	}

	@Test
	public void mergeExcludedSorts()
	{
		// given
		final AsSortExpression excludedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_1).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_2).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort1 = SortBuilder.anAsExcludedSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(excludedSort1Expression1, excludedSort1Expression2)).build();

		final AsSortExpression excludedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_4).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort2 = SortBuilder.anAsExcludedSort().withCode(SORT2_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(excludedSort2Expression1, excludedSort2Expression2)).build();

		getTarget().getExcludedSorts().put(excludedSort1.getCode(), createConfigurationHolder(excludedSort1));
		getSource().getExcludedSorts().put(excludedSort2.getCode(), createConfigurationHolder(excludedSort2));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getExcludedSorts().size());
		final List<AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>> excludedSorts = ((MergeMap<String, AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>>) getTarget()
				.getExcludedSorts()).orderedValues();

		final AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration> excludedSort2Holder = excludedSorts.get(0);
		assertSame(excludedSort2, excludedSort2Holder.getConfiguration());
		assertThat(excludedSort2Holder.getConfiguration().getExpressions()).containsExactly(excludedSort2Expression1,
				excludedSort2Expression2);
	}

	@Test
	public void mergeExcludedSortsWithDuplicates()
	{
		// given
		final AsSortExpression promotedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_1).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_2).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort1 = SortBuilder.anAsPromotedSort().withCode(SORT1_CODE).withUid(UID_1)
				.withExpressions(Arrays.asList(promotedSort1Expression1, promotedSort1Expression2)).build();

		final AsSortExpression promotedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression promotedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_4).withOrder(AsSortOrder.DESCENDING).build();
		final AsPromotedSort promotedSort2 = SortBuilder.anAsPromotedSort().withCode(SORT3_CODE).withUid(UID_2)
				.withExpressions(Arrays.asList(promotedSort2Expression1, promotedSort2Expression2)).build();

		final AsSortExpression promotedSort3Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_3).withOrder(AsSortOrder.ASCENDING).build();
		final AsPromotedSort promotedSort3 = SortBuilder.anAsPromotedSort().withCode(SORT5_CODE).withUid(UID_3)
				.withExpressions(Arrays.asList(promotedSort3Expression1)).build();

		final AsSortExpression sort1Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_5)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort1Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_6)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort1 = SortBuilder.anAsSort().withCode(SORT2_CODE).withUid(UID_4)
				.withExpressions(Arrays.asList(sort1Expression1, sort1Expression2)).build();

		final AsSortExpression sort2Expression1 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_7)
				.withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression sort2Expression2 = AsSortExpressionBuilder.anAsSortExpression().withIndexProperty(INDEX_PROPERTY_8)
				.withOrder(AsSortOrder.DESCENDING).build();
		final AsSort sort2 = SortBuilder.anAsSort().withCode(SORT4_CODE).withUid(UID_5)
				.withExpressions(Arrays.asList(sort2Expression1, sort2Expression2)).build();

		final AsSortExpression excludedSort1Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_9).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort1Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_10).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort1 = SortBuilder.anAsExcludedSort().withCode(SORT1_CODE).withUid(UID_6)
				.withExpressions(Arrays.asList(excludedSort1Expression1, excludedSort1Expression2)).build();

		final AsSortExpression excludedSort2Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_11).withOrder(AsSortOrder.ASCENDING).build();
		final AsSortExpression excludedSort2Expression2 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_12).withOrder(AsSortOrder.DESCENDING).build();
		final AsExcludedSort excludedSort2 = SortBuilder.anAsExcludedSort().withCode(SORT2_CODE).withUid(UID_7)
				.withExpressions(Arrays.asList(excludedSort2Expression1, excludedSort2Expression2)).build();

		final AsSortExpression excludedSort3Expression1 = AsSortExpressionBuilder.anAsSortExpression()
				.withIndexProperty(INDEX_PROPERTY_11).withOrder(AsSortOrder.ASCENDING).build();
		final AsExcludedSort excludedSort3 = SortBuilder.anAsExcludedSort().withCode(SORT6_CODE).withUid(UID_8)
				.withExpressions(Arrays.asList(excludedSort3Expression1)).build();

		getTarget().getSorts().put(sort1.getCode(), createConfigurationHolder(sort1));
		getTarget().getSorts().put(sort2.getCode(), createConfigurationHolder(sort2));
		getTarget().getPromotedSorts().put(promotedSort1.getCode(), createConfigurationHolder(promotedSort1));
		getTarget().getPromotedSorts().put(promotedSort2.getCode(), createConfigurationHolder(promotedSort2));
		getTarget().getExcludedSorts().put(excludedSort3.getCode(), createConfigurationHolder(excludedSort3));

		getSource().getExcludedSorts().put(excludedSort1.getCode(), createConfigurationHolder(excludedSort1));
		getSource().getExcludedSorts().put(excludedSort2.getCode(), createConfigurationHolder(excludedSort2));
		getTarget().getPromotedSorts().put(promotedSort3.getCode(), createConfigurationHolder(promotedSort3));

		// when
		mergeStrategy.mergeSorts(getSource(), getTarget());

		// then
		assertEquals(0, getTarget().getPromotedSorts().size());

		assertEquals(0, getTarget().getSorts().size());

		assertEquals(2, getTarget().getExcludedSorts().size());
		final List<AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>> excludedSorts = ((MergeMap<String, AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration>>) getTarget()
				.getExcludedSorts()).orderedValues();

		final AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration> excludedSort1Holder = excludedSorts.get(0);
		assertSame(excludedSort1, excludedSort1Holder.getConfiguration());
		assertThat(excludedSort1Holder.getConfiguration().getExpressions()).containsExactly(excludedSort1Expression1,
				excludedSort1Expression2);

		final AsConfigurationHolder<AsExcludedSort, AbstractAsSortConfiguration> excludedSort2Holder = excludedSorts.get(1);
		assertSame(excludedSort2, excludedSort2Holder.getConfiguration());
		assertThat(excludedSort2Holder.getConfiguration().getExpressions()).containsExactly(excludedSort2Expression1,
				excludedSort2Expression2);
	}
}