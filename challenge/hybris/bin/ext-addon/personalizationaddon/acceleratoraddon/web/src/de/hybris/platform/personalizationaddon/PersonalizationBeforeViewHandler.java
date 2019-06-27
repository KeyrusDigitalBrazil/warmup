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
package de.hybris.platform.personalizationaddon;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationaddon.data.CxViewActionResult;
import de.hybris.platform.personalizationaddon.data.CxViewValueCoder;
import de.hybris.platform.personalizationfacades.customersegmentation.CustomerSegmentationFacade;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;


public class PersonalizationBeforeViewHandler implements BeforeViewHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(PersonalizationBeforeViewHandler.class);

	public static final String PERSONALIZATION_ACTIONS = "personalizationActionList";
	public static final String PERSONALIZATION_SEGMENTS = "personalizationSegmentList";

	private CxService cxService;
	private CustomerSegmentationFacade customerSegmentationFacade;
	private UserService userService;
	private CxViewValueCoder cxViewValueCoder;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{
		try
		{
			final UserModel currentUser = userService.getCurrentUser();
			final List<CxAbstractActionResult> actionList = cxService.getActionResultsFromSession(currentUser).stream()
					.map(this::encodeActionResult).collect(Collectors.toList());
			modelAndView.addObject(PERSONALIZATION_ACTIONS, actionList);

			final List<SegmentData> segmentList = customerSegmentationFacade.getSegmentsForCurrentUser();
			segmentList.forEach(this::encodeSegmentData);
			modelAndView.addObject(PERSONALIZATION_SEGMENTS, segmentList);
		}
		catch (final RuntimeException e)
		{
			LOG.debug("Adding personalization data to the page has failed", e);
		}
	}


	protected CxViewActionResult encodeActionResult(final CxAbstractActionResult action)
	{
		final CxViewActionResult result = new CxViewActionResult();
		final String actionCode = cxViewValueCoder.encode(action.getActionCode());
		result.setActionCode(actionCode);

		final String variationCode = cxViewValueCoder.encode(action.getVariationCode());
		result.setVariationCode(variationCode);
		final String variationName = cxViewValueCoder.encode(action.getVariationName());
		result.setVariationName(variationName);

		final String customizationCode = cxViewValueCoder.encode(action.getCustomizationCode());
		result.setCustomizationCode(customizationCode);
		final String customizationName = cxViewValueCoder.encode(action.getCustomizationName());
		result.setCustomizationName(customizationName);

		final String type = cxViewValueCoder.encode(action.getClass().getSimpleName());
		result.setType(type);

		return result;
	}

	protected SegmentData encodeSegmentData(final SegmentData segment)
	{
		final String code = cxViewValueCoder.encode(segment.getCode());
		segment.setCode(code);
		return segment;
	}

	public void setCxService(final CxService cxService)
	{
		this.cxService = cxService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setCustomerSegmentationFacade(final CustomerSegmentationFacade customerSegmentationFacade)
	{
		this.customerSegmentationFacade = customerSegmentationFacade;
	}

	public void setCxViewValueCoder(final CxViewValueCoder cxViewValueCoder)
	{
		this.cxViewValueCoder = cxViewValueCoder;
	}
}
