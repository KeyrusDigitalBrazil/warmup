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
package de.hybris.platform.outboundservices.client.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateCreator;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationRestTemplateFactoryUnitTest
{
	@InjectMocks
	private DefaultIntegrationRestTemplateFactory factory;

	@Mock
	private ConsumedDestinationModel consumedDestination;
	@Mock
	private EndpointModel endpoint;
	@Mock
	private IntegrationRestTemplateCreator creator1;
	@Mock
	private IntegrationRestTemplateCreator creator2;
	@Mock
	private IntegrationRestTemplateCreator creator3;
	@Mock
	private RestOperations restTemplate;

	@Before
	public void setup()
	{
		factory.setRestTemplateCreators(Lists.newArrayList(creator1, creator2, creator3));
	}

	@Test
	public void shouldReturnRestTemplate()
	{
		when(creator1.isApplicable(consumedDestination)).thenReturn(true);
		when(creator2.isApplicable(consumedDestination)).thenReturn(false);
		when(creator3.isApplicable(consumedDestination)).thenReturn(true);

		when(creator1.create(consumedDestination)).thenReturn(restTemplate);

		final RestOperations restOperation = factory.create(consumedDestination);

		assertThat(restOperation).isEqualTo(restTemplate);
		verify(creator2, never()).create(consumedDestination);
		verify(creator3, never()).create(consumedDestination);
	}

	@Test
	public void shouldThrowUnsupportedException()
	{
		when(creator1.isApplicable(consumedDestination)).thenReturn(false);
		when(creator2.isApplicable(consumedDestination)).thenReturn(false);
		assertThatThrownBy(() -> factory.create(consumedDestination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> factory.create(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Consumed destination model cannot be null.");
	}
}
