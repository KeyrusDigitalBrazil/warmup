/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service.impl;

import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexedTypeDao;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.merchandising.dao.MerchIndexingConfigDao;
import com.hybris.merchandising.model.MerchIndexingConfigModel;
import com.hybris.merchandising.service.MerchIndexingConfigService;

/**
 * Default implementation of {@link MerchIndexingConfigService}.
 */
public class DefaultMerchIndexingConfigService implements MerchIndexingConfigService
{

	protected static final String ID_PARAM = "id";
	private SolrIndexedTypeDao solrIndexedTypeDao;
	private MerchIndexingConfigDao merchIndexingConfigDao;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<MerchIndexingConfigModel> getAllMerchIndexingConfigs()
	{
		return merchIndexingConfigDao.findAllMerchIndexingConfigs();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<MerchIndexingConfigModel> getMerchIndexingConfigForIndexedType(final String indexedType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage(ID_PARAM, indexedType);

		final SolrIndexedTypeModel solrIndexedType = solrIndexedTypeDao.findIndexedTypeByIdentifier(indexedType);

		return merchIndexingConfigDao.findMerchIndexingConfigByIndexedType(solrIndexedType);
	}

	public SolrIndexedTypeDao getSolrIndexedTypeDao()
	{
		return solrIndexedTypeDao;
	}

	@Required
	public void setSolrIndexedTypeDao(SolrIndexedTypeDao solrIndexedTypeDao)
	{
		this.solrIndexedTypeDao = solrIndexedTypeDao;
	}

	public MerchIndexingConfigDao getMerchIndexingConfigDao()
	{
		return merchIndexingConfigDao;
	}

	@Required
	public void setMerchIndexingConfigDao(MerchIndexingConfigDao merchIndexingConfigDao)
	{
		this.merchIndexingConfigDao = merchIndexingConfigDao;
	}

}
