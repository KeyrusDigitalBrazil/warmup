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

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultExceptionOutboundRequestErrorParserUnitTest
{
	@InjectMocks
	private DefaultExceptionOutboundRequestErrorParser<MonitoredRequestErrorModel> errorParser;

	@Test
	public void testIsApplicableIsAlwaysTrue()
	{
		assertThat(errorParser.isApplicable(null, -1)).isTrue();
	}

	@Test
	public void testParseError()
	{
		final MonitoredRequestErrorModel errorModel =
				errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 1000, "Some Payload");
		assertThat(errorModel).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "client_error")
							  .hasFieldOrPropertyWithValue("message", "Some Payload");
	}

	@Test
	public void testParseError_emptyPayload()
	{
		final MonitoredRequestErrorModel errorModel =
				errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 1000, null);
		assertThat(errorModel).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "client_error")
							  .hasFieldOrPropertyWithValue("message", "null");
	}

	@Test
	public void testParseError_longPayload()
	{
		final MonitoredRequestErrorModel errorModel =
				errorParser.parseErrorFrom(MonitoredRequestErrorModel.class, 1000, StringUtils.repeat("=", 1000));
		assertThat(errorModel).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "client_error")
							  .hasFieldOrPropertyWithValue("message", StringUtils.repeat("=", 255 - 3)+"...");
	}

}
