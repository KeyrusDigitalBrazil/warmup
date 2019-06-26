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
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.rules.rao.BaseStoreRAO;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigBaseStoreRAOProviderTest
{
	private ProductConfigBaseStoreRAOProvider classUnderTest;

	@Mock
	BaseStoreService baseStoreService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigBaseStoreRAOProvider();
		classUnderTest.setBaseStoreService(baseStoreService);
	}

	@Test
	public void testExpandFactModel()
	{
		final String baseStoreUid = "basestore";
		final BaseStoreModel baseStore = new BaseStoreModel();
		baseStore.setUid(baseStoreUid);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);

		final Set<BaseStoreRAO> result = classUnderTest.expandFactModel(null);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		final BaseStoreRAO baseStoreRAO = result.iterator().next();
		assertEquals(baseStoreUid, baseStoreRAO.getUid());
	}

	@Test
	public void testExpandFactModelNoBaseStore()
	{
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(null);

		final Set<BaseStoreRAO> result = classUnderTest.expandFactModel(null);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}


}
