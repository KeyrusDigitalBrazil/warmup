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
package de.hybris.platform.cmsfacades.common.service.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.common.service.ClassFieldFinder;
import de.hybris.platform.cmsfacades.common.service.ProductCatalogItemModelFinder;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;


/**
 * Default implementation of {@link ProductCatalogItemModelFinder} to find {@link ProductModel} and {@link CategoryModel} objects.
 */
public class DefaultProductCatalogItemModelFinder implements ProductCatalogItemModelFinder
{
	private SearchRestrictionService searchRestrictionService;
	private SessionService sessionService;
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	/**
	 * {@inheritDoc} by disabling search restrictions.
	 */
	@Override
	public List<ProductModel> getProductsForCompositeKeys(final List<String> compositeKeys)
	{
		if (CollectionUtils.isEmpty(compositeKeys))
		{
			return Lists.newArrayList();
		}

		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				try
				{
					getSearchRestrictionService().disableSearchRestrictions();
					return compositeKeys.stream()
							.map(itemId -> getItemModel(itemId, ProductModel.class)).collect(toList());
				}
				finally
				{
					getSearchRestrictionService().enableSearchRestrictions();
				}
			}
		});
	}

	/**
	 * {@inheritDoc} by disabling search restrictions.
	 */
	@Override
	public List<CategoryModel> getCategoriesForCompositeKeys(final List<String> compositeKeys)
	{
		if (CollectionUtils.isEmpty(compositeKeys))
		{
			return Lists.newArrayList();
		}

		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				try
				{
					getSearchRestrictionService().disableSearchRestrictions();
					return compositeKeys.stream()
							.map(itemId -> getItemModel(itemId, CategoryModel.class)).collect(toList());
				}
				finally
				{
					getSearchRestrictionService().enableSearchRestrictions();
				}
			}
		});
	}

	@Override
	public ProductModel getProductForCompositeKey(final String compositeKey)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				try
				{
					getSearchRestrictionService().disableSearchRestrictions();
					return getItemModel(compositeKey, ProductModel.class);
				}
				finally
				{
					getSearchRestrictionService().enableSearchRestrictions();
				}
			}
		});
	}

	@Override
	public CategoryModel getCategoryForCompositeKey(final String compositeKey)
	{
		return getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				try
				{
					getSearchRestrictionService().disableSearchRestrictions();
					return getItemModel(compositeKey, CategoryModel.class);
				}
				finally
				{
					getSearchRestrictionService().enableSearchRestrictions();
				}
			}
		});
	}

	protected <T> T getItemModel(final String itemId, final Class<T> clazz)
	{
		return getUniqueItemIdentifierService().getItemModel(itemId, clazz).get();
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}
}
