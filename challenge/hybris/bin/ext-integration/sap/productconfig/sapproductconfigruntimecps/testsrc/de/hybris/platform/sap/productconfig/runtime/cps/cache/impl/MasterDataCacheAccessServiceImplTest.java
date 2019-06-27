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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class MasterDataCacheAccessServiceImplTest
{
	private static final String LANGUAGE = "language";
	private static final String KB_ID = "kbId";
	private MasterDataCacheAccessServiceImpl classUnderTest;

	@Mock
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> cache;
	@Mock
	private CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> loader;
	@Mock
	private CPSCacheKeyGenerator keyGenerator;
	@Mock
	private ProductConfigurationCacheKey masterDataCacheKey;

	private CPSMasterDataKnowledgeBaseContainer kbContainer;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new MasterDataCacheAccessServiceImpl();
		classUnderTest.setCache(cache);
		classUnderTest.setLoader(loader);
		classUnderTest.setKeyGenerator(keyGenerator);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();

		when(cache.getWithLoader(Mockito.any(), Mockito.any())).thenReturn(kbContainer);
		when(keyGenerator.createMasterDataCacheKey(KB_ID, LANGUAGE)).thenReturn(masterDataCacheKey);
	}

	@Test
	public void testGetKB()
	{
		final CPSMasterDataKnowledgeBaseContainer result = classUnderTest.getKbContainer(KB_ID, LANGUAGE);
		Mockito.verify(cache).getWithLoader(Mockito.any(), Mockito.any());
		assertNotNull(result);
		assertEquals(kbContainer, result);
	}

	@Test
	public void testRemoveKbContainer()
	{
		classUnderTest.removeKbContainer(KB_ID, LANGUAGE);
		verify(keyGenerator).createMasterDataCacheKey(KB_ID, LANGUAGE);
		verify(cache).remove(masterDataCacheKey);
	}

	@Test
	public void testClearCache()
	{
		classUnderTest.clearCache();
		verify(cache).clearCache();
	}
}
