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
package com.hybris.backoffice.widgets.searchadapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;
import com.hybris.cockpitng.tree.node.DynamicNode;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReferenceFilterAdvancedSearchInitializerTest
{

	private final ReferenceFilterAdvancedSearchInitializer initializer = new ReferenceFilterAdvancedSearchInitializer();

	private final DynamicNode node = new DynamicNode("myNode", null, 1);
	private final DynamicNode invalidNode = new DynamicNode("invalidNode", null, 1);
	private final PK pk = PK.fromLong(1234L);
	private final ItemModel itemModel = new ItemModel()
	{
		@Override
		public de.hybris.platform.core.PK getPk()
		{
			return pk;
		}
	};
	private final ItemModel invalidModel = new ItemModel()
	{
		@Override
		public String getItemtype()
		{
			return "invalidItemType";
		}
	};

	private AdvancedSearchData searchData;

	private static final String CONDITION_ATTRIBUTE_NAME = "itemTypePk";


	@Before
	public void setUp() throws Exception
	{
		initializer.setConditionFieldForTypeMap(ImmutableMap.of(itemModel.getItemtype(), CONDITION_ATTRIBUTE_NAME));
		node.setData(itemModel);
		invalidNode.setData(invalidModel);
		searchData = new AdvancedSearchData();
	}

	@Test
	public void shouldAddCondition() throws Exception
	{
		initializer.addSearchDataConditions(searchData, Optional.of(node));
		final List<SearchConditionData> fqConditions = searchData.getFilterQueryRawConditions(CONDITION_ATTRIBUTE_NAME);

		assertNotNull(fqConditions);
		assertEquals(1, fqConditions.size());
		assertEquals(ValueComparisonOperator.EQUALS, fqConditions.get(0).getOperator());
		assertEquals(pk.getLong(), fqConditions.get(0).getValue());
		assertEquals(CONDITION_ATTRIBUTE_NAME, fqConditions.get(0).getFieldType().getName());
	}

	@Test
	public void shouldNotAddInvalidCondition() throws Exception
	{
		initializer.addSearchDataConditions(searchData, Optional.of(invalidNode));
		final Set<String> fqFields = searchData.getFilterQueryFields();

		assertNotNull(fqFields);
		assertEquals(0, fqFields.size());
	}

}