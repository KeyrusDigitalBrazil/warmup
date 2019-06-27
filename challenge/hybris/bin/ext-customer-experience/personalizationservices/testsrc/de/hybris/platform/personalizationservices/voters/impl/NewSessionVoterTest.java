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
/**
 *
 */
package de.hybris.platform.personalizationservices.voters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.voters.Vote;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class NewSessionVoterTest
{
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private HttpServletResponse response;

	@Mock
	private CxConfigurationService cxConfigurationService;

	private Set<RecalculateAction> defaultActions, actions;

	private final NewSessionVoter voter = new NewSessionVoter();

	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);

		when(request.getSession()).thenReturn(session);

		defaultActions = Sets.newHashSet(RecalculateAction.RECALCULATE);
		when(cxConfigurationService.getDefaultActionsForAnonymous()).thenReturn(defaultActions);

		voter.setCxConfigurationService(cxConfigurationService);
	}

	@Test
	public void testGetVoteForOldSession()
	{
		//given
		when(Boolean.valueOf(session.isNew())).thenReturn(Boolean.FALSE);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void testGetVoteForNewSession()
	{
		//given
		when(Boolean.valueOf(session.isNew())).thenReturn(Boolean.TRUE);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(defaultActions, vote.getRecalculateActions());
	}
}
