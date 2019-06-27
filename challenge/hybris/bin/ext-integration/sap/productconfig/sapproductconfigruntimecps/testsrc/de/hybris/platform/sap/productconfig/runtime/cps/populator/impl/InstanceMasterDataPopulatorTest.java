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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class InstanceMasterDataPopulatorTest
{
	private static final String INSTANCE_ID = "Instance Id";
	private static final String LANG_DEP_NAME = "LangDepName";
	private static final String TYPE = "Type";
	private static final String KEY = "Key";
	private static final String KB_ID = "99";

	@Mock
	private MasterDataContainerResolver resolver;

	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;
	private InstanceMasterDataPopulator classUnderTest;
	private CPSItem source;
	private InstanceModel target;

	@Before
	public void setup()
	{
		classUnderTest = new InstanceMasterDataPopulator();
		source = prepareCloudEngineItem();
		target = new InstanceModelImpl();

		MockitoAnnotations.initMocks(this);
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);
		classUnderTest.setMasterDataResolver(resolver);

		Mockito.when(resolver.getItemName(kbContainer, KEY, TYPE)).thenReturn(LANG_DEP_NAME);
	}

	private CPSItem prepareCloudEngineItem()
	{
		final CPSConfiguration cpsConfig = new CPSConfiguration();
		cpsConfig.setKbId(KB_ID);
		final CPSItem item = new CPSItem();
		item.setCharacteristicGroups(new ArrayList<>());
		item.setSubItems(new ArrayList<>());
		item.setKey(KEY);
		item.setType(TYPE);
		item.setParentConfiguration(cpsConfig);
		return item;
	}

	@Test
	public void testPopulateStandardCase()
	{
		classUnderTest.populate(source, target, ctxt);
		final String result = target.getLanguageDependentName();
		assertNotNull(result);
		assertEquals(LANG_DEP_NAME, result);
	}

	@Test
	public void testPopulateSubItems()
	{
		final CPSItem subItem = prepareCloudEngineItem();
		subItem.setId(INSTANCE_ID);
		source.getSubItems().add(subItem);

		assertTrue(target.getSubInstances().isEmpty());

		//This populator does not take care of sub items,
		//orchestration is taken care of by InstancePopulator
		classUnderTest.populate(source, target, ctxt);
		assertNotNull(target);
		assertNotNull(target.getSubInstances());
		assertEquals(0, target.getSubInstances().size());
	}



}
