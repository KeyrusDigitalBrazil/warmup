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
import static org.junit.Assert.assertTrue;

import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.logging.HybrisLogger;
import de.hybris.platform.util.logging.log4j2.HybrisLoggerContext;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;




/**
 * Unit tests for the SAPLoggingProxyListener class
 * 
 */

public class SAPLoggingProxyListenerTest extends ServicelayerTest
{

	private static Logger LOG = LogManager.getLogger();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static final String NEEDLE = "03df2bc152bf47a3b95bc17db333533a";
	private SAPLoggingProxyListener proxyLoggingListener;




	@Before
	public void setUp()
	{
		HybrisLogger.removeAllListeners();
		updateRootLoggerLevel(Level.DEBUG);

		// Setup the ListLogAppender
		proxyLoggingListener = new SAPLoggingProxyListener();
		proxyLoggingListener.setRotationCount(0);
		proxyLoggingListener.setRotationSize(0);
		proxyLoggingListener.setLogSeverity("INFO");
		proxyLoggingListener.setTraceSeverity("ERROR");
		proxyLoggingListener.setEnableTracing(false);
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


	/**
	 * Test the effect of changing the root logger's logging level.
	 * 
	 * The root logger will determine the basic threshold for all logging events. If it is set to OFF, nothing will ever
	 * get written no matter what logging/tracing severities are set. Similarly, if it is set to ALL or DEBUG, then it
	 * will allow the ListLog API to further determine what to log and what not to log.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRootLoggerLevelChangeEffect() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestRootLoggerLevelChangeEffect.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestRootLoggerLevelChangeEffect.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		// Test1: RootLogger = OFF, ListLog = DEBUG => nothing should get logged
		updateRootLoggerLevel(Level.OFF);
		proxyLoggingListener.setLogSeverity("DEBUG");
		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);
		assertTrue(currentLogFile.exists() && !currentLogFile.isDirectory());
		String fileContent = FileUtils.readFileToString(currentLogFile);
		int matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(0, matchCount);

		// Test2: RootLogger = DEBUG, ListLog = DEBUG => everything should get logged
		updateRootLoggerLevel(Level.DEBUG);
		proxyLoggingListener.setLogSeverity("DEBUG");
		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);
		assertTrue(currentLogFile.exists() && !currentLogFile.isDirectory());
		fileContent = FileUtils.readFileToString(currentLogFile);
		matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(5, matchCount);

		// Test3: RootLogger = WARN, ListLog = DEBUG => only WARN, ERROR and FATAL messages should get logged
		updateRootLoggerLevel(Level.WARN);
		proxyLoggingListener.setLogSeverity("DEBUG");
		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);
		assertTrue(currentLogFile.exists() && !currentLogFile.isDirectory());
		fileContent = FileUtils.readFileToString(currentLogFile);
		matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(8, matchCount); // 3 logs in addition to those from Test 2

	}





	@Test
	public void testLogFileCreation() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestLogFileCreation.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestLogFileCreation.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		proxyLoggingListener.setLogSeverity("INFO");
		LOG.info(NEEDLE);
		assertTrue(currentLogFile.exists() && !currentLogFile.isDirectory());
	}


	@Test
	public void testDynamicTraceToggling() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestDynamicTraceToggling.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestDynamicTraceToggling.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		// Test1: Trace severity = ERROR, Trace enabled = FALSE => Trace file should exist but be empty
		proxyLoggingListener.setTraceSeverity("ERROR");
		proxyLoggingListener.setEnableTracing(false);
		LOG.error(NEEDLE);
		LOG.error(NEEDLE);
		LOG.error(NEEDLE);
		assertTrue(currentTraceFile.exists() && !currentTraceFile.isDirectory());
		assertTrue(FileUtils.readFileToString(currentTraceFile).isEmpty());

		// Test2: Trace severity = ERROR, Trace enabled = TRUE => Trace file should be created and contain just error entry
		proxyLoggingListener.setTraceSeverity("ERROR");
		proxyLoggingListener.setEnableTracing(true);
		LOG.debug(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		assertTrue(currentTraceFile.exists() && !currentTraceFile.isDirectory());
		String fileContent = FileUtils.readFileToString(currentTraceFile);
		int matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(1, matchCount);

		// Test3: Trace severity = ERROR, Trace enabled = FALSE => Trace file should be existing but since tracing is off, the error should not be written
		proxyLoggingListener.setEnableTracing(false);
		LOG.debug(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		assertTrue(currentTraceFile.exists() && !currentTraceFile.isDirectory());
		fileContent = FileUtils.readFileToString(currentTraceFile);
		matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(1, matchCount); // should still be 1 from Test2
	}


	@Test
	public void testLogDebugSeverity() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestLogDebugSeverity.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestLogDebugSeverity.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		proxyLoggingListener.setLogSeverity("DEBUG");
		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);


		final String fileContent = FileUtils.readFileToString(currentLogFile);
		final int matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(5, matchCount);
	}


	@Test
	public void testLogWarnSeverity() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestLogWarnSeverity.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestLogWarnSeverity.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		proxyLoggingListener.setLogSeverity("WARNING");

		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);

		final String fileContent = FileUtils.readFileToString(currentLogFile);
		final int matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(3, matchCount);
	}




	@Test
	public void testNoneSeverityForLogAndTrace() throws IOException
	{
		// Temporary logging files; The TemporaryFolder Rule allows creation of files and folders that should be deleted when the test method finishes (whether it passes or fails)
		final File currentLogFile = temporaryFolder.newFile("logtestNoneSeverityForLogAndTrace.log");
		final File currentTraceFile = temporaryFolder.newFile("tracetestNoneSeverityForLogAndTrace.trc");

		proxyLoggingListener.setLogFilePath(currentLogFile.getAbsolutePath());
		proxyLoggingListener.setTraceFilePath(currentTraceFile.getAbsolutePath());
		proxyLoggingListener.init();

		proxyLoggingListener.setLogSeverity("NONE");
		proxyLoggingListener.setTraceSeverity("NONE");
		proxyLoggingListener.setEnableTracing(true);

		LOG.debug(NEEDLE);
		LOG.info(NEEDLE);
		LOG.warn(NEEDLE);
		LOG.error(NEEDLE);
		LOG.fatal(NEEDLE);

		// log file should exist but contain just the registeration info
		final String fileContent = FileUtils.readFileToString(currentLogFile);
		final int matchCount = StringUtils.countMatches(fileContent, NEEDLE);
		assertEquals(0, matchCount);

		// trace file should exist but should be empty
		assertTrue(currentTraceFile.exists() && !currentTraceFile.isDirectory());
		assertTrue((FileUtils.readFileToString(currentTraceFile)).isEmpty());
	}





}