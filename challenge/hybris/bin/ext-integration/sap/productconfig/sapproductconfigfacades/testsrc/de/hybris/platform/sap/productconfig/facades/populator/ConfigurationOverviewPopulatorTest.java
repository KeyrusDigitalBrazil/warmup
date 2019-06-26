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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.filters.DefaultOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.facades.filters.OverviewFilterList;
import de.hybris.platform.sap.productconfig.facades.filters.VisibleValueFilter;
import de.hybris.platform.sap.productconfig.facades.impl.ClassificationSystemCPQAttributesProviderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UiTypeFinderImpl;
import de.hybris.platform.sap.productconfig.facades.impl.ValueFormatTranslatorImpl;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;


/**
 * Unit tests
 */
@UnitTest
@SuppressWarnings("javadoc")
public class ConfigurationOverviewPopulatorTest
{
	public ConfigurationOverviewPopulator classUnderTest;
	public ConfigurationOverviewInstancePopulator configurationOverviewInstancePopulator;
	public ConfigOverviewGroupFilter overviewGroupFilter;
	public ConfigurationOverviewValuePopulator configurationOverviewValuePopulator;
	private OverviewFilterList overviewFilterList;
	private ConfigOverviewFilter visibleValueFilter;
	public ConfigPricingImpl configPricing;

	public ConfigModel source;
	public ConfigurationOverviewData target;

	@Mock
	public ProductDao productDaoMock;
	@Mock
	public ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategyMock;
	@Mock
	public PriceDataFactory priceDataFactoryMock;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ConfigurationOverviewPopulator();
		configurationOverviewInstancePopulator = new ConfigurationOverviewInstancePopulator();
		overviewGroupFilter = new DefaultOverviewGroupFilter();
		configurationOverviewInstancePopulator.setOverviewGroupFilter(overviewGroupFilter);
		visibleValueFilter = new VisibleValueFilter();
		configurationOverviewInstancePopulator.setVisibleValueFilter(visibleValueFilter);
		overviewFilterList = new OverviewFilterList();
		final List<ConfigOverviewFilter> filters = new ArrayList<>();
		overviewFilterList.setFilters(filters);
		classUnderTest.setOverviewFilterList(overviewFilterList);
		classUnderTest.setConfigurationOverviewInstancePopulator(configurationOverviewInstancePopulator);
		configPricing = new ConfigPricingImpl();
		configPricing.setPriceDataFactory(priceDataFactoryMock);
		classUnderTest.setConfigPricing(configPricing);
		configurationOverviewValuePopulator = new ConfigurationOverviewValuePopulator();
		configurationOverviewValuePopulator.setConfigPricing(configPricing);
		configurationOverviewInstancePopulator.setConfigurationOverviewValuePopulator(configurationOverviewValuePopulator);

		target = new ConfigurationOverviewData();
		source = ConfigurationTestData.createConfigModelWithGroupsAllVisible();

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = Mockito
				.spy(new ClassificationSystemCPQAttributesProviderImpl());
		nameProvider.setUiTypeFinder(new UiTypeFinderImpl());
		nameProvider.setValueFormatTranslator(new ValueFormatTranslatorImpl());
		configurationOverviewInstancePopulator.setNameProvider(nameProvider);
		configurationOverviewValuePopulator.setNameProvider(nameProvider);
		configurationOverviewInstancePopulator.setProductDao(productDaoMock);

		classUnderTest.setClassificationCacheStrategy(configurationClassificationCacheStrategyMock);
		Mockito.doReturn(ClassificationSystemCPQAttributesContainer.NULL_OBJ).when(nameProvider)
				.getCPQAttributes(Mockito.anyString(), Mockito.anyMap());

		doAnswer(this::createPriceData).when(priceDataFactoryMock).create(eq(PriceDataType.BUY), any(BigDecimal.class), eq("EUR"));
		when(productDaoMock.findProductsByCode(Mockito.anyString())).thenReturn(null);
	}

	protected PriceData createPriceData(final InvocationOnMock invocationOnMock)
	{
		final BigDecimal price = (BigDecimal) invocationOnMock.getArguments()[1];

		final PriceData priceData = new PriceData();
		priceData.setValue(price);
		priceData.setCurrencyIso("EUR");
		priceData.setPriceType(PriceDataType.BUY);

		return priceData;
	}

	@Test
	public void testConfigurationOverviewPopulator()
	{
		classUnderTest.populate(source, target);
		assertNotNull(target);
		assertEquals("We expect target Id: ", "1", target.getId());

		final List<CharacteristicValue> firstCsticValues = target.getGroups().get(0).getCharacteristicValues();
		assertEquals("We expect cstic description: ", ConfigurationTestData.CHBOX_LD_NAME,
				firstCsticValues.get(0).getCharacteristic());
		assertEquals("We expect value: ", "X", firstCsticValues.get(0).getValue());
		assertNull(firstCsticValues.get(0).getPriceDescription());

		final List<CharacteristicValue> secondCsticValues = target.getGroups().get(1).getCharacteristicValues();
		assertEquals("We expect cstic description: ", ConfigurationTestData.CHBOX_LIST_LD_NAME,
				secondCsticValues.get(0).getCharacteristic());
		assertEquals("We expect value: ", "VALUE 2", secondCsticValues.get(0).getValue());
		assertNull(secondCsticValues.get(0).getPriceDescription());

		assertNotNull(target.getPricing());

		final PricingData pricingData = target.getPricing();
		assertEquals(ConfigurationTestData.BASE_PRICE, pricingData.getBasePrice().getValue());
		assertEquals(ConfigurationTestData.SELECTED_OPTIONS_PRICE, pricingData.getSelectedOptions().getValue());
		assertEquals(ConfigurationTestData.TOTAL_PRICE, pricingData.getCurrentTotal().getValue());
	}

	@Test
	public void testConfigurationOverviewPopulatorWithFilters()
	{
		final List<FilterEnum> filterIds = new ArrayList<>();
		filterIds.add(FilterEnum.USER_INPUT);
		target.setAppliedCsticFilters(filterIds);

		final Set<String> filteredGroups = new HashSet<>();
		filteredGroups.add("GROUP1");
		target.setAppliedGroupFilters(filteredGroups);

		classUnderTest.populate(source, target);
		assertNotNull(target);
		assertEquals("We expect target Id: ", "1", target.getId());

		assertEquals(1, target.getGroups().size());
		final List<CharacteristicValue> firstCsticValues = target.getGroups().get(0).getCharacteristicValues();
		assertEquals("We expect cstic description: ", ConfigurationTestData.CHBOX_LD_NAME,
				firstCsticValues.get(0).getCharacteristic());
		assertEquals("We expect value: ", "X", firstCsticValues.get(0).getValue());
		assertNull(firstCsticValues.get(0).getPriceDescription());
	}

	@Test
	public void testCopyCsticsToNextLevelGroupIfOnlyOneCsticGroupExists()
	{

		final ConfigModel config = ConfigurationTestData.createConfigModelWithGroupsAndSubInstancesAllVisible();
		final InstanceModel sourceInstance = config.getRootInstance().getSubInstances().get(0);
		// Delete one cstic group that only one remains
		final List<CsticGroupModel> csticGroupModels = sourceInstance.getCsticGroups();
		csticGroupModels.remove(1);
		sourceInstance.setCsticGroups(csticGroupModels);

		classUnderTest.populate(config, target);
		assertNotNull(target);

		assertEquals("ConfigurationOverviewData should have 3 groups (2 cstic groups, 1 subinstance)", 3,
				target.getGroups().size());
		assertNotNull("Sub-Instance of root should have cstics AND sub-instance",
				target.getGroups().get(2).getCharacteristicValues());
		assertNotNull("Sub-Instance of root should have cstics AND sub-instance", target.getGroups().get(2).getSubGroups());
	}

}
