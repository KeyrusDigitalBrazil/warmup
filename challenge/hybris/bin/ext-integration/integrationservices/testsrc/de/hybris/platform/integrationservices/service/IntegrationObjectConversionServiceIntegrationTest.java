/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

@IntegrationTest
public class IntegrationObjectConversionServiceIntegrationTest extends ServicelayerTest
{
	@Resource(name = "integrationObjectConversionService")
	private IntegrationObjectConversionService conversionService;

	@Resource
	private UserService userService;

	@Resource
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private I18NService i18NService;

	private static final String INTEGRATION_OBJECT = "ProductIntegrationObject";
	private static final String CATALOG_ID = "Default";
	private static final String CATALOG_VERSION = "Staged";

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/test/integrationservices-conversionservices.impex", "UTF-8");
		i18NService.setCurrentLocale(Locale.FRENCH);
	}

	@Test
	public void shouldReturnEmptyValue_whenAttributeContainsNoValue()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION);

		final Map<String, Object> catalog = conversionService.convert(catalogVersion.getCatalog(), INTEGRATION_OBJECT);

		assertThat(catalog).containsOnly(
				entry("id", CATALOG_ID),
				//Collection of String
				entry("urlPatterns", Arrays.asList("url1", "url2")),
				entry("integrationKey", "Default"));
	}

	@Test
	public void shouldReturnIntegrationObjectMap_whenAttributeIsEnumType()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = new ClassAttributeAssignmentModel();
		classAttributeAssignment.setAttributeType(ClassificationAttributeTypeEnum.STRING);

		final ClassAttributeAssignmentModel model = flexibleSearchService.getModelByExample(classAttributeAssignment);

		final Map<String, Object> map = conversionService.convert(model, "ClassAttributeAssignment");

		final Map<String, Object> attributeType = (Map<String, Object>) map.get("attributeType");
		assertThat(attributeType).containsOnly(
				entry("code", "string"), entry("codex", "string")
		);
	}

	@Test
	public void shouldReturnIntegrationObjectMap()
	{
		final String productCode = "testProduct";
		final ProductModel testProduct =
				productService.getProductForCode(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION), productCode);

		final Map<String, Object> product = conversionService.convert(testProduct, INTEGRATION_OBJECT);
		final Map<String, Object> catalogVersion = (Map<String, Object>) product.get("catalogVersion");
		final Map<String, Object> catalog = (Map<String, Object>) catalogVersion.get("catalog");
		final List<Map<String, Object>> superCategories = (List<Map<String, Object>>) product.get("supercategories");

		assertThat(product).contains(
				entry("code", productCode),
				entry("endLineNumber", 1),
				entry("priceQuantity", 1.2),
				entry("name", "fr name for testProduct"),
				entry("integrationKey", "Staged|Default|testProduct"));

		assertThat(catalogVersion).doesNotContainKey("generatorInfo")
								  .contains(
				entry("version", CATALOG_VERSION),
				entry("integrationKey", "Staged|Default"));

		assertThat(catalog).containsOnly(
				entry("id", CATALOG_ID),
				//Collection of String
				entry("urlPatterns", Arrays.asList("url1", "url2")),
				entry("integrationKey", "Default"));

		assertThat(superCategories).hasSize(1);
		final Map<String, Object> testCategory = superCategories.get(0);
		assertThat(testCategory).contains(entry("code", "testCategory"), entry("name", "Test Category France"));

		//Collection of ItemModel
		assertThat((List<Map>)testCategory.get("thumbnails")).extracting("code").containsExactlyInAnyOrder("Media1", "Media2");
	}

	@Test
	public void shouldReturnIntegrationObjectMapWithLocalizedAttributesForLocalizedFields()
	{
		final String productCode = "testProduct";
		final ProductModel testProduct =
				productService.getProductForCode(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION), productCode);

		final Map<String, Object> product = conversionService.convert(testProduct, INTEGRATION_OBJECT);

		//default language populated in localized properties
		assertThat(product).contains(entry("name", "fr name for testProduct"));
		assertThat(product).contains(entry("description", "fr description for testProduct"));

		final List<Map<String, Object>> localizedAttributes = (List<Map<String, Object>>) product.get("localizedAttributes");
		assertThat(localizedAttributes).isNotNull()
										.hasSize(2);
		assertThat(localizedAttributes).containsExactlyInAnyOrder(
				ImmutableMap.of(
						"language", "en",
						"name", "en name for testProduct",
						"description", "en description for testProduct"),
				ImmutableMap.of(
						"language", "fr",
						"name", "fr name for testProduct",
						"description", "fr description for testProduct")
		);
	}

	@Test
	public void emptyLocalizedFieldShouldBeEmptyAndNullLocalizedFieldShouldNotBeIncluded()
	{
		final String productCode = "testProductEs";
		final ProductModel testProduct =
				productService.getProductForCode(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION), productCode);
		// Cannot set value to empty string via impex so have to do it programmatically
		testProduct.setName("", new Locale("es"));
		final Map<String, Object> product = conversionService.convert(testProduct, INTEGRATION_OBJECT);
		final List<Map<String, Object>> localizedAttributes = (List<Map<String, Object>>) product.get("localizedAttributes");

		assertThat(localizedAttributes).isNotNull()
				.hasSize(2);
		assertThat(localizedAttributes).containsExactlyInAnyOrder(
				// en description should not be included
				ImmutableMap.of(
						"language", "en",
						"name", "en name for testProduct"),
				// es name should be empty
				ImmutableMap.of(
						"language", "es",
						"name", "",
						"description", "es description for testProduct")
		);
	}

	@Test
	public void shouldThrowException_whenNoIntegrationObjectFound()
	{
		assertThatThrownBy(() -> conversionService.convert(new ItemModel(), INTEGRATION_OBJECT + "notFound"))
				.isInstanceOf(IntegrationObjectNotFoundException.class);
	}

	@Test
	public void shouldThrowIllegalArgumentException_whenNullIntegrationObjectCode()
	{
		assertThatThrownBy(() -> conversionService.convert(new ItemModel(), null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("integrationObjectCode cannot be null or empty");
	}

	@Test
	public void shouldThrowIllegalArgumentException_whenEmptyIntegrationObjectCode()
	{
		assertThatThrownBy(() -> conversionService.convert(new ItemModel(), ""))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("integrationObjectCode cannot be null or empty");
	}

	@Test
	public void shouldRethrowException_whenNotIntegrationObjectItemMatchFound()
	{
		final EmployeeModel adminUser = userService.getAdminUser();
		assertThatThrownBy(() -> conversionService.convert(adminUser, INTEGRATION_OBJECT))
				.isInstanceOf(ModelNotFoundException.class)
				.hasMessage("The Integration Object Definition of 'ProductIntegrationObject' was not found");
	}

	@Test
	public void shouldRethrowException_whenHasMoreThanOneIntegrationObjectItemMatchFound()
	{
		assertThatThrownBy(() -> conversionService.convert(new ProductModel(), "MoreThanOneMatch"))
				.isInstanceOf(AmbiguousIdentifierException.class)
				.hasMessage("The Integration Object and the ItemModel class provided have more than one match, "
						+ "please adjust the Integration Object definition of 'MoreThanOneMatch'");
	}
}
