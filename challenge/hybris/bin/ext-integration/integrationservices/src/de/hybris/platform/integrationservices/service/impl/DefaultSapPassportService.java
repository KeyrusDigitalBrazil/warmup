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
package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.passport.SapPassportBuilder;
import de.hybris.platform.integrationservices.service.SapPassportService;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Required;

import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.EncodeDecode;

/**
 * Default implementation of {@link SapPassportService}
 */
public class DefaultSapPassportService implements SapPassportService
{
	private IntegrationServicesConfiguration configuration;

	@Override
	public String generate(final String integrationObjectCode)
	{
		final long millis = System.currentTimeMillis();
		final SapPassportBuilder builder = SapPassportBuilder.newSapPassportBuilder();
		final DSRPassport passport = builder.withVersion(3)
											.withTraceFlag(0)
											.withSystemId(getConfiguration().getSapPassportSystemId())
											.withService(getConfiguration().getSapPassportServiceValue())
											.withUser(getConfiguration().getSapPassportUser())
											.withAction("sendMessage")
											.withActionType(0)
											.withPrevSystemId("")
											.withTransId(integrationObjectCode + millis)
											.withClientNumber("")
											.withSystemType(1)
											.withRootContextId(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
											.withConnectionId(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
											.withConnectionCounter(0)
											.build();

		final byte[] passportBytes = EncodeDecode.encodeBytePassport(passport);

		return Hex.encodeHexString(passportBytes);
	}

	protected IntegrationServicesConfiguration getConfiguration()
	{
		return configuration;
	}

	@Required
	public void setConfiguration(final IntegrationServicesConfiguration config)
	{
		this.configuration = config;
	}
}
