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
 */
package de.hybris.platform.odata2services.odata.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.processor.DefaultODataProcessor;
import de.hybris.platform.odata2services.odata.processor.ODataProcessorFactory;

import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultServiceFactoryUnitTest
{
	private static final ODataContext CONTEXT = mock(ODataContext.class);

	@Mock
	private EdmProviderFactory edmProviderFactory;
	@Mock
	private ODataProcessorFactory processorFactory;
	@InjectMocks
	private DefaultServiceFactory serviceFactory;

	@Test
	public void testCreateService()
	{
		when(edmProviderFactory.createInstance(any(ODataContext.class))).thenReturn(mock(EdmProvider.class));
		when(processorFactory.createProcessor(any(ODataContext.class))).thenReturn(new DefaultODataProcessor());

		assertThat(serviceFactory.createService(CONTEXT)).isNotNull();
	}

	@Test
	public void testErrorCallbackIsRegistered()
	{
		assertThat(serviceFactory.getCallback(ODataErrorCallback.class))
				.isNotNull()
				.isInstanceOf(CustomODataExceptionAwareErrorCallback.class);
	}

	@Test
	public void testErrorCallbackIsRegisteredButNotAssignable()
	{
		assertThat(serviceFactory.getCallback(OnWriteFeedContent.class)).isNull();
	}
}