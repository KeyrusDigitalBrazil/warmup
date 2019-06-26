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
package de.hybris.platform.customerinterestsfacades.productinterest.impl;

import static java.util.Objects.nonNull;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestEntryData;
import de.hybris.platform.customerinterestsfacades.data.ProductInterestRelationData;
import de.hybris.platform.customerinterestsfacades.productinterest.ProductInterestFacade;
import de.hybris.platform.customerinterestsfacades.strategies.CollectionSortStrategy;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.ProductInterestService;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ProductInterestFacade}
 */
public class DefaultProductInterestFacade implements ProductInterestFacade
{
	private ProductInterestService productInterestService;
	private Converter<ProductInterestModel, ProductInterestData> productInterestConverter;
	private Converter<ProductInterestData, ProductInterestModel> productInterestReverseConverter;
	private ProductService productService;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private Converter<Entry<ProductModel, List<ProductInterestEntryData>>, ProductInterestRelationData> productInterestRelationConverter;
	private Converter<Entry<NotificationType, Date>, ProductInterestEntryData> productInterestEntryConverter;
	private Map<String, CollectionSortStrategy> productInterestsSortStrategies;

	@Override
	public void saveProductInterest(final ProductInterestData productInterest)
	{
		final ProductInterestModel modifiedproductInterest = getProductInterest(productInterest.getProduct().getCode(),
				productInterest.getNotificationType()).orElse(new ProductInterestModel());
		getProductInterestReverseConverter().convert(productInterest, modifiedproductInterest);
		getProductInterestService().saveProductInterest(modifiedproductInterest);
	}

	@Override
	public void removeProductInterest(final ProductInterestData productInterest)
	{
		getProductInterest(productInterest.getProduct().getCode(), productInterest.getNotificationType())
				.ifPresent(x -> getProductInterestService().removeProductInterest(x));
	}

	@Override
	public Optional<ProductInterestData> getProductInterestDataForCurrentCustomer(final String productcode,
			final NotificationType notificationType)
	{
		return getProductInterest(productcode, notificationType)
				.map(productInterestsInterestModel -> getProductInterestConverter().convert(productInterestsInterestModel));
	}


	@Override
	public void removeAllProductInterests(final String productCode)
	{
		getProductInterestService().removeAllProductInterests(productCode);
	}

	protected Optional<ProductInterestModel> getProductInterest(final String productcode, final NotificationType notificationType)
	{
		final ProductModel product = getProductService().getProductForCode(productcode);
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		return getProductInterestService().getProductInterest(product, customer, notificationType, baseStore, baseSite);
	}

	@Override
	public List<ProductInterestRelationData> getProductsByCustomerInterests(final PageableData pageableData)
	{
		final List<ProductInterestRelationData> interestsList = buildProductInterestRelationData(
				productInterestService.getProductsByCustomerInterests(pageableData));
		final String sort = pageableData.getSort();
		if (StringUtils.isNotEmpty(sort))
		{
			final CollectionSortStrategy sortStrategy = productInterestsSortStrategies.get("name");
			if ("byNameAsc".equals(sort.trim()))
			{
				sortStrategy.ascendingSort(interestsList);
			}
			else
			{
				sortStrategy.descendingSort(interestsList);
			}
		}

		return interestsList;
	}

	@Override
	public SearchPageData<ProductInterestRelationData> getPaginatedProductInterestsByCustomer(final SearchPageData searchPageData)
	{

		final List<ProductInterestRelationData> interestsList = buildProductInterestRelationData(
				productInterestService.findProductInterestsByCustomer());
		sortingProductInterestsList(searchPageData, interestsList);
		return pagingProductInterestsList(searchPageData, interestsList);
	}

	protected void sortingProductInterestsList(final SearchPageData searchPageData,
			final List<ProductInterestRelationData> interestsList)
	{
		final List<SortData> sorts = searchPageData.getSorts();
		if (CollectionUtils.isNotEmpty(sorts))
		{
			final boolean isAsc = sorts.get(0).isAsc();
			final CollectionSortStrategy sortStrategy = productInterestsSortStrategies.get(sorts.get(0).getCode().toLowerCase(Locale.ROOT));
			if (nonNull(sortStrategy))
			{
				if (isAsc)
				{
					sortStrategy.ascendingSort(interestsList);
				}
				else
				{
					sortStrategy.descendingSort(interestsList);
				}
			}
			else
			{
				searchPageData.setSorts(Collections.emptyList());
			}
		}
	}

	protected SearchPageData<ProductInterestRelationData> pagingProductInterestsList(final SearchPageData searchPageData,
			final List<ProductInterestRelationData> interestsList)
	{
		final PaginationData paginationDataInput = searchPageData.getPagination();
		final List<ProductInterestRelationData> pagedList = interestsList.stream()
				.skip(paginationDataInput.getCurrentPage() * (long) paginationDataInput.getPageSize())
				.limit(paginationDataInput.getPageSize()).collect(Collectors.toList());

		final PaginationData paginationDataOutput = new PaginationData();
		paginationDataOutput.setPageSize(paginationDataInput.getPageSize());
		paginationDataOutput.setNeedsTotal(paginationDataInput.isNeedsTotal());
		paginationDataOutput.setTotalNumberOfResults(paginationDataInput.isNeedsTotal() ? interestsList.size() : pagedList.size());
		paginationDataOutput.setNumberOfPages((int) Math
				.ceil((double) paginationDataOutput.getTotalNumberOfResults() / (double) paginationDataOutput.getPageSize()));
		paginationDataOutput
				.setCurrentPage(Math.max(0, Math.min(paginationDataOutput.getNumberOfPages(), paginationDataInput.getCurrentPage())));

		final SearchPageData<ProductInterestRelationData> pagedResult = new SearchPageData<>();
		pagedResult.setPagination(paginationDataOutput);
		pagedResult.setResults(pagedList);
		pagedResult.setSorts(searchPageData.getSorts());
		return pagedResult;
	}

	protected List<ProductInterestRelationData> buildProductInterestRelationData(
			final Map<ProductModel, Map<NotificationType, Date>> productNotificationMap)
	{
		final Map<ProductModel, List<ProductInterestEntryData>> productProductInterestMap = new LinkedHashMap<>();
		productNotificationMap.forEach((product, interestTypeMap) -> {
			final List<ProductInterestEntryData> productInterestEntries = getProductInterestEntryConverter()
					.convertAll(interestTypeMap.entrySet());
			productProductInterestMap.put(product, productInterestEntries);
		});

		final Set<Entry<ProductModel, List<ProductInterestEntryData>>> productNotifications = productProductInterestMap.entrySet();
		final List<ProductInterestRelationData> productInterestRelations = getProductInterestRelationConverter()
				.convertAll(productNotifications);
		return productInterestRelations;

	}

	@Override
	public int getProductsCountByCustomerInterests(final PageableData pageableData)
	{

		return productInterestService.getProductsCountByCustomerInterests(pageableData);
	}


	protected ProductInterestService getProductInterestService()
	{
		return productInterestService;
	}

	@Required
	public void setProductInterestService(final ProductInterestService productInterestService)
	{
		this.productInterestService = productInterestService;
	}

	protected Converter<ProductInterestModel, ProductInterestData> getProductInterestConverter()
	{
		return productInterestConverter;
	}

	@Required
	public void setProductInterestConverter(final Converter<ProductInterestModel, ProductInterestData> productInterestConverter)
	{
		this.productInterestConverter = productInterestConverter;
	}

	protected Converter<ProductInterestData, ProductInterestModel> getProductInterestReverseConverter()
	{
		return productInterestReverseConverter;
	}

	@Required
	public void setProductInterestReverseConverter(
			final Converter<ProductInterestData, ProductInterestModel> productInterestReverseConverter)
	{
		this.productInterestReverseConverter = productInterestReverseConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected Converter<Entry<ProductModel, List<ProductInterestEntryData>>, ProductInterestRelationData> getProductInterestRelationConverter()
	{
		return productInterestRelationConverter;
	}

	@Required
	public void setProductInterestRelationConverter(
			final Converter<Entry<ProductModel, List<ProductInterestEntryData>>, ProductInterestRelationData> productInterestRelationConverter)
	{
		this.productInterestRelationConverter = productInterestRelationConverter;
	}

	protected Converter<Entry<NotificationType, Date>, ProductInterestEntryData> getProductInterestEntryConverter()
	{
		return productInterestEntryConverter;
	}

	@Required
	public void setProductInterestEntryConverter(
			final Converter<Entry<NotificationType, Date>, ProductInterestEntryData> productInterestEntryConverter)
	{
		this.productInterestEntryConverter = productInterestEntryConverter;
	}

	protected Map<String, CollectionSortStrategy> getProductInterestsSortStrategies()
	{
		return productInterestsSortStrategies;
	}

	@Required
	public void setProductInterestsSortStrategies(final Map<String, CollectionSortStrategy> productInterestsSortStrategies)
	{
		this.productInterestsSortStrategies = productInterestsSortStrategies;
	}


}
