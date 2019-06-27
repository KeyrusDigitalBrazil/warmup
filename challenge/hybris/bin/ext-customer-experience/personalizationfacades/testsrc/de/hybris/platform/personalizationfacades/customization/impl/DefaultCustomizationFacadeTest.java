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
package de.hybris.platform.personalizationfacades.customization.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.personalizationfacades.constants.PersonalizationfacadesConstants;
import de.hybris.platform.personalizationfacades.converters.ConfigurableConverter;
import de.hybris.platform.personalizationfacades.data.CustomizationData;
import de.hybris.platform.personalizationfacades.enums.CustomizationConversionOptions;
import de.hybris.platform.personalizationfacades.exceptions.AlreadyExistsException;
import de.hybris.platform.personalizationservices.customization.CxCustomizationService;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxCustomizationsGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

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
public class DefaultCustomizationFacadeTest
{
	private static final String CUSTOMIZATION_ID = "customization";
	private static final String CUSTOMIZATION_NAME = "customizationName";
	private static final String NOTEXISTING_CUSTOMIZATION_ID = "nonExistingCustomization";
	private static final String NEW_CUSTOMIZATION_ID = "newCustomization";
	private static final String NEW_CUSTOMIZATION_NAME = "newCustomizationName";

	private static final String CATALOG_ID = "c1";
	private static final String CATALOG_VERSION_STAGE_ID = "stage";
	private static final String NOTEXISTING_CATALOG_ID = "notExist";

	private final DefaultCustomizationFacade customizationFacade = new DefaultCustomizationFacade();
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CxCustomizationService customizationService;
	@Mock
	private ConfigurableConverter<CxCustomizationModel, CustomizationData, CustomizationConversionOptions> customizationConverter;
	@Mock
	private Converter<CustomizationData, CxCustomizationModel> customizationReverseConverter;
	@Mock
	private CatalogVersionModel catalogVersionStage;
	@Mock
	private CatalogVersionModel catalogVersionOnline;

	private CxCustomizationModel customization;
	private CxCustomizationModel customizationOnline;
	private CxCustomizationsGroupModel customizationGroupForStagedCV;

	private CustomizationData customizationData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		customizationFacade.setModelService(modelService);
		customizationFacade.setCustomizationService(customizationService);
		customizationFacade.setCatalogVersionService(catalogVersionService);
		customizationFacade.setCustomizationConverter(customizationConverter);
		customizationFacade.setCustomizationReverseConverter(customizationReverseConverter);

		customization = new CxCustomizationModel();
		customization.setCode(CUSTOMIZATION_ID);
		customization.setName(CUSTOMIZATION_NAME);
		customization.setCatalogVersion(catalogVersionStage);
		customizationOnline = new CxCustomizationModel();
		customizationOnline.setCode(CUSTOMIZATION_ID);
		customizationOnline.setCatalogVersion(catalogVersionOnline);

		customizationData = new CustomizationData();
		customizationData.setCode(CUSTOMIZATION_ID);
		customizationData.setName(CUSTOMIZATION_NAME);

		customizationGroupForStagedCV = new CxCustomizationsGroupModel();
		customizationGroupForStagedCV.setCode(PersonalizationfacadesConstants.DEFAULT_CX_CUSTOMIZATION_GROUP_CODE);
		customizationGroupForStagedCV.setCatalogVersion(catalogVersionStage);

		Mockito.when(modelService.create(CxCustomizationsGroupModel.class)).thenReturn(new CxCustomizationsGroupModel());
		Mockito.when(catalogVersionStage.getPk()).thenReturn(PK.fromLong(1l));
		Mockito.when(catalogVersionOnline.getPk()).thenReturn(PK.fromLong(2l));
	}

	//Tests for getCustomization
	@Test
	public void getCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));
		Mockito.when(customizationConverter.convert(Mockito.any(), Mockito.anyList())).thenReturn(customizationData);

		//when
		final CustomizationData result = customizationFacade.getCustomization(CUSTOMIZATION_ID, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		customizationFacade.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCustomizationWithNullCatalogTest()
	{
		//when
		customizationFacade.getCustomization(CUSTOMIZATION_ID, null, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomizationForNotExistingCatalogTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion("notExistingCatalog", CATALOG_VERSION_STAGE_ID))
				.thenThrow(new UnknownIdentifierException("CatalogVersion with catalogId 'notExistingCatalog' not found!"));

		//when
		customizationFacade.getCustomization(CUSTOMIZATION_ID, "notExistingCatalog", CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCustomizationWithNullCustomizationIdTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(null, catalogVersionStage))
				.thenThrow(new IllegalArgumentException("Customization code must not be null"));

		//when
		customizationFacade.getCustomization(null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//Tests for getCustomizations

	@Test
	public void getCustomizationsTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomizations(catalogVersionStage))
				.thenReturn(Collections.singletonList(customization));
		Mockito
				.when(customizationConverter.convertAll(Mockito.eq(Collections.singletonList(customization)),
						Matchers.<CustomizationConversionOptions> anyVararg()))
				.thenReturn(Collections.singletonList(customizationData));

		//when
		final List<CustomizationData> resultList = customizationFacade.getCustomizations(CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(resultList);
		Assert.assertEquals(1, resultList.size());
		final CustomizationData result = resultList.get(0);
		Assert.assertEquals(CUSTOMIZATION_ID, result.getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void getCustomizationsForNotExistingCatalogTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID))
				.thenThrow(new UnknownIdentifierException("Catalog not exist"));

		//when
		customizationFacade.getCustomizations(NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getCustomizationsForNullParametersTest()
	{
		//when
		customizationFacade.getCustomizations(null, null);
	}

	//Tests for create method

	@Test
	public void createCustomizationsTest()
	{
		//given
		customizationData.setCode(NEW_CUSTOMIZATION_ID);
		customizationData.setName(NEW_CUSTOMIZATION_NAME);
		customization.setCode(NEW_CUSTOMIZATION_ID);
		customization.setName(NEW_CUSTOMIZATION_NAME);
		customization.setRank(Integer.valueOf(1));
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NEW_CUSTOMIZATION_ID, catalogVersionStage)).thenReturn(Optional.empty());
		Mockito.when(Boolean.valueOf(customizationService.isDefaultGroup(catalogVersionStage))).thenReturn(Boolean.TRUE);
		Mockito.when(customizationReverseConverter.convert(customizationData)).thenReturn(customization);
		Mockito.when(customizationService.createCustomization(Mockito.eq(customization), Mockito.any(),
				Mockito.eq(customization.getRank()))).thenReturn(customization);
		Mockito.when(customizationConverter.convert(Mockito.any(), Mockito.anyList())).thenReturn(customizationData);

		//when
		final CustomizationData result = customizationFacade.createCustomization(customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_CUSTOMIZATION_ID, result.getCode());
		Mockito.verify(customizationService).createCustomization(Mockito.eq(customization), Mockito.any(),
				Mockito.eq(customization.getRank()));
	}

	@Test
	public void createCustomizationsTestForNoCustomizationGroup()
	{
		//given
		customizationData.setCode(NEW_CUSTOMIZATION_ID);
		customizationData.setName(NEW_CUSTOMIZATION_NAME);
		customization.setCode(NEW_CUSTOMIZATION_ID);
		customization.setName(NEW_CUSTOMIZATION_NAME);
		customization.setRank(Integer.valueOf(1));
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NEW_CUSTOMIZATION_ID, catalogVersionStage)).thenReturn(Optional.empty());
		Mockito.when(Boolean.valueOf(customizationService.isDefaultGroup(catalogVersionStage))).thenReturn(Boolean.FALSE);
		Mockito.when(customizationReverseConverter.convert(customizationData)).thenReturn(customization);
		Mockito.when(customizationService.createCustomization(Mockito.eq(customization), Mockito.any(),
				Mockito.eq(customization.getRank()))).thenReturn(customization);
		Mockito.when(customizationConverter.convert(Mockito.any(), Mockito.anyList())).thenReturn(customizationData);

		//when
		final CustomizationData result = customizationFacade.createCustomization(customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

		//then
		final CxCustomizationsGroupModel createdCustomizationGroup = Mockito.spy(customizationFacade)
				.getOrCreateCustomizationGroup(catalogVersionStage);

		Assert.assertNotNull(result);
		Assert.assertEquals(NEW_CUSTOMIZATION_ID, result.getCode());
		Mockito.verify(customizationService).createCustomization(Mockito.eq(customization), Mockito.eq(createdCustomizationGroup),
				Mockito.eq(customization.getRank()));
	}

	@Test(expected = AlreadyExistsException.class)
	public void createAltreadyExistedCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		customizationFacade.createCustomization(customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createCustomizationWithNullDataTest()
	{
		//when
		customizationFacade.createCustomization(null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createCustomizationsForNullCatalogTest()
	{
		//when
		customizationFacade.createCustomization(customizationData, null, null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void createCustomizationsForNotExistingCatalogTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID))
				.thenThrow(new UnknownIdentifierException("Catalog not exist"));

		//when
		customizationFacade.createCustomization(customizationData, NOTEXISTING_CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//update method tests
	@Test(expected = UnknownIdentifierException.class)
	public void updateNotExistingCustomizationTest()
	{
		//given
		customizationData.setCode(NOTEXISTING_CUSTOMIZATION_ID);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		customizationFacade.updateCustomization(NOTEXISTING_CUSTOMIZATION_ID, customizationData, CATALOG_ID,
				CATALOG_VERSION_STAGE_ID);

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateCustomizationWithNullIdTest()
	{
		//when
		customizationFacade.updateCustomization(null, customizationData, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateCustomizationWithNullDataTest()
	{
		//when
		customizationFacade.updateCustomization(CUSTOMIZATION_ID, null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	//delete method tests
	@Test
	public void removeCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.of(customization));

		//when
		customizationFacade.removeCustomization(CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);

		//then
		Mockito.verify(modelService).remove(customization);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void removeNotExistingCustomizationTest()
	{
		//given
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGE_ID)).thenReturn(catalogVersionStage);
		Mockito.when(customizationService.getCustomization(NOTEXISTING_CUSTOMIZATION_ID, catalogVersionStage))
				.thenReturn(Optional.empty());

		//when
		customizationFacade.removeCustomization(NOTEXISTING_CUSTOMIZATION_ID, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeCustomizationWithNullIdTest()
	{
		//when
		customizationFacade.removeCustomization(null, CATALOG_ID, CATALOG_VERSION_STAGE_ID);
	}
}
