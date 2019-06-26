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

import de.hybris.platform.acceleratorcms.component.slot.CMSPageSlotComponentService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.hybris.ymkt.recommendation.dao.OfferInteractionContext;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Interaction;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Offer;
import com.hybris.ymkt.recommendation.dao.OfferRecommendation;
import com.hybris.ymkt.recommendation.dao.OfferRecommendationContext;
import com.hybris.ymkt.recommendation.model.CMSSAPOfferRecoComponentModel;
import com.hybris.ymkt.recommendationaddon.facades.OfferRecommendationManagerFacade;


/**
 * Controller for offerrecommendationList view
 */
@Controller("SAPOfferRecommendationRetrieveController")
public class SAPOfferRecommendationRetrieveController
{
	private static final Logger LOG = LoggerFactory.getLogger(SAPOfferRecommendationRetrieveController.class);

	protected static final String OFFER_CLICK = "OFFER_CLICK";
	protected static final String OFFER_DISPLAY = "OFFER_DISPLAY";
	protected static final String VIEWNAME = "addon:/sapymktrecommendationaddon/cms/offerrecommendationlist";

	@Resource(name = "cmsPageSlotComponentService")
	protected CMSPageSlotComponentService cmsPageSlotComponentService;

	@Resource(name = "sapOfferRecommendationManagerFacade")
	protected OfferRecommendationManagerFacade offerRecommendationManagerFacade;

	@Autowired
	protected HttpServletRequest request;

	protected Optional<CMSSAPOfferRecoComponentModel> getComponent(final String componentId)
	{
		return Optional.ofNullable(componentId) //
				.map(cmsPageSlotComponentService::getComponentForId) //
				.filter(CMSSAPOfferRecoComponentModel.class::isInstance) //
				.map(CMSSAPOfferRecoComponentModel.class::cast);
	}

	@RequestMapping(value = "/action/offerClick/", method = RequestMethod.POST)
	@ResponseBody
	public void registerOfferClick(@RequestParam("componentId") final String componentId,
			@RequestParam("offerid") final String offerId, @RequestParam("offerContentId") final String offerContentId)
	{
		saveOfferInteraction(componentId, offerId, offerContentId, false);
	}

	@RequestMapping(value = "/action/offerDisplay/", method = RequestMethod.POST)
	@ResponseBody
	public void registerOfferDisplay(@RequestParam("componentId") final String componentId,
			@RequestParam("offerid") final String offerId, @RequestParam("offerContentId") final String offerContentId)
	{
		saveOfferInteraction(componentId, offerId, offerContentId, true);
	}

	/**
	 * Retrieve the recommended offers to be rendered in the UI
	 *
	 * @param id
	 * @param componentId
	 * @param model
	 * @return viewName
	 */
	@RequestMapping(value = "/action/offers/")
	public String retrieveOfferRecommendations(@RequestParam("id") final String id,
			@RequestParam("componentId") final String componentId, final Model model)
	{

		final Optional<CMSSAPOfferRecoComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return VIEWNAME;
		}

		if (StringUtils.isEmpty(component.get().getRecotype()))
		{
			LOG.debug("Recommendation Type has to be specified.");
			return VIEWNAME;
		}

		//Populate context
		final OfferRecommendationContext context = new OfferRecommendationContext();
		context.setScenarioId(component.get().getRecotype());
		context.setContentPosition(component.get().getContentposition());
		context.setLeadingItemType(component.get().getLeadingitemtype());
		context.setLeadingItemDSType(component.get().getLeadingitemdstype());
		context.setCartItemDSType(component.get().getCartitemdstype());
		context.setIncludeCart(component.get().isIncludecart());
		context.setIncludeRecent(component.get().isIncluderecent());

		//Get offer recommendations based on context
		final List<OfferRecommendation> offerRecommendations = offerRecommendationManagerFacade.getOfferRecommendations(context);
		model.addAttribute("componentId", component.get().getUid());
		model.addAttribute("offerRecoId", HtmlUtils.htmlEscape(id));
		model.addAttribute("offers", offerRecommendations);

		return VIEWNAME;
	}

	/**
	 *
	 * @param componentId
	 * @param offerId
	 * @param offerContentId
	 * @param bIsOfferDisplay
	 */
	public void saveOfferInteraction(final String componentId, final String offerId, final String offerContentId,
			final boolean bIsOfferDisplay)
	{
		final Optional<CMSSAPOfferRecoComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return;
		}

		final String interactionType = bIsOfferDisplay ? OFFER_DISPLAY : OFFER_CLICK;
		final OfferInteractionContext offerInteractionContext = new OfferInteractionContext();
		final Interaction interaction = new Interaction();
		final Offer offer = new Offer();

		offer.setId(offerId);
		offer.setContentItemId(offerContentId);
		offer.setRecommendationScenarioId(component.get().getRecotype());

		interaction.setInteractionType(interactionType);
		offerInteractionContext.setTimestamp(new Date());

		interaction.getOffers().add(offer);
		offerInteractionContext.getInteractions().add(interaction);

		offerRecommendationManagerFacade.saveOfferInteraction(offerInteractionContext);
	}

}
