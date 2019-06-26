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
package de.hybris.platform.commercefacades.basestores.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BaseStorePopulatorTest
{
	public static final String BASE_STORE_NAME = "baseStoreName";
	public static final boolean EXTERNAL_TAX_ENABLED = true;
	public static final String PAYMENT_PROVIDER = "Cybersource";
	public static final String CHECKOUT_FLOW_GROUP = "checkoutFlowGroup";
	public static final String RETURN_PROCESS_CODE = "returnProcessCode";
	public static final boolean EXPRESS_CHECKOUT_ENABLE = true;
	public static final Double MAX_RADIUS_FOR_POS_SEARCH = 2D;
	public static final String SUBMIT_ORDER_CODE = "submitOrderCode";
	public static final boolean TAX_ESTIMATION_ENABLED = true;

	@InjectMocks
	BaseStorePopulator baseStorePopulator = new BaseStorePopulator();

	@Mock
	private BaseStoreModel baseStoreModel;

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	@Mock
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;
	@Mock
	private Converter<LanguageModel, LanguageData> languageConverter;
	@Mock
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	@Mock
	private Converter<CountryModel, CountryData> countryConverter;

	@Mock
	private CurrencyModel currencyModel;
	@Mock
	private CurrencyData currencyData;
	@Mock
	private PointOfServiceModel pointOfServiceModel;
	@Mock
	private PointOfServiceData pointOfServiceData;
	@Mock
	private LanguageModel languageModel;
	@Mock
	private LanguageData languageData;
	@Mock
	private DeliveryModeModel deliveryModeModel;
	@Mock
	private DeliveryModeData deliveryModeData;
	@Mock
	private CountryModel countryModel;
	@Mock
	private CountryData countryData;

	private BaseStoreData baseStoreData;

	@Before
	public void setUp()
	{
		baseStoreData = new BaseStoreData();

		when(baseStoreModel.getName()).thenReturn(BASE_STORE_NAME);
		when(baseStoreModel.getExternalTaxEnabled()).thenReturn(EXTERNAL_TAX_ENABLED);
		when(baseStoreModel.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);
		when(baseStoreModel.getCreateReturnProcessCode()).thenReturn(RETURN_PROCESS_CODE);
		when(baseStoreModel.getMaxRadiusForPoSSearch()).thenReturn(MAX_RADIUS_FOR_POS_SEARCH);
		when(baseStoreModel.getSubmitOrderProcessCode()).thenReturn(SUBMIT_ORDER_CODE);

		when(baseStoreModel.getDefaultCurrency()).thenReturn(currencyModel);
		when(baseStoreModel.getDefaultDeliveryOrigin()).thenReturn(pointOfServiceModel);
		when(baseStoreModel.getDefaultLanguage()).thenReturn(languageModel);

		when(baseStoreModel.getDeliveryCountries()).thenReturn(Arrays.asList(countryModel));
		when(baseStoreModel.getPointsOfService()).thenReturn(Arrays.asList(pointOfServiceModel));
		when(baseStoreModel.getCurrencies()).thenReturn(Stream.of(currencyModel).collect(Collectors.toSet()));
		when(baseStoreModel.getLanguages()).thenReturn(Stream.of(languageModel).collect(Collectors.toSet()));
		when(baseStoreModel.getDeliveryModes()).thenReturn(Stream.of(deliveryModeModel).collect(Collectors.toSet()));

		doReturn(currencyData).when(currencyConverter).convert(currencyModel);
		doReturn(pointOfServiceData).when(pointOfServiceConverter).convert(pointOfServiceModel);
		doReturn(languageData).when(languageConverter).convert(languageModel);
		doReturn(deliveryModeData).when(deliveryModeConverter).convert(deliveryModeModel);
		doReturn(countryData).when(countryConverter).convert(countryModel);
	}

	@Test
	public void populate()
	{
		//When
		baseStorePopulator.populate(baseStoreModel, baseStoreData);

		assertEquals(BASE_STORE_NAME, baseStoreData.getName());
		assertEquals(EXTERNAL_TAX_ENABLED, baseStoreData.isExternalTaxEnabled());
		assertEquals(PAYMENT_PROVIDER, baseStoreData.getPaymentProvider());
		assertEquals(RETURN_PROCESS_CODE, baseStoreData.getCreateReturnProcessCode());
		assertEquals(MAX_RADIUS_FOR_POS_SEARCH, baseStoreData.getMaxRadiusForPosSearch());
		assertEquals(SUBMIT_ORDER_CODE, baseStoreData.getSubmitOrderProcessCode());

		assertEquals(currencyData, baseStoreData.getDefaultCurrency());
		assertEquals(pointOfServiceData, baseStoreData.getDefaultDeliveryOrigin());
		assertEquals(languageData, baseStoreData.getDefaultLanguage());

		assertEquals(1, baseStoreData.getDeliveryModes().getDeliveryModes().size());
		assertEquals(deliveryModeData, baseStoreData.getDeliveryModes().getDeliveryModes().get(0));
		assertEquals(1, baseStoreData.getDeliveryCountries().size());
		assertEquals(countryData, baseStoreData.getDeliveryCountries().get(0));
	}

}
