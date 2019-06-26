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
package de.hybris.platform.cmsfacades.rendering.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.rendering.impl.DefaultRestrictionContextProvider;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRestrictionContextProviderTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	@Mock
	private RestrictionData restrictionData;

	@Mock
	private SessionService sessionService;

	@InjectMocks
	private DefaultRestrictionContextProvider restrictionContextProvider;

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenNoRestrictionInSession_WhenGetIsCalled_ThenItReturnsNull()
	{
		// WHEN
		RestrictionData result = restrictionContextProvider.getRestrictionInContext();

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenRestrictionInSession_WhenGetIsCalled_ThenItReturnsTheRestriction()
	{
		// GIVEN
		setValueInSession(restrictionData);

		// WHEN
		RestrictionData result = restrictionContextProvider.getRestrictionInContext();

		// THEN
		assertThat(result, is(restrictionData));
	}

	@Test
	public void givenNoRestrictionInSession_WhenSetMethodCalled_ThenItSetsARestrictionInTheSession()
	{
		// GIVEN
		verify(sessionService, never()).setAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM, restrictionData);

		// WHEN
		restrictionContextProvider.setRestrictionInContext(restrictionData);

		// THEN
		verify(sessionService, times(1)).setAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM, restrictionData);
	}

	@Test
	public void givenRestrictionInSession_WhenSetMethodCalled_ThenItOverridesRestrictionInTheSession()
	{
		// GIVEN
		setValueInSession(restrictionData);
		verify(sessionService, never()).setAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM, restrictionData);

		// WHEN
		restrictionContextProvider.setRestrictionInContext(restrictionData);

		// THEN
		verify(sessionService, times(1)).setAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM, restrictionData);
	}

	@Test
	public void WhenRemoveIsCalled_ThenItRemovesRestriction()
	{
		// GIVEN
		verify(sessionService, never()).removeAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM);

		// WHEN
		restrictionContextProvider.removeRestrictionFromContext();

		// THEN
		verify(sessionService, times(1)).removeAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM);
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void setValueInSession(RestrictionData data)
	{
		when(sessionService.getAttribute(CmsfacadesConstants.SESSION_RESTRICTION_CONTEXT_ITEM)).thenReturn(data);
	}
}
