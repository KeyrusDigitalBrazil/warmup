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
package de.hybris.platform.integrationservices.passport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sap.jdsr.passport.DSRPassport;
import com.sap.jdsr.passport.EncodeDecode;

@RunWith(PowerMockRunner.class)
@UnitTest
@PrepareForTest({SapPassportBuilder.class, EncodeDecode.class, DSRPassport.class})
@PowerMockIgnore({"org.apache.logging.log4j.*", "javax.management.*"})
public class SapPassportBuilderUnitTest
{
	private static final int VERSION = 4;
	private static final int TRACE_FLAG = 10;
	private static final String SYSTEM_ID = "someSystemId";
	private static final int SERVICE = 100;
	private static final String USER = "someUser";
	private static final String ACTION = "someAction";
	private static final int ACTION_TYPE = 101;
	private static final String PREV_SYSTEM_ID = "somePrevSystemId";
	private static final String TRANS_ID = "someTransId";
	private static final String CLIENT_NUMBER = "someClientNumber";
	private static final int SYSTEM_TYPE = 102;
	private static final byte[] ROOT_CONTEXT_ID = new byte[] {1, 2, 3};
	private static final byte[] CONNECTION_ID = new byte[] {4, 5, 6};
	private static final int CONNECTION_COUNTER = 103;

	private SapPassportBuilder builder = SapPassportBuilder.newSapPassportBuilder();

	@Before
	public void setUp()
	{
		builder.withVersion(VERSION)
			   .withTraceFlag(TRACE_FLAG)
			   .withSystemId(SYSTEM_ID)
			   .withService(SERVICE)
			   .withUser(USER)
			   .withAction(ACTION)
			   .withActionType(ACTION_TYPE)
			   .withPrevSystemId(PREV_SYSTEM_ID)
			   .withTransId(TRANS_ID)
			   .withClientNumber(CLIENT_NUMBER)
			   .withSystemType(SYSTEM_TYPE)
			   .withRootContextId(ROOT_CONTEXT_ID)
			   .withConnectionId(CONNECTION_ID)
			   .withConnectionCounter(CONNECTION_COUNTER);
	}

	@Test
	public void testBuild() throws Exception
	{
		final DSRPassport passport = mock(DSRPassport.class);
		PowerMockito.whenNew(DSRPassport.class)
					.withArguments(VERSION,
							TRACE_FLAG,
							SYSTEM_ID,
							SERVICE, USER,
							ACTION,
							ACTION_TYPE,
							PREV_SYSTEM_ID,
							TRANS_ID,
							CLIENT_NUMBER,
							SYSTEM_TYPE,
							ROOT_CONTEXT_ID,
							CONNECTION_ID,
							CONNECTION_COUNTER)
					.thenReturn(passport);

		assertThat(builder.build()).isEqualTo(passport);
	}

	@Test
	public void testVersionCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withVersion(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("version cannot be null");
	}

	@Test
	public void testTraceFlagCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withTraceFlag(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("traceFlag cannot be null");
	}

	@Test
	public void testSystemIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withSystemId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("systemId cannot be null");
	}

	@Test
	public void testServiceCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withService(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("service cannot be null");
	}

	@Test
	public void testUserCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withUser(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("user cannot be null");
	}

	@Test
	public void testActionCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withAction(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("action cannot be null");
	}

	@Test
	public void testActionTypeCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withActionType(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("actionType cannot be null");
	}

	@Test
	public void testPrevSystemIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withPrevSystemId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("prevSystemId cannot be null");
	}

	@Test
	public void testTransIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withTransId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("transId cannot be null");
	}

	@Test
	public void testClientNumberCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withClientNumber(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("clientNumber cannot be null");
	}

	@Test
	public void testSystemTypeCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withSystemType(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("systemType cannot be null");
	}

	@Test
	public void testRootContextIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withRootContextId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("rootContextId cannot be null");
	}

	@Test
	public void testConnectionIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withConnectionId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("connectionId cannot be null");
	}

	@Test
	public void testConnectionCounterCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withConnectionCounter(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("connectionCounter cannot be null");
	}
}
