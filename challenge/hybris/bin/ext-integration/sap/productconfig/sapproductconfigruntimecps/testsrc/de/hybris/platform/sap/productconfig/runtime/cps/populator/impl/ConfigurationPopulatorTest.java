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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.argThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.ServiceVersionProvider;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationPopulatorTest
{
	private static final String KB_VERSION = "KB version";
	private static final String KB_NAME = "KB Name";
	private static final String LOGICAL_SYSTEM = "logical system";
	private static final String VERSION = "v2";
	private ConfigModel target;
	private CPSConfiguration source;
	private final CPSItem rootItem = new CPSItem();
	private static final String ROOT_ITEM_ID = "A";
	private static final String CFG_ID = "1";
	private static final String KB_ID = "99";
	private static final String PRODUCT_KEY = "pCode";
	private static final String RUNTIME_VERSION = "etag";
	private static final CPSMasterDataKnowledgebaseKey KB_KEY = new CPSMasterDataKnowledgebaseKey();

	private ConfigurationPopulator classUnderTest;

	@Mock
	private ContextualConverter<CPSItem, InstanceModel, MasterDataContext> mockedInstanceConverter;
	@Mock
	private ConfigurationMasterDataService masterDataService;
	@Mock
	private MasterDataContainerResolver resolver;
	@Mock
	private Converter<CPSConflict, SolvableConflictModel> conflictPopulator;
	@Mock
	private ServiceVersionProvider versionProviderMock;

	private CPSMasterDataKnowledgeBaseContainer kbContainer;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationPopulator();


		target = new ConfigModelImpl();
		source = new CPSConfiguration();
		rootItem.setId(ROOT_ITEM_ID);
		rootItem.setParentConfiguration(source);
		source.setRootItem(rootItem);

		classUnderTest.setMasterDataResolver(resolver);
		classUnderTest.setInstanceModelConverter(mockedInstanceConverter);
		classUnderTest.setMasterDataService(masterDataService);
		classUnderTest.setConflictModelConverter(conflictPopulator);
		given(masterDataService.isProductMultilevel(KB_ID, PRODUCT_KEY)).willReturn(Boolean.FALSE);
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		given(masterDataService.getMasterData(KB_ID)).willReturn(kbContainer);
		source.setId(CFG_ID);
		source.setETag(RUNTIME_VERSION);
		source.setKbId(KB_ID);
		source.setProductKey(PRODUCT_KEY);
		source.setKbKey(KB_KEY);
		KB_KEY.setLogsys(LOGICAL_SYSTEM);
		KB_KEY.setName(KB_NAME);
		KB_KEY.setVersion(KB_VERSION);

		given(mockedInstanceConverter.convertWithContext(Mockito.same(rootItem), argThat(matchContextContainingSameKbContainer())))
				.willReturn(new InstanceModelImpl());

		classUnderTest.setVersionProvider(versionProviderMock);
	}

	protected ArgumentMatcher<MasterDataContext> matchContextContainingSameKbContainer()
	{
		return new ArgumentMatcher<MasterDataContext>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				return ((MasterDataContext) argument).getKbCacheContainer() == kbContainer;
			}

		};
	}

	@Test
	public void testPopulateId()
	{
		classUnderTest.populate(source, target);
		assertEquals(CFG_ID, target.getId());
	}

	@Test
	public void testPopulateVersion()
	{
		classUnderTest.populate(source, target);
		assertEquals(RUNTIME_VERSION, target.getVersion());
	}

	@Test
	public void testPopulateKbId()
	{
		classUnderTest.populate(source, target);
		assertEquals(KB_ID, target.getKbId());
	}

	@Test
	public void testPopulateName()
	{
		classUnderTest.populate(source, target);
		assertEquals(PRODUCT_KEY, target.getName());
	}

	@Test
	public void testPopulateConsistent()
	{
		source.setConsistent(true);
		classUnderTest.populate(source, target);
		assertTrue(target.isConsistent());
	}

	@Test
	public void testPopulateComplete()
	{
		source.setComplete(true);
		classUnderTest.populate(source, target);
		assertTrue(target.isComplete());
	}

	@Test
	public void testPopulateRootItem()
	{
		rootItem.setParentConfiguration(null);
		classUnderTest.populate(source, target);
		final InstanceModel rootInstance = target.getRootInstance();
		assertNotNull(rootInstance);
	}


	@Test
	public void testKbIdAtSubItem()
	{
		classUnderTest.populate(source, target);
		final CPSItem cloudEngineItem = source.getRootItem();
		assertNotNull(cloudEngineItem);
		assertEquals(KB_ID, cloudEngineItem.getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateIsConsistent()
	{
		source.setConsistent(false);
		classUnderTest.populate(source, target);
		assertFalse(target.isConsistent());
	}

	@Test
	public void testPopulateConflicts()
	{
		final CPSConflict conflict = new CPSConflict();
		final String conflictId = "1";
		conflict.setId(conflictId);
		conflict.setExplanation("This is a conflict");
		conflict.setName("CONFLICT_1");
		conflict.setType(1);
		final List<CPSConflict> conflicts = new ArrayList<>();
		conflicts.add(conflict);
		source.setConflicts(conflicts);

		final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
		solvableConflict.setId(conflictId);
		Mockito.when(conflictPopulator.convert(conflict)).thenReturn(solvableConflict);

		classUnderTest.populateConflicts(source, target);
		assertNotNull(target.getSolvableConflicts());
		assertEquals(conflictId, target.getSolvableConflicts().get(0).getId());
	}

	@Test
	public void testPopulateConflictsWithNullList()
	{
		source.setConflicts(null);
		classUnderTest.populateConflicts(source, target);
		assertEquals(0, target.getSolvableConflicts().size());
	}

	@Test
	public void testPopulateConflictsWithEmptyList()
	{
		source.setConflicts(new ArrayList<>());
		classUnderTest.populateConflicts(source, target);
		assertEquals(0, target.getSolvableConflicts().size());
	}

	@Test
	public void testPopulateSinglelevel()
	{
		Mockito.when(Boolean.valueOf(resolver.isProductMultilevel(kbContainer, PRODUCT_KEY))).thenReturn(Boolean.FALSE);
		classUnderTest.populate(source, target);
		assertTrue(target.isSingleLevel());
	}

	@Test
	public void testPopulateMultilevel()
	{
		Mockito.when(Boolean.valueOf(resolver.isProductMultilevel(kbContainer, PRODUCT_KEY))).thenReturn(Boolean.TRUE);
		classUnderTest.populate(source, target);
		assertFalse(target.isSingleLevel());
	}

	@Test
	public void testPopulateKbKey()
	{
		classUnderTest.populate(source, target);
		assertEquals(LOGICAL_SYSTEM, target.getKbKey().getKbLogsys());
		assertEquals(PRODUCT_KEY, target.getKbKey().getProductCode());
		assertEquals(KB_NAME, target.getKbKey().getKbName());
		assertEquals(KB_VERSION, target.getKbKey().getKbVersion());
	}

	@Test(expected = IllegalStateException.class)
	public void testPopulateKbKeyNoKbKey()
	{
		source.setKbKey(null);
		classUnderTest.populate(source, target);
	}
}
