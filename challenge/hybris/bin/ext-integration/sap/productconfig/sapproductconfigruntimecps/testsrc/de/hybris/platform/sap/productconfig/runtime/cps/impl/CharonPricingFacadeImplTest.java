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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.RequestErrorHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClient;
import de.hybris.platform.sap.productconfig.runtime.cps.client.PricingClientBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.hybris.charon.exp.HttpException;
import rx.Observable;


/**
 * Test class for {@link CharonPricingFacadeImpl}
 */
@UnitTest
public class CharonPricingFacadeImplTest
{
	private CharonPricingFacadeImpl classUnderTest;
	@Mock
	private RequestErrorHandler errorHandler;

	@Mock
	private PricingClient client;

	@Mock
	private YaasServiceFactory yaasServiceFactory;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CharonPricingFacadeImpl();
		classUnderTest.setRequestErrorHandler(errorHandler);
		classUnderTest.setYaasServiceFactory(yaasServiceFactory);
		when(yaasServiceFactory.lookupService(PricingClient.class)).thenReturn(client);
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final PricingClientBase result = classUnderTest.getClient();
		assertNotNull(result);
	}

	@Test
	public void testCreatePricingDocumentErrorHandlerCalled() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final HttpException ex = new HttpException(Integer.valueOf(666), "something went horribly wrong");
		Mockito.doThrow(ex).when(client).createPricingDocument(any());
		classUnderTest.createPricingDocument(new PricingDocumentInput());
		Mockito.verify(errorHandler).processCreatePricingDocumentError(ex);
	}

	@Test
	public void testCreatePricingDocumentRuntimeExceptionHandlerCalled() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		final RuntimeException ex = new RuntimeException("something went horribly wrong");
		Mockito.doThrow(ex).when(client).createPricingDocument(any());
		classUnderTest.createPricingDocument(new PricingDocumentInput());
		Mockito.verify(errorHandler).processCreatePricingDocumentRuntimeException(ex);
	}

	@Test
	public void testCreatePricingDocument() throws PricingEngineException
	{
		classUnderTest.setClient(client);
		PricingDocumentResult pricingDocumentResult = new PricingDocumentResult();

		when(client.createPricingDocument(any())).thenReturn(
				Observable.just(pricingDocumentResult));

		PricingDocumentResult result = classUnderTest.createPricingDocument(new PricingDocumentInput());

		assertEquals(pricingDocumentResult, result);
	}
}
