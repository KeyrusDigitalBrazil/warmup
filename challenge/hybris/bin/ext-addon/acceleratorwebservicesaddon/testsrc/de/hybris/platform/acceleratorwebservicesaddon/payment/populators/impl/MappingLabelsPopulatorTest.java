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
package de.hybris.platform.acceleratorwebservicesaddon.payment.populators.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionRequest;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorwebservicesaddon.populators.impl.MappingLabelsPopulator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_COMBINED_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_COMBINED_SEPARATOR_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_ACCOUNT_HOLDER_NAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_CITY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_COUNTRY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_FIRSTNAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_LASTNAME;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_POSTALCODE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_BILLTO_STREET1;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_CVN;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRATION_MONTH;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRATION_YEAR;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_EXPIRY_DATE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_NUMBER;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_CARD_TYPE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_AMOUNT;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_CARD_NUMBER;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_CURRENCY;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_DECISION;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_REASON_CODE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_SUBSCRIPTION_ID;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_LABEL_SOP_SUBSCRIPTION_ID_PUBLIC_SIGNATURE;
import static de.hybris.platform.acceleratorwebservicesaddon.constants.AcceleratorwebservicesaddonConstants.PAYMENT_USES_PUBLIC_SIGNATURE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MappingLabelsPopulatorTest
{
	private static final String BASE_SITE_NAME = "electronics";
	private static final String PAYMENT_PROVIDER = "provider";

	private static final String PAYMENT_ACCOUNT_HOLDER_NAME = "account holder name";
	private static final String PAYMENT_CARD_TYPE = "001";
	private static final String PAYMENT_CARD_CVN = "123";
	private static final String PAYMENT_CARD_EXPIRATION_MONTH = "10";
	private static final String PAYMENT_CARD_EXPIRATION_YEAR = "2020";
	private static final String PAYMENT_CARD_EXPIRY_DATE = "10-2020";
	private static final String PAYMENT_CARD_NUMBER = "4111111111111111";
	private static final String COMBINED_EXPIRY_DATE = "true";
	private static final String SEPARATOR_EXPIRY_DATE = "-";

	private static final String ADDRESS_BILLTO_COUNTRY = "Canada";
	private static final String ADDRESS_BILLTO_FIRSTNAME = "John";
	private static final String ADDRESS_BILLTO_LASTNAME = "Doe";
	private static final String ADDRESS_BILLTO_STREET1 = "999 Blvd Maisonneuve";
	private static final String ADDRESS_BILLTO_CITY = "Montreal";
	private static final String ADDRESS_BILLTO_POSTALCODE = "12345";

	private static final String SOP_DECISION = "ACCEPT";
	private static final String SOP_AMOUNT = "99.99";
	private static final String SOP_CURRENCY = "CAD";
	private static final String SOP_REASON_CODE = "100";
	private static final String SOP_CARD_NUMBER = "************1111";
	private static final String SOP_SUBSCRIPTION_ID = "456789";
	private static final String SOP_USES_PUBLIC_SIGNATURE = "true";
	private static final String SOP_PUBLIC_SIGNATURE = "bvkjbvlknvblksdnv";

	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private BaseStoreModel baseStoreModel;
	@Mock
	private CreateSubscriptionRequest createSubscriptionRequest;

	@InjectMocks
	private MappingLabelsPopulator mappingLabelsPopulator = new MappingLabelsPopulator();
	private PaymentData paymentData;


	@Before
	public void setUp()
	{
		paymentData = new PaymentData();

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(createSubscriptionRequest.getSiteName()).thenReturn(BASE_SITE_NAME);
		when(baseStoreService.getBaseStoreForUid(BASE_SITE_NAME)).thenReturn(baseStoreModel);
		when(baseStoreModel.getPaymentProvider()).thenReturn(PAYMENT_PROVIDER);

		//	Payment Info
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_ACCOUNT_HOLDER_NAME)).thenReturn(PAYMENT_ACCOUNT_HOLDER_NAME);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_TYPE)).thenReturn(PAYMENT_CARD_TYPE);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_CVN)).thenReturn(PAYMENT_CARD_CVN);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_EXPIRATION_MONTH))
				.thenReturn(PAYMENT_CARD_EXPIRATION_MONTH);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_EXPIRATION_YEAR))
				.thenReturn(PAYMENT_CARD_EXPIRATION_YEAR);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_EXPIRY_DATE)).thenReturn(PAYMENT_CARD_EXPIRY_DATE);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_CARD_NUMBER)).thenReturn(PAYMENT_CARD_NUMBER);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_COMBINED_EXPIRY_DATE)).thenReturn(COMBINED_EXPIRY_DATE);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_COMBINED_SEPARATOR_EXPIRY_DATE)).thenReturn(SEPARATOR_EXPIRY_DATE);

		// Address Info
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_COUNTRY)).thenReturn(ADDRESS_BILLTO_COUNTRY);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_FIRSTNAME)).thenReturn(ADDRESS_BILLTO_FIRSTNAME);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_LASTNAME)).thenReturn(ADDRESS_BILLTO_LASTNAME);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_STREET1)).thenReturn(ADDRESS_BILLTO_STREET1);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_CITY)).thenReturn(ADDRESS_BILLTO_CITY);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_BILLTO_POSTALCODE)).thenReturn(ADDRESS_BILLTO_POSTALCODE);

		// Silent order post info
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_DECISION)).thenReturn(SOP_DECISION);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_AMOUNT)).thenReturn(SOP_AMOUNT);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_CURRENCY)).thenReturn(SOP_CURRENCY);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_REASON_CODE)).thenReturn(SOP_REASON_CODE);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_CARD_NUMBER)).thenReturn(SOP_CARD_NUMBER);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_SUBSCRIPTION_ID)).thenReturn(SOP_SUBSCRIPTION_ID);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_USES_PUBLIC_SIGNATURE)).thenReturn(SOP_USES_PUBLIC_SIGNATURE);
		when(configuration.getString(PAYMENT_PROVIDER + PAYMENT_LABEL_SOP_SUBSCRIPTION_ID_PUBLIC_SIGNATURE))
				.thenReturn(SOP_PUBLIC_SIGNATURE);
	}

	@Test
	public void testMappingLabelsPopulator()
	{
		mappingLabelsPopulator.populate(createSubscriptionRequest, paymentData);

		assertEquals(PAYMENT_ACCOUNT_HOLDER_NAME,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_ACCOUNT_HOLDER_NAME));
		assertEquals(PAYMENT_CARD_TYPE, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_TYPE));
		assertEquals(PAYMENT_CARD_CVN, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_CVN));
		assertEquals(PAYMENT_CARD_EXPIRATION_MONTH,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_EXPIRATION_MONTH));
		assertEquals(PAYMENT_CARD_EXPIRATION_YEAR,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_EXPIRATION_YEAR));
		assertEquals(PAYMENT_CARD_EXPIRY_DATE, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_EXPIRY_DATE));
		assertEquals(PAYMENT_CARD_NUMBER, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_CARD_NUMBER));
		assertEquals(COMBINED_EXPIRY_DATE, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_COMBINED_EXPIRY_DATE));
		assertEquals(SEPARATOR_EXPIRY_DATE,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SEPARATOR_EXPIRY_DATE));

		assertEquals(ADDRESS_BILLTO_COUNTRY, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_COUNTRY));
		assertEquals(ADDRESS_BILLTO_FIRSTNAME, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_FIRSTNAME));
		assertEquals(ADDRESS_BILLTO_LASTNAME, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_LASTNAME));
		assertEquals(ADDRESS_BILLTO_STREET1, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_STREET1));
		assertEquals(ADDRESS_BILLTO_CITY, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_CITY));
		assertEquals(ADDRESS_BILLTO_POSTALCODE,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_BILLTO_POSTALCODE));

		assertEquals(SOP_DECISION, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_DECISION));
		assertEquals(SOP_AMOUNT, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_AMOUNT));
		assertEquals(SOP_CURRENCY, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_CURRENCY));
		assertEquals(SOP_REASON_CODE, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_REASON_CODE));
		assertEquals(SOP_CARD_NUMBER, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_CARD_NUMBER));
		assertEquals(SOP_SUBSCRIPTION_ID, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_SUBSCRIPTION_ID));
		assertEquals(SOP_USES_PUBLIC_SIGNATURE,
				paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_USES_PUBLIC_SIGNATURE));
		assertEquals(SOP_PUBLIC_SIGNATURE, paymentData.getMappingLabels().get(MappingLabelsPopulator.HYBRIS_SOP_PUBLIC_SIGNATURE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMappingLabelNoSource()
	{
		mappingLabelsPopulator.populate(null, paymentData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMappingLabelNoTarget()
	{
		mappingLabelsPopulator.populate(createSubscriptionRequest, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMappingLabelNoConfiguration()
	{
		when(configurationService.getConfiguration()).thenReturn(null);
		mappingLabelsPopulator.populate(createSubscriptionRequest, paymentData);
	}

}
