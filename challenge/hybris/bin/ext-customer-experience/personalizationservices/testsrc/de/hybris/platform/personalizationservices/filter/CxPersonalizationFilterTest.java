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
package de.hybris.platform.personalizationservices.filter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.constants.PersonalizationservicesConstants;
import de.hybris.platform.personalizationservices.service.CxRecalculationService;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.personalizationservices.voters.Vote;
import de.hybris.platform.personalizationservices.voters.Voter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;

import jersey.repackaged.com.google.common.collect.Lists;


@UnitTest
public class CxPersonalizationFilterTest
{
	CxPersonalizationFilter personalizationFilter;

	Voter ignoreVoter, recalculateVoter, asyncVoter, loadVoter, updateVoter, multipleActionVoter;

	HttpServletRequest request;
	HttpServletResponse response;
	FilterChain filterChain;

	@Mock
	private UserModel currentUser;
	@Mock
	private Collection<CatalogVersionModel> sessionCatalogVersions;

	@Mock
	private CxRecalculationService recalculationService;
	@Mock
	private UserService userService;
	@Mock
	private CxService cxService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		filterChain = mock(FilterChain.class);

		personalizationFilter = new CxPersonalizationFilter();
		personalizationFilter.setCxRecalculationService(recalculationService);
		personalizationFilter.setSessionService(sessionService);

		ignoreVoter = mock(Voter.class);
		recalculateVoter = mock(Voter.class);
		loadVoter = mock(Voter.class);
		updateVoter = mock(Voter.class);
		asyncVoter = mock(Voter.class);
		multipleActionVoter = mock(Voter.class);


		given(userService.getCurrentUser()).willReturn(currentUser);
		given(catalogVersionService.getSessionCatalogVersions()).willReturn(sessionCatalogVersions);

		when(recalculateVoter.getVote(any(), any())).thenReturn(buildVote(false, RecalculateAction.RECALCULATE));
		when(asyncVoter.getVote(any(), any())).thenReturn(buildVote(false, RecalculateAction.ASYNC_PROCESS));
		when(loadVoter.getVote(any(), any())).thenReturn(buildVote(false, RecalculateAction.LOAD));
		when(updateVoter.getVote(any(), any())).thenReturn(buildVote(false, RecalculateAction.UPDATE));
		when(ignoreVoter.getVote(any(), any())).thenReturn(buildVote(false, RecalculateAction.IGNORE));

	}

	Vote buildVote(final boolean conclusive, final RecalculateAction... actions)
	{
		final Vote result = new Vote();
		result.setConclusive(conclusive);
		result.setRecalculateActions(Sets.newHashSet(actions));
		return result;
	}

	Optional<List<Voter>> buildVoters(final Voter... voters)
	{
		return Optional.of(Lists.newArrayList(voters));
	}

	@Test
	public void testActivatePersonalization() throws Exception
	{
		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(sessionService, times(1)).setAttribute(PersonalizationservicesConstants.ACTIVE_PERSONALIZATION, Boolean.TRUE);
	}

	@Test
	public void testAsyncVote() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(asyncVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.ASYNC_PROCESS)));
	}

	@Test
	public void testIgnoreVote() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(ignoreVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(0)).recalculate(any());
	}

	@Test
	public void testRecalculateVote() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(recalculateVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.RECALCULATE)));
	}

	@Test
	public void testUpdateVote() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(updateVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.UPDATE)));
	}

	@Test
	public void testLoadVote() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(loadVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.LOAD)));
	}

	@Test
	public void testVoteMerging() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(asyncVoter, recalculateVoter, updateVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate((List<RecalculateAction>) argThat(
				containsInAnyOrder(RecalculateAction.ASYNC_PROCESS, RecalculateAction.RECALCULATE, RecalculateAction.UPDATE)));
	}

	@Test
	public void testMultipleVote() throws Exception
	{
		//given
		when(multipleActionVoter.getVote(any(), any())).thenReturn(
				buildVote(true, RecalculateAction.RECALCULATE, RecalculateAction.UPDATE));
		personalizationFilter.setVoters(buildVoters(multipleActionVoter, asyncVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(0)).recalculate(
				eq(Arrays.asList(RecalculateAction.UPDATE, RecalculateAction.RECALCULATE, RecalculateAction.ASYNC_PROCESS)));
	}

	@Test
	public void testIgnoreVoteMerging() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(recalculateVoter, ignoreVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(0)).recalculate(any());
	}

	@Test
	public void testFinalVoteMerging() throws Exception
	{
		//given
		when(recalculateVoter.getVote(any(), any())).thenReturn(buildVote(true, RecalculateAction.RECALCULATE));
		personalizationFilter.setVoters(buildVoters(recalculateVoter, ignoreVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.RECALCULATE)));
	}

	@Test
	public void testFinalVoteOrder() throws Exception
	{
		//given
		when(recalculateVoter.getVote(any(), any())).thenReturn(buildVote(true, RecalculateAction.RECALCULATE));
		when(ignoreVoter.getVote(any(), any())).thenReturn(buildVote(true, RecalculateAction.IGNORE));
		personalizationFilter.setVoters(buildVoters(recalculateVoter, ignoreVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.RECALCULATE)));
	}


	@Test
	public void testActionListOptimization() throws Exception
	{
		//given
		personalizationFilter.setVoters(buildVoters(recalculateVoter, loadVoter));

		//when
		personalizationFilter.doFilter(request, response, filterChain);

		//then
		verify(recalculationService, times(1)).recalculate(eq(Arrays.asList(RecalculateAction.RECALCULATE)));
	}
}
