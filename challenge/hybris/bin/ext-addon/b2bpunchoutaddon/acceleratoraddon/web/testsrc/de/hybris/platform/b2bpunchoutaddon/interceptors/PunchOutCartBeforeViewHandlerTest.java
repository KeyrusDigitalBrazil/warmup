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
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


/**
 * Unit test for class {@link PunchOutBeforeViewHandler}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCartBeforeViewHandlerTest
{
	public final static String OLD_VIEW = "/oldPage";
	public final static String NEW_VIEW = "/newPage";

	private PunchOutBeforeViewHandler viewHandler;

	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;

	private HttpSession session;
	private ModelAndView modelAndView;


	@Before
	public void setup()
	{
		Map<String, Map<String, String>> viewMap;
		viewHandler = new PunchOutBeforeViewHandler();
		viewMap = new HashMap<String, Map<String, String>>();
		final Map<String, String> viewName = new HashMap<>();
		viewName.put("viewName", NEW_VIEW);
		viewMap.put(OLD_VIEW, viewName);
		viewHandler.setViewMap(viewMap);

		session = Mockito.mock(HttpSession.class);
		modelAndView = new ModelAndView();
		modelAndView.setViewName(OLD_VIEW);
	}

	@Test
	public void changesViewForPunchOutUser() throws Exception
	{
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn("myUser");

		viewHandler.beforeView(request, response, modelAndView);
		Assert.assertNotNull(modelAndView.getViewName());
		Assert.assertEquals(B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + NEW_VIEW, modelAndView.getViewName());
	}

	@Test
	public void keepsViewForNonPunchOutUser() throws Exception
	{
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn(null);

		viewHandler.beforeView(request, response, modelAndView);
		Assert.assertNotNull(modelAndView.getViewName());
		Assert.assertEquals(OLD_VIEW, modelAndView.getViewName());
	}

}
