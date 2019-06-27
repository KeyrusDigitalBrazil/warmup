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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.MasterDataContainerResolver;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



@SuppressWarnings("javadoc")
@UnitTest
public class ValueMasterDataPopulatorTest
{
	private static final String kbId = "99";
	private static final String characteristicId = "CSTIC_ID";
	private static final String valueString = "VALUE_ID";
	private static final String valueName = "Language dependent value name";
	private CPSValue sourceNumeric;

	@Mock
	private MasterDataContainerResolver resolver;

	private ValueMasterDataPopulator classUnderTest;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private MasterDataContext ctxt;
	private CPSValue source;
	private CsticValueModel target;

	@Before
	public void initialize()
	{
		classUnderTest = new ValueMasterDataPopulator();
		MockitoAnnotations.initMocks(this);

		final CPSConfiguration cpsConfig = new CPSConfiguration();
		cpsConfig.setKbId(kbId);
		final CPSItem cpsItem = new CPSItem();
		cpsItem.setParentConfiguration(cpsConfig);
		final CPSCharacteristic cspCstic = new CPSCharacteristic();
		cspCstic.setId(characteristicId);
		cspCstic.setParentItem(cpsItem);

		final CPSConfiguration cpsConfigNum = new CPSConfiguration();
		cpsConfigNum.setKbId(kbId);
		final CPSItem cpsItemNum = new CPSItem();
		cpsItemNum.setParentConfiguration(cpsConfigNum);
		final CPSCharacteristic cspCsticNum = new CPSCharacteristic();
		cspCsticNum.setId(characteristicId);
		cspCsticNum.setParentItem(cpsItemNum);

		source = new CPSValue();
		source.setParentCharacteristic(cspCstic);
		sourceNumeric = new CPSValue();
		sourceNumeric.setParentCharacteristic(cspCsticNum);
		target = new CsticValueModelImpl();

		source.setValue(valueString);
		sourceNumeric.setValue(null);

		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(kbContainer);
		classUnderTest.setMasterDataResolver(resolver);

		Mockito.when(resolver.getValueName(kbContainer, characteristicId, valueString)).thenReturn(valueName);
		Mockito.when(Boolean.valueOf(resolver.isCharacteristicNumeric(kbContainer, characteristicId))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testGetMasterDataService()
	{
		assertSame(resolver, classUnderTest.getMasterDataResolver());
	}

	@Test
	public void testPopulateWithNotNullOrNotEmptyValue()
	{
		classUnderTest.populate(source, target, ctxt);
		assertEquals(valueName, target.getLanguageDependentName());
	}

	@Test
	public void testPopulateWithNullValue()
	{
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertTrue(StringUtils.isEmpty(target.getLanguageDependentName()));
	}

	@Test
	public void testPopulateWithEmptyValue()
	{
		sourceNumeric.setValue("");
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertTrue(StringUtils.isEmpty(target.getLanguageDependentName()));
	}

	@Test
	public void testPopulateNumeric()
	{
		sourceNumeric.setValue("1.0");
		classUnderTest.populate(sourceNumeric, target, ctxt);
		assertTrue(target.isNumeric());
	}

}
