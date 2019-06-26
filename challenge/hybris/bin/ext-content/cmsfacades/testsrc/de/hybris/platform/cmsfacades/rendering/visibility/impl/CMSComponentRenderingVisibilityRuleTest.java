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
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.cmsfacades.rendering.RestrictionContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSComponentRenderingVisibilityRuleTest
{
	@InjectMocks
	private CMSComponentRenderingVisibilityRule renderingVisibilityRule;

	@Mock
	private AbstractRestrictionModel restrictionModel;
	@Mock
	private AbstractCMSComponentModel component;
	@Mock
	private RestrictionContextProvider restrictionContextProvider;
	@Mock
	private RestrictionData restrictionData;
	@Mock
	private CMSRestrictionService cmsRestrictionService;

	@Before
	public void setUp()
	{
		when(cmsRestrictionService.evaluateCMSComponent(component, restrictionData)).thenReturn(true);
		when(restrictionContextProvider.getRestrictionInContext()).thenReturn(restrictionData);
		when(component.getRestrictions()).thenReturn(Arrays.asList(restrictionModel));
		when(component.getVisible()).thenReturn(true);
	}

	@Test
	public void shouldReturnFalseIfComponentIsInvisible()
	{
		// GIVEN
		makeComponentInvisible(component);

		// WHEN
		boolean result = renderingVisibilityRule.isVisible(component);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnFalseIfComponentIsRestricted()
	{
		// GIVEN
		restrictComponent(component);

		// WHEN
		boolean result = renderingVisibilityRule.isVisible(component);

		// THEN
		assertFalse(result);
	}

	@Test
	public void shouldReturnTrueIfComponentIsNotRestrictedAndIsVisible()
	{
		// GIVEN
		removeRestrictionsFromComponent(component);

		// WHEN
		boolean result = renderingVisibilityRule.isVisible(component);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnTrueIfVisiblityIsNullAndNoRestrictions()
	{
		// WHEN
		makeComponentVisibilityNull(component);

		// WHEN
		boolean result = renderingVisibilityRule.isVisible(component);

		// THEN
		assertTrue(result);
	}

	@Test
	public void shouldReturnTrueIfComponentIsVisibleAndNoApplicableRestrictions()
	{
		// WHEN
		boolean result = renderingVisibilityRule.isVisible(component);

		// THEN
		assertTrue(result);
	}

	protected void restrictComponent(AbstractCMSComponentModel component)
	{
		when(cmsRestrictionService.evaluateCMSComponent(component, restrictionData)).thenReturn(false);
	}

	protected void makeComponentInvisible(AbstractCMSComponentModel component)
	{
		when(component.getVisible()).thenReturn(false);
	}

	protected void makeComponentVisibilityNull(AbstractCMSComponentModel component)
	{
		when(component.getVisible()).thenReturn(null);
	}

	protected void removeRestrictionsFromComponent(AbstractCMSComponentModel component)
	{
		when(component.getRestrictions()).thenReturn(Collections.emptyList());
	}
}
