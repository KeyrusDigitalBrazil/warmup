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
package de.hybris.platform.sap.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public abstract class AbstractOrderConfigurationPopulatorTest
{
	protected AbstractOrderConfigurationPopulator classUnderTest;

	protected List<AbstractOrderEntryModel> entryList;
	protected List<OrderEntryData> targetEntryList;
	protected OrderEntryData targetEntry;
	public final Integer entryNo = Integer.valueOf(1);
	public static final Integer ERROR_COUNT = Integer.valueOf(3);
	public static final String PK_VALUE = "123456";
	public static final int numberOfErrors = 4;
	protected AbstractOrderModel source;
	protected AbstractOrderData target;

	@Mock
	protected AbstractOrderEntryModel sourceEntry;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		entryList = new ArrayList();
		entryList.add(sourceEntry);

		targetEntryList = new ArrayList();
		targetEntry = new OrderEntryData();
		targetEntry.setEntryNumber(entryNo);
		targetEntry.setItemPK("123");
		targetEntryList.add(targetEntry);
		Mockito.when(sourceEntry.getCpqStatusSummaryMap()).thenReturn(Collections.EMPTY_MAP);
	}

	public void testWriteToTargetEntryIllegalArgument()
	{
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(Integer.valueOf(2));
		classUnderTest.writeToTargetEntry(sourceEntry, target);
	}

	public void testWriteToTargetEntry()
	{
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		Mockito.when(sourceEntry.getPk()).thenReturn(PK.parse(PK_VALUE));
		classUnderTest.writeToTargetEntry(sourceEntry, target);
		final OrderEntryData result = target.getEntries().get(0);
		assertNotNull(result);
		assertEquals(PK_VALUE, result.getItemPK());
		assertTrue(result.isConfigurationAttached());
		assertTrue(result.isConfigurationConsistent());
		assertEquals(0, result.getConfigurationErrorCount());
		assertTrue(result.getConfigurationInfos().isEmpty());
	}

	public void testWriteToTargetEntrySummaryMapNull()
	{
		Mockito.when(sourceEntry.getCpqStatusSummaryMap()).thenReturn(null);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		Mockito.when(sourceEntry.getPk()).thenReturn(PK.parse(PK_VALUE));
		classUnderTest.writeToTargetEntry(sourceEntry, target);
	}

	public void testWriteToTargetEntryInconsistent()
	{
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(entryNo);
		Mockito.when(sourceEntry.getPk()).thenReturn(PK.parse(PK_VALUE));
		final Map<ProductInfoStatus, Integer> cpqSummary = new HashMap<>();
		cpqSummary.put(ProductInfoStatus.ERROR, ERROR_COUNT);
		Mockito.when(sourceEntry.getCpqStatusSummaryMap()).thenReturn(cpqSummary);
		classUnderTest.writeToTargetEntry(sourceEntry, target);
		final OrderEntryData result = target.getEntries().get(0);
		assertNotNull(result);
		assertEquals(PK_VALUE, result.getItemPK());
		assertTrue(result.isConfigurationAttached());
		assertFalse(result.isConfigurationConsistent());
		assertEquals(ERROR_COUNT.intValue(), result.getConfigurationErrorCount());
		assertTrue(result.getConfigurationInfos().isEmpty());
	}

	public void testCreateConfigurationInfos()
	{
		final CPQOrderEntryProductInfoModel infoModel = new CPQOrderEntryProductInfoModel();
		infoModel.setCpqCharacteristicName("Label");
		final List<AbstractOrderEntryProductInfoModel> infoModelList = new ArrayList<>();
		infoModelList.add(infoModel);
		Mockito.when(sourceEntry.getProductInfos()).thenReturn(infoModelList);
		final List<ConfigurationInfoData> result = classUnderTest.createConfigurationInfos(sourceEntry);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		assertEquals(infoModel.getCpqCharacteristicName(), result.get(0).getConfigurationLabel());
	}

	public void testCreateConfigurationInfosException()
	{
		final AbstractOrderEntryProductInfoModel infoModel = new AbstractOrderEntryProductInfoModel();
		final List<AbstractOrderEntryProductInfoModel> infoModelList = new ArrayList<>();
		infoModelList.add(infoModel);
		Mockito.when(sourceEntry.getProductInfos()).thenReturn(infoModelList);
		classUnderTest.createConfigurationInfos(sourceEntry);
	}


}
