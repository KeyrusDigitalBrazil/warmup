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
package com.hybris.backoffice.cockpitng.search.builder.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.GenericCondition;
import de.hybris.platform.core.GenericConditionList;
import de.hybris.platform.core.GenericQuery;
import de.hybris.platform.core.GenericSearchField;
import de.hybris.platform.core.GenericSubQueryCondition;
import de.hybris.platform.core.GenericValueCondition;
import de.hybris.platform.core.Operator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.core.model.type.RelationMetaTypeModel;
import de.hybris.platform.core.model.type.ViewTypeModel;
import de.hybris.platform.servicelayer.type.impl.DefaultTypeService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.cockpitng.search.data.SearchAttributeDescriptor;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


@RunWith(MockitoJUnitRunner.class)
public class GenericConditionQueryBuilderTest
{

	public static final String DATE_TYPE_ATTRIBUTE = "dateAttribute";
	public static final String TYPE_TO_TYPE_RELATION = "TypeToTypeRelation";
	private static final String TYPE_CODE = "Product";
	private final Set<Character> queryBuilderSeparators = Sets.newHashSet(ArrayUtils.toObject(new char[]
	{ ' ', ',', ';', '\t', '\n', '\r' }));

	@Mock
	private DefaultTypeService typeService;

	@InjectMocks
	private GenericConditionQueryBuilder queryBuilder;

	@Mock
	private RelationDescriptorModel relDescriptor;
	@Mock
	private RelationMetaTypeModel relTypeModel;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		queryBuilder.setSeparators(queryBuilderSeparators);
		when(relDescriptor.getIsSource()).thenReturn(true);
		when(relDescriptor.getRelationType()).thenReturn(relTypeModel);
		when(relTypeModel.getCode()).thenReturn(TYPE_TO_TYPE_RELATION);
	}

	@Test
	public void shouldSearchForDateWithEquals()
	{
		// given
		final SearchAttributeDescriptor attributeDescriptor = mock(SearchAttributeDescriptor.class);
		when(attributeDescriptor.getAttributeName()).thenReturn(DATE_TYPE_ATTRIBUTE);

		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(typeService.getAttributeDescriptor(TYPE_CODE, DATE_TYPE_ATTRIBUTE)).thenReturn(mock(AttributeDescriptorModel.class));

		final Date now = new Date();
		final Date midnightOfToday = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
		final Date midnightOfTomorrow = DateUtils.addDays(midnightOfToday, 1);

		// when
		final GenericCondition condition = queryBuilder.createSingleTokenCondition(searchQueryData, attributeDescriptor, now,
				ValueComparisonOperator.EQUALS);


		assertThat(condition instanceof GenericConditionList).isTrue();
		final GenericConditionList conditionList = (GenericConditionList) condition;

		assertThat(conditionList.getConditionList()).hasSize(2);
		assertThat(conditionList.getOperator()).isEqualTo(Operator.AND);

		assertThat(conditionList.getConditionList().get(0).getOperator()).isEqualTo(Operator.GREATER_OR_EQUAL);
		assertThat(((GenericValueCondition) conditionList.getConditionList().get(0)).getValue()).isEqualTo(midnightOfToday);

		assertThat(conditionList.getConditionList().get(1).getOperator()).isEqualTo(Operator.LESS);
		assertThat(((GenericValueCondition) conditionList.getConditionList().get(1)).getValue()).isEqualTo(midnightOfTomorrow);
	}

	@Test
	public void shouldSearchForExactDateWithEquals()
	{
		// given
		final SearchAttributeDescriptor attributeDescriptor = mock(SearchAttributeDescriptor.class);
		when(attributeDescriptor.getAttributeName()).thenReturn(DATE_TYPE_ATTRIBUTE);
		final HashMap<String, String> editorParameters = new HashMap<>();
		editorParameters.put(GenericConditionQueryBuilder.EDITOR_PARAM_EQUALS_COMPARES_EXACT_DATE, "true");
		when(attributeDescriptor.getEditorParameters()).thenReturn(editorParameters);

		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(typeService.getAttributeDescriptor(TYPE_CODE, DATE_TYPE_ATTRIBUTE)).thenReturn(mock(AttributeDescriptorModel.class));

		final Date now = new Date();

		// when
		final GenericCondition condition = queryBuilder.createSingleTokenCondition(searchQueryData, attributeDescriptor, now,
				ValueComparisonOperator.EQUALS);

		// then
		assertThat(condition instanceof GenericValueCondition).isTrue();
		final GenericValueCondition valueCondition = (GenericValueCondition) condition;

		assertThat(valueCondition.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(valueCondition.getValue()).isEqualTo(now);
	}

	@Test
	public void shouldReturnNotInSubQueryWithEmptyWhereClauseInsteadOfNotExists()
	{
		//given
		final RelationDescriptorModel relationDescriptor = mock(RelationDescriptorModel.class);
		final RelationMetaTypeModel relType = mock(RelationMetaTypeModel.class);
		when(relType.getCode()).thenReturn("RelTypeTestCode");
		when(relationDescriptor.getRelationType()).thenReturn(relType);

		//when
		final GenericCondition genericCondition = queryBuilder.createMany2ManyRelationCondition(relationDescriptor, TYPE_CODE,
				Operator.NOT_EXISTS, new Object());

		//then
		assertThat(genericCondition).isInstanceOf(GenericSubQueryCondition.class);
		final GenericSubQueryCondition subQuery = (GenericSubQueryCondition) genericCondition;
		assertThat(subQuery.getOperator()).isEqualTo(Operator.NOT_IN);
		assertThat(subQuery.getSubQuery().getCondition()).isNull();

		final GenericSearchField searchField = subQuery.getField();
		assertThat(searchField.getQualifier()).isEqualTo(ItemModel.PK);
		assertThat(searchField.getTypeIdentifier()).isEqualTo(TYPE_CODE);
	}


	@Test
	public void shouldSearchForExactDateInViewTypes()
	{
		// given
		final SearchAttributeDescriptor attributeDescriptor = mock(SearchAttributeDescriptor.class);
		when(attributeDescriptor.getAttributeName()).thenReturn(DATE_TYPE_ATTRIBUTE);
		final HashMap<String, String> editorParameters = new HashMap<>();
		editorParameters.put(GenericConditionQueryBuilder.EDITOR_PARAM_EQUALS_COMPARES_EXACT_DATE, "false");
		when(attributeDescriptor.getEditorParameters()).thenReturn(editorParameters);

		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		when(searchQueryData.getSearchType()).thenReturn(TYPE_CODE);
		when(typeService.getAttributeDescriptor(TYPE_CODE, DATE_TYPE_ATTRIBUTE)).thenReturn(mock(AttributeDescriptorModel.class));
		when(typeService.getComposedTypeForCode(TYPE_CODE)).thenReturn(mock(ViewTypeModel.class));

		final Date now = new Date();

		// when
		final GenericCondition condition = queryBuilder.createSingleTokenCondition(searchQueryData, attributeDescriptor, now,
				ValueComparisonOperator.EQUALS);

		// then
		assertThat(condition instanceof GenericValueCondition).isTrue();
		final GenericValueCondition valueCondition = (GenericValueCondition) condition;

		assertThat(valueCondition.getOperator()).isEqualTo(Operator.EQUAL);
		assertThat(valueCondition.getValue()).isEqualTo(now);
	}

	public void buildMany2ManyQueryNullDataInOperator()
	{

		//when
		final GenericQuery query = queryBuilder.buildMany2ManyQuery(relDescriptor, null, Operator.IN);

		//then
		assertThat(query.getCondition()).isNull();
		assertThat(query.getInitialTypeCode()).isEqualTo(TYPE_TO_TYPE_RELATION);

	}

	@Test
	public void buildMany2ManyQueryNullDataContainsOperator()
	{
		//when
		final GenericQuery query = queryBuilder.buildMany2ManyQuery(relDescriptor, null, Operator.CONTAINS);

		//then
		assertThat(query.getCondition()).isNull();
		assertThat(query.getInitialTypeCode()).isEqualTo(TYPE_TO_TYPE_RELATION);
	}

	@Test
	public void buildMany2ManyQueryNonNullEqualsExpected()
	{
		//given
		final Object value = new Object();

		//when
		final GenericQuery query = queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.EQUAL);

		//then
		assertThat(query.getCondition()).matches(condition -> Operator.EQUAL.equals(condition.getOperator()));
		assertThat(query.getCondition()).isInstanceOf(GenericValueCondition.class);
		assertThat(((GenericValueCondition) query.getCondition()).getValue()).isEqualTo(value);
		assertThat(query.getInitialTypeCode()).isEqualTo(TYPE_TO_TYPE_RELATION);
	}


	@Test
	public void buildMany2ManyQueryNonNullInExpected()
	{
		//given
		final Object value = new Object();

		//when
		final GenericQuery query = queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.IN);

		//then
		assertThat(query.getCondition()).matches(condition -> Operator.IN.equals(condition.getOperator()));
		assertThat(query.getCondition()).isInstanceOf(GenericValueCondition.class);
		assertThat(((GenericValueCondition) query.getCondition()).getValue()).isEqualTo(Collections.singletonList(value));
		assertThat(query.getInitialTypeCode()).isEqualTo(TYPE_TO_TYPE_RELATION);
	}

	@Test
	public void buildMany2ManyQueryNonNullNotInExpected()
	{
		//given
		final Object value = Collections.singleton(new Object());

		//when
		final GenericQuery query = queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.NOT_IN);

		//then
		assertThat(query.getCondition()).matches(condition -> Operator.IN.equals(condition.getOperator()));
		assertThat(query.getCondition()).isInstanceOf(GenericValueCondition.class);
		assertThat(((GenericValueCondition) query.getCondition()).getValue()).isEqualTo(value);
		assertThat(query.getInitialTypeCode()).isEqualTo(TYPE_TO_TYPE_RELATION);
	}


	@Test(expected = IllegalArgumentException.class)
	public void buildMany2ManyQueryEmptyCollectionNotInOperator()
	{
		//given
		final Object value = Collections.emptyList();

		//when
		queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.NOT_IN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildMany2ManyQueryEmptyCollectionInOperator()
	{
		//given
		final Object value = Collections.emptyList();

		//when
		queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.IN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildMany2ManyQueryEmptyCollectionEqualsOperator()
	{
		//given
		final Object value = Collections.emptyList();

		//when
		queryBuilder.buildMany2ManyQuery(relDescriptor, value, Operator.EQUAL);
	}
}
