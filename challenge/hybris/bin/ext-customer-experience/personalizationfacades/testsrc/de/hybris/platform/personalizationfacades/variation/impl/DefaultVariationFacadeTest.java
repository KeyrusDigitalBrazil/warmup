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
package de.hybris.platform.personalizationfacades.variation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.personalizationfacades.converters.ConfigurableConverter;
import de.hybris.platform.personalizationfacades.data.VariationData;
import de.hybris.platform.personalizationfacades.enums.VariationConversionOptions;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.variation.CxVariationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultVariationFacadeTest
{

	private static final String VARIATION_ID = "variation";
	private static final String VARIATION_NAME = "variationName";
	private static final String NOTEXISTING_VARIATION_ID = "nonExistVariation";
	private static final String NEW_VARIATION_ID = "newVariation";

	private static final String CATALOG_ID = "c1";
	private static final String CATALOG_VERSION_STAGE_ID = "stage";
	private static final String CUSTOMIZATION_ID = "customization";
	private static final String CUSTOMIZATION_NAME = "customizationName";
	private static final String NOTEXISTING_CUSTOMIZATION_ID = "notExistingCustomization";

	private final DefaultVariationFacade variationFacade = new DefaultVariationFacade();
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CxCustomizationService customizationService;
	@Mock
	private CxVariationService variationService;
	@Mock
	private ConfigurableConverter<CxVariationModel, VariationData, VariationConversionOptions> variationConverter;
	@Mock
	private CatalogVersionModel catalogVersionStage;
	@Mock
	private CatalogVersionModel catalogVersionOnline;
	@Mock
	private Converter<VariationData, CxVariationModel> variationReverseConverter;

	private CxCustomizationModel customization;
	private CxVariationModel variation;
	private CxVariationModel variationOnline;
	private VariationData variationData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		variationFacade.setModelService(modelService);
		variationFacade.setCustomizationService(customizationService);
		variationFacade.setVariationService(variationService);
		variationFacade.setVariationConverter(variationConverter);
		variationFacade.setCatalogVersionService(catalogVersionService);
		variationFacade.setVariationReverseConverter(variationReverseConverter);

		variation = new CxVariationModel();
		variation.setCode(VARIATION_ID);
		variation.setName(VARIATION_NAME);
		variation.setCatalogVersion(catalogVersionStage);
		variationOnline = new CxVariationModel();
		variationOnline.setCode(VARIATION_ID);
		variationOnline.setName(VARIATION_NAME);
		variationOnline.setCatalogVersion(catalogVersionOnline);

		variationData = new VariationData();
		variationData.setCode(VARIATION_ID);
		variationData.setName(VARIATION_NAME);

		customization = new CxCustomizationModel();
		customization.setCode(CUSTOMIZATION_ID);
		customization.setName(CUSTOMIZATION_NAME);
		customization.setVariations(Collections.singletonList(variation));
	}

	//Tests for getVariation
	@Test
	public void getVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		Mockito.when(variationConverter.convert(Mockito.eq(variation), Mockito.anyList())).thenReturn(variationData);

		//when
		final VariationData result = variationFacade.getVariation(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(VARIATION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.getVariation(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getVariationWithNullCatalogTest()
	{
		//given
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.getVariation(CUSTOMIZATION_ID, VARIATION_ID, null, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getVariationForNotExistingCatalogTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion("notExistingCatalog", CATALOG_VERSION_STAGE_ID))
				.thenThrow(new UnknownIdentifierException("CatalogVersion with catalogId 'notExistingCatalog' not found!"));
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.getVariation(CUSTOMIZATION_ID, VARIATION_ID, "notExistingCatalog", CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getVariationWithNullVariationIdTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(null, customization))
				.thenThrow(new IllegalArgumentException("Variation code must not be null"));
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));


		//when
		variationFacade.getVariation(CUSTOMIZATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//Tests for getVariations

	@Test
	public void getVariationsTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		Mockito.when(variationConverter.convertAll(Mockito.anyList(), Matchers.<VariationConversionOptions> anyVararg()))
				.thenReturn(Collections.singletonList(variationData));
		//when
		final List<VariationData> resultList = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		final VariationData result = resultList.get(0);
		Assert.assertEquals(VARIATION_ID, result.getCode());
	}


	@Test
	public void getVariationsForCustomizationWithTwoCatalogVersionTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		customization.setVariations(Arrays.asList(variation, variationOnline));
		Mockito.when(variationConverter.convertAll(Mockito.anyList(), Matchers.<VariationConversionOptions> anyVararg()))
				.thenReturn(Collections.singletonList(variationData));

		//when
		final List<VariationData> resultList = variationFacade.getVariations(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		final VariationData result = resultList.get(0);
		Assert.assertEquals(VARIATION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getVariationsForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		variationFacade.getVariations(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getVariationsForNullParametersTest()
	{
		//when
		variationFacade.getVariations(null, null, null);
	}

	//Tests for create method

	@Test
	public void createVariationsTest()
	{
		//given
		variationData.setCode(NEW_VARIATION_ID);
		variationData.setRank(Integer.valueOf(1));
		variation.setCode(NEW_VARIATION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		Mockito.when(variationService.getVariation(NEW_VARIATION_ID, customization)).thenReturn(Optional.empty());
		Mockito.when(variationReverseConverter.convert(variationData)).thenReturn(variation);
		Mockito.when(variationService.createVariation(variation, customization, variationData.getRank())).thenReturn(variation);
		Mockito.when(variationConverter.convert(Mockito.eq(variation), Mockito.anyList())).thenReturn(variationData);

		//when
		final VariationData result = variationFacade.createVariation(CUSTOMIZATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_VARIATION_ID, result.getCode());
		Mockito.verify(variationService).createVariation(variation, customization, variationData.getRank());
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAltreadyExistedVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.createVariation(CUSTOMIZATION_ID, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createVariationWithNullCustomizationTest()
	{
		//when
		variationFacade.createVariation(null, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createVariationWithNullDataTest()
	{
		//when
		variationFacade.createVariation(CUSTOMIZATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createVariationsForNullParametersTest()
	{
		//when
		variationFacade.createVariation(null, null, null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createVariationsForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		variationFacade.createVariation(NOTEXISTING_CUSTOMIZATION_ID, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//update method tests
	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingVariationTest()
	{
		//given
		variationData.setCode(NOTEXISTING_VARIATION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.updateVariation(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void updateVariationForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		variationFacade.updateVariation(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, variationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateVariationWithNullIdTest()
	{
		//when
		variationFacade.updateVariation(CUSTOMIZATION_ID, null, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateVariationWithNullDataTest()
	{
		//when
		variationFacade.updateVariation(CUSTOMIZATION_ID, VARIATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateVariationWithNullCustomizationTest()
	{
		//when
		variationFacade.updateVariation(null, VARIATION_ID, variationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//delete method tests

	@Test
	public void deleteVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(VARIATION_ID, customization)).thenReturn(Optional.of(variation));
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		variationFacade.deleteVariation(CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Mockito.verify(modelService).remove(variation);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteNotExistingVariationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(variationService.getVariation(NOTEXISTING_VARIATION_ID, customization)).thenReturn(Optional.empty());
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		//when
		variationFacade.deleteVariation(CUSTOMIZATION_ID, NOTEXISTING_VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void deleteVariationForNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		variationFacade.deleteVariation(NOTEXISTING_CUSTOMIZATION_ID, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteVariationWithNullCustomizationTest()
	{
		//when
		variationFacade.deleteVariation(null, VARIATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteVariationWithNullIdTest()
	{
		//when
		variationFacade.deleteVariation(CUSTOMIZATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}
}
