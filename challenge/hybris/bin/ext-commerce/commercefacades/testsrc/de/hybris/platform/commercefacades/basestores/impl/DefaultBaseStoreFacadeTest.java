/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.commercefacades.basestores.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBaseStoreFacadeTest
{
	private static final String BASE_STORE_UID = "baseStore";
	private static final String OTHER_BASE_STORE_UID = "otherbaseStore";
	private static final String UNKONOWN_BASE_STORE_UID = "invalid";

	@InjectMocks
	DefaultBaseStoreFacade baseStoreFacade = new DefaultBaseStoreFacade();

	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private Converter<BaseStoreModel, BaseStoreData> baseStoreConverter;
	@Mock
	private BaseStoreData baseStoreData;

	@Before
	public void setUp()
	{
		when(baseStoreModel.getUid()).thenReturn(BASE_STORE_UID);
		when(baseStoreService.getBaseStoreForUid(BASE_STORE_UID)).thenReturn(baseStoreModel);

		when(baseStoreConverter.convert(baseStoreModel)).thenReturn(baseStoreData);
	}

	@Test
	public void getBaseStoreByUid()
	{
		//When
		BaseStoreData dataObject = baseStoreFacade.getBaseStoreByUid(BASE_STORE_UID);
		//Then
		assertEquals(baseStoreData, dataObject);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getBaseStoreByUidUnknown()
	{
		// Given
		when(baseStoreService.getBaseStoreForUid(UNKONOWN_BASE_STORE_UID)).thenThrow(UnknownIdentifierException.class);

		//When
		baseStoreFacade.getBaseStoreByUid(UNKONOWN_BASE_STORE_UID);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void getBaseStoreByUidAmbiguous()
	{
		//Given
		// Given
		when(baseStoreService.getBaseStoreForUid(OTHER_BASE_STORE_UID)).thenThrow(AmbiguousIdentifierException.class);
		//When
		baseStoreFacade.getBaseStoreByUid(OTHER_BASE_STORE_UID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getBaseStoreByUidNull()
	{
		//When
		baseStoreFacade.getBaseStoreByUid(null);
	}
}
