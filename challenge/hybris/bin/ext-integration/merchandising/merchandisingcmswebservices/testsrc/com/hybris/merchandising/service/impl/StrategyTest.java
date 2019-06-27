/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import com.hybris.merchandising.dto.DropdownElement;
import com.hybris.merchandising.model.Category;
import com.hybris.merchandising.model.MixAssociation;
import com.hybris.merchandising.model.Strategy;


/**
 * StrategyTest is an abstract class which contains utility methods for creating mock Strategy entities as well as
 * asserting that the generated Strategy is as expected.
 */
public abstract class StrategyTest
{
	public static final String STRAT_ID = "Strat";
	public static final String STRAT_NAME = "StratName";
	public static final Boolean STRAT_STATUS = Boolean.TRUE;
	public static final String STRAT_DESC = "Description";
	public static final String MIX_ASSOCIATION_ID = "Assoc";
	public static final String MIXCARD_ID = "MixcardID";
	public static final String TIME_START = "TimeStart";
	public static final String TIME_END = "TimeEnd";
	public static final String CATEGORY = "Category";

	/**
	 * verifyStrategy is a method for verifying that the returned {@link Strategy} is as expected.
	 */
	protected void verifyDropDown(final int number, final DropdownElement element)
	{
		Assert.assertNotNull("Expected Strategy to not be null", element);
		Assert.assertEquals("Expected strategy ID to be", STRAT_ID + number, element.getId());
		Assert.assertEquals("Expected strategy name to be", STRAT_NAME + number, element.getLabel());
	}

	protected void verifyStrategy(final int number, final Strategy strategy)
	{
		Assert.assertNotNull("Expected Strategy to not be null", strategy);
		Assert.assertEquals("Expected strategy ID to be", STRAT_ID + number, strategy.getId());
		Assert.assertEquals("Expected strategy name to be", STRAT_NAME + number, strategy.getName());
		Assert.assertEquals("Expected strategy name to be", STRAT_STATUS, strategy.getLive());
		Assert.assertEquals("Expected strategy description to be", STRAT_DESC + number, strategy.getDescription());

		Assert.assertEquals("Expected strategy to have a mix association", 1, strategy.getMixAssociations().size());
		final MixAssociation association = strategy.getMixAssociations().get(0);
		Assert.assertEquals("Expected mixassociation ID to be ", MIX_ASSOCIATION_ID + number, association.getId());
		Assert.assertEquals("Expected mixcard ID to be", MIXCARD_ID + number, association.getMixcardId());
		Assert.assertEquals("Expected time start to be", TIME_START, association.getTimeStart());
		Assert.assertEquals("Expected timeend to be ", TIME_END, association.getTimeEnd());

		Assert.assertEquals("Expected association to have a category", 1, association.getCategories().size());
		final Category category = association.getCategories().get(0);
		Assert.assertEquals("Expected category ID to be ", CATEGORY + number, category.getId());
		Assert.assertEquals("Expected category name to be", CATEGORY + number, category.getName());
	}

	/**
	 * getMockStrategies is a method for generating a list of mock {@link Strategy} objects.
	 *
	 * @param numberToCreate
	 *           the number to create.
	 */
	protected List<Strategy> getMockStrategies(final int numberToCreate)
	{
		final List<Strategy> strategies = new ArrayList<>();
		for (int i = 0; i < numberToCreate; i++)
		{
			strategies.add(getMockStrategy(i));
		}
		return strategies;
	}

	/**
	 * getMockStrategy is a method for creating a single mock {@link Strategy} object.
	 *
	 * @param identifier
	 *           an identifier for the strategy being created.
	 */
	protected Strategy getMockStrategy(final int identifier)
	{
		final Category category = new Category();
		category.setId(CATEGORY + identifier);
		category.setName(CATEGORY + identifier);
		final List<Category> categories = new ArrayList<>();
		categories.add(category);

		final MixAssociation association = new MixAssociation();
		association.setId(MIX_ASSOCIATION_ID + identifier);
		association.setMixcardId(MIXCARD_ID + identifier);
		association.setTimeStart(TIME_START);
		association.setTimeEnd(TIME_END);
		association.setCategories(categories);

		final List<MixAssociation> associations = new ArrayList<>();
		associations.add(association);

		final Strategy strategy = new Strategy();
		strategy.setMixAssociations(associations);
		strategy.setId(STRAT_ID + identifier);
		strategy.setLive(STRAT_STATUS);
		strategy.setName(STRAT_NAME + identifier);
		strategy.setDescription(STRAT_DESC + identifier);
		return strategy;
	}
}
