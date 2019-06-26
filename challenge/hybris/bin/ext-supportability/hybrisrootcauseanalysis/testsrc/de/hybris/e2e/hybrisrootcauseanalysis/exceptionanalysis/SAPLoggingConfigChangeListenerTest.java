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
package de.hybris.e2e.hybrisrootcauseanalysis.exceptionanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.e2e.hybrisrootcauseanalysis.exceptionanalysis.constants.ListLogConstants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.logging.HybrisLogger;
import de.hybris.platform.util.logging.log4j2.HybrisLoggerContext;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



/**
 * Integration test for the SAPLoggingConfigChangeListener and SAPLoggingProxyListener
 * 
 */
public class SAPLoggingConfigChangeListenerTest extends ServicelayerTest
{
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private SAPLoggingProxyListener proxyLoggingListener;
	private SAPLoggingConfigChangeListener configChangeListener;


	@Before
	public void setUp()
	{

		HybrisLogger.removeAllListeners();
		updateRootLoggerLevel(Level.DEBUG);

		proxyLoggingListener = new SAPLoggingProxyListener();
		proxyLoggingListener.setLogSeverity("DEBUG");
		proxyLoggingListener.setTraceSeverity("DEBUG");
		proxyLoggingListener.setEnableTracing(false);
		proxyLoggingListener.setRotationCount(0);
		proxyLoggingListener.setRotationSize(0);

		configChangeListener = new SAPLoggingConfigChangeListener();
		configChangeListener.setListLogLoggingListener(proxyLoggingListener);

	}





	@Test
	public void testAllowedRuntimeConfigurationChanges() throws IOException
	{

		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logTestAllowedRuntimeConfigurationChanges.log");
		final File currentTraceFile = temporaryFolder.newFile("traceTestAllowedRuntimeConfigurationChanges.trc");


		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		final String newLogSeverity = "FATAL";
		final String newTraceSeverity = "ERROR";
		final String newEnableTracing = "TRUE";

		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "logseverity", newLogSeverity);
		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "traceseverity", newTraceSeverity);
		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "enabletracing", newEnableTracing);

		assertEquals(proxyLoggingListener.getLogSeverity(), newLogSeverity);
		assertEquals(proxyLoggingListener.getTraceSeverity(), newTraceSeverity);
		assertTrue(proxyLoggingListener.isEnableTracing());

	}




	@Test
	public void testProhibitedRuntimeConfigurationChanges() throws IOException
	{

		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logTestProhibitedRuntimeConfigurationChanges.log");
		final File currentTraceFile = temporaryFolder.newFile("traceTestProhibitedRuntimeConfigurationChanges.trc");


		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		final String newLogPath = "someweridpath";
		final String newTracePath = "someevenweirderpath";
		final String newRotationCount = "352365";
		final String newRotationSize = "34";

		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "logpath", newLogPath);
		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "tracepath", newTracePath);
		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "rotationcount", newRotationCount);
		configChangeListener.configChanged(ListLogConstants.PROP_PREFIX + "rotationsize", newRotationSize);

		assertFalse(proxyLoggingListener.getLogFilePath() == newLogPath);
		assertFalse(proxyLoggingListener.getTraceFilePath() == newTracePath);
		assertFalse(proxyLoggingListener.getRotationCount() == Integer.parseInt(newRotationCount));
		assertFalse(proxyLoggingListener.getRotationSize() == Integer.parseInt(newRotationSize));

		final String fileContent = FileUtils.readFileToString(currentLogFile);
		final int matchCount = StringUtils.countMatches(fileContent, "either invalid or not changeable at runtime");
		assertEquals(4, matchCount);

	}

	@After
	public void tearDown()
	{
		proxyLoggingListener.deregister();
	}


	/**
	 * Updates the root logger's threshold level
	 * 
	 * @param rootLoggerLevel
	 */
	private void updateRootLoggerLevel(final Level rootLoggerLevel)
	{
		final HybrisLoggerContext loggerCtx = (HybrisLoggerContext) LogManager.getContext(false);
		final Configuration loggerCfg = loggerCtx.getConfiguration();
		final LoggerConfig rootLoggerCfg = loggerCfg.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		rootLoggerCfg.setLevel(rootLoggerLevel);
		loggerCtx.updateLoggers();
	}




}
