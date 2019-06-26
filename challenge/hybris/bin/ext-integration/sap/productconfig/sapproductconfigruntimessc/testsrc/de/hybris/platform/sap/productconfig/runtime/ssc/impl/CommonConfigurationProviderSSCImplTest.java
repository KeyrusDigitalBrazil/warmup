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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigContainer;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.ConfigInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.VariantCondKeyData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.kbo.local.OrchestratedCstic;
import com.sap.sce.front.base.Cstic;
import com.sap.sce.front.base.CsticType;
import com.sap.sce.front.base.PricingConditionRate;

@UnitTest
public class CommonConfigurationProviderSSCImplTest extends ConfigurationProviderSSCTestBase
{
	private CommonConfigurationProviderSSCImpl myProvider;

	private final static String valueName = "ABC";
	private static final String LOGSYS = "logsys";
	private static final String P_CODE = "pCode";
	private static final String VERSION = "version";
	private static final String INSTACE_NAME = "instanceName";

	@Mock
	protected OrchestratedCstic mockedOrchestratedCstic;
	@Mock
	private Cstic mockedFirstSharedCstic;
	@Mock
	private PricingConditionRate mockedPricingConditionRate;
	@Mock
	private CsticType mockedCsticType;
	@Mock
	private IConfigContainer mockedConfigContainer;
	@Mock
	private IConfigSession mockedSession;
	@Mock
	private ConfigurationProductUtil mockedConfigurationProductUtil;

	private final InstanceModel rootInstanceModel = new InstanceModelImpl();
	private final ConfigModel configModel = new ConfigModelImpl();

	private Date kbDate;

	@Override
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		myProvider = new CommonConfigurationProviderSSCImpl();
		myProvider.setConfigModelFactory(new ConfigModelFactoryImpl());
		myProvider.setConfigurationProductUtil(mockedConfigurationProductUtil);

		when(mockedOrchestratedCstic.getType()).thenReturn(mockedCsticType);
		when(mockedOrchestratedCstic.getFirstSharedCstic()).thenReturn(mockedFirstSharedCstic);
		when(mockedFirstSharedCstic.getDetailedPrice(valueName)).thenReturn(mockedPricingConditionRate);
		when(mockedOrchestratedCstic.getValueLangDependentName(valueName)).thenReturn("abc");
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueUserOwned(valueName))).thenReturn(Boolean.TRUE);
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ONE);
		when(mockedPricingConditionRate.getConditionRateUnitName()).thenReturn("USD");

		configModel.setKbKey(new KBKeyImpl(P_CODE, P_CODE, LOGSYS, VERSION));
		rootInstanceModel.setName(INSTACE_NAME);
		kbDate = configModel.getKbKey().getDate();
	}

	@Test
	public void testNumericTypefloat()
	{
		when(Integer.valueOf(mockedCsticType.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_FLOAT));

		final CsticValueModel valueModel = myProvider.createModelValue(mockedOrchestratedCstic, "VALUE_NAME", false, false, null);
		assertEquals(CsticValueModelImpl.class, valueModel.getClass());
		assertTrue(valueModel.isNumeric());
	}

	@Test
	public void testNonNumericTypeString()
	{
		when(Integer.valueOf(mockedCsticType.getValueType())).thenReturn(Integer.valueOf(CsticModel.TYPE_STRING));

		final CsticValueModel valueModel = myProvider.createModelValue(mockedOrchestratedCstic, "VALUE_NAME", false, false, null);
		assertEquals(CsticValueModelImpl.class, valueModel.getClass());
	}

	@Test
	public void testDeltaPriceMapping() throws Exception
	{
		final CsticValueModel modelValue = myProvider.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertTrue("wrong delta price", 0 == BigDecimal.ONE.compareTo(modelValue.getDeltaPrice().getPriceValue()));
		assertEquals("wrong delta price currency", "USD", modelValue.getDeltaPrice().getCurrency());

		assertTrue("wrong value price", 0 == BigDecimal.ONE.compareTo(modelValue.getValuePrice().getPriceValue()));
		assertEquals("wrong value price currency", "USD", modelValue.getValuePrice().getCurrency());
	}

	@Test
	public void testDeltaPriceMapping_emptyPrice() throws Exception
	{
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ZERO);
		when(mockedPricingConditionRate.getConditionRateUnitName()).thenReturn("");

		final CsticValueModel modelValue = myProvider.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertSame(PriceModel.NO_PRICE, modelValue.getDeltaPrice());
		assertSame(PriceModel.NO_PRICE, modelValue.getValuePrice());
	}

	@Test
	public void testDeltaPriceMapping_noPrice() throws Exception
	{
		Mockito.reset(mockedPricingConditionRate);

		final CsticValueModel modelValue = myProvider.createModelValue(mockedOrchestratedCstic, valueName, true, true, null);

		assertSame(PriceModel.NO_PRICE, modelValue.getDeltaPrice());
		assertSame(PriceModel.NO_PRICE, modelValue.getValuePrice());
	}

	@Test
	public void testDeltaPriceMapping_zeroPrice() throws Exception
	{
		when(mockedPricingConditionRate.getConditionRateValue()).thenReturn(BigDecimal.ZERO);

		final CsticValueModel modelValue = myProvider.createModelValue(mockedOrchestratedCstic, valueName, true, true,
				mockedPricingConditionRate);

		assertTrue("wrong delta price", 0 == BigDecimal.ZERO.compareTo(modelValue.getDeltaPrice().getPriceValue()));
		assertEquals("wrong delta price currency", "USD", modelValue.getValuePrice().getCurrency());

		assertTrue("wrong value price", 0 == BigDecimal.ZERO.compareTo(modelValue.getDeltaPrice().getPriceValue()));
		assertEquals("wrong value price currency", "USD", modelValue.getValuePrice().getCurrency());
	}

	@Test
	public void testCreateCsticValues() throws Exception
	{
		final CsticModel csticModel = new CsticModelImpl();
		csticModel.setAllowsAdditionalValues(true);
		csticModel.setConstrained(true);
		csticModel.setAuthor(CsticModel.AUTHOR_USER);

		when(mockedOrchestratedCstic.getValues()).thenReturn("A D".split(" "));
		when(mockedOrchestratedCstic.getDynamicDomain()).thenReturn("A B C".split(" "));
		when(mockedOrchestratedCstic.getTypicalDomain()).thenReturn("A B C".split(" "));
		when(mockedFirstSharedCstic.getDeltaPrices()).thenReturn(null);

		when(mockedOrchestratedCstic.getValueLangDependentName(Mockito.anyString())).thenReturn("xxx");
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueUserOwned(Mockito.anyString()))).thenReturn(Boolean.TRUE);
		when(Boolean.valueOf(mockedOrchestratedCstic.isValueDefault(Mockito.anyString()))).thenReturn(Boolean.TRUE);

		myProvider.createCsticValues(mockedOrchestratedCstic, csticModel);

		final List<CsticValueModel> assignableValues = csticModel.getAssignableValues();
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();

		assertEquals("wrong number assignable values", 4, assignableValues.size());
		assertEquals("wrong number assigned values", 2, assignedValues.size());

		assertEquals("wrong assignable values [0]", "A", assignableValues.get(0).getName());
		assertEquals("wrong assignable values [1]", "B", assignableValues.get(1).getName());
		assertEquals("wrong assignable values [2]", "C", assignableValues.get(2).getName());
		assertEquals("wrong assignable values [3]", "D", assignableValues.get(3).getName());
		assertTrue("value should be selectable", assignableValues.get(0).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(1).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(2).isSelectable());
		assertTrue("value should be selectable", assignableValues.get(3).isSelectable());
		assertTrue("value should be a domain value", assignableValues.get(0).isDomainValue());
		assertTrue("value should be a domain value", assignableValues.get(1).isDomainValue());
		assertTrue("value should be a domain value", assignableValues.get(2).isDomainValue());
		assertTrue("value should not be a domain value", !assignableValues.get(3).isDomainValue());

		assertEquals("wrong assigned values [0]", "A", assignedValues.get(0).getName());
		assertEquals("wrong assigned values [1]", "D", assignedValues.get(1).getName());

		assertEquals("wrong cstic author", CsticModel.AUTHOR_DEFAULT, csticModel.getAuthor());
	}

	protected CsticModel prepareIntervals()
	{
		final CsticModel cstic = new CsticModelImpl();
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		cstic.setIntervalInDomain(true);

		final List<CsticValueModel> assignableValues = new ArrayList<CsticValueModel>();
		final CsticValueModel csticValueInterval1 = new CsticValueModelImpl();
		csticValueInterval1.setName("10 - 20");
		csticValueInterval1.setDomainValue(true);
		assignableValues.add(csticValueInterval1);
		final CsticValueModel csticValueInterval2 = new CsticValueModelImpl();
		csticValueInterval2.setName("50 - 60");
		csticValueInterval2.setDomainValue(true);
		assignableValues.add(csticValueInterval2);
		cstic.setAssignableValues(assignableValues);
		return cstic;
	}

	@Test
	public void testAdjustIntervalInDomain() throws Exception
	{
		final CsticModel cstic = prepareIntervals();
		cstic.setAllowsAdditionalValues(true);
		cstic.setIntervalInDomain(false);

		myProvider.adjustIntervalInDomain(cstic);
		assertTrue(cstic.isIntervalInDomain());
	}

	@Test
	public void testFillConfigInfo()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final IConfigInfoData configInfo = new ConfigInfoData();
		configInfo.setKbId(123);
		configInfo.setConfigName("name");
		configInfo.setConfigId("id");
		configInfo.setSingleLevel(true);
		configInfo.setComplete(true);
		configInfo.setConsistent(true);
		configInfo.setKbLogSys("logsys");
		configInfo.setKbVersion("version");

		myProvider.fillConfigInfo(configModel, configInfo);

		assertEquals("123", configModel.getKbId());
		assertEquals("name", configModel.getName());
		assertTrue(configModel.isComplete());
		assertTrue(configModel.isConsistent());
		assertTrue(configModel.isSingleLevel());

		assertNull(configModel.getKbKey().getProductCode());
		assertEquals("name", configModel.getKbKey().getKbName());
		assertEquals("logsys", configModel.getKbKey().getKbLogsys());
		assertEquals("version", configModel.getKbKey().getKbVersion());
	}

	@Test
	public void testRetrieveVariantConditionsForInstance() throws IpcCommandException
	{
		final String configId = "CONFIG_ID";
		final String instanceId = "INSTANCE_ID";
		final VariantCondKeyData[] variantConditionDataArray = prepareVariantConditionData(configId, instanceId);

		when(mockedSession.getConfigItemInfo(configId)).thenReturn(mockedConfigContainer);
		when(mockedConfigContainer.getArrVariantCondKeyContainer()).thenReturn(variantConditionDataArray);

		final List<VariantConditionModel> variantConditionList = myProvider.retrieveVariantConditionsForInstance(mockedSession,
				configId, instanceId);
		assertNotNull(variantConditionList);
		assertEquals(2, variantConditionList.size());
		assertEquals("VKEY1", variantConditionList.get(0).getKey());
		assertEquals(0, (new BigDecimal("1.0")).compareTo(variantConditionList.get(0).getFactor()));
		assertEquals("VKEY2", variantConditionList.get(1).getKey());
		assertEquals(0, (new BigDecimal("2.0")).compareTo(variantConditionList.get(1).getFactor()));
	}

	@Test
	public void testCreateVariantConditionModels()
	{
		final String configId = "CONFIG_ID";
		final String instanceId = "INSTANCE_ID";
		final VariantCondKeyData[] variantConditionDataArray = prepareVariantConditionData(configId, instanceId);


		final List<VariantConditionModel> variantConditionList = myProvider.createVariantConditionModels(instanceId,
				variantConditionDataArray);
		assertNotNull(variantConditionList);
		assertEquals(2, variantConditionList.size());
		assertEquals("VKEY1", variantConditionList.get(0).getKey());
		assertEquals(0, (new BigDecimal("1.0")).compareTo(variantConditionList.get(0).getFactor()));
		assertEquals("VKEY2", variantConditionList.get(1).getKey());
		assertEquals(0, (new BigDecimal("2.0")).compareTo(variantConditionList.get(1).getFactor()));
	}

	protected VariantCondKeyData[] prepareVariantConditionData(final String configId, final String instanceId)
	{
		final VariantCondKeyData[] variantConditionDataArray = new VariantCondKeyData[3];
		final VariantCondKeyData variantConditionData1 = new VariantCondKeyData();
		variantConditionData1.setConfigID(configId);
		variantConditionData1.setInstID(instanceId);
		variantConditionData1.setVkey("VKEY1");
		variantConditionData1.setFactor("1.0");
		variantConditionDataArray[0] = variantConditionData1;
		final VariantCondKeyData variantConditionData2 = new VariantCondKeyData();
		variantConditionData2.setConfigID(configId);
		variantConditionData2.setInstID(instanceId);
		variantConditionData2.setVkey("VKEY2");
		variantConditionData2.setFactor("2.0");
		variantConditionDataArray[1] = variantConditionData2;
		final VariantCondKeyData variantConditionData3 = new VariantCondKeyData();
		variantConditionData3.setConfigID(configId);
		variantConditionData3.setInstID("ANOTHER_INSTANCE_ID");
		variantConditionData3.setVkey("VKEY3");
		variantConditionData3.setFactor("3.0");
		variantConditionDataArray[2] = variantConditionData3;
		return variantConditionDataArray;
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testChangeConfiguration() throws ConfigurationEngineException
	{
		myProvider.changeConfiguration(null);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testReleaseSession() throws ConfigurationEngineException
	{
		myProvider.releaseSession("configId", "version");
	}

	@Test
	public void testPrepareConfigurationContext() throws IpcCommandException
	{
		myProvider.setContextAndPricingWrapper(new ConfigurationContextAndPricingWrapperImpl());
		myProvider.prepareConfigurationContext(mockedSession, new KBKeyImpl("PRODUCT"));
		Mockito.verify(mockedSession).setContext((Mockito.any()));
	}

	@Test
	public void testPrepareConfigurationContextNotSet() throws IpcCommandException
	{
		myProvider.setContextAndPricingWrapper(null);
		myProvider.prepareConfigurationContext(mockedSession, new KBKeyImpl("PRODUCT"));
		Mockito.verify(mockedSession, Mockito.never()).setContext((Mockito.any()));
	}

	@Test
	public void testDetermineProductForContextAndPricingChangeableVariant()
	{
		final String variantProductCode = "VARIANT";
		final String baseProductCode = "BASE";
		final ERPVariantProductModel changeableVariantProductModel = new ERPVariantProductModel();
		changeableVariantProductModel.setChangeable(true);
		when(mockedConfigurationProductUtil.getProductForCurrentCatalog(variantProductCode)).thenReturn(changeableVariantProductModel);
		assertEquals(variantProductCode, myProvider.determineProductForContextAndPricing(baseProductCode, variantProductCode));
	}

	@Test
	public void testDetermineProductForContextAndPricingNotChangeableVariant()
	{
		final String variantProductCode = "VARIANT";
		final String baseProductCode = "BASE";
		final ERPVariantProductModel changeableVariantProductModel = new ERPVariantProductModel();
		changeableVariantProductModel.setChangeable(false);
		when(mockedConfigurationProductUtil.getProductForCurrentCatalog(variantProductCode)).thenReturn(changeableVariantProductModel);
		assertEquals(baseProductCode, myProvider.determineProductForContextAndPricing(baseProductCode, variantProductCode));
	}

	@Test
	public void testUpdateKbKeyFromRootInstanceNoUpdate()
	{
		myProvider.updateKbKeyFromRootInstance(configModel, rootInstanceModel);
		assertEquals(P_CODE, configModel.getKbKey().getKbName());
		assertEquals(P_CODE, configModel.getKbKey().getProductCode());
		assertEquals(VERSION, configModel.getKbKey().getKbVersion());
		assertEquals(LOGSYS, configModel.getKbKey().getKbLogsys());
		assertEquals(kbDate, configModel.getKbKey().getDate());
	}

	@Test
	public void testUpdateKbKeyFromRootInstance()
	{
		KBKey kbKey = new KBKeyImpl(null, null, LOGSYS, VERSION);

		configModel.setKbKey(kbKey);
		myProvider.updateKbKeyFromRootInstance(configModel, rootInstanceModel);
		assertEquals(INSTACE_NAME, configModel.getKbKey().getKbName());
		assertNull(configModel.getKbKey().getProductCode());
		assertEquals(VERSION, configModel.getKbKey().getKbVersion());
		assertEquals(LOGSYS, configModel.getKbKey().getKbLogsys());
		assertEquals(kbKey.getDate(), configModel.getKbKey().getDate());
	}

}
