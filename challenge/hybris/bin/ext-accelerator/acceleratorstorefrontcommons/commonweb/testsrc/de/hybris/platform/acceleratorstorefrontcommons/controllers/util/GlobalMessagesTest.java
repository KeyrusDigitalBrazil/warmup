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
package de.hybris.platform.acceleratorstorefrontcommons.controllers.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GlobalMessagesTest
{
	private static final String SIMPLE_ERROR_MESSAGE1 = "simple.error.message";
	private static final String SIMPLE_ERROR_MESSAGE2 = "simple.error.message2";
	private static final String SIMPLE_INFO_MESSAGE = "simple.info.message";
	private static final String SIMPLE_CONF_MESSAGE = "simple.conf.message";

	@Test
	public void shouldNotContainsMessageKey()
	{
		final Map<String, Object> modelMap = createModelMap();

		final Model model = Mockito.mock(Model.class);
		given(model.asMap()).willReturn(modelMap);

		assertFalse("Should not contain error message key.",
				GlobalMessages.containsMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, SIMPLE_ERROR_MESSAGE2));
	}

	@Test
	public void shouldContainsThreeMessageKeys()
	{
		final Map<String, Object> modelMap = createModelMap();

		final Model model = Mockito.mock(Model.class);
		given(model.asMap()).willReturn(modelMap);

		assertTrue("Should contain error message key.",
				GlobalMessages.containsMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, SIMPLE_ERROR_MESSAGE1));
		assertTrue("Should contain info message key.",
				GlobalMessages.containsMessage(model, GlobalMessages.INFO_MESSAGES_HOLDER, SIMPLE_INFO_MESSAGE));
		assertTrue("Should contain conf message key.",
				GlobalMessages.containsMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, SIMPLE_CONF_MESSAGE));
	}

	protected Map<String, Object> createModelMap()
	{
		final List<GlobalMessage> errorMsgList = new ArrayList<GlobalMessage>();
		final GlobalMessage simpleErrormsg = new GlobalMessage();
		simpleErrormsg.setCode(SIMPLE_ERROR_MESSAGE1);
		errorMsgList.add(simpleErrormsg);

		final List<GlobalMessage> infoMsgList = new ArrayList<GlobalMessage>();
		final GlobalMessage simpleInfomsg = new GlobalMessage();
		simpleInfomsg.setCode(SIMPLE_INFO_MESSAGE);
		infoMsgList.add(simpleInfomsg);

		final List<GlobalMessage> confMsgList = new ArrayList<GlobalMessage>();
		final GlobalMessage simpleConfmsg = new GlobalMessage();
		simpleConfmsg.setCode(SIMPLE_CONF_MESSAGE);
		confMsgList.add(simpleConfmsg);

		final Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put(GlobalMessages.ERROR_MESSAGES_HOLDER, errorMsgList);
		modelMap.put(GlobalMessages.INFO_MESSAGES_HOLDER, infoMsgList);
		modelMap.put(GlobalMessages.CONF_MESSAGES_HOLDER, confMsgList);

		return modelMap;
	}
}
