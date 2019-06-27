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
package de.hybris.platform.outboundservices.monitoring;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultHttp404OutboundRequestErrorParserUnitTest
{
	@InjectMocks
	private DefaultHttp404OutboundRequestErrorParser<MonitoredRequestErrorModel> errorParser;

	@Test
	public void testIsApplicableIsTrue()
	{
		assertThat(errorParser.isApplicable(null, 404)).isTrue();
	}

	@Test
	public void testIsApplicableIsFalse()
	{
		assertThat(errorParser.isApplicable(null, 399)).isFalse();
		assertThat(errorParser.isApplicable(null, 401)).isFalse();
	}

	@Test
	public void testParseError()
	{
		final MonitoredRequestErrorModel errorModel =
				errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 404, "Some Payload");
		assertThat(errorModel).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "http_404")
							  .hasFieldOrPropertyWithValue("message", "Destination not found");
	}

	@Test
	public void testParseError_emptyPayload()
	{
		final MonitoredRequestErrorModel errorModel =
				errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 404, null);
		assertThat(errorModel).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "http_404")
							  .hasFieldOrPropertyWithValue("message", "Destination not found");
	}
}
