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
package de.hybris.platform.hac.controller;

import de.hybris.platform.yhacext.data.SampleMonitorData;
import de.hybris.platform.yhacext.data.SampleMonitorResultData;

import java.text.MessageFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableList;


/**
 *
 */
@Controller
@RequestMapping("/yhacext/**")
public class YHacExtController
{

	public static final String CHECK_PENDING_ORDERS = "Pending Orders";
	public static final String CHECK_FAILING_ORDERS = "Failing Orders";

	@RequestMapping(value = "/extension", method = RequestMethod.GET)
	public @ResponseBody String sayHello()
	{
		return "Simple demonstration of extending hac by adding new controller and routing.";
	}


	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public String showCustomStatistics(final Model model)
	{
		final SampleMonitorData monitor = new SampleMonitorData();
		monitor.setResults(ImmutableList.of(createFakePendingOrders(), createFakeFailedOrders()));
		model.addAttribute("monitor", monitor);

		return "customStatistics";
	}

	public SampleMonitorResultData createFakePendingOrders()
	{
		final SampleMonitorResultData monitorResult = new SampleMonitorResultData();
		monitorResult.setName(CHECK_PENDING_ORDERS);
		monitorResult.setDuration(Long.valueOf(14L));
		monitorResult.setMessage("OK");

		return monitorResult;
	}

	public SampleMonitorResultData createFakeFailedOrders()
	{
		final MessageFormat format = new MessageFormat("There are {0} failing orders created more than {1} hour(s) ago");
		final Object[] array = new Object[]
		{ new Integer(5), new Integer(1) };

		final SampleMonitorResultData monitorResult = new SampleMonitorResultData();
		monitorResult.setName(CHECK_FAILING_ORDERS);
		monitorResult.setDuration(Long.valueOf(42L));
		monitorResult.setMessage(format.format(array));

		return monitorResult;
	}

}
