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
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.sapproductconfigsomservices.converters.populators.ConfigurationInfoNameProvider;
import de.hybris.platform.sap.sapproductconfigsomservices.converters.populators.DefaultAbstractOrderEntryConfigurationInfoPopulator;
import de.hybris.platform.sap.sapproductconfigsomservices.converters.populators.DefaultConfigurationInfoNameProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultAbstractOrderEntryConfigurationInfoPopulatorTest
{
	public static final String CONFIG_NAME = "Config Name";
	DefaultAbstractOrderEntryConfigurationInfoPopulator classUnderTest;
	private CsticModel csticModel;
	private ConfigurationInfoNameProvider nameProvider;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultAbstractOrderEntryConfigurationInfoPopulator();
		csticModel = new CsticModelImpl();
		csticModel.setValueType(CsticModel.TYPE_STRING);
		nameProvider = new DefaultConfigurationInfoNameProvider();
		classUnderTest.setConfigurationInfoNameProvider(nameProvider);
	}

	@Test
	public void testConfigInfoInlineEmpty()
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel);
		assertEquals("'configInfoInline' not equal empty String: ", StringUtils.EMPTY, configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotEmpty()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel);
		assertEquals("'configInfoInline' equal empty String: ", "Value 0", configInfoInline);
	}

	@Test
	public void testConfigInfoInlineNotContainsSemicolon()
	{
		final List<CsticValueModel> assignedValues = createAssignedValues(1);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertFalse("'configInfoInline' not contains ';': ",
				configInfoInline.toString().contains(DefaultAbstractOrderEntryConfigurationInfoPopulator.VALUE_SEPARATOR));
	}

	@Test
	public void testConfigInfoInlineContainsSemicolon()
	{
		final int numberOfAssignedValues = 4;
		final List<CsticValueModel> assignedValues = createAssignedValues(numberOfAssignedValues);
		final String configInfoInline = classUnderTest.generateConfigInfoInline(assignedValues, csticModel);
		assertNotNull("'configInfoInline' equals null", configInfoInline);
		assertTrue("'configInfoInline' not contains ';': ",
				configInfoInline.toString().contains(DefaultAbstractOrderEntryConfigurationInfoPopulator.VALUE_SEPARATOR));
		final int expectedNumberOfSemicolons = numberOfAssignedValues - 1;
		final int counter = countSemicolons(configInfoInline.toString());
		assertTrue("There should be " + expectedNumberOfSemicolons + "';' in 'configInfoInline': ",
				counter == expectedNumberOfSemicolons);
	}

	@Test
	public void testPopulate1Group1AssignedValue()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoModelList = new ArrayList();
		final ConfigModel configModel = createConfigModel(true);
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("1 ConfigurationInfoData entry should be returned", 1, configInfoModelList.size());
		final ConfigurationInfoData entry = configInfoModelList.get(0);
		assertEquals("Entry's cstic: ", "Radio Button Group", entry.getConfigurationLabel());
		assertEquals("Entry's value: ", "Value 1", entry.getConfigurationValue());
	}

	@Test
	public void testNoAssignedValues()
	{
		final int numberOfMaxCstics = 5;
		final List<ConfigurationInfoData> configInfoModelList = new ArrayList();
		final ConfigModel configModel = createConfigModel(false);
		classUnderTest.setMaxNumberOfDisplayedCsticsInCart(numberOfMaxCstics);
		classUnderTest.populate(configModel, configInfoModelList);
		assertEquals("1 initial CPQOrderEntryProductInfoModel entry should be returned", 1, configInfoModelList.size());
		final ConfigurationInfoData firstEntry = configInfoModelList.get(0);
		assertEquals("Configurator Type should be set to CPQCONFIGURATOR ", ConfiguratorType.CPQCONFIGURATOR,
				firstEntry.getConfiguratorType());
		assertNull("configuration label should be null: ", firstEntry.getConfigurationLabel());
	}

	/**
	 * @return created config model
	 */
	private ConfigModel createConfigModel(final boolean assignValue)
	{
		final ConfigModel model = new ConfigModelImpl();

		model.setId("WCEM_TEST_PRODUCT");
		model.setName(CONFIG_NAME);
		model.setComplete(false);
		model.setConsistent(true);

		final InstanceModel rootInstance = createRootInstance(assignValue);

		final List<InstanceModel> subInstances = new ArrayList<>();
		final InstanceModel subInstance = setSubInstance("Disselected UI Types");
		subInstances.add(subInstance);

		rootInstance.setSubInstances(subInstances);
		model.setRootInstance(rootInstance);

		return model;
	}


	@Test
	public void testConfigurationConsistent()
	{
		csticModel.setConsistent(true);
		final List<ConfigurationInfoData> configInfoModelList = new ArrayList();
		classUnderTest.addCsticForCartDisplay(configInfoModelList, csticModel);
		assertFalse(configInfoModelList.isEmpty());
		assertEquals(ProductInfoStatus.SUCCESS, configInfoModelList.get(0).getStatus());
	}

	@Test
	public void testConfigurationInConsistent()
	{
		csticModel.setConsistent(false);
		final List<ConfigurationInfoData> configInfoModelList = new ArrayList();
		classUnderTest.addCsticForCartDisplay(configInfoModelList, csticModel);
		assertFalse(configInfoModelList.isEmpty());
		assertEquals(ProductInfoStatus.ERROR, configInfoModelList.get(0).getStatus());
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
			if (configInfoInline.charAt(i) == DefaultAbstractOrderEntryConfigurationInfoPopulator.VALUE_SEPARATOR.toCharArray()[0])
			{
				counter++;
			}
		}
		return counter;
	}

	private InstanceModel createRootInstance(final boolean assignValue)
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setId("WCEM_TEST_PRODUCT");
		rootInstance.setLanguageDependentName("Selected UI Types");

		final List<CsticModel> cstics = new ArrayList<>();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setLanguageDependentName("Radio Button Group");
		cstic.setName("CSTIC1");

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue1 = createCsticValue("Value 1", "4.99");
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = createCsticValue("Value 2", "2.99");
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = createCsticValue("Value 3", "0.99");
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		if (assignValue)
		{
			final CsticValueModel assignedValue = createCsticValue("Value 1", "4.99");
			assignedValues.add(assignedValue);

		}
		cstic.setAssignedValues(assignedValues);
		cstic.setVisible(true);
		cstics.add(cstic);

		rootInstance.setCstics(cstics);
		final CsticGroupModel csticGroup = createCsticGroup("GROUP1", "Inline Config Test", "CSTIC1");
		final List<CsticGroupModel> groups = new ArrayList<>();
		groups.add(csticGroup);
		rootInstance.setCsticGroups(groups);
		return rootInstance;

	}

	private InstanceModel setSubInstance(final String languageDependentName)
	{
		final InstanceModel subInstance = new InstanceModelImpl();
		subInstance.setLanguageDependentName(languageDependentName);

		final List<CsticModel> cstics = new ArrayList<>();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setLanguageDependentName(languageDependentName);

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue1 = createCsticValue("Flag 1", "6.33");
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = createCsticValue("Flag 2", "9.11");
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = createCsticValue("Flag 3", "1.55");
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = createCsticValue("Flag 3", "1.55");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);
		cstics.add(cstic);

		subInstance.setCstics(cstics);

		return subInstance;
	}

	private CsticValueModel createCsticValue(final String languageDependentName, final String price)
	{
		final CsticValueModel assignableValue = new CsticValueModelImpl();
		assignableValue.setLanguageDependentName(languageDependentName);
		final PriceModel deltaPrice = new PriceModelImpl();
		deltaPrice.setCurrency("EURO");
		deltaPrice.setPriceValue(new BigDecimal(price));
		assignableValue.setDeltaPrice(deltaPrice);

		return assignableValue;
	}

	private CsticGroupModel createCsticGroup(final String groupName, final String description, final String... csticNames)
	{
		final List<String> csticNamesInGroup = new ArrayList<>();
		for (final String csticName : csticNames)
		{
			csticNamesInGroup.add(csticName);
		}

		final CsticGroupModel csticGroup = new CsticGroupModelImpl();
		csticGroup.setName(groupName);
		csticGroup.setDescription(description);
		csticGroup.setCsticNames(csticNamesInGroup);

		return csticGroup;
	}

}
