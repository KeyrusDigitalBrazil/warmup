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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.impl.ClassificationSystemCPQAttributesProviderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class ConfigurationOrderEntryProductInfoModelPopulatorTest
{
	private ConfigurationOrderEntryProductInfoModelPopulator classUnderTest;
	private CsticModel csticModel;
	@Mock
	public ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategyMock;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationOrderEntryProductInfoModelPopulator();
		csticModel = new CsticModelImpl();
		csticModel.setValueType(CsticModel.TYPE_STRING);
		final ClassificationSystemCPQAttributesProviderImpl nameProvider = Mockito
				.spy(new ClassificationSystemCPQAttributesProviderImpl());
		nameProvider.setUiTypeFinder(new UiTypeFinderImpl());
		nameProvider.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		classUnderTest.setNameProvider(nameProvider);
		classUnderTest.setClassificationCacheStrategy(configurationClassificationCacheStrategyMock);

		doReturn(ClassificationSystemCPQAttributesContainer.NULL_OBJ).when(nameProvider).getCPQAttributes(Mockito.anyString(),
				Mockito.anyMap());
	}

	@Test
	public void testConfigInfoInlineEmpty()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertEquals("'configInfoInline' not equal empty String: ", StringUtils.EMPTY, configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotEmpty()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertEquals("'configInfoInline' equal empty String: ", "Value 0", configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotContainsSemicolon()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertFalse("'configInfoInline' not contains ';': ", configInfoInline.toString().contains(classUnderTest.VALUE_SEPARATOR));
	}

	@Test
	public void testConfigInfoInlineContainsSemicolon()
	{
		final int numberOfAssignedValues = 4;
		final List<CsticValueModel> assignedValues = createAssignedValues(numberOfAssignedValues);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel,
				ClassificationSystemCPQAttributesContainer.NULL_OBJ, false);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertTrue("'configInfoInline' not contains ';': ", configInfoInline.toString().contains(classUnderTest.VALUE_SEPARATOR));
		final int expectedNumberOfSemicolons = numberOfAssignedValues - 1;
		final int counter = countSemicolons(configInfoInline.toString());
		assertTrue("There should be " + expectedNumberOfSemicolons + "';' in 'configInfoInline': ",
				counter == expectedNumberOfSemicolons);
	}

	@Test
	public void test1Group3ValuesMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("3 CPQOrderEntryProductInfoModel entries should be returned", 3, configInfoModelList.size());
		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", secondEntry.getCpqCharacteristicName());
	}

	@Test
	public void test1Group4ValuesWithMultiValuedCsticMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValuesMultiValued();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("4 CPQOrderEntryProductInfoModel entries should be returned", 4, configInfoModelList.size());
		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", ConfigurationTestData.CHBOX_LIST_LD_NAME, secondEntry.getCpqCharacteristicName());
		assertEquals("Second entry's cstic's values: ", "VALUE 2; VALUE 3", secondEntry.getCpqCharacteristicAssignedValues());
	}

	@Test
	public void testNoAssignedValues()
	{
		final int numberOfMaxCstics = 5;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModel();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("1 initial CPQOrderEntryProductInfoModel entry should be returned", 1, configInfoModelList.size());
		assertTrue(configInfoModelList.get(0) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel firstEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(0);
		assertEquals("Configurator Type should be set to CPQCONFIGURATOR ", ConfiguratorType.CPQCONFIGURATOR,
				firstEntry.getConfiguratorType());
		assertNull("configuration label should be null: ", firstEntry.getCpqCharacteristicName());
	}

	@Test
	public void test1Group3ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith1GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("2 CPQOrderEntryProductInfoModel entries should be returned", 2, configInfoModelList.size());
		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", secondEntry.getCpqCharacteristicName());
		assertEquals("Second entry's cstic's value: ", "defaultValue", secondEntry.getCpqCharacteristicAssignedValues());
	}

	@Test
	public void test2Groups6ValuesMax5()
	{
		final int numberOfMaxCstics = 5;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith2GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("5 CPQOrderEntryProductInfoModel entries should be returned", 5, configInfoModelList.size());
		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", secondEntry.getCpqCharacteristicName());

		assertTrue(configInfoModelList.get(4) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel fifthEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(4);
		assertEquals("Fifth entry's cstic: ", "CSTIC 2.3", fifthEntry.getCpqCharacteristicName());
		assertEquals("Fifth entry's cstic's value: ", "VALUE_2", fifthEntry.getCpqCharacteristicAssignedValues());
	}

	@Test
	public void test2Groups6ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith2GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("2 CPQOrderEntryProductInfoModel entries should be returned", 2, configInfoModelList.size());

		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", secondEntry.getCpqCharacteristicName());
	}

	@Test
	public void test3Groups6ValuesMax2()
	{
		final int numberOfMaxCstics = 2;
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		final ConfigModel configModel = ConfigurationTestData.createConfigModelWith3GroupAndAssignedValues();
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("2 ConfigurationInfoData entries should be returned", 2, configInfoModelList.size());
		assertTrue(configInfoModelList.get(1) instanceof CPQOrderEntryProductInfoModel);
		final CPQOrderEntryProductInfoModel secondEntry = (CPQOrderEntryProductInfoModel) configInfoModelList.get(1);
		assertEquals("Second entry's cstic: ", "CSTIC 1.4", secondEntry.getCpqCharacteristicName());
	}

	@Test
	public void testConfigurationConsistent()
	{
		csticModel.setConsistent(true);
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		classUnderTest.addCsticForCartDisplay(configInfoModelList, csticModel, null);
		assertFalse(configInfoModelList.isEmpty());
		assertTrue(configInfoModelList.get(0) instanceof CPQOrderEntryProductInfoModel);
		assertEquals(ProductInfoStatus.SUCCESS, configInfoModelList.get(0).getProductInfoStatus());
	}

	@Test
	public void testConfigurationInConsistent()
	{
		csticModel.setConsistent(false);
		final List<AbstractOrderEntryProductInfoModel> configInfoModelList = new ArrayList();
		classUnderTest.addCsticForCartDisplay(configInfoModelList, csticModel, null);
		assertFalse(configInfoModelList.isEmpty());
		assertTrue(configInfoModelList.get(0) instanceof CPQOrderEntryProductInfoModel);
		assertEquals(ProductInfoStatus.ERROR, configInfoModelList.get(0).getProductInfoStatus());
	}

	protected List<CsticValueModel> createAssignedValues(final int size)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			final CsticValueModel value = new CsticValueModelImpl();
			final String langDepName = "Value " + i;
			value.setLanguageDependentName(langDepName);
			assignedValues.add(value);
		}
		return assignedValues;
	}

	protected int countSemicolons(final String configInfoInline)
	{
		int counter = 0;
		for (int i = 0; i < configInfoInline.length(); i++)
		{
			if (configInfoInline.charAt(i) == classUnderTest.VALUE_SEPARATOR.toCharArray()[0])
			{
				counter++;
			}
		}
		return counter;
	}
}
