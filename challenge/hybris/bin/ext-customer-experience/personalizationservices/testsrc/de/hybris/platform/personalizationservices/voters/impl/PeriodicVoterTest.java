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
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.enums.CxUserType;
import de.hybris.platform.personalizationservices.model.config.CxPeriodicVoterConfigModel;
import de.hybris.platform.personalizationservices.stub.MockTimeService;
import de.hybris.platform.personalizationservices.voters.Vote;
import de.hybris.platform.servicelayer.session.MockSessionService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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
public class PeriodicVoterTest
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

	private final SessionService sessionService = new MockSessionService();


	private final TimeService timeService = new MockTimeService();

	@Mock
	private CxConfigurationService cxConfigurationService;

	private Set<RecalculateAction> allBasicActions;

	private final PeriodicVoter voter = new PeriodicVoter();

	private Set<CxPeriodicVoterConfigModel> createConfigurationAllBasicActions(final CxUserType userType)
	{
		final Set<CxPeriodicVoterConfigModel> periodicVoterConfigModels = new HashSet<>();

		periodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("recalculate", 0, Sets.newHashSet("RECALCULATE"), (long) 0, userType));
		periodicVoterConfigModels.add(createPeriodicVoterConfiguration("ignore", 0, Sets.newHashSet("IGNORE"), (long) 0, userType));
		periodicVoterConfigModels.add(createPeriodicVoterConfiguration("loadload", 0, Sets.newHashSet("LOAD"), (long) 0, userType));
		periodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("async_process", 0, Sets.newHashSet("ASYNC_PROCESS"), (long) 0, userType));
		periodicVoterConfigModels.add(createPeriodicVoterConfiguration("update", 0, Sets.newHashSet("UPDATE"), (long) 0, userType));

		return periodicVoterConfigModels;
	}

	private CxPeriodicVoterConfigModel createPeriodicVoterConfiguration(final String code, final Integer userMinRequestNumber,
			final Set<String> actionsList, final Long userMinTime, final CxUserType userType)
	{
		final CxPeriodicVoterConfigModel periodicVoterConfigModel = new CxPeriodicVoterConfigModel();
		periodicVoterConfigModel.setCode(code);
		periodicVoterConfigModel.setUserMinRequestNumber(userMinRequestNumber);
		periodicVoterConfigModel.setUserMinTime(userMinTime);
		periodicVoterConfigModel.setActions(actionsList);
		periodicVoterConfigModel.setUserType(userType);
		return periodicVoterConfigModel;
	}

	@Before
	public void setupMocks()
	{
		MockitoAnnotations.initMocks(this);



		given(userService.getCurrentUser()).willReturn(user);

		allBasicActions = Sets.newHashSet(RecalculateAction.ASYNC_PROCESS,RecalculateAction.IGNORE, RecalculateAction.RECALCULATE,
				RecalculateAction.LOAD, RecalculateAction.UPDATE);

		voter.setCxConfigurationService(cxConfigurationService);
		voter.setUserService(userService);
		voter.setSessionService(sessionService);
		voter.setTimeService(timeService);

		when(cxConfigurationService.getPeriodicVoterConfigurations())
				.thenReturn(createConfigurationAllBasicActions(CxUserType.ALL));
	}

	@Test
	public void shouldReturnDefaultVoteWhenNoConfigurations()
	{
		//given
		givenAnonymousUser(false);
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(Collections.emptySet());

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenNoProperUserTypeConfigurationsRegistered()
	{
		//given
		givenAnonymousUser(false);
		when(cxConfigurationService.getPeriodicVoterConfigurations())
				.thenReturn(createConfigurationAllBasicActions(CxUserType.ANONYMOUS));

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenNoProperUserTypeConfigurationsAnonymous()
	{
		//given
		givenAnonymousUser(true);
		when(cxConfigurationService.getPeriodicVoterConfigurations())
				.thenReturn(createConfigurationAllBasicActions(CxUserType.REGISTERED));

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenOnlyOneNotExistingAction()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels.add(createPeriodicVoterConfiguration("not_existing_action", 0,
				Sets.newHashSet("NOT_EXISTING_ACTION"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnOnlyExistingActions()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = createConfigurationAllBasicActions(CxUserType.ALL);
		testPeriodicVoterConfigModels.add(createPeriodicVoterConfiguration("not_existing_action", 0,
				Sets.newHashSet("NOT_EXISTING_ACTION"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(allBasicActions.size()));
		assertEquals(allBasicActions, vote.getRecalculateActions());
	}

	@Test
	public void shouldReturnAllBasicActions()
	{
		//given
		when(cxConfigurationService.getPeriodicVoterConfigurations())
				.thenReturn(createConfigurationAllBasicActions(CxUserType.ALL));
		givenAnonymousUser(true);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertEquals(allBasicActions, vote.getRecalculateActions());
	}

	@Test
	public void shouldReturnOnlyUniqueActions()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = createConfigurationAllBasicActions(CxUserType.ALL);
		testPeriodicVoterConfigModels.add(createPeriodicVoterConfiguration("duplicatedActions", 0,
				Sets.newHashSet("UPDATE", "RECALCULATE", "LOAD", "ASYNC_PROCESS", "IGNORE"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(allBasicActions.size()));
		assertEquals(allBasicActions, vote.getRecalculateActions());
	}

	@Test
	public void shouldReturnActionForRegisteredUser()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = createConfigurationAllBasicActions(
				CxUserType.ANONYMOUS);
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("actions", 0, Sets.newHashSet("UPDATE"), (long) 0, CxUserType.REGISTERED));
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("actions2", 0, Sets.newHashSet("LOAD"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(false);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(2));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.LOAD));
	}

	@Test
	public void shouldReturnActionForAnonymousUser()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = createConfigurationAllBasicActions(
				CxUserType.REGISTERED);
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("actions", 0, Sets.newHashSet("UPDATE"), (long) 0, CxUserType.ANONYMOUS));
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("actions", 0, Sets.newHashSet("LOAD"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		final Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(2));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.LOAD));
	}

	@Test
	public void shouldReturnActionsWithPostiveMinimalNumberOfRequests()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("action", 2, Sets.newHashSet("IGNORE"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.IGNORE));

	}

	@Test
	public void shouldReturnDefaultVoteWithNegativeMinimalNumberOfRequests()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("action", -1, Sets.newHashSet("IGNORE"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnActionsAfterPostitiveIntervalTime()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("action", 0, Sets.newHashSet("IGNORE"), (long) 999, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());

		givenTimePassed(1000);

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));
		assertTrue(vote.getRecalculateActions().contains(RecalculateAction.IGNORE));
	}

	@Test
	public void shouldReturnDefaultVoteAfterNegativeIntervalTime()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("action", 0, Sets.newHashSet("IGNORE"), (long) -1, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());

		givenTimePassed(1000);

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnDefaultVoteWhenNegativeMinimalNumberOfRequestsAndNegativeIntervalTime()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("action", -1, Sets.newHashSet("IGNORE"), (long) -1, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(true);

		//when
		Vote vote = voter.getVote(request, response);

		//given
		givenTimePassed(1000);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void shouldReturnActionsForEveryRequest()
	{
		//given
		final Set<CxPeriodicVoterConfigModel> testPeriodicVoterConfigModels = new HashSet<>();
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("ignore", 0, Sets.newHashSet("IGNORE"), (long) 0, CxUserType.ALL));
		when(cxConfigurationService.getPeriodicVoterConfigurations()).thenReturn(testPeriodicVoterConfigModels);

		givenAnonymousUser(false);

		//when
		Vote vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));

		//given
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("update", 2, Sets.newHashSet("UPDATE"), (long) 0, CxUserType.ALL));

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(2));

		//given
		testPeriodicVoterConfigModels
				.add(createPeriodicVoterConfiguration("load", 0, Sets.newHashSet("LOAD"), (long) 999, CxUserType.ALL));

		givenTimePassed(1000);

		//when
		vote = voter.getVote(request, response);

		//then
		assertNotNull(vote);
		assertThat(vote.getRecalculateActions(), IsCollectionWithSize.hasSize(1));
	}

	private void givenAnonymousUser(final boolean isAnonymous)
	{
		given(Boolean.valueOf(userService.isAnonymousUser(user))).willReturn(Boolean.valueOf(isAnonymous));
	}

	private void givenTimePassed(final long time)
	{
		final long newTime = timeService.getCurrentTime().getTime() + time;
		timeService.setCurrentTime(new Date(newTime));
	}


}
