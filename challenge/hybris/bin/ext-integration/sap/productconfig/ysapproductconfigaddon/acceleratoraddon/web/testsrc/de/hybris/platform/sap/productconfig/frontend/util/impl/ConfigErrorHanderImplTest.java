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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


@UnitTest
public class ConfigErrorHanderImplTest
{
	private ConfigErrorHandlerImpl classUnderTest;
	@Mock
	private Model model;

	private MockHttpServletRequest request;

	@Mock
	protected WebApplicationContext wac;

	@Mock
	protected MockServletContext servletContext;

	@Mock
	private RedirectAttributesModelMap redirectModel;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigErrorHandlerImpl();
		servletContext = new MockServletContext();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		request = new MockHttpServletRequest(servletContext);
		redirectModel = new RedirectAttributesModelMap();

	}

	@Test
	public void testHandleError()
	{
		final String result = classUnderTest.handleError();
		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT, result);
	}

	@Test
	public void testHandleErrorAjax()
	{
		final ModelAndView result = classUnderTest.handleErrorForAjaxRequest(request, model);
		Mockito.verify(model).addAttribute("redirectUrl", request.getContextPath());
		assertEquals(
				SapproductconfigfrontendWebConstants.ADDON_PREFIX + AbstractController.ROOT
						+ SapproductconfigaddonConstants.EXTENSIONNAME + SapproductconfigfrontendWebConstants.CONFIG_ERROR_ROOT,
				result.getViewName());
	}

	@Test
	public void testHandleErrorFromOrderHistory()
	{
		final String orderCode = "00001000";
		final String result = classUnderTest.handleErrorFromOrderHistory(orderCode, 0, redirectModel);
		assertEquals(AbstractController.REDIRECT_PREFIX + SapproductconfigfrontendWebConstants.ORDER_DETAILS + orderCode, result);
	}

	@Test
	public void testHandleErrorFromQuotes()
	{
		final String orderCode = "00001000";
		final String result = classUnderTest.handleErrorFromQuotes(orderCode, 0, redirectModel);
		assertEquals(AbstractController.REDIRECT_PREFIX + SapproductconfigfrontendWebConstants.QUOTES + orderCode, result);
	}

	@Test
	public void testHandleErrorFromSavedCarts()
	{
		final String orderCode = "00001000";
		final String result = classUnderTest.handleErrorFromSavedCarts(orderCode, 0, redirectModel);
		assertEquals(AbstractController.REDIRECT_PREFIX + SapproductconfigfrontendWebConstants.SAVED_CARTS + orderCode, result);
	}

	@Test
	public void testHandleConfigurationAccessError()
	{
		final String result = classUnderTest.handleConfigurationAccessError();
		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT, result);
	}


}
