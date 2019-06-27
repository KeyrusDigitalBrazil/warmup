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
package de.hybris.platform.acceleratorfacades.productcarousel.impl;

import static de.hybris.platform.cms2.misc.CMSFilter.PREVIEW_TICKET_ID_PARAM;

import de.hybris.platform.acceleratorcms.productcarousel.ProductCarouselRendererService;
import de.hybris.platform.acceleratorfacades.productcarousel.ProductCarouselFacade;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facade to fetch list of products for a given product carousel component.
 */
public class DefaultProductCarouselFacade implements ProductCarouselFacade
{

	private ProductFacade productFacade;

	private SessionService sessionService;

	private ModelService modelService;

	private ProductCarouselRendererService productCarouselRendererService;

	private SearchRestrictionService searchRestrictionService;

	private Converter<ProductModel, ProductData> productConverter;

	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator;

	private CMSPreviewService cmsPreviewService;

	protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

	@Override
	public List<ProductData> collectProducts(final ProductCarouselComponentModel component)
	{
		if (!isPreview())
		{
			return fetchProductsForNonPreviewMode(component);
		}
		else
		{
			return fetchProductsForPreviewMode(component);
		}
	}

	/**
	 * Fetches list of products for a given product carousel component when not in preview (i.e., no cmsTicketId in
	 * present in the session).
	 *
	 * @param component
	 *           The product carousel component model
	 * @return List<ProductData> list of available products
	 */
	protected List<ProductData> fetchProductsForNonPreviewMode(final ProductCarouselComponentModel component)
	{

		final List<ProductData> products = new ArrayList<>();

		for (final ProductModel productModel : component.getProducts())
		{
			products.add(getProductFacade().getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
		}

		for (final CategoryModel categoryModel : component.getCategories())
		{
			for (final ProductModel productModel : categoryModel.getProducts())
			{
				products.add(getProductFacade().getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS));
			}
		}

		return products;

	}

	/**
	 * Fetches list of products for a given product carousel component when in preview (i.e., cmsTicketId in present in
	 * the session).
	 *
	 * @param component
	 *           The product carousel component model
	 * @return List<ProductData> list of available products
	 */
	protected List<ProductData> fetchProductsForPreviewMode(final ProductCarouselComponentModel component)
	{

		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{

			@Override
			public Object execute()
			{
				try
				{
					getSearchRestrictionService().disableSearchRestrictions();

					final List<ProductData> products = new ArrayList<>();

					for (final ProductModel productModel : getDisplayableProductsForProductCarousel(component))
					{
						products.add(getProductForOptions(productModel, PRODUCT_OPTIONS));
					}

					for (final CategoryModel categoryModel : getListOfCategoriesForProductCarousel(component))
					{
						for (final ProductModel productModel : getDisplayableProductsForCategory(categoryModel))
						{
							products.add(getProductForOptions(productModel, PRODUCT_OPTIONS));
						}
					}

					return products;


				}
				finally
				{
					getSearchRestrictionService().enableSearchRestrictions();
				}
			}

		});

	}

	/**
	 * Convert from productModel to productData
	 *
	 * @param productModel
	 *           The product model
	 * @param options
	 *           The product options
	 * @return productData The product data
	 */
	protected ProductData getProductForOptions(final ProductModel productModel, final Collection<ProductOption> options)
	{
		final ProductData productData = getProductConverter().convert(productModel);

		if (options != null)
		{
			getProductConfiguredPopulator().populate(productModel, productData, options);
		}

		return productData;
	}

	/**
	 * Checks if we are in preview mode by checking the presence of a cmsTicketId in session.
	 *
	 * @return true if in preview mode
	 */
	protected boolean isPreview()
	{
		return getSessionService().getAttribute(PREVIEW_TICKET_ID_PARAM) != null;
	}

	/**
	 * If in versioning preview then returns the products from the provided {@link ProductCarouselComponentModel}
	 * else returns the full list of products without the session catalog version filtering out the ones from different
	 * versions. This is needed when the session catalog version is not the active version. This is possible through CMS
	 * tooling.
	 *
	 * @param productCarouselComponentModel
	 *           the product carousel model
	 * @return a list of {@link ProductModel}
	 */
	protected List<ProductModel> getDisplayableProductsForProductCarousel(final ProductCarouselComponentModel component)
	{
		return getProductCarouselRendererService().getDisplayableProducts(refreshComponent(component));
	}

	/**
	 * If in versioning preview then returns the categories from the provided {@link CategoryModel}
	 * else returns the full list of categories without the session catalog version filtering out the ones from different
	 * versions.
	 * This is needed when the session catalog version is not the active version. This is possible through CMS tooling
	 *
	 * @param productCarouselComponentModel
	 *           the product carousel model
	 * @return a list of {@link CategoryModel}
	 */
	protected List<CategoryModel> getListOfCategoriesForProductCarousel(final ProductCarouselComponentModel component)
	{
		return refreshComponent(component).getCategories();
	}

	/**
	 * If in versioning preview then returns the products from the provided {@link CategoryModel}
	 * else returns the full list of products without the session catalog version filtering out the ones from different
	 * versions.
	 * This is needed when the session catalog version is not the active version. This is possible through CMS tooling
	 *
	 * @param CategoryModel
	 *           the category model
	 * @return a list of {@link ProductModel}
	 */
	protected List<ProductModel> getDisplayableProductsForCategory(final CategoryModel component)
	{
		return getProductCarouselRendererService().getDisplayableProducts(refreshComponent(component));
	}

	/**
	 * Returns the component based on the type of preview.
	 * If in versioning preview, returns the component as is, else returns component by re-fetching it again without the
	 * session catalog version.
	 *
	 * @param component
	 *           the component
	 * @return the refreshed component
	 */
	protected <T extends ItemModel> T refreshComponent(final T component)
	{
		return getCmsPreviewService().isVersioningPreview() ? component : getModelService().get(component.getPk());
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected ProductCarouselRendererService getProductCarouselRendererService()
	{
		return productCarouselRendererService;
	}

	@Required
	public void setProductCarouselRendererService(final ProductCarouselRendererService productCarouselRendererService)
	{
		this.productCarouselRendererService = productCarouselRendererService;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductConfiguredPopulator()
	{
		return productConfiguredPopulator;
	}

	@Required
	public void setProductConfiguredPopulator(
			final ConfigurablePopulator<ProductModel, ProductData, ProductOption> productConfiguredPopulator)
	{
		this.productConfiguredPopulator = productConfiguredPopulator;
	}

	protected CMSPreviewService getCmsPreviewService()
	{
		return cmsPreviewService;
	}

	@Required
	public void setCmsPreviewService(final CMSPreviewService cmsPreviewService)
	{
		this.cmsPreviewService = cmsPreviewService;
	}

}
