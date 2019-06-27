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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.EncodeDecode;

@RunWith(PowerMockRunner.class)
@UnitTest
@PrepareForTest({DefaultSapPassportService.class, System.class, UUID.class})
@PowerMockIgnore({"org.apache.logging.log4j.*", "javax.management.*"})
public class DefaultSapPassportServiceUnitTest
{
	private static final long SYSTEM_TIME_MILLIS = 123L;

	@InjectMocks
	private DefaultSapPassportService service;
	@Mock
	private IntegrationServicesConfiguration integrationServicesConfiguration;

	@Before
	public void setUp()
	{
		when(integrationServicesConfiguration.getSapPassportSystemId()).thenReturn("My SAP Commerce");
		when(integrationServicesConfiguration.getSapPassportServiceValue()).thenReturn(139);
		when(integrationServicesConfiguration.getSapPassportUser()).thenReturn("My SAP User");
	}

	@Test
	public void testGenerate() throws Exception
	{
		PowerMockito.mockStatic(UUID.class);
		final UUID uuid1 = UUID.fromString("cbd9c48c-b2aa-4f78-b01a-a9acf5fdeb9f");
		final UUID uuid2 = UUID.fromString("9e2b514c-b000-47f4-8f62-8ff3074a1b5e");
		PowerMockito.when(UUID.randomUUID()).thenReturn(uuid1).thenReturn(uuid2);

		PowerMockito.spy(System.class);
		PowerMockito.when(System.currentTimeMillis()).thenReturn(SYSTEM_TIME_MILLIS);

		final String passport = service.generate("MY_CODE");
		final byte[] passportBytes = Hex.decodeHex(passport.toCharArray());
		final DSRPassport dsrPassport = EncodeDecode.decodeBytePassport(passportBytes);

		assertThat(dsrPassport).hasFieldOrPropertyWithValue("version", 3)
							   .hasFieldOrPropertyWithValue("traceFlag", 0)
							   .hasFieldOrPropertyWithValue("systemIdByte", getBytes("My SAP Commerce"))
							   .hasFieldOrPropertyWithValue("service", 139)
							   .hasFieldOrPropertyWithValue("userIdByte", getBytes("My SAP User"))
							   .hasFieldOrPropertyWithValue("actionByte", getBytes("sendMessage"))
							   .hasFieldOrPropertyWithValue("actionType", 0)
							   .hasFieldOrPropertyWithValue("prevSystemIdByte", getBytes(""))
							   .hasFieldOrPropertyWithValue("transIdByte", getBytes("MY_CODE" + SYSTEM_TIME_MILLIS, 32))
							   .hasFieldOrPropertyWithValue("clientByte", getBytes("", 3))  // ""
							   .hasFieldOrPropertyWithValue("systemType", 1)
							   .hasFieldOrPropertyWithValue("rootContextIdByte", getBytes(uuid1.toString(), 16))
							   .hasFieldOrPropertyWithValue("connectionIdByte", getBytes(uuid2.toString(), 16))
							   .hasFieldOrPropertyWithValue("connectionCounter", 0);

		verify(integrationServicesConfiguration).getSapPassportSystemId();
		verify(integrationServicesConfiguration).getSapPassportServiceValue();
		verify(integrationServicesConfiguration).getSapPassportUser();
	}

	private byte[] getBytes(final String value, final int length)
	{
		final byte[] newArray = new byte[length];
		final byte[] array = value.getBytes(StandardCharsets.UTF_8);

		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

	private byte[] getBytes(final String value)
	{
		return value.getBytes(StandardCharsets.UTF_8);
	}
}
