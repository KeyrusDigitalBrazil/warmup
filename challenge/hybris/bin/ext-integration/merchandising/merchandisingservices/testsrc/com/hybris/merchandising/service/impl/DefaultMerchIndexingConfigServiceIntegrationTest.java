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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang.CharEncoding;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.hybris.merchandising.model.MerchIndexingConfigModel;
import com.hybris.merchandising.model.MerchPropertyModel;
import com.hybris.merchandising.service.MerchIndexingConfigService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;


@IntegrationTest
public class DefaultMerchIndexingConfigServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String LANGUAGE_EN_ISOCODE = "en";
	private static final String LANGUAGE_DE_ISOCODE = "de";
	private static final String CURRENCY_GBP_ISOCODE = "GBP";
	private static final String CURRENCY_EUR_ISOCODE = "EUR";

	private static final String INDEXED_TYPE_1_ID = "solrIndexedType1";
	private static final String INDEXED_TYPE_2_ID = "solrIndexedType2";
	private static final String NOT_EXISTING_INDEXED_TYPE_ID = "notExistingIndexedTypeId";

	private static final String INDEXED_PROPERTY_11_NAME = "solrIndexedProperty11";
	private static final String INDEXED_PROPERTY_12_NAME = "solrIndexedProperty12";
	private static final String INDEXED_PROPERTY_13_NAME = "solrIndexedProperty13";
	private static final String INDEXED_PROPERTY_21_NAME = "solrIndexedProperty21";
	private static final String INDEXED_PROPERTY_22_NAME = "solrIndexedProperty22";
	private static final String INDEXED_PROPERTY_23_NAME = "solrIndexedProperty23";

	private static final String INDEXED_MERCH_MAPPED_PROPERTY_11_NAME = "solrIndexedPropertyMerchMappedName11";
	private static final String INDEXED_MERCH_MAPPED_PROPERTY_22_NAME = "solrIndexedPropertyMerchMappedName22";
	private static final String INDEXED_MERCH_MAPPED_PROPERTY_23_NAME = "solrIndexedPropertyMerchMappedName23";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private MerchIndexingConfigService merchIndexingConfigService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/integration/DefaultMerchIndexingConfigServiceIntegrationTest.impex", CharEncoding.UTF_8);
	}

	@Test
	public void shouldFindAllMerchIndexingConfigs()
	{
		//when
		final Collection<MerchIndexingConfigModel> allMerchIndexingConfigs = merchIndexingConfigService
				.getAllMerchIndexingConfigs();

		//then
		assertThat(allMerchIndexingConfigs).hasSize(2);
	}

	@Test
	public void shouldFindEngGbpMerchIndexinConfig()
	{
		//when
		final Optional<MerchIndexingConfigModel> merchIndexingConfig = merchIndexingConfigService
				.getMerchIndexingConfigForIndexedType(INDEXED_TYPE_1_ID);

		//then
		assertTrue(merchIndexingConfig.isPresent());

		assertThat(merchIndexingConfig.get().getLanguage()).extracting(LanguageModel::getIsocode).containsOnly(LANGUAGE_EN_ISOCODE);
		assertThat(merchIndexingConfig.get().getCurrency()).extracting(CurrencyModel::getIsocode).containsOnly(CURRENCY_GBP_ISOCODE);
		assertThat(merchIndexingConfig.get().getMerchProperties())
				.extracting(MerchPropertyModel::getIndexedProperty)
				.extracting(SolrIndexedPropertyModel::getName)
				.containsExactly(INDEXED_PROPERTY_11_NAME, INDEXED_PROPERTY_12_NAME, INDEXED_PROPERTY_13_NAME);
		assertThat(merchIndexingConfig.get().getMerchProperties())
				.extracting(MerchPropertyModel::getMerchMappedName)
				.containsExactly(INDEXED_MERCH_MAPPED_PROPERTY_11_NAME, null, null);
	}


	@Test
	public void shouldFindDeEurMerchIndexinConfig()
	{
		//when
		final Optional<MerchIndexingConfigModel> merchIndexingConfig = merchIndexingConfigService
				.getMerchIndexingConfigForIndexedType(INDEXED_TYPE_2_ID);

		//then
		assertTrue(merchIndexingConfig.isPresent());

		assertThat(merchIndexingConfig.get().getLanguage()).extracting(LanguageModel::getIsocode).containsOnly(LANGUAGE_DE_ISOCODE);
		assertThat(merchIndexingConfig.get().getCurrency()).extracting(CurrencyModel::getIsocode).containsOnly(CURRENCY_EUR_ISOCODE);
		assertThat(merchIndexingConfig.get().getMerchProperties())
				.extracting(MerchPropertyModel::getIndexedProperty)
				.extracting(SolrIndexedPropertyModel::getName)
				.containsExactly(INDEXED_PROPERTY_21_NAME, INDEXED_PROPERTY_22_NAME, INDEXED_PROPERTY_23_NAME);
		assertThat(merchIndexingConfig.get().getMerchProperties())
				.extracting(MerchPropertyModel::getMerchMappedName)
				.containsExactly(null, INDEXED_MERCH_MAPPED_PROPERTY_22_NAME, INDEXED_MERCH_MAPPED_PROPERTY_23_NAME);
	}

	@Test(expected=UnknownIdentifierException.class)
	public void shouldNotFindMerchIndexinConfigByNotExistingIndexedTypeId()
	{
		final Optional<MerchIndexingConfigModel> merchIndexingConfig = merchIndexingConfigService
				.getMerchIndexingConfigForIndexedType(NOT_EXISTING_INDEXED_TYPE_ID);
	}
}
