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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.filters.DefaultOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.facades.filters.PriceRelevantValueFilter;
import de.hybris.platform.sap.productconfig.facades.filters.UserAssignedValueFilter;
import de.hybris.platform.sap.productconfig.facades.filters.VisibleValueFilter;
import de.hybris.platform.sap.productconfig.facades.impl.ClassificationSystemCPQAttributesProviderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit tests
 */
@UnitTest
public class ConfigurationOverviewInstancePopulatorTest
{
	public ConfigurationOverviewInstancePopulator classUnderTest;
	public ConfigurationOverviewValuePopulator configurationOverviewValuePopulator;
	public ConfigOverviewGroupFilter overviewGroupFilter;
	public InstanceModel source;
	public List<CharacteristicGroup> target;
	private ConfigOverviewFilter visibleValueFilter;
	private ConfigOverviewFilter userAssignedValueFilter;
	private ConfigOverviewFilter priceRelevantValueFilter;
	public ConfigPricingImpl configPricing;
	@Mock
	public PriceModel deltaPrice;
	@Mock
	public ProductDao productDao;
	@Mock
	public ProductModel productModel;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ConfigurationOverviewInstancePopulator();
		configurationOverviewValuePopulator = new ConfigurationOverviewValuePopulator();
		configPricing = new ConfigPricingImpl();
		configurationOverviewValuePopulator.setConfigPricing(configPricing);
		classUnderTest.setConfigurationOverviewValuePopulator(configurationOverviewValuePopulator);
		overviewGroupFilter = new DefaultOverviewGroupFilter();
		classUnderTest.setOverviewGroupFilter(overviewGroupFilter);

		visibleValueFilter = new VisibleValueFilter();
		classUnderTest.setVisibleValueFilter(visibleValueFilter);

		target = new ArrayList<>();
		source = new InstanceModelImpl();
		source.setId("WCEM_TEST_PRODUCT");
		source.setLanguageDependentName("Simple UI Types");

		final List<CsticModel> cstics = new ArrayList<>();
		final CsticModel cstic = new CsticModelImpl();
		cstic.setLanguageDependentName("Radio Button Group");

		final List<CsticValueModel> assignableValues = new ArrayList<>();
		final CsticValueModel assignableValue1 = ConfigurationTestData.createCsticValue("Value 1", "4.99");
		assignableValues.add(assignableValue1);
		final CsticValueModel assignableValue2 = ConfigurationTestData.createCsticValue("Value 2", "2.99");
		assignableValues.add(assignableValue2);
		final CsticValueModel assignableValue3 = ConfigurationTestData.createCsticValue("Value 3", "0.99");
		assignableValues.add(assignableValue3);
		cstic.setAssignableValues(assignableValues);

		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel assignedValue = ConfigurationTestData.createCsticValue("Value 2", "2.99");
		assignedValues.add(assignedValue);
		cstic.setAssignedValues(assignedValues);
		cstics.add(cstic);

		source.setCstics(cstics);

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = Mockito
				.spy(new ClassificationSystemCPQAttributesProviderImpl());
		nameProvider.setUiTypeFinder(new UiTypeFinderImpl());
		nameProvider.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		classUnderTest.setNameProvider(nameProvider);
		configurationOverviewValuePopulator.setNameProvider(nameProvider);
		Mockito.doReturn(ClassificationSystemCPQAttributesContainer.NULL_OBJ).when(nameProvider)
				.getCPQAttributes(Mockito.anyString(), Mockito.anyMap());

		classUnderTest.setProductDao(productDao);
	}

	public List<ConfigOverviewFilter> setUpFilters()
	{
		userAssignedValueFilter = new UserAssignedValueFilter();
		priceRelevantValueFilter = new PriceRelevantValueFilter();
		final List<ConfigOverviewFilter> filters = new ArrayList<>();
		filters.add(userAssignedValueFilter);
		filters.add(priceRelevantValueFilter);
		return filters;
	}

	@Test
	public void testConfigurationOverviewInstancePopulatorWithoutSubGroups()
	{
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(source, target, options);
		assertNotNull(target);
		for (final CharacteristicGroup targetGroup : target)
		{
			final List<CharacteristicValue> csticValues = targetGroup.getCharacteristicValues();
			for (final CharacteristicValue value : csticValues)
			{
				assertEquals("We expect group description: ", "Radio Button Group", value.getCharacteristic());
				assertEquals("We expect group description: ", "Value 2", value.getValue());
				assertEquals("We expect group description: ", "2.99", value.getPriceDescription());
			}
		}

	}

	@Test
	public void testOptions_1CsticGroup_0Subinstance()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithOneGroup();
		final InstanceModel sourceInstance = config.getRootInstance();
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(sourceInstance, target, options);
		assertNotNull(options.iterator().next());
		assertTrue("OptionsMap should have 1 entry 'HAS_ONLY_ONE_CSTIC_GROUP' as only one cstic group exists",
				options.iterator().next().containsKey(ConfigurationOverviewInstancePopulator.HAS_ONLY_ONE_CSTIC_GROUP));
		assertNotNull(target);
		assertEquals("One Characteristic Group should be created", 1, target.size());
		assertFalse("Charactierstic Group should have CharacteristicValues", target.get(0).getCharacteristicValues().isEmpty());
	}

	@Test
	public void testOptions_2CsticGroups_0Subinstance()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAllVisible();
		final InstanceModel sourceInstance = config.getRootInstance();
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(sourceInstance, target, options);
		assertFalse(
				"OptionsMap should have no entry 'HAS_ONLY_ONE_CSTIC_GROUP' as two cstic groups exists and flag should only be set if one cstic group exist",
				options.iterator().next().containsKey(ConfigurationOverviewInstancePopulator.HAS_ONLY_ONE_CSTIC_GROUP));
		assertNotNull(target);
		assertEquals("Two CharacteristicGroups should be created", 2, target.size());
		assertFalse("Charactierstic Group 1 should have CharacteristicValues", target.get(0).getCharacteristicValues().isEmpty());
		assertFalse("Charactierstic Group 2 should have CharacteristicValues", target.get(1).getCharacteristicValues().isEmpty());
	}

	private Collection<Map> fillOptions(final List<ConfigOverviewFilter> csticFilters, final Set<String> groupsFilter)
	{
		final Collection<Map> options = new ArrayList<>();
		final HashMap optionsMap = new HashMap();
		optionsMap.put(ConfigurationOverviewInstancePopulator.APPLIED_CSTIC_FILTERS, csticFilters);
		optionsMap.put(ConfigurationOverviewInstancePopulator.APPLIED_GROUP_FILTERS, groupsFilter);
		options.add(optionsMap);
		return options;
	}

	@Test
	public void testFilterUserAssigned()
	{
		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroups();
		final InstanceModel sourceInstance = config.getRootInstance();
		classUnderTest.populate(sourceInstance, target, fillOptions(setUpFilters(), new HashSet()));
		assertNotNull(target);
		assertEquals("One CharacteristicGroup should be created after filtering for user-assigned values", 1, target.size());
		assertEquals("Charactierstic Group 1 should one CharacteristicValues", 1, target.get(0).getCharacteristicValues().size());
	}


	@Test
	public void testOptions_0CsticGroups_1emptySubinstance()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithSubInstance();
		final InstanceModel sourceInstance = config.getRootInstance();
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(sourceInstance, target, options);
		assertFalse(
				"OptionsMap should have no entry 'HAS_ONLY_ONE_CSTIC_GROUP' as two cstic groups exists and flag should only be set if one cstic group exist",
				options.iterator().next().containsKey(ConfigurationOverviewInstancePopulator.HAS_ONLY_ONE_CSTIC_GROUP));
		assertEquals("No groups should be created as sub-instances are empty and no cstic groups exist", 0, target.size());
	}

	@Test
	public void testOptions_2CsticGroups_1Subinstance_1emptySubinstance()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAndSubInstancesAllVisible();
		final InstanceModel sourceInstance = config.getRootInstance();
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(sourceInstance, target, options);
		assertFalse(
				"OptionsMap should have no entry 'HAS_ONLY_ONE_CSTIC_GROUP' as two cstic groups exists and flag should only be set if one cstic group exist",
				options.iterator().next().containsKey(ConfigurationOverviewInstancePopulator.HAS_ONLY_ONE_CSTIC_GROUP));
		assertEquals(
				"3 groups should be created as 2 cstic groups and 1 sub-instance exist (1 other sub-instance is empty and should not be created as group)",
				3, target.size());
		assertFalse("Charactierstic Group 1 should have CharacteristicValues", target.get(0).getCharacteristicValues().isEmpty());
		assertFalse("Charactierstic Group 2 should have CharacteristicValues", target.get(1).getCharacteristicValues().isEmpty());
		assertNull("Charactierstic Group 3 should have no CharacteristicValues but subgroups",
				target.get(2).getCharacteristicValues());
		assertFalse("Charactierstic Group 3 should have no CharacteristicValues but subgroups",
				target.get(2).getSubGroups().isEmpty());
	}


	@Test
	public void testOptions_1CsticGroups_1Subinstance_1emptySubinstance()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAndSubInstancesAllVisible();
		final InstanceModel sourceInstance = config.getRootInstance();
		// Delete one cstic group that only one remains
		List<CsticGroupModel> csticGroupModels = sourceInstance.getCsticGroups();
		csticGroupModels.remove(1);
		sourceInstance.setCsticGroups(csticGroupModels);

		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		classUnderTest.populate(sourceInstance, target, options);
		assertTrue("OptionsMap should have 1 entry 'HAS_ONLY_ONE_CSTIC_GROUP' as only one cstic group exists",
				options.iterator().next().containsKey(ConfigurationOverviewInstancePopulator.HAS_ONLY_ONE_CSTIC_GROUP));
		assertEquals(
				"2 groups should be created as 1 cstic group1 and 1 sub-instance exist (1 other sub-instance is empty and should not be created as group)",
				2, target.size());
		assertFalse("Charactierstic Group 1 should have CharacteristicValues", target.get(0).getCharacteristicValues().isEmpty());
		assertFalse("Charactierstic Group 2 should have no CharacteristicValues but subgroups",
				target.get(1).getSubGroups().isEmpty());
	}



	@Test
	public void testConfigurationOverviewInstancePopulatorWithOneSubGroups()
	{
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet());
		final List<InstanceModel> subInstances = new ArrayList<>();
		final InstanceModel instance = ConfigurationTestData.setSubInstance("Disselected UI Types");
		subInstances.add(instance);
		source.setSubInstances(subInstances);

		classUnderTest.populate(source, target, options);
		assertNotNull(target);
		for (int cstic = 0; cstic < target.size(); cstic++)
		{
			if (cstic == 0)
			{
				final List<CharacteristicValue> csticValues = target.get(cstic).getCharacteristicValues();
				for (final CharacteristicValue value : csticValues)
				{
					assertEquals("We expect group description: ", "Radio Button Group", value.getCharacteristic());
					assertEquals("We expect value: ", "Value 2", value.getValue());
					assertEquals("We expect price: ", "2.99", value.getPriceDescription());
				}
			}
			else
			{
				final List<CharacteristicValue> csticValues = target.get(cstic).getCharacteristicValues();
				for (final CharacteristicValue value : csticValues)
				{
					assertEquals("We expect group description: ", "Disselected UI Types", value.getCharacteristic());
					assertEquals("We expect value: ", "Flag 3", value.getValue());
					assertEquals("We expect price: ", "1.55", value.getPriceDescription());
				}
			}
		}
	}

	@Test
	public void testHybrisNameForSubinstance()
	{
		final String hybrisProductName = "HYBRIS_PRODUCT_NAME";

		final List<ProductModel> products = new ArrayList<>();
		products.add(productModel);
		Mockito.when(productModel.getName()).thenReturn(hybrisProductName);
		Mockito.when(productDao.findProductsByCode(Mockito.anyString())).thenReturn(products);

		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAndSubInstancesAllVisible();
		final InstanceModel sourceInstance = config.getRootInstance();
		final Collection<Map> options = fillOptions(new ArrayList(), new HashSet<>());

		classUnderTest.populate(sourceInstance, target, options);
		assertEquals("Expected hybris product name", hybrisProductName, target.get(2).getGroupDescription());
	}

	@Test
	public void testValuePositionType()
	{
		int size = 4;
		int index = 0;
		ValuePositionTypeEnum flag = classUnderTest.determineValuePositionType(size, index);
		assertEquals(ValuePositionTypeEnum.FIRST, flag);
		size = 4;
		index = 2;
		flag = classUnderTest.determineValuePositionType(size, index);
		assertEquals(ValuePositionTypeEnum.INTERJACENT, flag);
		size = 1;
		index = 0;
		flag = classUnderTest.determineValuePositionType(size, index);
		assertEquals(ValuePositionTypeEnum.ONLY_VALUE, flag);
		size = 5;
		index = 4;
		flag = classUnderTest.determineValuePositionType(size, index);
		assertEquals(ValuePositionTypeEnum.LAST, flag);
		size = 5;
		index = 3;
		flag = classUnderTest.determineValuePositionType(size, index);
		assertEquals(ValuePositionTypeEnum.INTERJACENT, flag);
	}

}
