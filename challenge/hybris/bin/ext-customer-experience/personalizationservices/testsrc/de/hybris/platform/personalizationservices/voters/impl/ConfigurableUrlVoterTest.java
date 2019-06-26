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
package de.hybris.platform.personalizationservices.voters.impl;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.configuration.CxConfigurationService;
import de.hybris.platform.personalizationservices.model.config.CxUrlVoterConfigModel;
import de.hybris.platform.personalizationservices.voters.Vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;


@UnitTest
public class ConfigurableUrlVoterTest
{
	private static final String CHECKOUT_URL = "http://mywebsite/checkout";
	private static final String INCORRECT_URL_CONFIG = "incorrectRegexpConfigCode";
	private static final String INCORRECT_REGEXP = "[";
	private static final Set RECALCULATE_ACTION_SET = Sets.newHashSet("RECALCULATE");

	@Mock
	CxConfigurationService cxConfigurationService;

	private ConfigurableUrlVoter configurableUrlPersonalizationVoter;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		configurableUrlPersonalizationVoter = new ConfigurableUrlVoter();
		configurableUrlPersonalizationVoter.setCxConfigurationService(cxConfigurationService);

		when(cxConfigurationService.getUrlVoterConfigurations()).thenReturn(createConfiguration());
	}

	private List<CxUrlVoterConfigModel> createConfiguration()
	{
		final List<CxUrlVoterConfigModel> urlVoterConfigurationModels = new ArrayList<>();

		urlVoterConfigurationModels.add(createUrloVoterConfiguration("checkout", "checkout", Sets.newHashSet("RECALCULATE")));
		urlVoterConfigurationModels.add(createUrloVoterConfiguration("user.checkout", "user/checkout", Sets.newHashSet("LOAD")));
		urlVoterConfigurationModels
				.add(createUrloVoterConfiguration("duplicatedUrl1", ".*/cart", Sets.newHashSet("RECALCULATE", "ASYNC_PROCESS")));
		urlVoterConfigurationModels
				.add(createUrloVoterConfiguration("duplicatedUrl2", ".*/cart", Sets.newHashSet("UPDATE", "RECALCULATE")));
		urlVoterConfigurationModels.add(createUrloVoterConfiguration("wrongAction", "url", Sets.newHashSet("NOT_EXISTING_ACTION")));
		urlVoterConfigurationModels.add(
				createUrloVoterConfiguration("working", "url2", Sets.newHashSet("UPDATE", "RECALCULATE", "LOAD", "ASYNC_PROCESS")));
		urlVoterConfigurationModels.add(createUrloVoterConfiguration("emptyUrl", "", Sets.newHashSet("UPDATE")));

		return urlVoterConfigurationModels;
	}

	private CxUrlVoterConfigModel createUrloVoterConfiguration(final String code, final String url, final Set<String> actionsList)
	{
		final CxUrlVoterConfigModel urlVoterConfigurationModel = new CxUrlVoterConfigModel();
		urlVoterConfigurationModel.setCode(code);
		urlVoterConfigurationModel.setUrlRegexp(url);
		urlVoterConfigurationModel.setActions(actionsList);
		return urlVoterConfigurationModel;
	}

	@Test
	public void testRecalculateVote()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getRequestURI()).thenReturn("http://mywebsite/checkout");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
	}

	@Test
	public void testMultipleActionVote()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("http://mywebsite/url2");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertEquals(4, vote.getRecalculateActions().size());
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.ASYNC_PROCESS));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.LOAD));
	}

	@Test
	public void testNotConfiguredUrl()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("http://mywebsite/product/1234");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void testDuplicatedUrl()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("http://mywebsite/user/cart");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertEquals(3, vote.getRecalculateActions().size());
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.ASYNC_PROCESS));
	}

	@Test
	public void testEncodingUrl()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("http://mywebsite/user/c%61rt");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertEquals(3, vote.getRecalculateActions().size());
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.RECALCULATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.UPDATE));
		Assert.assertTrue(vote.getRecalculateActions().contains(RecalculateAction.ASYNC_PROCESS));
	}

	@Test
	public void testNotExistingActionName()
	{
		//given
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn("http://mywebsite/url");

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertEquals(0, vote.getRecalculateActions().size());
	}

	@Test
	public void testEmptyRegexpInConfiguration()
	{
		//given
		final List<CxUrlVoterConfigModel> urlVoterConfiguration = Collections
				.singletonList(createUrloVoterConfiguration("emptyRegexp", "   ", RECALCULATE_ACTION_SET));
		when(cxConfigurationService.getUrlVoterConfigurations()).thenReturn(urlVoterConfiguration);

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(CHECKOUT_URL);

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void testIncorrectRegexpInConfiguration()
	{
		//given
		final List<CxUrlVoterConfigModel> urlVoterConfiguration = Collections
				.singletonList(createUrloVoterConfiguration(INCORRECT_URL_CONFIG, INCORRECT_REGEXP, RECALCULATE_ACTION_SET));
		when(cxConfigurationService.getUrlVoterConfigurations()).thenReturn(urlVoterConfiguration);

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(CHECKOUT_URL);

		//when
		final Vote vote = configurableUrlPersonalizationVoter.getVote(request, null);

		//then
		Assert.assertTrue(vote.getRecalculateActions().isEmpty());
	}

	@Test
	public void testIncorrectRegexpIsCompiledOnlyOnce()
	{
		//given
		final List<CxUrlVoterConfigModel> urlVoterConfiguration = Collections
				.singletonList(createUrloVoterConfiguration(INCORRECT_URL_CONFIG, INCORRECT_REGEXP, RECALCULATE_ACTION_SET));
		when(cxConfigurationService.getUrlVoterConfigurations()).thenReturn(urlVoterConfiguration);
		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getRequestURI()).thenReturn(CHECKOUT_URL);

		final ConfigurableUrlVoter spy = spy(configurableUrlPersonalizationVoter);

		//when
		spy.getVote(request, null);
		spy.getVote(request, null);
		spy.getVote(request, null);

		//then
		verify(spy, times(1)).compilePattern("[");
	}

}
