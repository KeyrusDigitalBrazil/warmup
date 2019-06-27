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
package com.hybris.ymkt.recommendationaddon.controllers;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hybris.ymkt.recommendation.model.CMSSAPOfferRecoComponentModel;
import com.hybris.ymkt.recommendationaddon.constants.SapymktrecommendationaddonConstants;


/**
 * Controller for CMS CMSSAPRecommendationComponentController
 */
@Controller("CMSSAPOfferRecoComponentController")
@RequestMapping(value = "/view/CMSSAPOfferRecoComponentController")
public class CMSSAPOfferRecoComponentController extends AbstractCMSAddOnComponentController<CMSSAPOfferRecoComponentModel>
{
	@Override
	protected String getAddonUiExtensionName(final CMSSAPOfferRecoComponentModel component)
	{
		return SapymktrecommendationaddonConstants.EXTENSIONNAME;
	}

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final CMSSAPOfferRecoComponentModel component)
	{
		model.addAttribute("componentId", component.getUid());
	}
}
