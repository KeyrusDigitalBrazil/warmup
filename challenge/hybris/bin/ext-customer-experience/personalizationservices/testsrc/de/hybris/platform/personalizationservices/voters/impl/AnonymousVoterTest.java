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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.stub.MockTimeService;
import de.hybris.platform.personalizationservices.voters.Vote;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class AnonymousVoterTest
{
	@Mock
	private UserModel user;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession session;

	@Mock
	private HttpServletResponse response;

	@Mock
	private UserService userService;

	private final SessionService sessionService = new MockSessionService()
	{
		@Override
		public <T extends Object> T getOrLoadAttribute(final String name, final SessionService.SessionAttributeLoader<T> loader)
		{
			T result = (T) getAttribute(name);

			if (result == null)
			{
				result = (T) getAttribute(name);
				if (result == null)
				{
					result = loader.load();
					setAttribute(name, result);
				}
			}
			return result;
		}
	};

	private final TimeService timeService = new MockTimeService();

	@Mock
	private CxConfigurationService cxConfigurationService;

	private Set<RecalculateAction> defaultActions, actions;

	private final AnonymousVoter voter = new AnonymousVoter();

	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);

		given(userService.getCurrentUser()).willReturn(user);
		given(request.getSession()).willReturn(session);

		given(cxConfigurationService.isIgnoreRecalcForAnonymous()).willReturn(Boolean.FALSE);

		defaultActions = Sets.newHashSet(RecalculateAction.RECALCULATE);
		given(cxConfigurationService.getDefaultActionsForAnonymous()).willReturn(defaultActions);
		actions = Sets.newHashSet(RecalculateAction.ASYNC_PROCESS);
		given(cxConfigurationService.getActionsForAnonymous()).willReturn(actions);




		voter.setCxConfigurationService(cxConfigurationService);
		voter.setUserService(userService);
		voter.setSessionService(sessionService);
		voter.setTimeService(timeService);
	}

	@Test
	public void shouldReturnDefaultVoteWhenNotAnonymous()
	{
		//given
		givenNewSession(false);
		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenNotAnonymousAndNewSession()
	{
		//given
		givenNewSession(true);
		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenNotAnonymousAndIgnoreAnonymous()
	{
		//given
		givenNewSession(false);
		givenAnonymousUser(false);
		givenIgnoreAnonymous();

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnIgnoreWhenIgnoreAnonymous()
	{
		//given
		givenNewSession(false);
		givenAnonymousUser(true);
		givenIgnoreAnonymous();

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));
		assertEquals(vote.getRecalculateActions().iterator().next(), RecalculateAction.IGNORE);
	}

	@Test
	public void shouldReturnIgnoreWhenIgnoreAnonymousAndNewSession()
	{
		//given
		givenNewSession(true);
		givenAnonymousUser(true);
		givenIgnoreAnonymous();

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));
		assertEquals(RecalculateAction.IGNORE, vote.getRecalculateActions().iterator().next());
	}

	@Test
	public void shouldReturnDefaultActionsForNewSession()
	{
		//given
		givenNewSession(true);
		givenAnonymousUser(true);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(defaultActions, vote.getRecalculateActions());
	}

	@Test
	public void shouldReturnActionsForEveryRequest()
	{
		//given
		givenNewSession(false);
		givenAnonymousUser(true);
		givenMinRequestNumber(0);
		givenMinTimeInterval(0L);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(actions, vote.getRecalculateActions());

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(actions, vote.getRecalculateActions());

		//given
		givenTimePassed(1000);

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(actions, vote.getRecalculateActions());
	}

	private void givenNewSession(final boolean isNew)
	{
		given(Boolean.valueOf(session.isNew())).willReturn(Boolean.valueOf(isNew));
	}

	private void givenAnonymousUser(final boolean isAnonymous)
	{
		given(Boolean.valueOf(userService.isAnonymousUser(user))).willReturn(Boolean.valueOf(isAnonymous));
	}

	private void givenIgnoreAnonymous()
	{
		given(cxConfigurationService.isIgnoreRecalcForAnonymous()).willReturn(Boolean.TRUE);
	}

	private void givenMinRequestNumber(final int number)
	{
		given(cxConfigurationService.getMinRequestNumberForAnonymousActions()).willReturn(Integer.valueOf(number));
	}

	private void givenMinTimeInterval(final long number)
	{
		given(cxConfigurationService.getMinTimeForAnonymousActions()).willReturn(Long.valueOf(number));
	}

	private void givenTimePassed(final long time)
	{
		final long newTime = timeService.getCurrentTime().getTime() + time;
		timeService.setCurrentTime(new Date(newTime));
	}


}
