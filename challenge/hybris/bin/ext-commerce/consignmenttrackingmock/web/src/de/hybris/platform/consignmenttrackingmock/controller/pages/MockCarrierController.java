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
package de.hybris.platform.consignmenttrackingmock.controller.pages;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.consignmenttrackingmock.controller.ConsignmenttrackingmockControllerConstants;
import de.hybris.platform.consignmenttrackingmock.data.MockDataProvider;
import de.hybris.platform.consignmenttrackingmock.forms.TrackingEventForm;
import de.hybris.platform.consignmenttrackingmock.service.impl.MockConsignmentTrackingService;
import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 *
 */
@Controller
@RequestMapping("/tracking/mock")
public class MockCarrierController
{

	@Resource(name = "consignmentTrackingService")
	private ConsignmentTrackingService consignmentTrackingService;

	@Resource(name = "mockDataProvider")
	private MockDataProvider mockDataProvider;

	@Resource(name = "carrier")
	private String carrier;

	@Resource(name = "mockConsignmentTrackingService")
	private MockConsignmentTrackingService mockConsignmentTrackingService;

	private static final String[] DISALLOWED_FIELDS = new String[] {};

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@RequestMapping(value = "/events", method = RequestMethod.GET)
	public String redirectToMockCarrier() 
	{
		return ConsignmenttrackingmockControllerConstants.Pages.MOCKCARRIERPAGE;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String display(final Model model)
	{
		final List<ConsignmentStatus> statuses = new ArrayList<>();
		statuses.add(ConsignmentStatus.IN_TRANSIT);
		statuses.add(ConsignmentStatus.DELIVERING);
		statuses.add(ConsignmentStatus.DELIVERY_COMPLETED);
		statuses.add(ConsignmentStatus.DELIVERY_REJECTED);
		model.addAttribute("statuses", statuses);
		model.addAttribute("carrier", carrier);
		return ConsignmenttrackingmockControllerConstants.Pages.CONSIGNMENTMOCKPAGE;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String prepare(final TrackingEventForm form)
	{

		final String trackingId = form.getTrackingId();
		final Optional<ConsignmentModel> consignmentResult = mockDataProvider.getConsignmentForTrackingId(carrier, trackingId);
		if (!consignmentResult.isPresent())
		{
			return "redirect:/tracking/mock";
		}
		consignmentResult.ifPresent(consignment -> {

			final String consignmentCode = consignment.getCode();
			final String orderCode = consignment.getOrder().getCode();
			final List<ConsignmentEventData> consignmentEvents = mockConsignmentTrackingService
					.getConsignmentEventsByTrackingId(trackingId);
			final ConsignmentEventData consignmentEvent = new ConsignmentEventData();
			consignmentEvent.setDetail(form.getEventDetail());
			consignmentEvent.setLocation(form.getEventLocation());
			consignmentEvent.setReferenceCode(form.getConsignmentStatus());

			final Calendar calendar = Calendar.getInstance();
			final int minute = calendar.get(Calendar.MINUTE);
			calendar.setTime(form.getEventDate());
			calendar.set(Calendar.MINUTE, minute);
			consignmentEvent.setEventDate(calendar.getTime());

			consignmentEvents.add(consignmentEvent);
			mockConsignmentTrackingService.saveConsignmentEvents(trackingId, consignmentEvents);

			consignmentTrackingService.updateConsignmentStatusForCode(orderCode, consignmentCode,
					ConsignmentStatus.valueOf(form.getConsignmentStatus()));
		});

		return "redirect:/tracking/mock";
	}
}
