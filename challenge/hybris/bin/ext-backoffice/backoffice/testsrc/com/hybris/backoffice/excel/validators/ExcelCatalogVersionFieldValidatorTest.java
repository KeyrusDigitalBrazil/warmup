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

import static com.hybris.backoffice.excel.validators.ExcelCatalogVersionValidator.VALIDATION_CATALOG_DOESNT_EXIST;
import static com.hybris.backoffice.excel.validators.ExcelCatalogVersionValidator.VALIDATION_CATALOG_EMPTY;
import static com.hybris.backoffice.excel.validators.ExcelCatalogVersionValidator.VALIDATION_CATALOG_VERSION_DOESNT_EXIST;
import static com.hybris.backoffice.excel.validators.ExcelCatalogVersionValidator.VALIDATION_CATALOG_VERSION_DOESNT_MATCH;
import static com.hybris.backoffice.excel.validators.ExcelCatalogVersionValidator.VALIDATION_CATALOG_VERSION_EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(MockitoJUnitRunner.class)
public class ExcelCatalogVersionFieldValidatorTest
{

	public static final String NOT_EXISTING_CATALOG = "notExistingCatalog";
	public static final String SECOND_A_VERSION = "secondAVersion";
	public static final String FIRST_B_VERSION = "firstBVersion";
	public static final String FIRST_CATALOG = "firstCatalog";
	public static final String SECOND_CATALOG = "secondCatalog";
	public static final String FIRST_A_VERSION = "firstAVersion";
	public static final String NOT_EXISTING_VERSION = "Not existing version";
	public static final String FIRST_CATALOG_FIRST_BVERSION = "firstCatalog:firstBVersion";

	@Mock
	private TypeService typeService;

	@Mock
	private UserService userService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@InjectMocks
	private ExcelCatalogVersionValidator excelCatalogVersionValidator;

	@Before
	public void setup()
	{
		final CatalogModel firstCatalog = mock(CatalogModel.class);
		final CatalogModel secondCatalog = mock(CatalogModel.class);


		when(firstCatalog.getId()).thenReturn(FIRST_CATALOG);
		when(secondCatalog.getId()).thenReturn(SECOND_CATALOG);

		final CatalogVersionModel firstAVersion = mock(CatalogVersionModel.class);
		final CatalogVersionModel firstBVersion = mock(CatalogVersionModel.class);
		final CatalogVersionModel secondAVersion = mock(CatalogVersionModel.class);

		when(catalogVersionService.getAllWritableCatalogVersions(any()))
				.thenReturn(Arrays.asList(firstAVersion, firstBVersion, secondAVersion));
		when(firstAVersion.getVersion()).thenReturn(FIRST_A_VERSION);
		when(firstAVersion.getCatalog()).thenReturn(firstCatalog);

		when(firstBVersion.getVersion()).thenReturn(FIRST_B_VERSION);
		when(firstBVersion.getCatalog()).thenReturn(firstCatalog);

		when(secondAVersion.getVersion()).thenReturn(SECOND_A_VERSION);
		when(secondAVersion.getCatalog()).thenReturn(secondCatalog);

		when(firstCatalog.getCatalogVersions()).thenReturn(Sets.newHashSet(firstAVersion, firstBVersion));
		when(secondCatalog.getCatalogVersions()).thenReturn(Sets.newHashSet(secondAVersion));
	}

	@Test
	public void shouldHandleWhenParamsContainsCatalogAndVersion()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> singleParams = new HashMap<>();
		parametersList.add(singleParams);
		singleParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		singleParams.put(CatalogVersionModel.VERSION, FIRST_A_VERSION);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notBlank", null,
				parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final boolean canHandle = excelCatalogVersionValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldNotHandleWhenParamsDoesNotContainsCatalogAndVersion()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> singleParams = new HashMap<>();
		parametersList.add(singleParams);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "notBlank", null,
				parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final boolean canHandle = excelCatalogVersionValidator.canHandle(importParameters, attributeDescriptor);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldNotReturnValidationErrorWhenParamsContainsExistingCatalogsAndVersions()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		firstParams.put(CatalogVersionModel.VERSION, FIRST_B_VERSION);

		final Map<String, String> secondParams = new HashMap<>();
		parametersList.add(secondParams);
		secondParams.put(CatalogVersionModel.CATALOG, SECOND_CATALOG);
		secondParams.put(CatalogVersionModel.VERSION, SECOND_A_VERSION);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null,
				"firstCatalog:firstBVersion,secondCatalog:secondAVersion", null, parametersList);
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final ExcelValidationResult validationCellResult = excelCatalogVersionValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		assertThat(validationCellResult.hasErrors()).isFalse();
		assertThat(validationCellResult.getValidationErrors()).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenCatalogIsEmpty()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, null);
		firstParams.put(CatalogVersionModel.VERSION, FIRST_B_VERSION);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, ":firstBVersion", null,
				parametersList);

		// when
		testCatalogValidation(importParameters, new ValidationMessage(VALIDATION_CATALOG_EMPTY));
	}

	@Test
	public void shouldReturnValidationErrorWhenParamsDoesNotContainsExistingCatalog()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		firstParams.put(CatalogVersionModel.VERSION, FIRST_B_VERSION);

		final Map<String, String> secondParams = new HashMap<>();
		parametersList.add(secondParams);
		secondParams.put(CatalogVersionModel.CATALOG, NOT_EXISTING_CATALOG);
		secondParams.put(CatalogVersionModel.VERSION, SECOND_A_VERSION);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, FIRST_CATALOG_FIRST_BVERSION,
				null, parametersList);

		// when
		testCatalogValidation(importParameters, new ValidationMessage(VALIDATION_CATALOG_DOESNT_EXIST, NOT_EXISTING_CATALOG));
	}


	@Test
	public void shouldReturnValidationErrorWhenCatalogVersionIsEmpty()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		firstParams.put(CatalogVersionModel.VERSION, null);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, "firstCatalog:", null,
				parametersList);

		// when
		testCatalogValidation(importParameters, new ValidationMessage(VALIDATION_CATALOG_VERSION_EMPTY));
	}

	@Test
	public void shouldReturnValidationErrorWhenParamsDoesNotContainsExistingVersions()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		firstParams.put(CatalogVersionModel.VERSION, FIRST_B_VERSION);

		final Map<String, String> secondParams = new HashMap<>();
		parametersList.add(secondParams);
		secondParams.put(CatalogVersionModel.CATALOG, SECOND_CATALOG);
		secondParams.put(CatalogVersionModel.VERSION, NOT_EXISTING_VERSION);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, FIRST_CATALOG_FIRST_BVERSION,
				null, parametersList);

		// when
		testCatalogValidation(importParameters,
				new ValidationMessage(VALIDATION_CATALOG_VERSION_DOESNT_EXIST, NOT_EXISTING_VERSION));
	}

	@Test
	public void shouldReturnValidationErrorWhenParamsCatalogAndVersionDoesNotMatch()
	{
		// given
		final List<Map<String, String>> parametersList = new ArrayList<>();
		final Map<String, String> firstParams = new HashMap<>();
		parametersList.add(firstParams);
		firstParams.put(CatalogVersionModel.CATALOG, FIRST_CATALOG);
		firstParams.put(CatalogVersionModel.VERSION, FIRST_B_VERSION);

		final Map<String, String> secondParams = new HashMap<>();
		parametersList.add(secondParams);
		secondParams.put(CatalogVersionModel.CATALOG, SECOND_CATALOG);
		secondParams.put(CatalogVersionModel.VERSION, FIRST_A_VERSION);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, FIRST_CATALOG_FIRST_BVERSION,
				null, parametersList);

		// when
		testCatalogValidation(importParameters,
				new ValidationMessage(VALIDATION_CATALOG_VERSION_DOESNT_MATCH, FIRST_A_VERSION, SECOND_CATALOG));
	}

	protected void testCatalogValidation(final ImportParameters importParameters,
			final ValidationMessage expectedValidationMessage)
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final TypeModel typeModel = mock(TypeModel.class);
		when(attributeDescriptor.getAttributeType()).thenReturn(typeModel);
		when(typeModel.getCode()).thenReturn(Boolean.class.getCanonicalName());

		// when
		final ExcelValidationResult validationCellResult = excelCatalogVersionValidator.validate(importParameters,
				attributeDescriptor, new HashMap<>());

		// then
		assertThat(validationCellResult.hasErrors()).isTrue();
		assertThat(validationCellResult.getValidationErrors()).hasSize(1);
		assertThat(validationCellResult.getValidationErrors().get(0).getMessageKey())
				.isEqualTo(expectedValidationMessage.getMessageKey());
		assertThat(validationCellResult.getValidationErrors().get(0).getParams()).contains(expectedValidationMessage.getParams());
	}

}
