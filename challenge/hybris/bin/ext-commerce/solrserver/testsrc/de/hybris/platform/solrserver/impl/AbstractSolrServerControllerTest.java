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
package de.hybris.platform.solrserver.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrserver.SolrInstance;
import de.hybris.platform.solrserver.SolrServerException;
import de.hybris.platform.solrserver.impl.AbstractSolrServerController.CommandResult;
import de.hybris.platform.solrserver.impl.AbstractSolrServerController.ServerStatus;
import de.hybris.platform.solrserver.impl.AbstractSolrServerController.ServerStatus.Status;
import de.hybris.platform.solrserver.strategies.SolrServerConfigurationProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class AbstractSolrServerControllerTest
{
	public static final String INSTANCE_NAME = "testInst1";
	public static final String INSTANCE_PORT = "9991";

	public static final String SOLT_MULTIPLE_SERVERS_STDOUT_FILE = "/test/solr_multiple_servers_stdout.txt";
	public static final String SOLT_NO_JSON_STDOUT_FILE = "/test/solr_no_json_stdout.txt";
	public static final String SOLT_NO_SERVER_STDOUT_FILE = "/test/solr_no_server_stdout.txt";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SolrServerConfigurationProvider solrServerConfigurationProvider;

	private AbstractSolrServerController solrServerController;
	private SolrInstance solrInstance;
	private ServerStatus stoppedServerStatus;
	private ServerStatus unknownServerStatus;
	private ServerStatus startedServerStatus;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		solrServerController = Mockito.spy(new AbstractSolrServerControllerSpy());
		solrServerController.setSolrServerConfigurationProvider(solrServerConfigurationProvider);

		when(solrServerConfigurationProvider.getConfiguration()).thenReturn(new HashMap<>());

		solrInstance = new DefaultSolrInstance(INSTANCE_NAME);

		final Map<String, String> configuration = solrInstance.getConfiguration();
		configuration.put(DefaultSolrInstance.PORT_PROPERTY, INSTANCE_PORT);

		stoppedServerStatus = new ServerStatus();
		stoppedServerStatus.setPort(solrInstance.getPort());
		stoppedServerStatus.setStatus(Status.STOPPED);

		unknownServerStatus = new ServerStatus();
		unknownServerStatus.setPort(solrInstance.getPort());
		unknownServerStatus.setStatus(Status.UNKNOWN);

		startedServerStatus = new ServerStatus();
		startedServerStatus.setPort(solrInstance.getPort());
		startedServerStatus.setSolrHome(solrInstance.getConfigDir());
		startedServerStatus.setStatus(Status.STARTED);
	}

	@Test
	public void startServer() throws Exception
	{
		// given
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_START_COMMAND), any());
		doReturn(stoppedServerStatus).doReturn(startedServerStatus).when(solrServerController).getSolrServerStatus(solrInstance);

		// when
		solrServerController.start(solrInstance);

		// then
		verify(solrServerController).ensureToStartSolr(solrInstance);
	}

	@Test
	public void startServerWithStatusRetry() throws Exception
	{
		// given
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_START_COMMAND), any());
		doReturn(stoppedServerStatus).doReturn(unknownServerStatus).doReturn(startedServerStatus).when(solrServerController)
				.getSolrServerStatus(solrInstance);

		// when
		solrServerController.start(solrInstance);

		// then
		verify(solrServerController).ensureToStartSolr(solrInstance);
	}

	@Test
	public void restartRunningServer() throws Exception
	{
		// given
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_START_COMMAND), any());
		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_STOP_COMMAND), any());
		doReturn(startedServerStatus).doReturn(stoppedServerStatus).doReturn(startedServerStatus).when(solrServerController)
				.getSolrServerStatus(solrInstance);

		// when
		solrServerController.start(solrInstance);

		// then
		verify(solrServerController).ensureToStopSolr(solrInstance);
		verify(solrServerController).ensureToStartSolr(solrInstance);
	}

	@Test
	public void thrownExceptionBecauseWrongServerIsRunning() throws Exception
	{
		// given
		startedServerStatus.setSolrHome(solrInstance.getConfigDir() + "_notfound");
		doReturn(startedServerStatus).when(solrServerController).getSolrServerStatus(solrInstance);

		// expect
		expectedException.expect(SolrServerException.class);

		// when
		solrServerController.start(solrInstance);
	}

	@Test
	public void serverDoesNotStart() throws Exception
	{
		// given
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_START_COMMAND), any());
		doReturn(stoppedServerStatus).when(solrServerController).getSolrServerStatus(solrInstance);

		// expect
		expectedException.expect(SolrServerException.class);

		// when
		solrServerController.start(solrInstance);
	}

	@Test
	public void stopServer() throws Exception
	{
		// given
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_STOP_COMMAND), any());
		doReturn(startedServerStatus).doReturn(stoppedServerStatus).when(solrServerController).getSolrServerStatus(solrInstance);

		// when
		solrServerController.stop(solrInstance);

		// then
		verify(solrServerController).ensureToStopSolr(solrInstance);
	}

	@Test
	public void statusShouldBeStopped() throws Exception
	{
		// given
		final String output = readFile(SOLT_NO_SERVER_STDOUT_FILE);
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);
		commandResult.setOutput(output);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_STATUS_COMMAND), any());

		// when
		final ServerStatus serverStatus = solrServerController.getSolrServerStatus(solrInstance);

		// then
		assertEquals(ServerStatus.Status.STOPPED, serverStatus.getStatus());
	}

	@Test
	public void statusShouldBeStarted() throws Exception
	{
		// given
		final String output = readFile(SOLT_MULTIPLE_SERVERS_STDOUT_FILE);
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);
		commandResult.setOutput(output);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_STATUS_COMMAND), any());

		// when
		final ServerStatus serverStatus = solrServerController.getSolrServerStatus(solrInstance);

		// then
		assertEquals(ServerStatus.Status.STARTED, serverStatus.getStatus());
	}

	@Test
	public void statusShouldBeUnknown() throws Exception
	{
		// given
		final String output = readFile(SOLT_NO_JSON_STDOUT_FILE);
		final CommandResult commandResult = new CommandResult();
		commandResult.setExitValue(0);
		commandResult.setOutput(output);

		doReturn(commandResult).when(solrServerController).callSolrCommand(eq(solrInstance),
				eq(AbstractSolrServerController.SOLR_STATUS_COMMAND), any());

		// when
		final ServerStatus serverStatus = solrServerController.getSolrServerStatus(solrInstance);

		// then
		assertEquals(ServerStatus.Status.UNKNOWN, serverStatus.getStatus());
	}

	protected String readFile(final String file) throws IOException
	{
		final InputStream inputStream = AbstractSolrServerControllerTest.class.getResourceAsStream(file);
		if (inputStream == null)
		{
			throw new FileNotFoundException("file [" + file + "] cannot be found");
		}

		return IOUtils.toString(inputStream);
	}

	protected class AbstractSolrServerControllerSpy extends AbstractSolrServerController
	{
		@Override
		protected void configureSolrCommandInvocation(final SolrInstance solrInstance, final ProcessBuilder processBuilder,
				final String command)
		{
			// NOOP
		}

		@Override
		protected void configureZKCommandInvocation(final SolrInstance solrInstance, final ProcessBuilder processBuilder,
				final String command)
		{
			// NOOP
		}
	}
}
