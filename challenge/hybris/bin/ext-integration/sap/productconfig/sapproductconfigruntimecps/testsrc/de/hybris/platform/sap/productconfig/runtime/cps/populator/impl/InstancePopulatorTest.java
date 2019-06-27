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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.AbstractContextualPopulatingConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.VariantConditionModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class InstancePopulatorTest
{
	private static final String CONDITION_FACTOR = "1.23";
	private static final BigDecimal CONDITION_FACTOR_BIG_DECIMAL = new BigDecimal(CONDITION_FACTOR);
	private static final String CONDITION_KEY = "Condition key";
	private static final String instanceId = "1";
	private static final String bomPosition = "10";
	private static final String instanceProdnr = "HT-1010";
	private static final String groupid = "GROUP 1";
	private static final String kbId = "99";
	private static final String productId = "PRODUCT_ID";
	private static final String csticId = "CsticId";
	private static final String UOM = "PCE";

	@Mock
	private ContextualConverter<CPSCharacteristicGroup, CsticGroupModel, MasterDataContext> mockedGroupConverter;
	@Mock
	private ContextualConverter<CPSCharacteristic, CsticModel, MasterDataContext> mockedCsticConverter;
	@Mock
	private Converter<CPSVariantCondition, VariantConditionModel> mockedVariantConditionConverter;

	private InstancePopulator classUnderTest;
	private CPSCharacteristic characteristic;
	private AbstractContextualPopulatingConverter instanceConverter;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;
	private CPSItem source;
	private InstanceModel target;
	private CPSCharacteristicGroup characteristicGroup;




	private final CPSQuantity quantity = new CPSQuantity();

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new InstancePopulator();
		instanceConverter = new AbstractContextualPopulatingConverter<CPSItem, InstanceModel, MasterDataContext>();
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		kbContainer.setHeaderInfo(new CPSMasterDataKBHeaderInfo());
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);

		instanceConverter.setContextualPopulators(Collections.singletonList(classUnderTest));
		instanceConverter.setTargetClass(InstanceModelImpl.class);
		classUnderTest.setInstanceModelConverter(instanceConverter);
		classUnderTest.setCharacteristicGroupConverter(mockedGroupConverter);
		classUnderTest.setCharacteristicConverter(mockedCsticConverter);

		classUnderTest.setVariantConditionConverter(mockedVariantConditionConverter);
		final VariantConditionModel variantConditionModel = new VariantConditionModelImpl();
		variantConditionModel.setKey(CONDITION_KEY);
		variantConditionModel.setFactor(CONDITION_FACTOR_BIG_DECIMAL);
		Mockito.when(mockedVariantConditionConverter.convert(Mockito.any())).thenReturn(variantConditionModel);

		source = new CPSItem();
		source.setCharacteristicGroups(new ArrayList<>());
		source.setSubItems(new ArrayList<>());
		source.setCharacteristics(new ArrayList<>());
		source.setQuantity(quantity);
		source.setKey(productId);
		source.setId(instanceId);
		quantity.setUnit(UOM);
		final CPSConfiguration parentConfiguration = new CPSConfiguration();
		parentConfiguration.setKbId(kbId);
		parentConfiguration.setRootItem(source);
		source.setParentConfiguration(parentConfiguration);
		target = new InstanceModelImpl();

		//sub items
		final CPSItem subItem = new CPSItem();
		subItem.setId(instanceId);
		subItem.setParentItem(source);
		subItem.setParentConfiguration(parentConfiguration);
		subItem.setCharacteristicGroups(new ArrayList<>());
		subItem.setVariantConditions(new ArrayList<>());
		subItem.setSubItems(new ArrayList<>());
		source.getSubItems().add(subItem);

		//groups
		characteristicGroup = new CPSCharacteristicGroup();
		characteristicGroup.setId(groupid);
		characteristicGroup.setParentItem(source);
		source.getCharacteristicGroups().add(characteristicGroup);

		//characteristics
		characteristic = new CPSCharacteristic();
		characteristic.setId(csticId);
		characteristic.setParentItem(source);
		characteristic.setPossibleValues(new ArrayList<>());
		characteristic.setValues(new ArrayList<>());
		source.getCharacteristics().add(characteristic);

		// variant conditions
		source.setVariantConditions(prepareVariantConditions());
	}

	@Test
	public void testCsticConverter()
	{
		assertEquals(mockedCsticConverter, classUnderTest.getCharacteristicConverter());
	}

	@Test
	public void testGroupModelConverter()
	{
		assertEquals(mockedGroupConverter, classUnderTest.getCharacteristicGroupConverter());
	}

	@Test
	public void testInstanceModelConverter()
	{
		assertEquals(instanceConverter, classUnderTest.getInstanceModelConverter());
	}

	@Test
	public void testPopulateId()
	{
		source.setId(instanceId);
		classUnderTest.populateCoreAttributes(source, target);
		assertNotNull(target);
		assertEquals(instanceId, target.getId());
	}

	@Test
	public void testPopulateBomPosition()
	{
		source.setBomPosition(bomPosition);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(bomPosition, target.getPosition());
	}

	@Test
	public void testPopulateBomPositionName()
	{
		source.setKey(instanceProdnr);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(instanceProdnr, target.getName());
	}

	@Test
	public void testPopulateComplete()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse(target.isComplete());
		source.setComplete(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isComplete());
	}

	@Test
	public void testPopulateConsistent()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertFalse(target.isConsistent());
		source.setConsistent(true);
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isConsistent());
	}

	@Test
	public void testPopulateSubItems()
	{
		classUnderTest.populate(source, target, ctxt);
		assertNotNull(target);
		final List<InstanceModel> subInstances = target.getSubInstances();
		assertNotNull(subInstances);
		assertFalse(subInstances.isEmpty());
		assertEquals(1, subInstances.size());
		assertEquals(instanceId, subInstances.get(0).getId());
	}

	@Test
	public void testPopulateSubItemsNull()
	{
		source.setSubItems(null);
		classUnderTest.populate(source, target, ctxt);
		assertNotNull(target);
		final List<InstanceModel> subInstances = target.getSubInstances();
		assertNotNull(subInstances);
		assertTrue(subInstances.isEmpty());
	}

	@Test
	public void testSubinstanceExistsInTarget()
	{
		final InstanceModel subinstance = new InstanceModelImpl();
		subinstance.setId(instanceId);
		final List<InstanceModel> subInstances = target.getSubInstances();
		subInstances.add(subinstance);
		target.setSubInstances(subInstances);
		assertEquals(1, target.getSubInstances().size());

		//The populator just adds to the list of target sub instances, so we expect 2 items now
		classUnderTest.populate(source, target, ctxt);
		assertEquals(2, target.getSubInstances().size());
		assertEquals(instanceId, target.getSubInstances().get(0).getId());
	}

	@Test
	public void testPopulateGroups()
	{
		characteristicGroup.setParentItem(null);
		classUnderTest.populateGroups(source, target, ctxt);
		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(1, csticGroups.size());
	}

	@Test
	public void testPopulateDefaultAndNotDefaultGroups()
	{
		// non default group
		characteristicGroup.setParentItem(null);

		//default group
		final CPSCharacteristicGroup defaultCharacteristicGroup = new CPSCharacteristicGroup();
		defaultCharacteristicGroup.setId(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID);
		defaultCharacteristicGroup.setParentItem(null);
		source.getCharacteristicGroups().add(defaultCharacteristicGroup);

		final CsticGroupModel characteristicGroupModel = new CsticGroupModelImpl();
		characteristicGroupModel.setName(characteristicGroup.getId());
		final CsticGroupModel defaultCharacteristicGroupModel = new CsticGroupModelImpl();
		defaultCharacteristicGroupModel.setName(InstanceModel.GENERAL_GROUP_NAME);

		Mockito.when(mockedGroupConverter.convertWithContext(characteristicGroup, ctxt)).thenReturn(characteristicGroupModel);
		Mockito.when(mockedGroupConverter.convertWithContext(defaultCharacteristicGroup, ctxt))
				.thenReturn(defaultCharacteristicGroupModel);

		classUnderTest.populateGroups(source, target, ctxt);

		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(2, csticGroups.size());
		// default group has to be the first in the list
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, csticGroups.get(0).getName());
		assertEquals(groupid, csticGroups.get(1).getName());
	}

	@Test
	public void testPopulateCstics()
	{
		classUnderTest.populateCstics(source, target, ctxt);
		final List<CsticModel> characteristcis = target.getCstics();
		assertNotNull(characteristcis);
		assertEquals(1, characteristcis.size());
	}

	@Test
	public void testPopulateCsticsNull()
	{
		source.setCharacteristics(null);
		classUnderTest.populateCstics(source, target, ctxt);
		final List<CsticModel> characteristcis = target.getCstics();
		assertNotNull(characteristcis);
		assertTrue(characteristcis.isEmpty());
	}

	@Test
	public void testPopulateGroupsKeysHaveBeenPushedDown()
	{
		final CPSConfiguration config = new CPSConfiguration();
		config.setKbId(kbId);

		source.setId(productId);
		config.setRootItem(source);

		source.setParentConfiguration(config);
		classUnderTest.populateGroups(source, target, ctxt);
		classUnderTest.populateCstics(source, target, ctxt);
		assertEquals(kbId, characteristicGroup.getParentItem().getParentConfiguration().getKbId());
		assertEquals(productId, characteristicGroup.getParentItem().getParentConfiguration().getRootItem().getId());
		assertEquals(kbId, characteristic.getParentItem().getParentConfiguration().getKbId());
	}

	@Test
	public void testPopulateTakesCareOfGroups()
	{
		classUnderTest.populate(source, target, ctxt);
		final List<CsticGroupModel> csticGroups = target.getCsticGroups();
		assertNotNull(csticGroups);
		assertEquals(1, csticGroups.size());
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateRootUOMToMasterDataCacheNoParentConfiguration()
	{
		source.setParentConfiguration(null);
		classUnderTest.populateRootUOMToMasterDataCache(source, ctxt);
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateRootUOMToMasterDataCacheNoKbId()
	{
		ctxt.setKbCacheContainer(null);
		classUnderTest.populateRootUOMToMasterDataCache(source, ctxt);
	}

	@Test
	public void testPopulateUOMToMasterDataCache()
	{
		classUnderTest.populateUOMToMasterDataCache(source, ctxt);
		assertEquals(UOM, kbContainer.getRootUnitOfMeasure());
	}

	@Test
	public void testPopulateVariantConditions()
	{
		classUnderTest.populateVariantConditions(source, target);
		final List<VariantConditionModel> result = target.getVariantConditions();
		assertNotNull(result);
		assertEquals(1, result.size());
		final VariantConditionModel resultCondition = result.get(0);
		assertEquals(CONDITION_KEY, resultCondition.getKey());
		assertEquals(0, CONDITION_FACTOR_BIG_DECIMAL.compareTo(resultCondition.getFactor()));
	}

	protected List<CPSVariantCondition> prepareVariantConditions()
	{
		final List<CPSVariantCondition> variantConditions = new ArrayList<>();
		final CPSVariantCondition variantCondition = new CPSVariantCondition();
		variantCondition.setKey(CONDITION_KEY);
		variantCondition.setFactor(CONDITION_FACTOR);
		variantConditions.add(variantCondition);
		return variantConditions;
	}

}
