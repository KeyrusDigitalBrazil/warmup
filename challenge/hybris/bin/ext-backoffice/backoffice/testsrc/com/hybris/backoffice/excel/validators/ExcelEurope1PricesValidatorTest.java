/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.PriceRowChannel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.translators.ExcelEurope1PricesTypeTranslator;
import com.hybris.backoffice.excel.util.DefaultExcelDateUtils;
import com.hybris.backoffice.excel.util.ExcelDateUtils;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class ExcelEurope1PricesValidatorTest
{
	public static final String EXISTING_USER = "existingUser";
	public static final String EXISTING_CHANNEL = "existingChannel";
	public static final String EXISTING_PRICE_GROUP = "existingPriceGroup";
	public static final String EXISTING_UNIT = "existingUnit";
	public static final String CORRECT_PRICE_QUANTITY = "10 " + EXISTING_UNIT;
	public static final String CORRECT_PRICE_CURRENCY = "10 usd";
	public static final String NOT_EXISTING_UPG_USER = "abc";
	public static final String NON_EXISTING_CHANNEL = "anyChannel";
	public static final String NOT_BLANK = "not blank";
	@Mock
	private CurrencyDao currencyDao;
	@Mock
	private UnitService unitService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private UserService userService;
	@Spy
	private final ExcelDateUtils excelDateUtils = new DefaultExcelDateUtils();

	@InjectMocks
	private ExcelEurope1PricesValidator excelPriceValidator;
	private Date dateFrom;
	private Date dateTo;

	@Before
	public void setup()
	{
		dateFrom = Date.from(LocalDateTime.of(2017, 10, 23, 10, 46).toInstant(ZoneOffset.UTC));
		dateTo = Date.from(LocalDateTime.of(2017, 12, 11, 4, 22).toInstant(ZoneOffset.UTC));
		final CurrencyModel usdModel = mock(CurrencyModel.class);
		final CurrencyModel euroModel = mock(CurrencyModel.class);
		given(usdModel.getIsocode()).willReturn("usd");
		given(euroModel.getIsocode()).willReturn("euro");
		given(currencyDao.findCurrencies()).willReturn(Arrays.asList(usdModel, euroModel));

		final UserModel user = mock(UserModel.class);
		given(userService.getUserForUID(EXISTING_USER)).willReturn(user);
		given(userService.getUserForUID(NOT_EXISTING_UPG_USER)).willThrow(new UnknownIdentifierException(""));

		final PriceRowChannel channel = mock(PriceRowChannel.class);
		given(enumerationService.getEnumerationValue(PriceRowChannel.class, NON_EXISTING_CHANNEL))
				.willThrow(new UnknownIdentifierException(""));
		given(enumerationService.getEnumerationValue(PriceRowChannel.class, EXISTING_CHANNEL)).willReturn(channel);

		final UserPriceGroup priceGroup = mock(UserPriceGroup.class);
		given(enumerationService.getEnumerationValue(UserPriceGroup.class, NOT_EXISTING_UPG_USER))
				.willThrow(new UnknownIdentifierException(""));
		given(enumerationService.getEnumerationValue(UserPriceGroup.class, EXISTING_USER))
				.willThrow(new UnknownIdentifierException(""));
		given(enumerationService.getEnumerationValue(UserPriceGroup.class, EXISTING_PRICE_GROUP)).willReturn(priceGroup);

		final UnitModel unit = mock(UnitModel.class);
		when(unit.getCode()).thenReturn(EXISTING_UNIT);
		given(unitService.getAllUnits()).willReturn(Sets.newHashSet(unit));
	}

	@Test
	public void shouldHandleWhenDescriptorTypeIsPriceRow()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel typeModel = mock(CollectionTypeModel.class);
		final TypeModel elementTypeModel = mock(TypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(typeModel);
		given(typeModel.getElementType()).willReturn(elementTypeModel);
		given(elementTypeModel.getCode()).willReturn(PriceRowModel._TYPECODE);

		// when
		final boolean canHandle = excelPriceValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenCellIsEmpty()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "", null, new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel typeModel = mock(CollectionTypeModel.class);
		final TypeModel elementTypeModel = mock(TypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(typeModel);
		given(typeModel.getElementType()).willReturn(elementTypeModel);
		given(elementTypeModel.getCode()).willReturn(PriceRowModel._TYPECODE);

		// when
		final boolean canHandle = excelPriceValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotHandleWhenDescriptorTypeIsNotPriceRow()
	{
		// given
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				new ArrayList<>());
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final CollectionTypeModel typeModel = mock(CollectionTypeModel.class);
		final TypeModel elementTypeModel = mock(TypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(typeModel);
		given(typeModel.getElementType()).willReturn(elementTypeModel);
		given(elementTypeModel.getCode()).willReturn(PriceRowModel._PRODUCT2OWNEUROPE1PRICES);

		// when
		final boolean canHandle = excelPriceValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldReturnPriceFormatError()
	{
		testPricesIncorrectFormat("10");
		testPricesIncorrectFormat("usd");
		testPricesIncorrectFormat("usd 10");
		testPricesIncorrectFormat("10.usd");
		testPricesIncorrectFormat("-10 usd");
	}

	public void testPricesIncorrectFormat(final String priceCurrencyValue)
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.PRICE_CURRENCY, priceCurrencyValue);

		// when
		final ExcelValidationResult validationCellResult = validateWithParams(params);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_INCORRECT_PRICE_CURRENCY);
	}

	@Test
	public void shouldNotReturnPriceFormatError()
	{
		testPricesHasCorrectFormat("10 usd");
		testPricesHasCorrectFormat("4.3 usd");
		testPricesHasCorrectFormat("4.3   usd");
		testPricesHasCorrectFormat("333.3usd");
	}

	public void testPricesHasCorrectFormat(final String priceCurrencyValue)
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.PRICE_CURRENCY, priceCurrencyValue);

		// when
		final ExcelValidationResult validationCellResult = validateWithParams(params);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReturnNonExistingCurrencyError()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.PRICE_CURRENCY, "10 pln");
		// when
		final ExcelValidationResult validationCellResult = validateWithParams(params);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_CURRENCY_DOESNT_EXIST);
	}

	@Test
	public void shouldReturnErrorWhenNetValueIsInCorrect()
	{
		testNetGrossIncorrect("B");
		testNetGrossIncorrect("Gross");
		testNetGrossIncorrect(" N");
		testNetGrossIncorrect("N ");
	}

	public void testNetGrossIncorrect(final String netGross)
	{
		// given
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(ExcelEurope1PricesTypeTranslator.NET_GROSS,
				netGross);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_INVALID_NET_GROSS);
	}

	@Test
	public void shouldNotReturnErrorWhenNetValueIsCorrect()
	{
		testNetGrossCorrect("N");
		testNetGrossCorrect("G");
	}

	public void testNetGrossCorrect(final String netGross)
	{
		// given
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(ExcelEurope1PricesTypeTranslator.NET_GROSS,
				netGross);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldNotReturnErrorWhenQuantityUnitIsCorrect()
	{
		testQuantityUnitIsCorrect("10 " + EXISTING_UNIT);
		testQuantityUnitIsCorrect("10" + EXISTING_UNIT);
		testQuantityUnitIsCorrect("2" + EXISTING_UNIT);
		testQuantityUnitIsCorrect("2 " + EXISTING_UNIT);
	}

	public void testQuantityUnitIsCorrect(final String quantityUnit)
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(
				ExcelEurope1PricesTypeTranslator.QUANTITY_UNIT, quantityUnit);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReturnErrorWhenQuantityUnitIsIncorrect()
	{
		testQuantityUnitIsIncorrect("10 cows", ExcelEurope1PricesValidator.VALIDATION_INCORRECT_UNIT);
		testQuantityUnitIsIncorrect("10_ cows", ExcelEurope1PricesValidator.VALIDATION_INCORRECT_QUANTITY_UNIT);
		testQuantityUnitIsIncorrect("-10 " + EXISTING_UNIT, ExcelEurope1PricesValidator.VALIDATION_INCORRECT_QUANTITY_UNIT);
		testQuantityUnitIsIncorrect("1.1 " + EXISTING_UNIT, ExcelEurope1PricesValidator.VALIDATION_INCORRECT_QUANTITY_UNIT);
		testQuantityUnitIsIncorrect("0 " + EXISTING_UNIT, ExcelEurope1PricesValidator.VALIDATION_INCORRECT_QUANTITY_LOWE_THAN_ONE);
	}

	public void testQuantityUnitIsIncorrect(final String quantityUnit, final String validationMsg)
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(
				ExcelEurope1PricesTypeTranslator.QUANTITY_UNIT, quantityUnit);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey()).isEqualTo(validationMsg);
	}

	@Test
	public void shouldNotReturnErrorWhenGivenUserPriceGroupExistsOrUser()
	{
		testGivenUserOrUserGroupExists(EXISTING_USER);
		testGivenUserOrUserGroupExists(EXISTING_PRICE_GROUP);
	}

	public void testGivenUserOrUserGroupExists(final String userPriceGroupOrUser)
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.QUANTITY_UNIT, CORRECT_PRICE_QUANTITY);
		params.put(ExcelEurope1PricesTypeTranslator.USER_OR_UPG, userPriceGroupOrUser);

		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(params);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReturnErrorWhenGivenUserPriceGroupDoesNotExist()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.QUANTITY_UNIT, CORRECT_PRICE_QUANTITY);
		params.put(ExcelEurope1PricesTypeTranslator.USER_OR_UPG, NOT_EXISTING_UPG_USER);

		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(params);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_INCORRECT_USER_OR_USER_PRICE_GROUP);
	}

	@Test
	public void shouldReturnErrorWhenUserGroupDefinedButNoPriceQuantity()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelEurope1PricesTypeTranslator.USER_OR_UPG, EXISTING_PRICE_GROUP);

		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(params);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_QUANTITY_UNIT_CANNOT_BE_EMPTY_WHEN_USER_DEFINED);
	}

	@Test
	public void shouldReturnErrorChannelDoesNotExist()
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(ExcelEurope1PricesTypeTranslator.CHANNEL,
				NON_EXISTING_CHANNEL);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_NO_SUCH_CHANNEL);
	}

	@Test
	public void shouldNotReturnErrorChannelExists()
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(ExcelEurope1PricesTypeTranslator.CHANNEL,
				EXISTING_CHANNEL);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReturnErrorWhenDateHasWrongFormat()
	{
		testIncorrectDateRangeFormat(dateFrom + " to " + dateTo);

		final String start = excelDateUtils.exportDate(dateFrom);
		final String end = excelDateUtils.exportDate(dateTo);
		testIncorrectDateRangeFormat(start + end);
	}

	protected void testIncorrectDateRangeFormat(final String dateRange)
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(excelDateUtils.getDateRangeParamKey(),
				dateRange);

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_INCORRECT_DATE_RANGE);
	}

	@Test
	public void shouldNotReturnErrorWhenDateIsOk()
	{
		final String start = excelDateUtils.exportDate(dateFrom);
		final String end = excelDateUtils.exportDate(dateTo);
		testCorrectDateRangeFormat(start + " to " + end);
		testCorrectDateRangeFormat(start + "to " + end);
		testCorrectDateRangeFormat(start + " to" + end);
		testCorrectDateRangeFormat(start + "to" + end);
	}

	protected void testCorrectDateRangeFormat(final String dateRange)
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(excelDateUtils.getDateRangeParamKey(),
				dateRange);

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
	}

	@Test
	public void shouldReturnErrorWhenStartDateIsAfterEndDate()
	{
		// when
		final ExcelValidationResult validationCellResult = validateWithCorrectPriceAnd(excelDateUtils.getDateRangeParamKey(),
				excelDateUtils.exportDate(dateTo) + " to " + excelDateUtils.exportDate(dateFrom));

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(ExcelEurope1PricesValidator.VALIDATION_START_DATE_AFTER_END_DATE);
	}

	protected ExcelValidationResult validateWithCorrectPriceAnd(final String param, final String value)
	{
		final Map<String, String> params = new HashMap<>();
		params.put(param, value);
		return validateWithCorrectPriceAnd(params);
	}

	protected ExcelValidationResult validateWithCorrectPriceAnd(final Map<String, String> params)
	{
		params.put(ExcelEurope1PricesTypeTranslator.PRICE_CURRENCY, CORRECT_PRICE_CURRENCY);
		return validateWithParams(params);
	}

	protected ExcelValidationResult validateWithParams(final Map<String, String> params)
	{
		final List<Map<String, String>> parametersList = new ArrayList<>();
		parametersList.add(params);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, NOT_BLANK, null,
				parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);

		// when
		return excelPriceValidator.validate(importParameters, attributeDescriptor, new HashMap<>());
	}
}
