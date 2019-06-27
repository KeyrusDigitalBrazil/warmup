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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit tests
 */
@UnitTest
public class VariantOverviewPopulatorTest
{
	public static final String PRODUCT_CODE = "MY_PRODUCT_VARIANT";
	public static final String CSTIC_COLOR = "Car Color";
	public static final String CSTIC_ENGINE = "Car Engine";
	public static final String CSTIC_ACC = "Accessories of Car";
	public static final String VALUE_RED = "Dark Red";
	public static final String VALUE_HYBRID = "Hybrid Engine";
	public static final String VALUE_RADIO = "Advanced Radio 3000";
	public static final String VALUE_CUP = "Cup Holder";
	public static final String VALUE_NAVI = "Navigation System";

	public List<FeatureData> features;

	public VariantOverviewPopulator classUnderTest;
	public FeatureProvider featureProvider;
	@Mock
	private ProductModel productMock;
	@Mock
	private ProductClassificationPopulator<ProductModel, ProductData> productPopulatorMock;
	private VariantOverviewValuePopulator variantOverviewValuePopulator;


	@Before
	public void setUp()
	{
		classUnderTest = new VariantOverviewPopulator();
		featureProvider = new FeatureProvider();
		classUnderTest.setFeatureProvider(featureProvider);
		variantOverviewValuePopulator = new VariantOverviewValuePopulator();
		classUnderTest.setVariantOverviewValuePopulator(variantOverviewValuePopulator);
		MockitoAnnotations.initMocks(this);
		mockClassificationPopulator();
	}

	protected void mockClassificationPopulator()
	{
		final Answer answer = new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final ProductData data = (ProductData) args[1];
				data.setClassifications(VariantOverviewPopulatorTest.mockClassifications());
				return null;
			}
		};
		Mockito.doAnswer(answer).when(productPopulatorMock)
				.populate(Mockito.any(ProductModel.class), Mockito.any(ProductData.class));
		classUnderTest.setClassificationPopulator(productPopulatorMock);
	}

	public static List<ClassificationData> mockClassifications()
	{
		final List<ClassificationData> classifications = new ArrayList<>();
		final ClassificationData cData = new ClassificationData();
		cData.setCode(PRODUCT_CODE);
		cData.setName(PRODUCT_CODE);
		cData.setFeatures(VariantOverviewPopulatorTest.mockFeatures());
		classifications.add(cData);
		return classifications;
	}

	public static List<FeatureData> mockFeatures()
	{
		final List<FeatureData> features = new ArrayList<>();
		features.add(VariantOverviewPopulatorTest.mockFeatureColorRed());
		features.add(VariantOverviewPopulatorTest.mockFeatureEngineHybrid());
		features.add(VariantOverviewPopulatorTest.mockFeatureAccessories());
		return features;
	}

	public static FeatureData mockFeatureColorRed()
	{
		final FeatureData fData = new FeatureData();
		fData.setCode("PowertoolsClassification/1.0/WEC_CDRAGON_CAR.wec_dc_color");
		fData.setName(CSTIC_COLOR);
		fData.setComparable(true);
		fData.setRange(false);
		fData.setFeatureValues(mockFeatureValueData(VALUE_RED));
		return fData;
	}

	public static FeatureData mockFeatureEngineHybrid()
	{
		final FeatureData fData = new FeatureData();
		fData.setCode("PowertoolsClassification/1.0/WEC_CDRAGON_CAR.wec_dc_engine");
		fData.setName(CSTIC_ENGINE);
		fData.setComparable(true);
		fData.setRange(false);
		fData.setFeatureValues(mockFeatureValueData(VALUE_HYBRID));
		return fData;
	}

	public static FeatureData mockFeatureAccessories()
	{
		final FeatureData fData = new FeatureData();
		fData.setCode("PowertoolsClassification/1.0/WEC_CDRAGON_CAR.wec_dc_accessory");
		fData.setName(CSTIC_ACC);
		fData.setComparable(true);
		fData.setRange(false);
		fData.setFeatureValues(mockFeatureValueData(VALUE_RADIO, VALUE_CUP, VALUE_NAVI));
		return fData;
	}

	protected static List<FeatureValueData> mockFeatureValueData(final String... valueNames)
	{
		final ArrayList<FeatureValueData> list = new ArrayList<>();
		for (final String valueName : valueNames)
		{
			final FeatureValueData valueData = new FeatureValueData();
			valueData.setValue(valueName);
			list.add(valueData);
		}
		return list;
	}

	@Test
	public void testProcessEmptyFeatureList()
	{
		features = Collections.emptyList();
		final List<CharacteristicValue> values = new ArrayList<>();
		classUnderTest.processFeatureList(features, values);
		assertTrue("List of values not empty: ", CollectionUtils.isEmpty(values));
	}

	@Test
	public void testProcessNotEmptyFeatureList()
	{
		features = mockFeatures();
		final List<CharacteristicValue> values = new ArrayList<>();
		classUnderTest.processFeatureList(features, values);
		assertTrue("List of values empty: ", !CollectionUtils.isEmpty(values));
	}

	@Test
	public void testPopulator()
	{
		final ProductModel source = mock(ProductModel.class);
		final ConfigurationOverviewData target = new ConfigurationOverviewData();
		classUnderTest.populate(source, target);

		assertNotNull(target);
		assertNotNull(target.getGroups());
		final CharacteristicGroup theOnlyGroup = target.getGroups().get(0);
		assertEquals("We expect one artificial group with id: ", "_GEN", theOnlyGroup.getId());
		assertEquals("We expect one artificial group with dummy description: ", "[_GEN]", theOnlyGroup.getGroupDescription());

		final List<CharacteristicValue> csticValues = theOnlyGroup.getCharacteristicValues();
		assertEquals("We expect 5 values: ", 5, csticValues.size());

		final CharacteristicValue value1 = csticValues.get(0);
		assertEquals("We expect cstic description: ", CSTIC_COLOR, value1.getCharacteristic());
		assertEquals("We expect value: ", VALUE_RED, value1.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.ONLY_VALUE, value1.getValuePositionType());

		final CharacteristicValue value2 = csticValues.get(1);
		assertEquals("We expect cstic description: ", CSTIC_ENGINE, value2.getCharacteristic());
		assertEquals("We expect value: ", VALUE_HYBRID, value2.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.ONLY_VALUE, value2.getValuePositionType());

		final CharacteristicValue value3 = csticValues.get(2);
		assertEquals("We expect cstic description: ", CSTIC_ACC, value3.getCharacteristic());
		assertEquals("We expect value: ", VALUE_RADIO, value3.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.FIRST, value3.getValuePositionType());

		final CharacteristicValue value4 = csticValues.get(3);
		assertEquals("We expect cstic description: ", CSTIC_ACC, value4.getCharacteristic());
		assertEquals("We expect value: ", VALUE_CUP, value4.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.INTERJACENT, value4.getValuePositionType());

		final CharacteristicValue value5 = csticValues.get(4);
		assertEquals("We expect cstic description: ", CSTIC_ACC, value4.getCharacteristic());
		assertEquals("We expect value: ", VALUE_NAVI, value5.getValue());
		assertEquals("We expect value position type: ", ValuePositionTypeEnum.LAST, value5.getValuePositionType());
	}

}
