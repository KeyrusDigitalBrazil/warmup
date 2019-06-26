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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;

import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.HttpException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class PCIRequestErrorHandlerImplTest
{

	private static final String MESSAGE_TEXT = "message";
	private final PCIRequestErrorHandlerImpl classUnderTest = new PCIRequestErrorHandlerImpl();
	private AnalyticsDocument input;


	@Mock
	private HttpException ex;

	@Mock
	private RuntimeException runtimeEx;

	@Mock
	private TimeoutException rootCauseTimeoutException;

	@Mock
	private NullPointerException npException;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final Observable<String> justMessage = Observable.just(MESSAGE_TEXT);
		Mockito.when(ex.getServerMessage()).thenReturn(justMessage);
		Mockito.when(runtimeEx.getCause()).thenReturn(rootCauseTimeoutException);


		input = new AnalyticsDocument();
		input.setRootProduct("pCode");
	}

	protected void assertDocumentEmpty(final AnalyticsDocument output)
	{
		assertNotNull(output);
		assertEquals("pCode", output.getRootProduct());
		assertNull(output.getRootItem());
	}

	@Test
	public void testCreateEmptyAnalyticsDocument()
	{
		final AnalyticsDocument output = classUnderTest.createEmptyAnalyticsDocument(input);
		assertDocumentEmpty(output);
	}

	@Test
	public void testProcessCreateAnalyticsDocumentHttpError()
	{
		final AnalyticsDocument output = classUnderTest.processCreateAnalyticsDocumentHttpError(ex, input);
		assertDocumentEmpty(output);
		Mockito.verify(ex).getServerMessage();
	}

	@Test
	public void testProcessCreateAnalyticsDocumentRuntimeException()
	{
		final AnalyticsDocument output = classUnderTest.processCreateAnalyticsDocumentRuntimeException(runtimeEx, input);
		assertDocumentEmpty(output);
		Mockito.verify(runtimeEx).getCause();
	}

	@Test(expected = RuntimeException.class)
	public void testProcessCreateAnalyticsDocumentRuntimeExceptionNoTimeout()
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(npException);
		classUnderTest.processCreateAnalyticsDocumentRuntimeException(runtimeEx, input);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessCreateAnalyticsDocumentRuntimeExceptionNoCauseAtAll()
	{
		Mockito.when(runtimeEx.getCause()).thenReturn(null);
		classUnderTest.processCreateAnalyticsDocumentRuntimeException(runtimeEx, input);
	}

	@Test
	public void testGetServerMessage()
	{
		assertEquals(MESSAGE_TEXT, classUnderTest.getServerMessage(ex));
	}

	@Test
	public void testGetServerMessageNullServerMessage()
	{
		Mockito.when(ex.getServerMessage()).thenReturn(null);
		assertEquals(PCIRequestErrorHandlerImpl.NO_SERVER_MESSAGE, classUnderTest.getServerMessage(ex));
	}



}
