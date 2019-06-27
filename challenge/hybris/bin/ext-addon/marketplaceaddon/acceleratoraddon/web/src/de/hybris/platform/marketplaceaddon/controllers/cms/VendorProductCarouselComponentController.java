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
package de.hybris.platform.marketplaceaddon.controllers.cms;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.marketplaceaddon.controllers.MarketplaceaddonControllerConstants;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS ProductCarouselComponent.
 */
@Controller
@RequestMapping(value = MarketplaceaddonControllerConstants.Actions.Cms.ProductCarouselComponent)
public class VendorProductCarouselComponentController
		extends GenericMarketplaceCMSComponentController<ProductCarouselComponentModel>
{
	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "imageConverter")
	private Converter<MediaModel, ImageData> imageConverter;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ProductCarouselComponentModel component)
	{
		super.fillModel(request, model, component);
		final List<ProductData> products = new ArrayList<>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));

		model.addAttribute("title", component.getTitle());
		model.addAttribute("productData", products);

		// set logo and url of vendor
		final MediaModel logo = component.getMedia();
		if (logo != null)
		{
			model.addAttribute("vendorLogo", imageConverter.convert(logo));
		}
		model.addAttribute("landingPage", component.getUrl());
	}

	protected List<ProductData> collectLinkedProducts(final ProductCarouselComponentModel component)
	{
		final List<ProductData> products = new ArrayList<>();

		for (final ProductModel productModel : component.getProducts())
		{
			products.add(productFacade.getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
		}

		for (final CategoryModel categoryModel : component.getCategories())
		{
			for (final ProductModel productModel : categoryModel.getProducts())
			{
				products.add(productFacade.getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
			}
		}

		return products;
	}

	protected List<ProductData> collectSearchProducts(final ProductCarouselComponentModel component)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(component.getSearchQuery());
		final String categoryCode = component.getCategoryCode();

		if (searchQueryData.getValue() != null && categoryCode != null)
		{
			final SearchStateData searchState = new SearchStateData();
			searchState.setQuery(searchQueryData);

			final PageableData pageableData = new PageableData();
			pageableData.setPageSize(100); // Limit to 100 matching results

			return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
		}

		return Collections.emptyList();
	}
}
