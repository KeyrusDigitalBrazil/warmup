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
package com.sap.hybris.sapcustomerb2b.outbound;

import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_B2BUNIT;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_EMAIL;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_FIRST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_LAST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.B2BCUSTOMER_TITLE_CODE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_CUSTOMER_ID;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_EMAIL;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_FIRST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_LAST_NAME;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_SESSION_LANGUAGE;
import static com.sap.hybris.sapcustomerb2b.CustomerB2BConstantsUtils.KEY_TITLE;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.RAW_HYBRIS_B2B_CUSTOMER;
import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.SAPCONTACT_OUTBOUND_FEED;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;
import com.hybris.datahub.core.services.DataHubOutboundService;


@UnitTest
public class B2BCustomerExportServiceTest
{

	@InjectMocks
	private final B2BCustomerExportService b2BCustomerExportService = new B2BCustomerExportService();

	@Mock
	private final CustomerNameStrategy customerNameStrategy = mock(CustomerNameStrategy.class);

	@Mock
	private final B2BCustomerModel b2bCustomerModel = mock(B2BCustomerModel.class);

	@Mock
	private final DataHubOutboundService dataHubOutboundService = mock(DataHubOutboundService.class);

	@Test
	public void testSendB2BContactData() throws InterceptorException
	{
		// given
		mockBaseCustomerData();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "DH_TYPE");
		b2BCustomerExportService.setBatchIdAttributes(batchIdAttributes);
		

		final List<Map<String, Object>> b2bContactData = new ArrayList<>();
		b2bContactData.add(b2BCustomerExportService.prepareB2BCustomerData(b2bCustomerModel, "de"));
		b2BCustomerExportService.setFeed(SAPCONTACT_OUTBOUND_FEED);
		b2BCustomerExportService.setDataHubOutboundService(dataHubOutboundService);
		b2BCustomerExportService.sendRawItemsToDataHub(RAW_HYBRIS_B2B_CUSTOMER, b2bContactData);
		try
		{
			verify(dataHubOutboundService, times(1)).sendToDataHub(SAPCONTACT_OUTBOUND_FEED, RAW_HYBRIS_B2B_CUSTOMER,
					b2bContactData);
		}
		catch (final DataHubCommunicationException e)
		{
			fail("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
		}
		catch (final DataHubOutboundException e)
		{
			fail("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
		}

	}

	@Test
	public void testNotSendEmptyB2BContactDataNull() throws InterceptorException
	{
		// given
		final List<Map<String, Object>> b2bContactData = null;
		b2BCustomerExportService.sendRawItemsToDataHub(RAW_HYBRIS_B2B_CUSTOMER, b2bContactData);

		try
		{
			verify(dataHubOutboundService, times(0)).sendToDataHub("DEFAULT_FEED", RAW_HYBRIS_B2B_CUSTOMER, b2bContactData);
		}
		catch (final DataHubCommunicationException e)
		{
			fail("Error processing sending data to Data Hub. DataHubCommunicationException: " + e.getMessage());
		}
		catch (final DataHubOutboundException e)
		{
			fail("Error processing sending data to Data Hub. DataHubOutboundException: " + e.getMessage());
		}
	}

	@Test
	public void testPrepareB2BContactDataNoLnaguage() throws InterceptorException
	{
		mockBaseCustomerData();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "DH_TYPE");
		b2BCustomerExportService.setBatchIdAttributes(batchIdAttributes);
		
		final TitleModel title = mock(TitleModel.class);
		given(title.getCode()).willReturn(B2BCUSTOMER_TITLE_CODE);
		given(b2bCustomerModel.getTitle()).willReturn(title);
		final Map<String, Object> b2bCustomerData = b2BCustomerExportService.prepareB2BCustomerData(b2bCustomerModel, "de");
		checkBaseCustomerData(b2bCustomerData);
		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), B2BCUSTOMER_TITLE_CODE);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), "de");
	}

	@Test
	public void testPrepareB2BCustomerDataNoLanguageNoTitleCode() throws InterceptorException
	{
		// given
		mockBaseCustomerData();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "DH_TYPE");
		b2BCustomerExportService.setBatchIdAttributes(batchIdAttributes);
		
		final Map<String, Object> b2bCustomerData = b2BCustomerExportService.prepareB2BCustomerData(b2bCustomerModel, "de");
		checkBaseCustomerData(b2bCustomerData);
		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), null);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), "de");
	}

	@Test
	public void testPrepareB2BContactData() throws InterceptorException
	{
		// given
		mockBaseCustomerData();
		
		Map<String, String> batchIdAttributes = new HashMap<String, String>();
		batchIdAttributes.put("dh_batchId", "000001");
		batchIdAttributes.put("dh_sourceId", "HYBRIS");
		batchIdAttributes.put("dh_type", "DH_TYPE");
		b2BCustomerExportService.setBatchIdAttributes(batchIdAttributes);
		
		final TitleModel title = mock(TitleModel.class);
		given(title.getCode()).willReturn(B2BCUSTOMER_TITLE_CODE);
		given(b2bCustomerModel.getTitle()).willReturn(title);

		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getIsocode()).willReturn(B2BCUSTOMER_SESSION_LANGUAGE);
		given(b2bCustomerModel.getSessionLanguage()).willReturn(languageModel);


		final Map<String, Object> b2bCustomerData = b2BCustomerExportService.prepareB2BCustomerData(b2bCustomerModel, "de");
		checkBaseCustomerData(b2bCustomerData);
		Assert.assertEquals(b2bCustomerData.get(KEY_SESSION_LANGUAGE), B2BCUSTOMER_SESSION_LANGUAGE);
		Assert.assertEquals(b2bCustomerData.get(KEY_TITLE), B2BCUSTOMER_TITLE_CODE);
	}

	protected void mockBaseCustomerData()
	{
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		given(b2bCustomerModel.getDefaultB2BUnit()).willReturn(b2bUnit);
		given(b2bCustomerModel.getDefaultB2BUnit().getUid()).willReturn(B2BCUSTOMER_B2BUNIT);
		given(b2bCustomerModel.getEmail()).willReturn(B2BCUSTOMER_EMAIL);
		given(b2bCustomerModel.getName()).willReturn(B2BCUSTOMER_NAME);
		final String[] names = new String[]
		{ B2BCUSTOMER_FIRST_NAME, B2BCUSTOMER_LAST_NAME };
		given(customerNameStrategy.splitName(b2bCustomerModel.getName())).willReturn(names);
		b2BCustomerExportService.setCustomerNameStrategy(customerNameStrategy);
		given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID);
	}

	protected void checkBaseCustomerData(final Map<String, Object> b2bContactData)
	{
		Assert.assertFalse(b2bContactData.isEmpty());
		Assert.assertEquals(b2bContactData.get(KEY_EMAIL), B2BCUSTOMER_EMAIL);
		Assert.assertEquals(b2bContactData.get(KEY_CUSTOMER_ID), CUSTOMER_ID);
		Assert.assertEquals(b2bContactData.get(KEY_FIRST_NAME), B2BCUSTOMER_FIRST_NAME);
		Assert.assertEquals(b2bContactData.get(KEY_LAST_NAME), B2BCUSTOMER_LAST_NAME);
	}

}