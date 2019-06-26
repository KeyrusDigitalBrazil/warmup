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
package de.hybris.platform.commercefacades.basestores;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Integration test suite for {@link de.hybris.platform.commercefacades.basestores.impl.DefaultBaseStoreFacade}
 */
@IntegrationTest
public class DefaultBaseStoreFacadeIntegrationTest extends ServicelayerTest
{
	private static final Logger LOG = Logger.getLogger(DefaultBaseStoreFacadeIntegrationTest.class);

	private static final String LANG_EN = "en";
	private static final String BASE_STORE_UID = "defaultstore";
	private static final String BASE_STORE_EN_NAME = "Default Store";
	private static final String BASE_STORE_CURRENCY = "CHF";

	@Resource
	private BaseStoreFacade baseStoreFacade;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/platformservices/test/catalog/testdata_catalogVersion.csv", "UTF-8");
		importCsv("/commercefacades/test/testProductFacade.csv", "UTF-8");
		importCsv("/commercefacades/test/testBaseSite.csv", "UTF-8");
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));
	}

	@Test
	public void testGetBaseStoreByUid()
	{
		final BaseStoreData baseStoreData = baseStoreFacade.getBaseStoreByUid(BASE_STORE_UID);

		assertNotNull(baseStoreData);

		assertEquals(BASE_STORE_EN_NAME, baseStoreData.getName());
		assertEquals(2, baseStoreData.getLanguages().size());
		assertEquals(LANG_EN, baseStoreData.getDefaultLanguage().getIsocode());
		assertEquals(1, baseStoreData.getCurrencies().size());
		assertEquals(BASE_STORE_CURRENCY, baseStoreData.getDefaultCurrency().getIsocode());
	}

}
