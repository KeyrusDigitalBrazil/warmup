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
package de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ssc.service.ExternalConfigurationParser;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigConditionModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHierarchyModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigInstanceModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigValueModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.sap.sce.kbrt.cfg_ext_cstic_val_seq;
import com.sap.sce.kbrt.cfg_ext_inst_seq;
import com.sap.sce.kbrt.cfg_ext_part_seq;
import com.sap.sce.kbrt.cfg_ext_price_key_seq;
import com.sap.sce.kbrt.ext_configuration;
import com.sap.sce.kbrt.imp.c_ext_cfg_cstic_val_imp;
import com.sap.sce.kbrt.imp.c_ext_cfg_inst_imp;
import com.sap.sce.kbrt.imp.c_ext_cfg_part_imp;
import com.sap.sce.kbrt.imp.c_ext_cfg_price_key_imp;
import com.sap.sxe.sys.seq.array_enumeration;

import static org.mockito.Mockito.when;


@UnitTest
public class SSCConfigurationOrderEntryMapperTest
{

	private static final String VALUE_TXT = "value_txt";
	private static final String CHARC_TXT = "charc_txt";
	private static final String KB_PROFILE_NAME = "kb_profile_name";
	private static final String OBJECT_TYPE = "objectType";
	private static final String BOM_POSITION_AUTHOR = "bomPositionAuthor";
	private static final String OBJECT_CLASS_TYPE = "objectClassType";
	private static final String OBJECT_ID = "objectId";
	private static final String UNIT_OF_MEASURE = "PCE";
	private static final String UNIT_OF_MEASURE2 = "ST";
	private static final String OBJECT_KEY_AUTHOR = "objectKeyAuthor";
	private static final String SUB_OBJECT_TYPE = "subObjectType";
	private static final String SUB_OBJECT_ID = "subObjectId";
	private static final String SUB_ITEM_ID = "2";
	private static final String BOM_POSITION_CLASS_TYPE = "bomPositionClassType";
	private static final String SUB_ITEM_BOM_POSITION = "subBomPosition";
	private static final String SUB_BOM_POSITION_CLASS_TYPE = "subBomPositionClassType";
	private static final String SUB_BOM_POSITION_AUTHOR = "subBomPositionAuthor";
	private static final String VALUE = "value";
	private static final String VALUE_AUTHOR = "valueAuthor";
	private static final String CHARACTERISTIC_ID = "characteristicId";
	private static final String ROOT_ITEM_ID = "1";
	private static final String KB_VERSION = "kbVersion";
	private static final String KB_NAME = "kbName";
	private static final String EXTERNAL_CONFIG = "externalConfig";
	private static final String ENTRY_NUMBER = "1";
	private static final String EXTERNAL_CONFIG_XML1 = "<CONFIGURATION CFGINFO=\"VCOND=WEC_SURCHARGE\" SCEVERSION=\" \">"
			+ "<INST AUTHOR=\"5\" OBJ_KEY=\"WCEM_MULTILEVEL\">" + "<CSTICS>"
			+ "	<CSTIC AUTHOR=\" \" CHARC=\"EXP_NO_USERS\" CHARC_TXT=\"Expected Number of Users\" VALUE=\"250.0\"/>"
			+ "</CSTICS></INST></CONFIGURATION>";
	private static final String EXTERNAL_CONFIG_SOLUTION = "<SOLUTION>" + EXTERNAL_CONFIG_XML1 + "<SALES_STRUCTURE>"
			+ "</SALES_STRUCTURE>" + "</SOLUTION>";
	private static final String EXTERNAL_CONFIG_XML2 = "<CONFIGURATION CFGINFO=\"VCOND=WEC_SURCHARGE\" CLIENT=\"000\""
			+ " COMPLETE=\"F\" CONSISTENT=\"T\" KBBUILD=\"2\" KBNAME=\"WCEM_MULTILEVEL_KB\""
			+ " KBPROFILE=\"WCEM_MULTILEVEL_PROFILE\" KBVERSION=\"3800\" LANGUAGE=\"E\" LANGUAGE_ISO=\"EN\""
			+ " NAME=\"SCE 5.0\" ROOT_NR=\"1\" SCEVERSION=\" \"><INST AUTHOR=\"5\" CLASS_TYPE=\"300\""
			+ " COMPLETE=\"F\" CONSISTENT=\"T\" INSTANCE_GUID=\"\" INSTANCE_ID=\"01\" NR=\"1\""
			+ " OBJ_KEY=\"WCEM_MULTILEVEL\" OBJ_TXT=\"SAP Complex Multi level Test\" OBJ_TYPE=\"MARA\" QTY=\"1.0\" UNIT=\"ST\">"
			+ "<CSTICS>" + "	<CSTIC AUTHOR=\"5\" CHARC=\"EXP_NO_USERS\" CHARC_TXT=\"Expected Number of Users\" VALUE=\"250.0\"/>"
			+ "</CSTICS></INST></CONFIGURATION>";
	private static final String EXTERNAL_CONFIG_SOLUTION2 = "<SOLUTION>" + EXTERNAL_CONFIG_XML2 + "<SALES_STRUCTURE>"
			+ "</SALES_STRUCTURE>" + "</SOLUTION>";
	private static final String COND_KEY1 = "key1";
	private static final String COND_FACTOR1 = "1.0";
	private static final String COND_KEY2 = "key2";
	private static final String COND_FACTOR2 = "2.0";

	private SSCConfigurationOrderEntryMapper classUnderTest;

	@Mock
	private OrderModel orderModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private ExternalConfigurationParser externalConfigurationParser;
	@Mock
	private ext_configuration externalConfig;
	@Mock
	private cfg_ext_inst_seq extInstances;
	@Mock
	private c_ext_cfg_inst_imp extInstance;
	@Mock
	private cfg_ext_part_seq extParts;
	@Mock
	private c_ext_cfg_part_imp extPart;
	@Mock
	private cfg_ext_cstic_val_seq extCsticValues;
	@Mock
	private c_ext_cfg_cstic_val_imp extCsticValue;
	@Mock
	private cfg_ext_price_key_seq extPriceKeys;
	@Mock
	private c_ext_cfg_price_key_imp extPriceKey;
	@Mock
	private c_ext_cfg_price_key_imp extPriceKey2;
	private SAPCpiOutboundOrderModel outboundOrder;
	private SAPCpiOutboundOrderItemModel outboundOrderItem;


	@Before
	public void setup()
	{
		classUnderTest = new SSCConfigurationOrderEntryMapper();
		MockitoAnnotations.initMocks(this);
		classUnderTest.setExternalConfigurationParser(externalConfigurationParser);
		prepareExternalConfigMock();
		when(externalConfigurationParser.readExternalConfigFromString(Mockito.anyString())).thenReturn(externalConfig);
		when(orderModel.getEntries()).thenReturn(ImmutableList.of(orderEntryModel));
		when(orderEntryModel.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIG);
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER));
		outboundOrder = new SAPCpiOutboundOrderModel();
		outboundOrderItem = new SAPCpiOutboundOrderItemModel();
		outboundOrderItem.setEntryNumber(ENTRY_NUMBER);
		classUnderTest.initProductConfigSets(outboundOrder);
		outboundOrder.setSapCpiOutboundOrderItems(ImmutableSet.of(outboundOrderItem));
	}

	protected void prepareExternalConfigMock()
	{
		when(externalConfig.get_kb_name()).thenReturn(KB_NAME);
		when(externalConfig.get_kb_version()).thenReturn(KB_VERSION);
		when(externalConfig.get_kb_profile_name()).thenReturn(KB_PROFILE_NAME);
		when(externalConfig.is_complete_p()).thenReturn(true);
		when(externalConfig.is_consistent_p()).thenReturn(true);
		when(externalConfig.get_root_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
		when(externalConfig.get_insts()).thenReturn(extInstances);
		when(extInstances.elements()).thenReturn(new array_enumeration(new c_ext_cfg_inst_imp[]
		{ extInstance }));
		when(extInstances.length()).thenReturn(1);
		when(extInstance.get_author()).thenReturn(BOM_POSITION_AUTHOR);
		when(extInstance.get_class_type()).thenReturn(OBJECT_CLASS_TYPE);
		when(extInstance.get_inst_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
		when(extInstance.get_obj_key()).thenReturn(OBJECT_ID);
		when(extInstance.get_obj_type()).thenReturn(OBJECT_TYPE);
		when(extInstance.get_quantity()).thenReturn("1.0");
		when(extInstance.get_quantity_unit()).thenReturn(UNIT_OF_MEASURE2);
		when(extInstance.is_complete_p()).thenReturn(true);
		when(extInstance.is_consistent_p()).thenReturn(true);
		when(externalConfig.get_parts()).thenReturn(extParts);
		when(extParts.elements()).thenReturn(new array_enumeration(new c_ext_cfg_part_imp[]
		{ extPart }));
		when(extPart.get_author()).thenReturn(SUB_BOM_POSITION_AUTHOR);
		when(extPart.get_class_type()).thenReturn(SUB_BOM_POSITION_CLASS_TYPE);
		when(extPart.get_inst_id()).thenReturn(Integer.valueOf(SUB_ITEM_ID));
		when(extPart.get_obj_key()).thenReturn(SUB_OBJECT_ID);
		when(extPart.get_obj_type()).thenReturn(SUB_OBJECT_TYPE);
		when(extPart.get_parent_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
		when(extPart.get_pos_nr()).thenReturn(SUB_ITEM_BOM_POSITION);
		when(extPart.is_sales_relevant_p()).thenReturn(true);
		when(externalConfig.get_cstics_values()).thenReturn(extCsticValues);
		when(extCsticValues.elements()).thenReturn(new array_enumeration(new c_ext_cfg_cstic_val_imp[]
		{ extCsticValue }));
		when(extCsticValue.get_author()).thenReturn(VALUE_AUTHOR);
		when(extCsticValue.get_charc()).thenReturn(CHARACTERISTIC_ID);
		when(extCsticValue.get_charc_txt()).thenReturn(CHARC_TXT);
		when(extCsticValue.get_inst_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
		when(extCsticValue.get_value()).thenReturn(VALUE);
		when(extCsticValue.get_value_txt()).thenReturn(VALUE_TXT);

		when(externalConfig.get_price_keys()).thenReturn(extPriceKeys);
		when(extPriceKeys.elements()).thenReturn(new array_enumeration(new c_ext_cfg_price_key_imp[]
		{ extPriceKey, extPriceKey2 }));
		when(extPriceKey.get_factor()).thenReturn(Double.valueOf(COND_FACTOR1));
		when(extPriceKey.get_key()).thenReturn(COND_KEY1);
		when(extPriceKey.get_inst_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
		when(extPriceKey2.get_factor()).thenReturn(Double.valueOf(COND_FACTOR1));
		when(extPriceKey2.get_key()).thenReturn(COND_KEY1);
		when(extPriceKey2.get_inst_id()).thenReturn(Integer.valueOf(ROOT_ITEM_ID));
	}

	@Test
	public void testExtractConfigurationFromXML() throws Exception
	{
		assertEquals(EXTERNAL_CONFIG_XML1, classUnderTest.extractConfigurationFromXml(EXTERNAL_CONFIG_SOLUTION));
	}

	@Test
	public void testMap()
	{
		final int result = classUnderTest.mapConfiguration(orderEntryModel, outboundOrder, ENTRY_NUMBER);
		assertEquals(1, result);
		final SAPCpiOutboundOrderItemModel outboundItem = outboundOrder.getSapCpiOutboundOrderItems().iterator().next();
		final SAPCpiOutboundOrderItemConfigHeaderModel headerModel = outboundOrder.getProductConfigHeaders().iterator().next();
		assertNotNull(headerModel);
		assertEquals(KB_NAME, headerModel.getKbName());
		final SAPCpiOutboundOrderItemConfigValueModel valueModel = outboundOrder.getProductConfigValues().iterator().next();
		assertEquals(VALUE, valueModel.getValueId());
		final SAPCpiOutboundOrderItemConfigInstanceModel instanceModel = outboundOrder.getProductConfigInstances().iterator()
				.next();
		assertEquals(ROOT_ITEM_ID, instanceModel.getInstanceId());
		assertEquals(2, outboundOrder.getProductConfigConditions().size());
	}

	@Test
	public void testReadExternalConfigFromEntry()
	{
		when(orderEntryModel.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIG_SOLUTION2);
		final ext_configuration result = classUnderTest.readExternalConfigFromEntry(orderEntryModel);
		assertNotNull(result);

		assertEquals(KB_NAME, result.get_kb_name());
	}

	@Test
	public void testMapConfiguration()
	{
		classUnderTest.mapConfiguration(orderEntryModel, outboundOrder, ENTRY_NUMBER);
		final SAPCpiOutboundOrderItemConfigHeaderModel result = outboundOrder.getProductConfigHeaders().iterator().next();
		assertNotNull(result);
		assertEquals(KB_NAME, result.getKbName());
		assertEquals(ENTRY_NUMBER, result.getConfigurationId());
	}

	@Test
	public void testMapConfigurationHeader()
	{
		classUnderTest.mapConfigurationHeader(externalConfig, outboundOrder, orderEntryModel.getEntryNumber().toString());
		final SAPCpiOutboundOrderItemConfigHeaderModel result = outboundOrder.getProductConfigHeaders().iterator().next();
		assertEquals(KB_NAME, result.getKbName());
		assertEquals(KB_VERSION, result.getKbVersion());
		assertEquals(KB_PROFILE_NAME, result.getKbProfile());
		assertEquals(true, result.getConsistent());
		assertEquals(true, result.getComplete());
		assertEquals(ROOT_ITEM_ID, result.getRootInstanceId());
		assertTrue(result.getCommerceLeading());
		assertEquals(ENTRY_NUMBER, result.getExternalItemId());
		assertEquals(ENTRY_NUMBER, result.getConfigurationId());
	}

	@Test
	public void testMapConfigInstances()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		classUnderTest.mapConfigInstances(outboundOrder, externalConfig.get_insts(), entryNumber);
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
	public void testMapConfigHierarchiesSingleLevel()
	{
		when(extParts.elements()).thenReturn(new array_enumeration(new c_ext_cfg_part_imp[] {}));
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		classUnderTest.mapConfigHierarchies(outboundOrder, externalConfig.get_parts(), entryNumber);
		final Set<SAPCpiOutboundOrderItemConfigHierarchyModel> result = outboundOrder.getProductConfigHierarchies();
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testMapConfigHierarchiesMultiLevel()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		classUnderTest.mapConfigHierarchies(outboundOrder, externalConfig.get_parts(), entryNumber);
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
	public void testMapConfigValues()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		classUnderTest.mapConfigValues(outboundOrder, externalConfig.get_cstics_values(), entryNumber);
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
	public void testMapConfigConditions()
	{
		final String entryNumber = orderEntryModel.getEntryNumber().toString();
		classUnderTest.mapConfigConditions(outboundOrder, externalConfig.get_price_keys(), entryNumber);
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
}