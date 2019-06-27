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
package de.hybris.platform.ruleengine.init.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineContainerRegistryUnitTest
{
	@Mock
	private KieContainer kieContainer;
	@Mock
	private ConcurrentMapFactory concurrentMapFactory;

	private static final String RELEASEID_GROUPID = "test-group";
	private static final String RELEASEID_ARTIFACTID = "test-artifact";
	private static final String RELEASEID_VERSION = "test-version";
	
	private Map containerMap;
	
	private DefaultRuleEngineContainerRegistry ruleEngineContainerRegistry;
	
	private ReleaseIdImpl releaseIdX;
	private ReleaseIdImpl releaseIdY;

	@Mock
	private KieContainer kieContainerX;
	@Mock
	private KieContainer kieContainerY;

	@Before
	public void setUp()
	{
		containerMap = new ConcurrentHashMap(3, 0.75F, 2);
		when(concurrentMapFactory.createNew()).thenReturn(containerMap);
		ruleEngineContainerRegistry = new DefaultRuleEngineContainerRegistry();
		ruleEngineContainerRegistry.setConcurrentMapFactory(concurrentMapFactory);
		ruleEngineContainerRegistry.setup();
		
		// same groupId xor artifactId as regular test data
		releaseIdX = new ReleaseIdImpl("test_groupX", RELEASEID_ARTIFACTID, "1.0.0.1");
		releaseIdY = new ReleaseIdImpl(RELEASEID_GROUPID, "test_artifactY", "1.0.0.1");
	}

	@Test
	public void testSetGetActiveContainer()
	{
		final ReleaseIdImpl releaseId = new ReleaseIdImpl(RELEASEID_GROUPID, RELEASEID_ARTIFACTID, RELEASEID_VERSION);
		ruleEngineContainerRegistry.setActiveContainer(releaseId, kieContainer);

		assertThat(ruleEngineContainerRegistry.getActiveContainer(releaseId)).isEqualTo(kieContainer);
	}

	@Test
	public void testSetRemoveGetActiveContainer()
	{
		final ReleaseIdImpl releaseId = new ReleaseIdImpl(RELEASEID_GROUPID, RELEASEID_ARTIFACTID, RELEASEID_VERSION);
		ruleEngineContainerRegistry.setActiveContainer(releaseId, kieContainer);

		final KieContainer removedKieContainer = ruleEngineContainerRegistry.removeActiveContainer(releaseId);
		assertThat(removedKieContainer).isEqualTo(kieContainer);

		assertThat(ruleEngineContainerRegistry.getActiveContainer(releaseId)).isNull();
	}

	@Test
	public void  testLookupForDeployedRelease()
	{
		final ReleaseIdImpl releaseId1 = new ReleaseIdImpl("test_group1", "test_artifact1", RELEASEID_VERSION);  // NOSONAR
		final ReleaseIdImpl releaseId2 = new ReleaseIdImpl("test_group2", "test_artifact2", RELEASEID_VERSION);  // NOSONAR
		final KieContainer kieContainer1 = Mockito.mock(KieContainer.class);
		final KieContainer kieContainer2 = Mockito.mock(KieContainer.class);

		ruleEngineContainerRegistry.setActiveContainer(releaseId1, kieContainer1);
		ruleEngineContainerRegistry.setActiveContainer(releaseId2, kieContainer2);

		assertThat(ruleEngineContainerRegistry.lookupForDeployedRelease("test_group1", "test_artifact1")).isPresent().contains(releaseId1);
		assertThat(ruleEngineContainerRegistry.lookupForDeployedRelease("test_group2", "test_artifact2")).isPresent().contains(releaseId2);
		assertThat(ruleEngineContainerRegistry.lookupForDeployedRelease("test_group1", "test_artifact2")).isNotPresent();
	}

	@Test
	public void testLockReadingRegistry()
	{
		ruleEngineContainerRegistry.lockReadingRegistry();
		assertThat(ruleEngineContainerRegistry.isLockedForReading()).isTrue();

		ruleEngineContainerRegistry.unlockReadingRegistry();
		assertThat(ruleEngineContainerRegistry.isLockedForReading()).isFalse();
	}

	@Test
	public void testLockWritingRegistry()
	{
		ruleEngineContainerRegistry.lockWritingRegistry();
		assertThat(ruleEngineContainerRegistry.isLockedForWriting()).isTrue();

		ruleEngineContainerRegistry.unlockWritingRegistry();
		assertThat(ruleEngineContainerRegistry.isLockedForWriting()).isFalse();
	}
	
	@Test
	public void testRemoveAllPreviousKieContainersEnabled()
	{
		final int size = setupWithUnrelatedContainers(true);

		final ReleaseIdImpl releaseId1 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.1");
		final KieContainer kieContainer1 = Mockito.mock(KieContainer.class);

		final ReleaseIdImpl releaseId2 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.2");
		final KieContainer kieContainer2 = Mockito.mock(KieContainer.class);

		final ReleaseIdImpl releaseId3 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.3");
		final KieContainer kieContainer3 = Mockito.mock(KieContainer.class);

		// each time a new kiecontainer of same groupId/artifactId is added, the previous one(s) are removed
		ruleEngineContainerRegistry.setActiveContainer(releaseId1, kieContainer1);
		assertEquals(kieContainer1, ruleEngineContainerRegistry.getActiveContainer(releaseId1));
		assertEquals(size + 1, containerMap.size());
		ruleEngineContainerRegistry.setActiveContainer(releaseId2, kieContainer2);
		assertEquals(kieContainer2, ruleEngineContainerRegistry.getActiveContainer(releaseId2));
		assertEquals(size + 1, containerMap.size());

		ruleEngineContainerRegistry.setActiveContainer(releaseId3, kieContainer3);
		assertEquals(kieContainer3, ruleEngineContainerRegistry.getActiveContainer(releaseId3));
		assertEquals(size + 1, containerMap.size());

		assertEquals(null, ruleEngineContainerRegistry.getActiveContainer(releaseId1));
		assertEquals(null, ruleEngineContainerRegistry.getActiveContainer(releaseId2));

		// the unrelated kieContainers remain intact
		assertEquals(kieContainerX, ruleEngineContainerRegistry.getActiveContainer(releaseIdX));
		assertEquals(kieContainerY, ruleEngineContainerRegistry.getActiveContainer(releaseIdY));


	}

	@Test
	public void testRemoveAllPreviousKieContainersDisabled()
	{
		ruleEngineContainerRegistry.setKeepOnlyOneContainerVersion(false);

		final int size = setupWithUnrelatedContainers(false);

		final ReleaseIdImpl releaseId1 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.1");
		final KieContainer kieContainer1 = Mockito.mock(KieContainer.class);

		final ReleaseIdImpl releaseId2 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.2");
		final KieContainer kieContainer2 = Mockito.mock(KieContainer.class);

		final ReleaseIdImpl releaseId3 = new ReleaseIdImpl("test_group1", "test_artifact1", "1.0.0.3");
		final KieContainer kieContainer3 = Mockito.mock(KieContainer.class);

		// each time a new kiecontainer of same groupId/artifactId is added, the previous ones remain
		ruleEngineContainerRegistry.setActiveContainer(releaseId1, kieContainer1);
		assertEquals(kieContainer1, ruleEngineContainerRegistry.getActiveContainer(releaseId1));
		assertEquals(size + 1, containerMap.size());
		ruleEngineContainerRegistry.setActiveContainer(releaseId2, kieContainer2);
		assertEquals(kieContainer2, ruleEngineContainerRegistry.getActiveContainer(releaseId2));
		assertEquals(size + 2, containerMap.size());

		ruleEngineContainerRegistry.setActiveContainer(releaseId3, kieContainer3);
		assertEquals(kieContainer3, ruleEngineContainerRegistry.getActiveContainer(releaseId3));
		assertEquals(size + 3, containerMap.size());

		assertEquals(kieContainer1, ruleEngineContainerRegistry.getActiveContainer(releaseId1));
		assertEquals(kieContainer2, ruleEngineContainerRegistry.getActiveContainer(releaseId2));

		// the unrelated kieContainers remain intact
		assertEquals(kieContainerX, ruleEngineContainerRegistry.getActiveContainer(releaseIdX));
		assertEquals(kieContainerY, ruleEngineContainerRegistry.getActiveContainer(releaseIdY));


	}

	protected int setupWithUnrelatedContainers(final boolean flag)
	{
		ruleEngineContainerRegistry.setKeepOnlyOneContainerVersion(flag);

		releaseIdX = new ReleaseIdImpl("test_groupX", "test_artifact1", "1.0.0.1");
		releaseIdY = new ReleaseIdImpl("test_group1", "test_artifactY", "1.0.0.1");

		ruleEngineContainerRegistry.setActiveContainer(releaseIdX, kieContainerX);

		assertEquals(kieContainerX, ruleEngineContainerRegistry.getActiveContainer(releaseIdX));
		assertEquals(1, containerMap.size());

		ruleEngineContainerRegistry.setActiveContainer(releaseIdY, kieContainerY);

		assertEquals(kieContainerY, ruleEngineContainerRegistry.getActiveContainer(releaseIdY));
		assertEquals(2, containerMap.size());

		return containerMap.size();
	}

	

}
