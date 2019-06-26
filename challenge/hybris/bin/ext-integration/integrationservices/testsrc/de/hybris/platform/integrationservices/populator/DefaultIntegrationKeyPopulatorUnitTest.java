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
package de.hybris.platform.integrationservices.populator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyCalculationException;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyGenerator;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationKeyPopulatorUnitTest
{
	private static final String MY_INTEGRATION_KEY = "MY-INTEGRATION-KEY";

	@Mock
	private IntegrationKeyGenerator integrationKeyGenerator;

	@InjectMocks
	private DefaultIntegrationKeyPopulator<ItemToMapConversionContext, Map<String, Object>> populator;

	@Test
	public void testPopulate()
	{
		when(integrationKeyGenerator.generate(any(), any())).thenReturn(MY_INTEGRATION_KEY);

		final Map<String, Object> map = Maps.newHashMap();
		populator.populate(mock(ItemToMapConversionContext.class), map);
		assertThat(map).contains(entry("integrationKey", MY_INTEGRATION_KEY));
	}

	@Test
	public void testPopulateWithError()
	{
		when(integrationKeyGenerator.generate(any(), any())).thenThrow(IntegrationKeyCalculationException.class);

		assertThatThrownBy(() -> populator.populate(mock(ItemToMapConversionContext.class), Maps.newHashMap()))
				.isInstanceOf(IntegrationKeyCalculationException.class);
	}
}
