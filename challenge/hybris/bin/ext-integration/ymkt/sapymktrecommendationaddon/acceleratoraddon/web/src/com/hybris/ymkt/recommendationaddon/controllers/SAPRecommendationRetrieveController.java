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
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

import com.hybris.ymkt.recommendation.dao.ImpressionContext;
import com.hybris.ymkt.recommendation.dao.InteractionContext;
import com.hybris.ymkt.recommendation.dao.RecommendationContext;
import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;
import com.hybris.ymkt.recommendation.services.InteractionService;
import com.hybris.ymkt.recommendation.services.RecommendationService;
import com.hybris.ymkt.recommendationaddon.facades.ProductRecommendationManagerFacade;


/**
 * Controller for RecommendationList view
 */
@Controller("SAPRecommendationRetrieveController")
public class SAPRecommendationRetrieveController
{
	private static final Logger LOG = LoggerFactory.getLogger(SAPRecommendationRetrieveController.class);

	protected static final String VIEWNAME = "addon:/sapymktrecommendationaddon/cms/productrecommendationlist";

	@Resource(name = "cmsPageSlotComponentService")
	protected CMSPageSlotComponentService cmsPageSlotComponentService;

	@Resource(name = "interactionService")
	protected InteractionService interactionService;

	@Resource(name = "recommendationService")
	protected RecommendationService recommendationService;

	@Resource(name = "sapProductRecommendationManagerFacade")
	protected ProductRecommendationManagerFacade productRecommendationManagerFacade;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource(name = "commerceStockService")
	private CommerceStockService commerceStockService;

	@Autowired
	protected HttpServletRequest request;

	//Removes non available products from recommended products list
	//If removal results in an empty list, then return the original list instead
	private List<ProductReferenceData> filterAvailableProducts(final List<ProductReferenceData> productRecommendations)
	{
		LOG.debug("Filtering for available product recommendation");

		final List<ProductReferenceData> availableProductRecommendations = //
				productRecommendations.stream() //
						.filter(this::isProductAvailable) //
						.collect(Collectors.toList());

		return availableProductRecommendations.isEmpty() ? productRecommendations : availableProductRecommendations;
	}

	//Removes cart products from recommended products list
	//If removal results in an empty list, then return the original list instead
	private List<ProductReferenceData> filterCartProducts(final List<ProductReferenceData> productRecommendations)
	{
		LOG.debug("Filtering product recommendation to exclude cart");

		//Get current products in cart (once per request)
		final List<String> cartItems = recommendationService.getCartItemsFromSession();
		final Predicate<ProductReferenceData> productInCart = p -> cartItems.contains(p.getTarget().getCode());

		final List<ProductReferenceData> productRecommendationsNotInCart = //
				productRecommendations.stream() //
						.filter(productInCart.negate()) //
						.collect(Collectors.toList());

		return productRecommendationsNotInCart.isEmpty() ? productRecommendations : productRecommendationsNotInCart;
	}

	protected Optional<CMSSAPRecommendationComponentModel> getComponent(final String componentId)
	{
		return Optional.ofNullable(componentId) //
				.map(cmsPageSlotComponentService::getComponentForId) //
				.filter(CMSSAPRecommendationComponentModel.class::isInstance) //
				.map(CMSSAPRecommendationComponentModel.class::cast);
	}

	private boolean isProductAvailable(final ProductReferenceData product)
	{
		final String productCode = product.getTarget().getCode();

		try
		{
			final String baseSite = baseSiteService.getCurrentBaseSite().getUid();
			final BaseSiteModel baseSiteModel = baseSiteService.getBaseSiteForUID(baseSite);
			final BaseStoreModel baseStoreModel = baseSiteModel.getStores().get(0);
			final ProductModel productModel = productService.getProductForCode(productCode);

			return StockLevelStatus.INSTOCK
					.equals(commerceStockService.getStockLevelStatusForProductAndBaseStore(productModel, baseStoreModel));
		}
		catch (final SystemException e)
		{
			LOG.error("Error checking availability for recommended product code {}.", productCode, e);
			return false;
		}
	}

	/**
	 * Create a click-through interaction when a recommended product is clicked
	 *
	 * @param id
	 * @param componentId
	 */
	@RequestMapping(value = "/action/prodRecoInteraction/", method = RequestMethod.POST)
	@ResponseBody
	public void registerClickthrough(@RequestParam("id") final String id, @RequestParam("componentId") final String componentId)
	{
		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return;
		}

		final Optional<ProductModel> optProduct = this.productRecommendationManagerFacade.findProduct(id);
		if (!optProduct.isPresent())
		{
			return;
		}

		final InteractionContext interactionContext = new InteractionContext();
		interactionContext.setProductId(optProduct.get().getCode());
		interactionContext.setProductType(component.get().getLeadingitemdstype());
		interactionContext.setSourceObjectId(this.request.getSession().getId());
		interactionContext.setScenarioId(component.get().getRecotype());
		this.interactionService.saveClickthrough(interactionContext);
	}

	/**
	 * Creates an impression row in the table with the component's item count and ScenarioId
	 *
	 * @param itemCount
	 * @param componentId
	 */
	@RequestMapping(value = "/action/prodRecoImpression/", method = RequestMethod.POST)
	@ResponseBody
	public void registerProdRecoImpression(@RequestParam("itemCount") final int itemCount,
			@RequestParam("componentId") final String componentId)
	{
		if (itemCount <= 0 || itemCount > 100)
		{
			LOG.warn("Invalid itemCount={} for componentId={}.", itemCount, componentId);
			return;
		}

		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return;
		}

		final ImpressionContext impressionContext = new ImpressionContext(component.get().getRecotype(), itemCount);

		this.productRecommendationManagerFacade.saveImpression(impressionContext);
	}

	/**
	 * Retrieve the recommended products to be rendered in the UI
	 *
	 * @param id
	 * @param productCode
	 * @param model
	 * @return viewName
	 */
	@RequestMapping(value = "/action/recommendations/")
	public String retrieveRecommendations(@RequestParam("id") final String id,
			@RequestParam("productCode") final String productCode, @RequestParam("componentId") final String componentId,
			final Model model)
	{
		final Optional<CMSSAPRecommendationComponentModel> component = this.getComponent(componentId);
		if (!component.isPresent())
		{
			return VIEWNAME;
		}

		if (StringUtils.isEmpty(component.get().getRecotype()))
		{
			return VIEWNAME;
		}

		final RecommendationContext context = new RecommendationContext();
		context.setLeadingProductId(productCode);
		context.setScenarioId(component.get().getRecotype());
		context.setIncludeCart(component.get().isIncludecart());
		context.setIncludeRecent(component.get().isIncluderecent());
		context.setLeadingItemDSType(component.get().getLeadingitemdstype());
		context.setLeadingItemType(component.get().getLeadingitemtype());
		context.setCartItemDSType(component.get().getCartitemdstype());

		final List<ProductReferenceData> recommendations = this.productRecommendationManagerFacade
				.getProductRecommendation(context);
		final List<ProductReferenceData> recommendationsNotInCart = this.filterCartProducts(recommendations);
		final List<ProductReferenceData> recommendationsAvailable = this.filterAvailableProducts(recommendationsNotInCart);

		model.addAttribute("title", component.get().getTitle());
		model.addAttribute("recoId", HtmlUtils.htmlEscape(id));
		model.addAttribute("recoType", component.get().getRecotype());
		model.addAttribute("componentId", component.get().getUid());
		model.addAttribute("leadingitemdstype", component.get().getLeadingitemdstype());
		model.addAttribute("cartitemdstype", component.get().getCartitemdstype());
		model.addAttribute("productReferences", recommendationsAvailable);
		model.addAttribute("numberOfItems", recommendationsAvailable.size());

		return VIEWNAME;
	}
}