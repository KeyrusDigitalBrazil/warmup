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
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.pci.client.PCIClient;
import de.hybris.platform.yaasconfiguration.service.YaasServiceFactory;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.ServiceUnavailableException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class PCICharonFacadeImplTest
{
	private PCICharonFacadeImpl classUnderTest;
	@Mock
	private PCIClient client;
	@Mock
	private YaasServiceFactory yaasServiceFactory;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PCICharonFacadeImpl();
		classUnderTest.setClient(client);
		classUnderTest.setYaasServiceFactory(yaasServiceFactory);
		final PCIRequestErrorHandlerImpl errorHandler = spy(new PCIRequestErrorHandlerImpl());
		classUnderTest.setPciRequestErrorHandler(errorHandler);
		willReturn("server error msg").given(errorHandler).getServerMessage(any(HttpException.class));
		Mockito.when(yaasServiceFactory.lookupService(PCIClient.class)).thenReturn(client);
	}

	@Test
	public void testGetClient()
	{
		classUnderTest.setClient(null);
		final PCIClient result = classUnderTest.getClient();
		assertNotNull(result);
	}


	@Test
	public void testCreateAnalyticsDocumentHttpException()
	{
		final HttpException ex = new ServiceUnavailableException(Integer.valueOf(123), "message");
		Mockito.when(client.createAnalyticsDocument(Mockito.any())).thenThrow(ex);
		final AnalyticsDocument output = classUnderTest.createAnalyticsDocument(new AnalyticsDocument());
		assertNotNull(output);
	}

	@Test
	public void testCreateAnalyticsDocument()
	{
		final Observable<AnalyticsDocument> observable = Observable.from(Arrays.asList(new AnalyticsDocument()));
		Mockito.when(client.createAnalyticsDocument(Mockito.any())).thenReturn(observable);
		final AnalyticsDocument input = new AnalyticsDocument();
		final AnalyticsDocument result = classUnderTest.createAnalyticsDocument(input);
		assertNotNull(result);
		Mockito.verify(client).createAnalyticsDocument(input);
	}

}
