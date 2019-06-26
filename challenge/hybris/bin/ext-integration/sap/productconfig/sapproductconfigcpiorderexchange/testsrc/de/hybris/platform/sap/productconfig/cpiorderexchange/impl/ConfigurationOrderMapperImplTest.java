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
package de.hybris.platform.sap.productconfig.cpiorderexchange.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import static org.mockito.Mockito.when;


@UnitTest
public class ConfigurationOrderMapperImplTest
{
	private static final String ENTRY_NUMBER = "1";

	private ConfigurationOrderMapperImpl classUnderTest;

	@Mock
	private OrderModel orderModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private AbstractOrderEntryModel orderEntryModel2;
	@Mock
	private ConfigurationOrderEntryMapper orderEntryMapper;

	private SAPCpiOutboundOrderModel outboundOrder;
	private SAPCpiOutboundOrderItemModel outboundOrderItem;
	private SAPCpiOutboundOrderItemModel outboundOrderItem2;

	@Before
	public void setup()
	{
		classUnderTest = new ConfigurationOrderMapperImpl();
		MockitoAnnotations.initMocks(this);
		when(orderModel.getEntries()).thenReturn(ImmutableList.of(orderEntryModel));
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(ENTRY_NUMBER));

		outboundOrder = new SAPCpiOutboundOrderModel();
		outboundOrderItem = new SAPCpiOutboundOrderItemModel();
		outboundOrderItem.setEntryNumber(ENTRY_NUMBER);
		outboundOrder.setSapCpiOutboundOrderItems(ImmutableSet.of(outboundOrderItem));
		classUnderTest.setOrderEntryMapper(orderEntryMapper);
		when(orderEntryMapper.isMapperApplicable(Mockito.any(), Mockito.any())).thenReturn(true);
		when(orderEntryMapper.mapConfiguration(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
	}

	private void prepareTwoOrderEntries()
	{
		when(orderEntryModel2.getEntryNumber()).thenReturn(Integer.valueOf("2"));
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList();
		orderEntries.add(orderEntryModel);
		orderEntries.add(orderEntryModel2);
		when(orderModel.getEntries()).thenReturn(orderEntries);

		outboundOrderItem.setEntryNumber(ENTRY_NUMBER);
		outboundOrderItem2 = new SAPCpiOutboundOrderItemModel();
		outboundOrderItem2.setEntryNumber("2");
		final Set<SAPCpiOutboundOrderItemModel> outboundItemSet = new HashSet();
		outboundItemSet.add(outboundOrderItem);
		outboundItemSet.add(outboundOrderItem2);
		outboundOrder.setSapCpiOutboundOrderItems(outboundItemSet);
	}

	@Test
	public void testMap()
	{
		outboundOrderItem.setEntryNumber(ENTRY_NUMBER);
		classUnderTest.map(orderModel, outboundOrder);
		assertEquals(ENTRY_NUMBER, outboundOrder.getSapCpiOutboundOrderItems().iterator().next().getEntryNumber());
	}

	@Test
	public void testMapEntryNumberIncrementMultilevel()
	{
		when(orderEntryMapper.mapConfiguration(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(2);
		prepareTwoOrderEntries();
		classUnderTest.map(orderModel, outboundOrder);

		assertEquals(ENTRY_NUMBER, outboundOrderItem.getEntryNumber());
		assertEquals(String.valueOf(3), outboundOrderItem2.getEntryNumber());
	}

	@Test
	public void testMapEntryNumberIncrementSinglelevel()
	{
		when(orderEntryMapper.mapConfiguration(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		prepareTwoOrderEntries();
		classUnderTest.map(orderModel, outboundOrder);

		assertEquals(ENTRY_NUMBER, outboundOrderItem.getEntryNumber());
		assertEquals(String.valueOf(2), outboundOrderItem2.getEntryNumber());
	}

	@Test
	public void testMapEntryNumberIncrementNotConfigurable()
	{
		when(orderEntryMapper.isMapperApplicable(Mockito.any(), Mockito.any())).thenReturn(false);

		prepareTwoOrderEntries();
		when(orderEntryModel.getExternalConfiguration()).thenReturn(null);
		when(orderEntryModel2.getExternalConfiguration()).thenReturn(null);
		classUnderTest.map(orderModel, outboundOrder);

		assertEquals(ENTRY_NUMBER, outboundOrderItem.getEntryNumber());
		assertEquals("2", outboundOrderItem2.getEntryNumber());
		assertEquals(String.valueOf(2), outboundOrderItem2.getEntryNumber());
	}

	@Test
	public void testInitProductConfigSets()
	{
		final SAPCpiOutboundOrderModel outboundOrder2 = new SAPCpiOutboundOrderModel();
		assertNull(outboundOrder2.getProductConfigHeaders());
		classUnderTest.initProductConfigSets(outboundOrder2);
		assertNotNull(outboundOrder2.getProductConfigHeaders());
		assertEquals(0, outboundOrder2.getProductConfigHeaders().size());
		final SAPCpiOutboundOrderItemConfigHeaderModel header = new SAPCpiOutboundOrderItemConfigHeaderModel();
		outboundOrder2.getProductConfigHeaders().add(header);
		assertEquals(1, outboundOrder2.getProductConfigHeaders().size());
		classUnderTest.initProductConfigSets(outboundOrder2);
		assertEquals(1, outboundOrder2.getProductConfigHeaders().size());
	}

	@Test
	public void testIsConfigurationMappingNeeded()
	{
		assertTrue(classUnderTest.isConfigurationMappingNeeded(orderModel.getEntries(), outboundOrder));
	}

	@Test
	public void testIsConfigurationMappingNeededFalse()
	{
		when(orderEntryMapper.isMapperApplicable(Mockito.any(), Mockito.any())).thenReturn(false);
		assertFalse(classUnderTest.isConfigurationMappingNeeded(orderModel.getEntries(), outboundOrder));
	}

	@Test
	public void testFindOutboundItem()
	{
		final SAPCpiOutboundOrderItemModel outboundItem = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel);
		assertNotNull(outboundItem);
		assertEquals(ENTRY_NUMBER, outboundItem.getEntryNumber());
	}

	@Test
	public void testFindOutboundItemEntryNumberNotExisting()
	{
		when(orderEntryModel.getEntryNumber()).thenReturn(Integer.valueOf(2));
		final SAPCpiOutboundOrderItemModel outboundItem = classUnderTest.findOutboundItem(outboundOrder, orderEntryModel);
		assertNull(outboundItem);
	}

}
