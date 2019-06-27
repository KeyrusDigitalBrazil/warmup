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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class CharacteristicGroupMasterDataPopulatorTest
{
	CharacteristicGroupMasterDataPopulator classUnderTest = new CharacteristicGroupMasterDataPopulator();

	private CPSCharacteristicGroup source;
	private CsticGroupModel target;
	private List<String> csticIds;
	private static final String groupId = "GROUP_ID";
	private static final String groupName = "Lang dependent name";
	private static final String kbId = "99";
	private static final String itemKey = "Can be product or class";

	private static final String itemTypeProduct = SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA;
	private static final String itemTypeClassNode = SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH;

	@Mock
	private MasterDataContainerResolver resolver;

	private CPSItem item;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);
		classUnderTest.setMasterDataResolver(resolver);

		csticIds = new ArrayList<>();
		Mockito.when(resolver.getGroupName(kbContainer, itemKey, itemTypeProduct, groupId)).thenReturn(groupName);
		Mockito.when(resolver.getGroupCharacteristicIDs(kbContainer, itemKey, itemTypeProduct, groupId)).thenReturn(csticIds);
		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);
		item = new CPSItem();
		item.setKey(itemKey);
		item.setType(itemTypeProduct);
		item.setParentConfiguration(config);
		source = new CPSCharacteristicGroup();
		source.setParentItem(item);
		source.setId(groupId);
		target = new CsticGroupModelImpl();
	}

	@Test
	public void testPopulate()
	{

		classUnderTest.populate(source, target, ctxt);
		assertEquals(groupName, target.getDescription());
		assertEquals(csticIds, target.getCsticNames());
		assertEquals(groupId, target.getName());
	}

	@Test
	public void testPopulateDefaultGroup()
	{
		Mockito.when(resolver.getGroupName(kbContainer, itemKey, itemTypeProduct,
				SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID)).thenReturn(groupName);
		Mockito.when(resolver.getGroupCharacteristicIDs(kbContainer, itemKey, itemTypeProduct,
				SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID)).thenReturn(csticIds);

		source.setId(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID);
		classUnderTest.populate(source, target, ctxt);
		assertEquals(groupName, target.getDescription());
		assertEquals(csticIds, target.getCsticNames());
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, target.getName());
	}

	@Test
	public void testPopulateParentIsClass()
	{
		item.setType(itemTypeClassNode);

		Mockito.when(resolver.getGroupName(kbContainer, itemKey, itemTypeClassNode,
				SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID)).thenReturn(groupName);
		Mockito.when(resolver.getGroupCharacteristicIDs(kbContainer, itemKey, itemTypeProduct,
				SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID)).thenReturn(csticIds);

		source.setId(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID);
		classUnderTest.populate(source, target, ctxt);
		assertEquals(groupName, target.getDescription());
		assertEquals(csticIds, target.getCsticNames());
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, target.getName());
	}

	@Test
	public void testMasterDataService()
	{
		assertEquals(resolver, classUnderTest.getMasterDataResolver());
	}
}
