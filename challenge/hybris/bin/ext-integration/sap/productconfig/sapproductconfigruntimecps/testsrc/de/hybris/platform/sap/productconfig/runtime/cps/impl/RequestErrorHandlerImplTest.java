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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;

import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.exp.ClientException;
import com.hybris.charon.exp.ForbiddenException;
import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;
import com.hybris.charon.exp.ServiceUnavailableException;

import rx.Observable;


@SuppressWarnings("javadoc")
@UnitTest
public class RequestErrorHandlerImplTest
{
	private static final String CONFIG_ID = "config id";
	private static final String MESSAGE_TEXT = "message";
	private RequestErrorHandlerImpl classUnderTest;

	@Mock
	HttpException ex;

	@Mock
	ForbiddenException forbiddenEx;

	@Mock
	HttpException exWithoutServerMessage;

	@Mock
	NotFoundException notFoundEx;

	@Mock
	ServiceUnavailableException unAvEx;

	@Mock
	private RuntimeException runtimeEx;

	@Mock
	private TimeoutException timeOutException;

	@Mock
	private NullPointerException nullPointerEx;

	@Mock
	private ClientException clientEx;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Mock
	private CPSCache cache;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		final Observable<String> justMessage = Observable.just(MESSAGE_TEXT);
		when(ex.getServerMessage()).thenReturn(justMessage);
		when(forbiddenEx.getServerMessage()).thenReturn(justMessage);
		when(notFoundEx.getServerMessage()).thenReturn(justMessage);
		when(clientEx.getServerMessage()).thenReturn(justMessage);
		when(unAvEx.getServerMessage()).thenReturn(justMessage);
		when(exWithoutServerMessage.getServerMessage()).thenReturn(null);
		when(runtimeEx.getCause()).thenReturn(timeOutException);
		classUnderTest = new RequestErrorHandlerImpl();
		classUnderTest.setCache(cache);

	}

	@Test
	public void testProcessUpdateConfigurationErrorEtag() throws ConfigurationEngineException
	{
		expected.expect(ConfigurationEngineException.class);
		when(clientEx.getCode()).thenReturn(Integer.valueOf(412));
		try
		{
			classUnderTest.processUpdateConfigurationError(clientEx, CONFIG_ID);
		}
		finally
		{
			checkCacheCleanUp();
		}
	}

	@Test
	public void testProcessGetConfigurationForbiddenError() throws ConfigurationEngineException
	{
		expected.expect(ConfigurationEngineException.class);
		try
		{
			classUnderTest.processGetConfigurationError(forbiddenEx, CONFIG_ID);
		}
		finally
		{
			checkCacheCleanUp();
		}
	}

	@Test
	public void testProcessCreateDefaultConfigurationError()
	{
		expected.expect(IllegalStateException.class);
		classUnderTest.processCreateDefaultConfigurationError(unAvEx);
	}

	@Test
	public void testProcessGetConfigurationError()
	{
		expected.expect(IllegalStateException.class);
		classUnderTest.processCreateDefaultConfigurationError(notFoundEx);
	}

	@Test
	public void testProcessDeleteConfigurationError()
	{
		expected.expect(IllegalStateException.class);
		classUnderTest.processDeleteConfigurationError(notFoundEx);
	}

	@Test
	public void testProcessDeleteConfigurationErrorEtag()
	{
		expected.expect(IllegalStateException.class);
		when(clientEx.getCode()).thenReturn(Integer.valueOf(412));
		classUnderTest.processDeleteConfigurationError(clientEx);
	}

	@Test
	public void testProcessGetExternalConfigurationError() throws ConfigurationEngineException
	{
		expected.expect(ConfigurationEngineException.class);
		classUnderTest.processGetExternalConfigurationError(notFoundEx, CONFIG_ID);
		checkCacheCleanUp();
	}

	@Test
	public void testProcessCreateRuntimeConfigurationFromExternalError()
	{
		expected.expect(IllegalStateException.class);
		classUnderTest.processCreateRuntimeConfigurationFromExternalError(notFoundEx);
	}

	@Test
	public void testProcessCreatePricingDocumentError() throws PricingEngineException
	{
		expected.expect(PricingEngineException.class);
		classUnderTest.processCreatePricingDocumentError(notFoundEx);
	}

	@Test
	public void testProcessHasKbError()
	{
		expected.expect(IllegalStateException.class);
		classUnderTest.processHasKbError(notFoundEx);
	}

	@Test
	public void testIfNotFound()
	{
		expected.expect(IllegalStateException.class);
		expected.expectCause(instanceOf(ConfigurationNotFoundException.class));
		classUnderTest.ifNotFoundThrowIllegalState(notFoundEx);
	}

	@Test
	public void testIfNotFoundGenericException()
	{
		final HttpException ex = new HttpException(Integer.valueOf(404), MESSAGE_TEXT);
		//In this case we don't expect a RT exception since the http exception doesn't indicate 'not found'
		classUnderTest.ifNotFoundThrowIllegalState(ex);
	}

	@Test
	public void testCheckNotFound() throws ConfigurationEngineException
	{
		expected.expect(ConfigurationNotFoundException.class);
		final HttpException ex = new NotFoundException(Integer.valueOf(404), MESSAGE_TEXT);
		classUnderTest.checkNotFound(ex);
	}

	@Test
	public void testCheckNotFoundGenericException() throws ConfigurationEngineException
	{

		//In this case we don't expect a RT exception since the http exception doesn't indicate 'not found'
		classUnderTest.checkNotFound(forbiddenEx);
	}

	@Test
	public void testTraceRequestError()
	{
		classUnderTest.logRequestError("pci", forbiddenEx);
		verify(forbiddenEx).getServerMessage();
	}

	@Test
	public void testTraceRequestErrorNoMessage()
	{
		assertEquals(RequestErrorHandlerImpl.NO_SERVER_MESSAGE, classUnderTest.getServerMessage(exWithoutServerMessage));
	}

	@Test
	public void testProcessCreatePricingDocumentRuntimeException() throws PricingEngineException
	{
		expected.expect(PricingEngineException.class);
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test
	public void testProcessCreatePricingDocumentRuntimeExceptionOtherCause() throws PricingEngineException
	{
		expected.expect(RuntimeException.class);
		Mockito.when(runtimeEx.getCause()).thenReturn(nullPointerEx);
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test
	public void testProcessCreatePricingDocumentRuntimeExceptionNoCauseAtAll() throws PricingEngineException
	{
		expected.expect(RuntimeException.class);
		Mockito.when(runtimeEx.getCause()).thenReturn(null);
		classUnderTest.processCreatePricingDocumentRuntimeException(runtimeEx);
	}

	@Test
	public void testProcessConfigurationRuntimeException() throws ConfigurationEngineException
	{
		expected.expect(ConfigurationEngineException.class);
		try
		{
			classUnderTest.processConfigurationRuntimeException(runtimeEx, CONFIG_ID);
		}
		finally
		{
			checkCacheCleanUp();
		}
	}

	@Test
	public void testProcessConfigurationRuntimeExceptionOtherCause() throws ConfigurationEngineException
	{
		expected.expect(RuntimeException.class);
		Mockito.when(runtimeEx.getCause()).thenReturn(nullPointerEx);
		try
		{
			classUnderTest.processConfigurationRuntimeException(runtimeEx, CONFIG_ID);
		}
		finally
		{
			checkCacheCleanUp();
		}
	}

	@Test
	public void testProcessConfigurationRuntimeExceptionNoCauseAtAll() throws ConfigurationEngineException
	{
		expected.expect(RuntimeException.class);
		Mockito.when(runtimeEx.getCause()).thenReturn(null);
		try
		{
			classUnderTest.processConfigurationRuntimeException(runtimeEx, CONFIG_ID);
		}
		finally
		{
			checkCacheCleanUp();
		}
	}

	@Test
	public void testCleanUpCache()
	{
		classUnderTest.cleanUpCache(CONFIG_ID);
		checkCacheCleanUp();
	}

	private void checkCacheCleanUp()
	{
		verify(cache).removeConfiguration(CONFIG_ID);
		verify(cache).removeCookies(CONFIG_ID);
	}
}
