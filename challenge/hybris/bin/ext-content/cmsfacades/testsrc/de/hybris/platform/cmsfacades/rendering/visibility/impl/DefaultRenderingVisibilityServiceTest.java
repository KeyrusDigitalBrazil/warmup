/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.rendering.visibility.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityRule;
import de.hybris.platform.core.model.ItemModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRenderingVisibilityServiceTest
{
	@InjectMocks
	private DefaultRenderingVisibilityService renderingVisibilityService;

	@Mock
	private RenderingVisibilityRule<ItemModel> visibilityRule1;
	@Mock
	private Predicate<ItemModel> visibilityRulePredicate1;

	@Mock
	private RenderingVisibilityRule<ItemModel> visibilityRule2;
	@Mock
	private Predicate<ItemModel> visibilityRulePredicate2;

	@Mock
	private List<RenderingVisibilityRule<ItemModel>> renderingVisibilityRules;

	@Mock
	private ItemModel itemModel;

	@Before
	public void start()
	{
		renderingVisibilityRules = Arrays.asList(visibilityRule1, visibilityRule2);
		renderingVisibilityService.setRenderingVisibilityRules(renderingVisibilityRules);

		when(visibilityRule1.restrictedBy()).thenReturn(visibilityRulePredicate1);
		when(visibilityRule2.restrictedBy()).thenReturn(visibilityRulePredicate2);
	}

	@Test
	public void shouldReturnFalseIfItemIsNull()
	{
		// WHEN
		final boolean result = renderingVisibilityService.isVisible(null);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueIfRulesCanNotBeApplied()
	{
		// GIVEN
		when(visibilityRulePredicate1.test(itemModel)).thenReturn(false);
		when(visibilityRulePredicate2.test(itemModel)).thenReturn(false);

		// WHEN
		final boolean result = renderingVisibilityService.isVisible(itemModel);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnFalseIfRuleReturnsFalse()
	{
		// GIVEN
		when(visibilityRulePredicate1.test(itemModel)).thenReturn(true);
		when(visibilityRulePredicate2.test(itemModel)).thenReturn(false);
		when(visibilityRule1.isVisible(itemModel)).thenReturn(false);

		// WHEN
		final boolean result = renderingVisibilityService.isVisible(itemModel);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueIfRuleReturnsTrue()
	{
		// GIVEN
		when(visibilityRulePredicate1.test(itemModel)).thenReturn(true);
		when(visibilityRulePredicate2.test(itemModel)).thenReturn(false);
		when(visibilityRule1.isVisible(itemModel)).thenReturn(true);

		// WHEN
		final boolean result = renderingVisibilityService.isVisible(itemModel);

		// THEN
		assertTrue(result);
	}
}

