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

package de.hybris.platform.kymaintegrationservices.utils;

import static de.hybris.platform.kymaintegrationservices.utils.KymaApiExportHelper.getDestinationId;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.EndpointModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;

import org.junit.Test;


@UnitTest
public class KymaApiExportHelperTest
{
	@Test
	public void getDestinationIdTest()
	{
		final ExposedDestinationModel destination = mock(ExposedDestinationModel.class);
		final EndpointModel endpoint = mock(EndpointModel.class);
		when(endpoint.getVersion()).thenReturn("testVersion");
		when(destination.getId()).thenReturn("testId");
		when(destination.getEndpoint()).thenReturn(endpoint);
		final String expectedDestinationId = destination.getId() + "-" + endpoint.getVersion();
		assertEquals(expectedDestinationId, getDestinationId(destination));
	}
}
