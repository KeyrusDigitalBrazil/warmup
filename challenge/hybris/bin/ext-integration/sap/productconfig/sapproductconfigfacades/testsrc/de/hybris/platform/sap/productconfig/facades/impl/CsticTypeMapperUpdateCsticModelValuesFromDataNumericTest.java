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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigPricingImplTest.DummyPriceDataFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CsticTypeMapperUpdateCsticModelValuesFromDataNumericTest
{
	private CsticTypeMapperImpl typeMapper;

	@Mock
	private ClassificationSystemService classificationService;

	@Mock
	private FlexibleSearchService flexSearch;
	@Mock
	private SearchResult<Object> attrSearchResult;
	@Mock
	private SearchResult<Object> attrValueSearchResult;

	@Mock
	private DefaultBaseStoreService baseStoreService;

	@Mock
	private ClassificationAttributeModel attribute;

	@Mock
	private ClassificationAttributeValueModel attributeValue;

	@Mock
	private BaseStoreModel baseStore;

	private final ClassificationSystemVersionModel classificationVersion = new ClassificationSystemVersionModel();

	@Mock
	private ClassificationSystemModel catalogModel;

	private static final String CHARACTERISTIC_CODE = "SAP_STRING_SIMPLE";

	private Locale formatLocale;

	@Mock
	private I18NService i18nService;

	private CsticData csticData;

	private CsticModelImpl csticModel;

	private NumberFormat formatter;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		typeMapper = new CsticTypeMapperImpl();
		typeMapper.setUiKeyGenerator(new UniqueUIKeyGeneratorImpl());
		typeMapper.setUiTypeFinder(new UiTypeFinderImpl());
		final ValueFormatTranslatorImpl valueFormatTranslater = new ValueFormatTranslatorImpl();
		valueFormatTranslater.setI18NService(i18nService);
		typeMapper.setValueFormatTranslator(valueFormatTranslater);
		final ConfigPricingImpl configPicingFactory = new ConfigPricingImpl();
		typeMapper.setPricingFactory(configPicingFactory);
		final DummyPriceDataFactory dummyFactory = new DummyPriceDataFactory();
		configPicingFactory.setPriceDataFactory(dummyFactory);

		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);

		given(classificationService.getAttributeForCode(eq(classificationVersion), anyString())).willReturn(null);
		given(classificationService.getAttributeValueForCode(eq(classificationVersion), anyString())).willReturn(null);

		given(attribute.getDefaultAttributeValues()).willReturn(Collections.singletonList(attributeValue));

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = new ClassificationSystemCPQAttributesProviderImpl();
		nameProvider.setBaseStoreService(baseStoreService);
		nameProvider.setClassificationService(classificationService);
		nameProvider.setFlexibleSearchService(flexSearch);
		typeMapper.setNameProvider(nameProvider);

		csticData = new CsticData();

		csticModel = new CsticModelImpl();
		csticModel.setValueType(CsticModel.TYPE_FLOAT);
		formatter = DecimalFormat.getNumberInstance(Locale.ENGLISH);
	}

	@Test
	public void testDropDownNumberGerman() throws Exception
	{
		formatLocale = Locale.GERMAN;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.DROPDOWN);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(numberValue, csticModel.getSingleValue());
		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testDropDownAddValueNumberGerman() throws Exception
	{
		formatLocale = Locale.GERMAN;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.DROPDOWN_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);
		csticData.setAdditionalValue("2,0");

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testDropDownAddValueNumberEnglish() throws Exception
	{
		formatLocale = Locale.ENGLISH;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.DROPDOWN_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);
		csticData.setAdditionalValue("2.0");

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testDropDownNumberEnglish() throws Exception
	{
		formatLocale = Locale.ENGLISH;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.DROPDOWN);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}


	// RadioButton
	@Test
	public void testRadioButtonNumberGerman() throws Exception
	{
		formatLocale = Locale.GERMAN;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(numberValue, csticModel.getSingleValue());
		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testRadioButtonAddValueNumberGerman() throws Exception
	{
		formatLocale = Locale.GERMAN;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);
		csticData.setAdditionalValue("2,0");

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testRadioButtonAddValueNumberEnglish() throws Exception
	{
		formatLocale = Locale.ENGLISH;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);
		csticData.setAdditionalValue("2.0");

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testRadioButtonAddValueNoAddValueSetNumberEnglish() throws Exception
	{
		formatLocale = Locale.ENGLISH;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testRadioButtonAddValueNoAddValueSetNumberGerman() throws Exception
	{
		formatLocale = Locale.GERMAN;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

	@Test
	public void testRadioButtonNumberEnglish() throws Exception
	{
		formatLocale = Locale.ENGLISH;
		given(i18nService.getCurrentLocale()).willReturn(formatLocale);

		csticData.setType(UiType.RADIO_BUTTON);
		final String numberValue = "2.0";
		csticData.setValue(numberValue);

		typeMapper.updateCsticModelValuesFromData(csticData, csticModel);

		assertEquals(2, formatter.parse(csticModel.getSingleValue()).intValue());
	}

}
