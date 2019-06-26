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

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hybris.ymkt.recommendation.model.CMSSAPRecommendationComponentModel;
import com.hybris.ymkt.recommendationaddon.constants.SapymktrecommendationaddonConstants;


/**
 * Controller for CMS CMSSAPRecommendationComponentController
 */
@Controller("CMSSAPRecommendationComponentController")
@RequestMapping(value = "/view/CMSSAPRecommendationComponentController")
public class CMSSAPRecommendationComponentController
		extends AbstractCMSAddOnComponentController<CMSSAPRecommendationComponentModel>
{
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final CMSSAPRecommendationComponentModel component)
	{
		final RequestContextData requestContext = this.getRequestContextData(request);

		final List<String> categoryCodes = new ArrayList<>();

		Optional.ofNullable(requestContext.getSearch()) //
				.map(FacetSearchPageData.class::cast) //
				.map(FacetSearchPageData::getBreadcrumbs) //
				.<Stream<?>> map(List::stream).orElse(Stream.empty()) //
				.map(BreadcrumbData.class::cast) //
				.filter(this::isCategory) //
				.map(BreadcrumbData::getFacetValueCode) //
				.forEach(categoryCodes::add);

		Optional.ofNullable(requestContext.getProduct()) //
				.map(ProductModel::getSupercategories) //
				.map(Collection::stream).orElse(Stream.empty()) //
				.map(CategoryModel::getCode) //
				.forEach(categoryCodes::add);

		Optional.ofNullable(requestContext.getCategory()).map(CategoryModel::getCode).ifPresent(categoryCodes::add);

		final String productCode = Optional.ofNullable(requestContext.getProduct()).map(ProductModel::getCode).orElse("");

		model.addAttribute("categoryCode", categoryCodes);
		model.addAttribute("productCode", productCode);

		model.addAttribute("componentId", component.getUid());
	}

	@Override
	protected String getAddonUiExtensionName(final CMSSAPRecommendationComponentModel component)
	{
		return SapymktrecommendationaddonConstants.EXTENSIONNAME;
	}

	protected boolean isCategory(final BreadcrumbData<?> data)
	{
		return "category".equals(data.getFacetCode());
	}
}
