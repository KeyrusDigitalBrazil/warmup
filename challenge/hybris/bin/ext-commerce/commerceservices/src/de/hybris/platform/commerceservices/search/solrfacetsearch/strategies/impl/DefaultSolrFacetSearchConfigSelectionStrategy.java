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
package de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.SolrFacetSearchConfigSelectionStrategy;
import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SolrFacetSearchConfigSelectionStrategy} that checks:
 * <ul>
 * <li>SolrFacetSearchConfig that is bound with current base site</li>
 * <li>SolrFacetSearchConfig that is bound with current base store</li>
 * <li>SolrFacetSearchConfig that is bound with current product catalog versions</li>
 * </ul>
 *
 *
 *
 */
public class DefaultSolrFacetSearchConfigSelectionStrategy implements SolrFacetSearchConfigSelectionStrategy
{

	private BaseSiteService baseSiteService;
	private BaseStoreService baseStoreService;
	private CatalogVersionService catalogVersionService;
	private SolrFacetSearchConfigDao solrFacetSearchConfigDao;


	@Override
	public SolrFacetSearchConfigModel getCurrentSolrFacetSearchConfig() throws NoValidSolrConfigException
	{
		SolrFacetSearchConfigModel result = getSolrConfigForBaseSite();
		if (result == null)
		{
			result = getSolrConfigForBaseStore();
		}
		if (result == null)
		{
			result = getSolrConfigForProductCatalogVersions();
		}
		if (result == null)
		{
			throw new NoValidSolrConfigException(
					"No Valid SolrFacetSearchConfig configured neither for base site/base store/session product catalog versions.");
		}
		return result;
	}



	protected SolrFacetSearchConfigModel getSolrConfigForProductCatalogVersions()
	{
		final Collection<CatalogVersionModel> sessionProductCatalogVersions = getSessionProductCatalogVersions();
		for (final SolrFacetSearchConfigModel solrConfigModel : solrFacetSearchConfigDao.findAllFacetSearchConfigs())
		{
			if (solrConfigModel.getCatalogVersions() != null
					&& solrConfigModel.getCatalogVersions().containsAll(sessionProductCatalogVersions))
			{
				return solrConfigModel;
			}
		}
		return null;
	}

	protected SolrFacetSearchConfigModel getSolrConfigForBaseStore()
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		if (currentBaseStore != null)
		{
			return currentBaseStore.getSolrFacetSearchConfiguration();
		}
		return null;
	}

	protected SolrFacetSearchConfigModel getSolrConfigForBaseSite()
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		if (currentBaseSite != null)
		{
			return currentBaseSite.getSolrFacetSearchConfiguration();
		}
		return null;
	}

	protected Collection<CatalogVersionModel> getSessionProductCatalogVersions()
	{
		final BaseSiteModel currentSite = getBaseSiteService().getCurrentBaseSite();
		final List<CatalogModel> productCatalogs = getBaseSiteService().getProductCatalogs(currentSite);

		final Collection<CatalogVersionModel> sessionCatalogVersions = getCatalogVersionService().getSessionCatalogVersions();

		final Collection<CatalogVersionModel> result = new ArrayList();
		if (CollectionUtils.isNotEmpty(sessionCatalogVersions) && CollectionUtils.isNotEmpty(productCatalogs))
		{
			for (final CatalogVersionModel sessionCatalogVersion : sessionCatalogVersions)
			{
				if (sessionCatalogVersion != null && productCatalogs.contains(sessionCatalogVersion.getCatalog()))
				{
					result.add(sessionCatalogVersion);
				}
			}
		}
		return result;
	}


	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public SolrFacetSearchConfigDao getSolrFacetSearchConfigDao()
	{
		return solrFacetSearchConfigDao;
	}

	@Required
	public void setSolrFacetSearchConfigDao(final SolrFacetSearchConfigDao solrFacetSearchConfigDao)
	{
		this.solrFacetSearchConfigDao = solrFacetSearchConfigDao;
	}
}
