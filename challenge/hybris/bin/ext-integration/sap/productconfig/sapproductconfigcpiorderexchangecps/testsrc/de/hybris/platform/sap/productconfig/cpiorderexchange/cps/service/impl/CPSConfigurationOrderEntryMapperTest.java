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
package de.hybris.platform.sap.productconfig.cpiorderexchange.cps.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSFlatListContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKnowledgebaseKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigConditionModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHierarchyModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigInstanceModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigValueModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;


@UnitTest
public class CPSConfigurationOrderEntryMapperTest
{
	private static final String OBJECT_TYPE = "objectType";
	private static final String BOM_POSITION_AUTHOR = "bomPositionAuthor";
	private static final String OBJECT_CLASS_TYPE = "objectClassType";
	private static final String OBJECT_ID = "objectId";
	private static final String UNIT_OF_MEASURE = "PCE";
	private static final String UNIT_OF_MEASURE2 = "ST";
	private static final String OBJECT_KEY_AUTHOR = "objectKeyAuthor";
	private static final String SUB_OBJECT_TYPE = "subObjectType";
	private static final String SUB_OBJECT_ID = "subObjectId";
	private static final String SUB_ITEM_ID = "subItemId";
	private static final String BOM_POSITION_CLASS_TYPE = "bomPositionClassType";
	private static final String SUB_ITEM_BOM_POSITION = "subBomPosition";
	private static final String SUB_BOM_POSITION_CLASS_TYPE = "subBomPositionClassType";
	private static final String SUB_BOM_POSITION_AUTHOR = "subBomPositionAuthor";
	private static final String VALUE = "value";
	private static final String VALUE_AUTHOR = "valueAuthor";
	private static final String CHARACTERISTIC_ID = "characteristicId";
	private static final String ROOT_ITEM_ID = "rootItemId";
	private static final String KB_VERSION = "kbVersion";
	private static final String KB_NAME = "kbName";
	private static final String EXTERNAL_CONFIG = "externalConfig";
	private static final String ENTRY_NUMBER = "1";
	private static final String EXTERNAL_CONFIG_JSON = "{\"externalConfiguration\":{\"kbId\":0,\"kbKey\":{\"logsys\":\"string\",\"name\":\"string\",\"version\":\"string\"},\"consistent\":false,\"complete\":false,\"rootItem\":{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"string\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[{}]}},\"unitCodes\":{\"PCE\":\"PCE\"}}";
	private static final String EXTERNAL_CONFIG_JSON_WITH_ADDITIONAL_ATTRIBUTE = "{\"externalConfiguration\":{\"kbId\":0,\"kbKey\":{\"logsys\":\"string\",\"name\":\"string\",\"version\":\"string\"},\"consistent\":false,\"complete\":false,\"rootItem\":{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"string\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[{}]},\"nonExistingProperty\":\"value\"},\"unitCodes\":{\"PCE\":\"PCE\"}}";
	private static final String COND_KEY1 = "key1";
	private static final String COND_FACTOR1 = "factor1";
	private static final String COND_KEY2 = "key2";
	private static final String COND_FACTOR2 = "factor2";

	private CPSConfigurationOrderEntryMapper classUnderTest;

	private CPSCommerceExternalConfiguration commerceExternalConfiguration;
	private Map<String, String> unitCodes;
	private CPSExternalConfiguration externalConfiguration;
	private CPSExternalItem rootItem;
	private CPSExternalCharacteristic characteristic;
	private CPSExternalValue value;
	private CPSVariantCondition condition1;
	private CPSVariantCondition condition2;

	private CPSExternalItem subItem;
	private CPSExternalCharacteristic characteristicForSubItem;
	private CPSExternalValue valueForSubItem;

	@Mock
	private OrderModel orderModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private ObjectMapper objectMapper;
	private SAPCpiOutboundOrderModel outboundOrder;
	private SAPCpiOutboundOrderItemModel outboundOrderItem;


	@Before
	public void setup()
	{
		classUnderTest = new CPSConfigurationOrderEntryMapper();
		MockitoAnnotations.initMocks(this);
		classUnderTest.setObjectMapper(objectMapper);
		when(orderModel.getEntries()).thenReturn(ImmutableList.of(orderEntryModel));
		when(orderEntryModel.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIG);
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER));
		initializeSingleLevelConfiguration();
		try
		{
			when(objectMapper.readValue(EXTERNAL_CONFIG, CPSCommerceExternalConfiguration.class))
					.thenReturn(commerceExternalConfiguration);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		outboundOrder = new SAPCpiOutboundOrderModel();
		outboundOrderItem = new SAPCpiOutboundOrderItemModel();
		outboundOrder.setSapCpiOutboundOrderItems(ImmutableSet.of(outboundOrderItem));
		outboundOrder.setProductConfigHeaders(new HashSet<SAPCpiOutboundOrderItemConfigHeaderModel>());
		outboundOrder.setProductConfigInstances(new HashSet<SAPCpiOutboundOrderItemConfigInstanceModel>());
		outboundOrder.setProductConfigHierarchies(new HashSet<SAPCpiOutboundOrderItemConfigHierarchyModel>());
		outboundOrder.setProductConfigValues(new HashSet<SAPCpiOutboundOrderItemConfigValueModel>());
		outboundOrder.setProductConfigConditions(new HashSet<SAPCpiOutboundOrderItemConfigConditionModel>());
	}

	private void initializeSingleLevelConfiguration()
	{
		commerceExternalConfiguration = new CPSCommerceExternalConfiguration();
		unitCodes = new HashMap<>();
		unitCodes.put(UNIT_OF_MEASURE, UNIT_OF_MEASURE2);
		commerceExternalConfiguration.setUnitCodes(unitCodes);
		// attributes on highest level
		externalConfiguration = new CPSExternalConfiguration();
		commerceExternalConfiguration.setExternalConfiguration(externalConfiguration);
		externalConfiguration.setComplete(true);
		externalConfiguration.setConsistent(true);
		externalConfiguration.setKbId("kbId");

		//knowledgebase
		externalConfiguration.setKbKey(new CPSMasterDataKnowledgebaseKey());
		externalConfiguration.getKbKey().setLogsys("logsys");
		externalConfiguration.getKbKey().setName(KB_NAME);
		externalConfiguration.getKbKey().setVersion(KB_VERSION);

		// root item
		rootItem = new CPSExternalItem();
		rootItem.setId(ROOT_ITEM_ID);
		rootItem.setBomPositionAuthor(BOM_POSITION_AUTHOR);
		rootItem.setComplete(true);
		rootItem.setConsistent(true);
		rootItem.setFixedQuantity(false);
		rootItem.setObjectKeyAuthor(OBJECT_KEY_AUTHOR);
		rootItem.setQuantity(new CPSQuantity());
		rootItem.getQuantity().setValue(Double.valueOf(1));
		rootItem.getQuantity().setUnit(UNIT_OF_MEASURE);
		rootItem.setSalesRelevant(true);
		externalConfiguration.setRootItem(rootItem);
		rootItem.setCharacteristics(new ArrayList<>());
		rootItem.setSubItems(new ArrayList<>());
		rootItem.setBomPositionObjectKey(new CPSExternalObjectKey());
		rootItem.getBomPositionObjectKey().setClassType(BOM_POSITION_CLASS_TYPE);
		rootItem.getBomPositionObjectKey().setId("bomPositionId");
		rootItem.getBomPositionObjectKey().setType("bomType");
		rootItem.setObjectKey(new CPSExternalObjectKey());
		rootItem.getObjectKey().setId(OBJECT_ID);
		rootItem.getObjectKey().setClassType(OBJECT_CLASS_TYPE);
		rootItem.getObjectKey().setType(OBJECT_TYPE);
		// characteristic
		characteristic = new CPSExternalCharacteristic();
		characteristic.setId(CHARACTERISTIC_ID);
		characteristic.setRequired(true);
		characteristic.setVisible(true);
		rootItem.getCharacteristics().add(characteristic);
		characteristic.setValues(new ArrayList<>());
		// value
		value = new CPSExternalValue();
		value.setAuthor(VALUE_AUTHOR);
		value.setValue(VALUE);
		characteristic.getValues().add(value);
		// conditions
		rootItem.setVariantConditions(new ArrayList<>());
		condition1 = new CPSVariantCondition();
		condition1.setKey(COND_KEY1);
		condition1.setFactor(COND_FACTOR1);
		rootItem.getVariantConditions().add(condition1);
		condition2 = new CPSVariantCondition();
		condition2.setKey(COND_KEY2);
		condition2.setFactor(COND_FACTOR2);
		rootItem.getVariantConditions().add(condition2);
	}

	private void initializeMultiLevelConfiguration()
	{
		initializeSingleLevelConfiguration();
		subItem = new CPSExternalItem();
		subItem.setId(SUB_ITEM_ID);
		subItem.setParentItem(rootItem);
		subItem.setBomPositionAuthor(SUB_BOM_POSITION_AUTHOR);
		subItem.setComplete(true);
		subItem.setConsistent(true);
		subItem.setFixedQuantity(false);
		subItem.setObjectKeyAuthor("subObjectKeyAuthor");
		subItem.setQuantity(new CPSQuantity());
		subItem.getQuantity().setValue(Double.valueOf(2));
		subItem.getQuantity().setUnit("subUnitOfMeasure");
		subItem.setSalesRelevant(true);
		subItem.setCharacteristics(new ArrayList<>());
		subItem.setBomPosition(SUB_ITEM_BOM_POSITION);
		subItem.setSubItems(new ArrayList<>());
		subItem.setBomPositionObjectKey(new CPSExternalObjectKey());
		subItem.getBomPositionObjectKey().setClassType(SUB_BOM_POSITION_CLASS_TYPE);
		subItem.getBomPositionObjectKey().setId("subBomPositionId");
		subItem.getBomPositionObjectKey().setType("subBomType");
		subItem.setObjectKey(new CPSExternalObjectKey());
		subItem.getObjectKey().setId(SUB_OBJECT_ID);
		subItem.getObjectKey().setClassType("subObjectClassType");
		subItem.getObjectKey().setType(SUB_OBJECT_TYPE);
		rootItem.getSubItems().add(subItem);

		characteristicForSubItem = new CPSExternalCharacteristic();
		characteristicForSubItem.setId("subCharacteristicId");
		characteristicForSubItem.setRequired(true);
		characteristicForSubItem.setVisible(true);
		subItem.getCharacteristics().add(characteristicForSubItem);
		characteristicForSubItem.setValues(new ArrayList<>());

		valueForSubItem = new CPSExternalValue();
		valueForSubItem.setAuthor("subValueAuthor");
		valueForSubItem.setValue("subValue");
		characteristicForSubItem.getValues().add(valueForSubItem);
		try
		{
			when(objectMapper.readValue(EXTERNAL_CONFIG, CPSCommerceExternalConfiguration.class))
					.thenReturn(commerceExternalConfiguration);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testMapMultiLevel()
	{
		initializeMultiLevelConfiguration();
		classUnderTest.mapConfiguration(orderEntryModel, outboundOrder, ENTRY_NUMBER);
		final SAPCpiOutboundOrderItemConfigHeaderModel header = outboundOrder.getProductConfigHeaders().iterator().next();
		assertNotNull(header);
		assertEquals(KB_NAME, header.getKbName());
		assertEquals(ENTRY_NUMBER, header.getConfigurationId());
		final SAPCpiOutboundOrderItemConfigHierarchyModel hierarchyModel = outboundOrder.getProductConfigHierarchies().iterator()
				.next();
		assertEquals(SUB_ITEM_ID, hierarchyModel.getInstanceId());
	}

	@Test
	public void testReadExternalConfigFromEntryConfigNull()
	{
		when(orderEntryModel.getExternalConfiguration()).thenReturn(null);
		assertNull(classUnderTest.readExternalConfigFromEntry(orderEntryModel));
	}

	@Test
	public void testReadExternalConfigFromEntry()
	{
		classUnderTest.setObjectMapper(null);
		when(orderEntryModel.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIG_JSON);
		final CPSCommerceExternalConfiguration result = classUnderTest.readExternalConfigFromEntry(orderEntryModel);
		assertNotNull(result);
		assertEquals(String.valueOf(0), result.getExternalConfiguration().getKbId());
		assertEquals(1, result.getExternalConfiguration().getRootItem().getSubItems().size());
	}


	@Test(expected = IllegalStateException.class)
	public void testReadExternalConfigFromEntryInvalidExternalConfig()
	{
		classUnderTest.setObjectMapper(null);
		when(orderEntryModel.getExternalConfiguration()).thenReturn("Invalid External Config");
		final CPSCommerceExternalConfiguration result = classUnderTest.readExternalConfigFromEntry(orderEntryModel);
	}

	@Test
	public void testGetObjectMapper()
	{
		classUnderTest.setObjectMapper(null);
		assertNotNull(classUnderTest.getObjectMapper());
	}

	@Test
	public void testMapConfiguration()
	{
		final int numberOfInstances = classUnderTest.mapConfiguration(orderEntryModel, outboundOrder, ENTRY_NUMBER);
		final SAPCpiOutboundOrderItemConfigHeaderModel result = outboundOrder.getProductConfigHeaders().iterator().next();
		assertNotNull(result);
		assertEquals(KB_NAME, result.getKbName());
		assertEquals(ENTRY_NUMBER, result.getConfigurationId());
	}

	@Test
	public void testMapConfigurationHeader()
	{

		classUnderTest.mapConfigurationHeader(commerceExternalConfiguration.getExternalConfiguration(), outboundOrder,
				orderEntryModel.getEntryNumber().toString());
		final SAPCpiOutboundOrderItemConfigHeaderModel result = outboundOrder.getProductConfigHeaders().iterator().next();
		assertEquals(KB_NAME, result.getKbName());
		assertEquals(KB_VERSION, result.getKbVersion());
		assertEquals(true, result.getConsistent());
		assertEquals(true, result.getComplete());
		assertEquals(ROOT_ITEM_ID, result.getRootInstanceId());
		assertTrue(result.getCommerceLeading());
		assertEquals(ENTRY_NUMBER, result.getExternalItemId());
		assertEquals(ENTRY_NUMBER, result.getConfigurationId());
	}

	@Test
	public void testCreateFlatListContainerSingleLevel()
	{
		initializeSingleLevelConfiguration();
		final CPSFlatListContainer result = classUnderTest.createFlatListContainer(externalConfiguration);
		assertNotNull(result);
		assertNotNull(result.getItems());
		assertNotNull(result.getSubItems());
		assertNotNull(result.getValues());
		assertEquals(1, result.getItems().size());
		assertTrue(result.getSubItems().isEmpty());
		assertEquals(1, result.getValues().size());
	}

	@Test
	public void testIntializeFlatListContainer()
	{
		final CPSFlatListContainer result = classUnderTest.initializeFlatListContainer();
		assertNotNull(result);
		assertNotNull(result.getItems());
		assertNotNull(result.getSubItems());
		assertNotNull(result.getValues());
		assertNotNull(result.getConditions());
		assertTrue(result.getItems().isEmpty());
		assertTrue(result.getSubItems().isEmpty());
		assertTrue(result.getValues().isEmpty());
		assertTrue(result.getConditions().isEmpty());
	}

	@Test
	public void testFillListContainerForInstance()
	{
		initializeMultiLevelConfiguration();
		final CPSFlatListContainer result = classUnderTest.initializeFlatListContainer();

		classUnderTest.fillListContainerForInstance(rootItem, result);
		assertEquals(rootItem, result.getItems().get(0));
		assertEquals(subItem, result.getItems().get(1));
		assertNull(result.getItems().get(0).getParentItem());
		assertEquals(rootItem, result.getItems().get(1).getParentItem());
		assertEquals(rootItem, characteristic.getParentItem());
		assertEquals(subItem, characteristicForSubItem.getParentItem());
		assertEquals(condition2, result.getConditions().get(1));
		assertEquals(rootItem.getId(), result.getConditions().get(1).getParentItemId());
	}

	@Test
	public void testFillListContainerForCharacteristic()
	{
		initializeMultiLevelConfiguration();
		characteristic.setParentItem(rootItem);
		final CPSFlatListContainer result = classUnderTest.initializeFlatListContainer();

		classUnderTest.fillListContainerForCharacteristic(characteristic, result);
		assertEquals(1, result.getValues().size());
		assertEquals(characteristic, result.getValues().get(0).getParentCharacteristic());
	}

	@Test
	public void testFillListContainerForConditions()
	{
		final CPSFlatListContainer result = classUnderTest.initializeFlatListContainer();

		classUnderTest.fillListContainerForConditions(rootItem, result);
		assertEquals(2, result.getConditions().size());
		assertEquals(condition1, result.getConditions().get(0));
		assertEquals(rootItem.getId(), condition1.getParentItemId());
		assertEquals(condition2, result.getConditions().get(1));
		assertEquals(rootItem.getId(), condition2.getParentItemId());
	}

	@Test
	public void testMapConfigValues()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigValues(outboundOrder, flatListContainer.getValues(), entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigValueModel> result = outboundOrder.getProductConfigValues();
		assertNotNull(result);
		assertEquals(1, result.size());
		final SAPCpiOutboundOrderItemConfigValueModel valueModel = result.iterator().next();
		assertEquals(CHARACTERISTIC_ID, valueModel.getCharacteristicId());
		assertEquals(VALUE, valueModel.getValueId());
		assertEquals(VALUE_AUTHOR, valueModel.getAuthor());
		assertEquals(outboundOrder, valueModel.getSapCpiOutboundOrder());
		assertEquals(ROOT_ITEM_ID, valueModel.getInstanceId());
		assertEquals(entryNumber, valueModel.getConfigurationId());
	}

	@Test
	public void testMapConfigHierarchiesSingleLevel()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final SAPCpiOutboundOrderItemConfigHeaderModel configHeader = new SAPCpiOutboundOrderItemConfigHeaderModel();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigHierarchies(outboundOrder, flatListContainer.getSubItems(), entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigHierarchyModel> result = outboundOrder.getProductConfigHierarchies();
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testMapConfigHierarchiesMultiLevel()
	{
		initializeMultiLevelConfiguration();
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigHierarchies(outboundOrder, flatListContainer.getSubItems(), entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigHierarchyModel> result = outboundOrder.getProductConfigHierarchies();
		assertNotNull(result);
		assertEquals(1, result.size());
		final SAPCpiOutboundOrderItemConfigHierarchyModel hierarchyModel = result.iterator().next();
		assertEquals(SUB_BOM_POSITION_AUTHOR, hierarchyModel.getAuthor());
		assertEquals(SUB_BOM_POSITION_CLASS_TYPE, hierarchyModel.getClassType());
		assertEquals(SUB_ITEM_ID, hierarchyModel.getInstanceId());
		assertEquals(SUB_OBJECT_ID, hierarchyModel.getObjectKey());
		assertEquals(SUB_OBJECT_TYPE, hierarchyModel.getObjectType());
		assertEquals(ROOT_ITEM_ID, hierarchyModel.getParentId());
		assertEquals(SUB_ITEM_BOM_POSITION, hierarchyModel.getBomNumber());
		assertEquals(true, hierarchyModel.getSalesRelevant());
		assertEquals(entryNumber, hierarchyModel.getConfigurationId());
	}

	@Test
	public void testMapConfigInstances()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigInstances(outboundOrder, flatListContainer.getItems(), commerceExternalConfiguration.getUnitCodes(),
				entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigInstanceModel> result = outboundOrder.getProductConfigInstances();
		assertNotNull(result);
		assertEquals(1, result.size());
		final SAPCpiOutboundOrderItemConfigInstanceModel instanceModel = result.iterator().next();
		assertEquals(BOM_POSITION_AUTHOR, instanceModel.getAuthor());
		assertEquals(OBJECT_CLASS_TYPE, instanceModel.getClassType());
		assertEquals(ROOT_ITEM_ID, instanceModel.getInstanceId());
		assertEquals(OBJECT_ID, instanceModel.getObjectKey());
		assertEquals(OBJECT_TYPE, instanceModel.getObjectType());
		assertEquals("1.0", instanceModel.getQuantity());
		assertEquals(UNIT_OF_MEASURE2, instanceModel.getQuantityUnit());
		assertEquals(true, instanceModel.getComplete());
		assertEquals(true, instanceModel.getConsistent());
		assertEquals(entryNumber, instanceModel.getConfigurationId());
	}

	@Test
	public void testMapConfigConditions()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigConditions(outboundOrder, flatListContainer.getConditions(), entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigConditionModel> result = outboundOrder.getProductConfigConditions();
		assertNotNull(result);
		assertEquals(2, result.size());
		final Iterator<SAPCpiOutboundOrderItemConfigConditionModel> resultIterator = result.iterator();
		while (resultIterator.hasNext())
		{
			final SAPCpiOutboundOrderItemConfigConditionModel conditionModel = resultIterator.next();
			if (conditionModel.getConditionKey().equals(COND_KEY1))
			{
				assertEquals(COND_FACTOR1, conditionModel.getConditionFactor());
				assertEquals(ROOT_ITEM_ID, conditionModel.getInstanceId());
				assertEquals(entryNumber, conditionModel.getConfigurationId());
			}
			else
			{
				assertEquals(COND_FACTOR2, conditionModel.getConditionFactor());
				assertEquals(ROOT_ITEM_ID, conditionModel.getInstanceId());
				assertEquals(entryNumber, conditionModel.getConfigurationId());
			}
		}
	}

	@Test
	public void testMapConfigConditions_MultipleExecution()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		final Integer entryNumber2Int = orderEntryModel.getEntryNumber() + 1;
		final String entryNumber2 = entryNumber2Int.toString();
		final CPSFlatListContainer flatListContainer = classUnderTest
				.createFlatListContainer(commerceExternalConfiguration.getExternalConfiguration());
		classUnderTest.mapConfigConditions(outboundOrder, flatListContainer.getConditions(), entryNumber);
		classUnderTest.mapConfigConditions(outboundOrder, flatListContainer.getConditions(), entryNumber2);

		final Set<SAPCpiOutboundOrderItemConfigConditionModel> result = outboundOrder.getProductConfigConditions();
		assertNotNull(result);
		assertEquals(4, result.size());
	}

}
