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
package com.hybris.backoffice.cockpitng.dataaccess.facades.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryConditionList;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformFieldSearchFacadeStrategyTest
{
	@Spy
	private DefaultPlatformFieldSearchFacadeStrategy fieldSearchFacadeStrategy;

	@Mock
	private SearchQueryCondition condition;
	@Mock
	private SearchQueryConditionList conditionList;

	@Test
	public void isValidQueryData()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.singletonList(new Object()));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isTrue();
	}

	@Test
	public void isValidQueryWhenDataIsSingletonCollectionWithOneNull()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.singletonList(null));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsCollectionWithOneNull()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Lists.newArrayList(new Object(), null, new Object()));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsNull()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(null);

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsEmptyCollection()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.emptyList());

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsCollectionWhichContainsEmptyCollection()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.singleton(Collections.emptyList()));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsCollectionWhichContainsNull()
	{
		//given
		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.singleton(null));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(condition);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsCollectionWithNotEmptyObject()
	{
		//given
		when(conditionList.getOperator()).thenReturn(ValueComparisonOperator.AND);
		when(conditionList.getValue()).thenReturn(null);

		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.singleton(new Object()));
		when(conditionList.getConditions()).thenReturn(Lists.newArrayList(condition));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(conditionList);

		//then
		assertThat(validQueryCondition).isTrue();
	}

	@Test
	public void isValidQueryWhenDataIsNestedCollectionWithNull()
	{
		//given
		when(conditionList.getOperator()).thenReturn(ValueComparisonOperator.AND);
		when(conditionList.getValue()).thenReturn(null);

		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(null);
		when(conditionList.getConditions()).thenReturn(Lists.newArrayList(condition));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(conditionList);

		//then
		assertThat(validQueryCondition).isFalse();
	}

	@Test
	public void isValidQueryWhenDataIsNestedCollectionWithEmptyCollection()
	{
		//given
		when(conditionList.getOperator()).thenReturn(ValueComparisonOperator.AND);
		when(conditionList.getValue()).thenReturn(null);

		when(condition.getOperator()).thenReturn(ValueComparisonOperator.CONTAINS);
		when(condition.getValue()).thenReturn(Collections.emptyList());
		when(conditionList.getConditions()).thenReturn(Lists.newArrayList(condition));

		//when
		final boolean validQueryCondition = fieldSearchFacadeStrategy.isValidQueryCondition(conditionList);

		//then
		assertThat(validQueryCondition).isFalse();
	}
}
