/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ProductConfigSessionAttributeContainer;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@SuppressWarnings("javadoc")
@UnitTest
/**
 * Unit tests for {@link SessionAccessServiceImpl}
 */
public class SessionAccessServiceImplTest
{
	private static final String SESSION_ID = "123";
	private long startTime;
	private static final long unblockedMaximumExecutionTime = 100;
	private static final long blockingTime = 250;
	private final List<Long> times = new ArrayList<>();
	private long firstThreadTime;
	private long secondThreadTime;
	private static final String configId = "1";

	SessionAccessServiceImpl classUnderTest = new SessionAccessServiceImpl();
	private final Session session2 = new DefaultSession();
	private final ProductConfigSessionAttributeContainer sessionContainer = new ProductConfigSessionAttributeContainer();

	private class SessionAccessThread extends Thread
	{
		@Override
		public void run()
		{
			classUnderTest.retrieveSessionAttributeContainer();
		}
	}

	@Mock
	private SessionService mockSessionService;
	@Mock
	private Session mockSession;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(mockSession.getSessionId()).thenReturn(SESSION_ID);
		when(mockSessionService.getCurrentSession()).thenReturn(mockSession);
		when(mockSessionService.getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER))
				.thenReturn(sessionContainer);

		classUnderTest.setSessionService(mockSessionService);
	}

	@Test
	public void testSessionService()
	{
		assertNotNull(classUnderTest.getSessionId());
	}

	@Test
	public void testCartEntryConfigId()
	{
		final String configId = "1";
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatus()
	{
		final String cartEntryKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}

	@Test
	public void testUIStatusProduct()
	{
		final String productKey = "X";
		final Object status = "S";
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
		classUnderTest.removeUiStatusForProduct(productKey);
		assertNull(classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		assertEquals(cartEntryKey, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureLastAssignmentWins()
	{
		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertEquals(cartEntryKeySecond, classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testConfigIdForCartEntryEnsureFirstAssignmentGone()
	{
		final String cartEntryKey = "X";
		final String cartEntryKeySecond = "Y";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.setConfigIdForCartEntry(cartEntryKeySecond, configId);
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeConfigIdForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getCartEntryForConfigId(configId));
	}

	@Test
	public void testCartEntryForProduct()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryId, configId);
		classUnderTest.setConfigIdForProduct(productKey, configId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeCartEntryForProduct(productKey);
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryCartEntryMap()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryId, configId);
		classUnderTest.setConfigIdForProduct(productKey, configId);
		assertEquals(cartEntryId, classUnderTest.getCartEntryForProduct(productKey));
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryId);
		//We expect that the corresponding product/cartEntry entry is gone!
		assertNull(classUnderTest.getCartEntryForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryDraftConfigRemoved()
	{
		final String cartEntryId = "1";
		final String productKey = "X";
		classUnderTest.setDraftConfigIdForCartEntry(cartEntryId, configId);
		classUnderTest.setConfigIdForProduct(productKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryId);
		//We expect that the corresponding product/cartEntry entry is gone!
		assertNull(classUnderTest.getConfigIdForProduct(productKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigMap()
	{
		final String cartEntryKey = "X";
		classUnderTest.setConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testRemoveSessionArtifactsForCartEntryConfigDraftMap()
	{
		final String cartEntryKey = "X";
		classUnderTest.setDraftConfigIdForCartEntry(cartEntryKey, configId);
		classUnderTest.removeSessionArtifactsForCartEntry(cartEntryKey);
		assertNull(classUnderTest.getDraftConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testGetSolrProperties()
	{
		final Set<String> solrProperties = new HashSet<>();
		classUnderTest.setSolrIndexedProperties(solrProperties);
		assertEquals(solrProperties, classUnderTest.getSolrIndexedProperties());
	}

	@Test
	public void testConfigurationProvider()
	{
		final ConfigurationProvider provider = new DummyConfigurationProvider();
		classUnderTest.setConfigurationProvider(provider);
		assertEquals(provider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testConfigurationModelEngineState()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		classUnderTest.setConfigurationModelEngineState(configId, configModel);
		assertEquals(configModel, classUnderTest.getConfigurationModelEngineState(configId));
		classUnderTest.removeConfigAttributeState(configId);
		assertNull(classUnderTest.getConfigurationModelEngineState(configId));
	}

	@Test
	public void testRetrieveSessionAttributeContainerThreadsWait() throws InterruptedException
	{
		delaySynchronizedCall();

		final SessionAccessThread firstThread = new SessionAccessThread();
		firstThread.start();
		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		Thread.sleep(blockingTime + unblockedMaximumExecutionTime);
		checkBothThreadsFinished();
		assertTrue("First thread took too long: " + firstThreadTime, firstThreadTime < unblockedMaximumExecutionTime);
		assertTrue("First thread took not long enough: " + secondThreadTime, secondThreadTime >= blockingTime);
	}

	@Test
	public void testRetrieveSessionAttributeContainerThreadsDontWait() throws InterruptedException
	{
		delaySynchronizedCall();

		final SessionAccessThread firstThread = new SessionAccessThread();
		firstThread.start();
		Thread.sleep(unblockedMaximumExecutionTime);

		//Now simulate a different user session so that threads don't need to wait for each other
		when(mockSessionService.getCurrentSession()).thenReturn(session2);

		final SessionAccessThread secondThread = new SessionAccessThread();
		secondThread.start();
		Thread.sleep(unblockedMaximumExecutionTime);
		checkBothThreadsFinished();
		assertTrue("First thread took too long: " + firstThreadTime, firstThreadTime < unblockedMaximumExecutionTime);
		assertTrue("Second thread took too long: " + secondThreadTime, secondThreadTime < blockingTime);
	}

	protected void checkBothThreadsFinished()
	{
		assertEquals("We expect both threads to be finished", 2, times.size());
		firstThreadTime = times.get(0).longValue();
		secondThreadTime = times.get(1).longValue();
	}

	protected void delaySynchronizedCall()
	{
		startTime = System.currentTimeMillis();
		times.clear();
		doAnswer(new Answer()
		{
			@Override
			public Void answer(final InvocationOnMock invocation) throws Throwable
			{
				times.add(Long.valueOf(System.currentTimeMillis() - startTime));
				Thread.sleep(blockingTime);
				return null;
			}
		}).when(mockSessionService).getAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER);
	}


	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		verify(mockSessionService).setAttribute(SessionAccessServiceImpl.PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER, null);
	}

	@Test
	public void testConfigCacheGrowsNotEndless()
	{
		classUnderTest.setConfigurationModelEngineState("configID", new ConfigModelImpl());
		final int maxCachedConfigs = classUnderTest.getMaxCachedConfigsInSession();
		for (int ii = 0; ii <= maxCachedConfigs; ii++)
		{
			final String configId = String.valueOf(ii);
			classUnderTest.setConfigurationModelEngineState(configId, new ConfigModelImpl());
		}

		assertNull(classUnderTest.getConfigurationModelEngineState("configID"));
		assertEquals(classUnderTest.getMaxCachedConfigsInSession() / 2 + 2,
				classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
	}

	@Test
	public void testOldConfigsAreStillAvailable()
	{
		assertEquals(0, classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());

		classUnderTest.setConfigurationModelEngineState("configID", new ConfigModelImpl());
		final int maxCachedConfigs = classUnderTest.getMaxCachedConfigsInSession() / 2 - 1;
		for (int ii = 0; ii <= maxCachedConfigs; ii++)
		{
			final String configId = String.valueOf(ii);
			classUnderTest.setConfigurationModelEngineState(configId, new ConfigModelImpl());
			assertEquals(ii + 2, classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
		}

		assertNotNull(classUnderTest.getConfigurationModelEngineState("configID"));
		assertEquals(classUnderTest.getMaxCachedConfigsInSession() / 2 + 1,
				classUnderTest.retrieveSessionAttributeContainer().getConfigurationModelEngineStates().size());
	}

	@Test
	public void testLinkWithProductCode()
	{
		final String pCode = "123";
		assertNull(classUnderTest.getConfigIdForProduct(pCode));
		classUnderTest.setConfigIdForProduct(pCode, configId);
		assertEquals(configId, classUnderTest.getConfigIdForProduct(pCode));
	}

	@Test
	public void testLinkWithProductCodeIsCleanedUp()
	{
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		classUnderTest.removeConfigIdForProduct(pCode);
		assertNull(classUnderTest.getConfigIdForProduct(pCode));
	}

	@Test
	public void testGetProductForConfigId()
	{
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		final String result = classUnderTest.getProductForConfigId(configId);
		assertNotNull(result);
		assertEquals(pCode, result);
	}

	@Test
	public void testGetProductForConfigIdNoProduct()
	{
		assertNull(classUnderTest.getProductForConfigId(configId));
	}

	@Test
	public void testGetProductForConfigIdNull()
	{
		//fill at least one entry into the cache
		final String pCode = "123";
		classUnderTest.setConfigIdForProduct(pCode, configId);
		assertNull(classUnderTest.getProductForConfigId(null));
	}

	@Test
	public void testFindInMapNullEntriesWork()
	{
		final Map<String, String> map = new HashMap<>();
		map.put("cart entry key", null);
		final List<String> result = classUnderTest.findConfigIdInMap(configId, map);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetCartEntryForDraftConfigIdReturnsNull()
	{
		assertNull(classUnderTest.getCartEntryForDraftConfigId(configId));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCartEntryForDraftConfigIdThrowsExpection()
	{
		final Map<String, String> cartEntryDraftConfigurations = new HashMap<>();
		final String configId_1 = "config_1";
		cartEntryDraftConfigurations.put("key_1", configId_1);
		cartEntryDraftConfigurations.put("key_2", configId_1);
		cartEntryDraftConfigurations.put("key_3", "config_2");
		sessionContainer.setCartEntryDraftConfigurations(cartEntryDraftConfigurations);

		classUnderTest.getCartEntryForDraftConfigId(configId_1);
	}

	@Test
	public void testGetCartEntryForDraftConfigId()
	{
		final Map<String, String> cartEntryDraftConfigurations = new HashMap<>();
		final String configId_2 = "config_2";
		final String key_2 = "key_2";
		cartEntryDraftConfigurations.put("key_1", "config_1");
		cartEntryDraftConfigurations.put(key_2, configId_2);
		cartEntryDraftConfigurations.put("key_3", "config_3");
		sessionContainer.setCartEntryDraftConfigurations(cartEntryDraftConfigurations);

		assertEquals(key_2, classUnderTest.getCartEntryForDraftConfigId(configId_2));
	}
}
