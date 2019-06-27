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
package de.hybris.platform.personalizationcmsweb.queries;

import static de.hybris.platform.personalizationcmsweb.queries.CxReplaceComponentWithContainerExecutor.OLD_COMPONENT_CATALOG;
import static de.hybris.platform.personalizationcmsweb.queries.CxReplaceComponentWithContainerExecutor.OLD_ID;
import static de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants.CATALOG;
import static de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants.CATALOG_VERSION;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.personalizationcmsweb.data.CxCmsComponentContainerData;
import de.hybris.platform.personalizationwebservices.data.CatalogVersionWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;


@UnitTest
public class CxReplaceComponentWithContainerExecutorTest
{
	private static final String COMPONENT_ID = "componentId";
	private static final String SLOT_ID = "slotId";
	private static final String CATALOG_ID = "catalogId";
	private static final String CATALOG_VERSION_ID = "catalogVersionId";
	private static final String PARENT_CATALOG_ID = "parentCatalogId";
	private static final String ONLINE_CATALOG_VERSION = "Online";

	private final CxReplaceComponentWithContainerExecutor replaceComponentWithContainerExecutor = new CxReplaceComponentWithContainerExecutor();
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogService catalogService;
	@Mock
	private Converter<CxCmsComponentContainerModel, CxCmsComponentContainerData> converter;
	@Mock
	private CxCmsComponentContainerModel container;
	@Mock
	private CxCmsComponentContainerModel parentContainer;
	@Mock
	private SimpleCMSComponentModel component;
	@Mock
	private ContentSlotModel slot;
	@Mock
	private CxCmsComponentContainerData containerData;
	@Mock
	private CxCmsComponentContainerData parentContainerData;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionModel parentCatalogVersion;
	@Mock
	private CatalogModel parentCatalog;

	@Before
	public void setup() throws CMSItemNotFoundException
	{
		MockitoAnnotations.initMocks(this);
		replaceComponentWithContainerExecutor.setCmsComponentService(cmsComponentService);
		replaceComponentWithContainerExecutor.setCatalogVersionService(catalogVersionService);
		replaceComponentWithContainerExecutor.setCatalogService(catalogService);
		replaceComponentWithContainerExecutor.setModelService(modelService);
		replaceComponentWithContainerExecutor.setConverter(converter);
		replaceComponentWithContainerExecutor.setFlexibleSearchService(flexibleSearchService);

		Mockito.when(component.getUid()).thenReturn(COMPONENT_ID);
		Mockito.when(component.getCatalogVersion()).thenReturn(catalogVersion);
		Mockito.when(cmsComponentService.getAbstractCMSComponent(Mockito.eq(COMPONENT_ID), Mockito.anyCollection())).thenReturn(
				component);
		Mockito.when(slot.getUid()).thenReturn(SLOT_ID);
		Mockito.when(flexibleSearchService.getModelByExample(Mockito.any())).thenReturn(slot);
		Mockito.when(modelService.create(CxCmsComponentContainerModel.class)).thenReturn(container);
		Mockito.when(converter.convert(container)).thenReturn(containerData);
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_ID)).thenReturn(catalogVersion);
		final ArrayList<AbstractCMSComponentModel> components = new ArrayList<>();
		components.add(component);
		Mockito.when(slot.getCmsComponents()).thenReturn(components);
		Mockito.when(parentContainer.getCatalogVersion()).thenReturn(parentCatalogVersion);
	}

	@Test
	public void validateInputParamsTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		final Errors errors = new MapBindingResult(params, "params");

		//when
		replaceComponentWithContainerExecutor.validateInputParams(params, errors);

		//then
		Assert.assertEquals(errors.getErrorCount(), 0);
	}

	@Test
	public void validateInputParamsWhenNoParamsTest()
	{
		//given
		final Map<String, String> params = new HashMap<String, String>();
		final Errors errors = new MapBindingResult(params, "params");

		//when
		replaceComponentWithContainerExecutor.validateInputParams(params, errors);

		//then
		Assert.assertEquals(errors.getErrorCount(), 4);
		assertFieldErrorsContains(errors.getFieldErrors(), OLD_ID);
		assertFieldErrorsContains(errors.getFieldErrors(), SLOT_ID);
		assertFieldErrorsContains(errors.getFieldErrors(), CATALOG);
		assertFieldErrorsContains(errors.getFieldErrors(), CATALOG_VERSION);
	}

	private void assertFieldErrorsContains(final List<FieldError> fieldErrors, final String fieldName)
	{
		Assert.assertTrue(fieldErrors.stream().anyMatch(e -> e.getField().equals(fieldName)));
	}

	@Test
	public void executeAfterValidationTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);

		//when
		final Object result = replaceComponentWithContainerExecutor.executeAfterValidation(params);

		//then
		Assert.assertEquals(containerData, result);
	}

	@Test(expected = NotFoundException.class)
	public void executeAfterValidationWhenComponentNotExistTest() throws CMSItemNotFoundException
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		Mockito.when(cmsComponentService.getAbstractCMSComponent(Mockito.eq(COMPONENT_ID), Mockito.anyCollection())).thenThrow(
				new CMSItemNotFoundException("Component not found"));

		//when
		replaceComponentWithContainerExecutor.executeAfterValidation(params);
	}

	@Test(expected = NotFoundException.class)
	public void executeAfterValidationWhenComponentNotInSlotTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		Mockito.when(slot.getCmsComponents()).thenReturn(Collections.emptyList());

		//when
		replaceComponentWithContainerExecutor.executeAfterValidation(params);
	}

	@Test
	public void executeAfterValidationWhenComponentFromParentCatalogTest() throws CMSItemNotFoundException
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		params.put(OLD_COMPONENT_CATALOG, PARENT_CATALOG_ID);
		Mockito.when(catalogService.getCatalogForId(PARENT_CATALOG_ID)).thenReturn(parentCatalog);
		Mockito.when(parentCatalog.getActiveCatalogVersion()).thenReturn(parentCatalogVersion);

		//when
		final Object result = replaceComponentWithContainerExecutor.executeAfterValidation(params);

		//then
		Assert.assertEquals(containerData, result);
		final ArgumentCaptor<Collection> argument = ArgumentCaptor.forClass(Collection.class);
		Mockito.verify(cmsComponentService).getAbstractCMSComponent(Mockito.eq(COMPONENT_ID), argument.capture());
		final Collection catalogVersions = argument.getValue();
		assertTrue(catalogVersions.contains(parentCatalogVersion));
	}

	@Test
	public void executeAfterValidationWhenComponentInParentContainerTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		final ArrayList<AbstractCMSComponentModel> components = new ArrayList<>();
		components.add(parentContainer);
		Mockito.when(slot.getCmsComponents()).thenReturn(components);
		Mockito.when(parentContainer.getDefaultCmsComponent()).thenReturn(component);

		//when
		final Object result = replaceComponentWithContainerExecutor.executeAfterValidation(params);

		//then
		Assert.assertEquals(containerData, result);
	}

	@Test
	public void executeAfterValidationWhenComponentAlreadyInContainerTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		final ArrayList<AbstractCMSComponentModel> components = new ArrayList<>();
		components.add(container);
		Mockito.when(slot.getCmsComponents()).thenReturn(components);
		Mockito.when(container.getDefaultCmsComponent()).thenReturn(component);

		//when
		final Object result = replaceComponentWithContainerExecutor.executeAfterValidation(params);

		//then
		Assert.assertEquals(containerData, result);
	}


	@Test
	public void getCatalogsForWriteAccessTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);

		//when
		final List<CatalogVersionWsDTO> catalogs = replaceComponentWithContainerExecutor.getCatalogsForWriteAccess(params);

		//then
		Assert.assertNotNull(catalogs);
		Assert.assertTrue(catalogs.size() == 1);
		final CatalogVersionWsDTO catalogVersion = catalogs.get(0);
		Assert.assertEquals(CATALOG_ID, catalogVersion.getCatalog());
		Assert.assertEquals(CATALOG_VERSION_ID, catalogVersion.getVersion());
	}

	@Test
	public void getCatalogsForReadAccessTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);

		//when
		final List<CatalogVersionWsDTO> catalogs = replaceComponentWithContainerExecutor.getCatalogsForReadAccess(params);

		//then
		Assert.assertNotNull(catalogs);
		Assert.assertTrue(catalogs.size() == 1);
		final CatalogVersionWsDTO catalogVersion = catalogs.get(0);
		Assert.assertEquals(CATALOG_ID, catalogVersion.getCatalog());
		Assert.assertEquals(CATALOG_VERSION_ID, catalogVersion.getVersion());
	}

	@Test
	public void getCatalogsForReadAccessWhenComponentFromParentTest()
	{
		//given
		final Map<String, String> params = createParams(COMPONENT_ID, SLOT_ID, CATALOG_ID, CATALOG_VERSION_ID);
		params.put(OLD_COMPONENT_CATALOG, PARENT_CATALOG_ID);
		Mockito.when(catalogService.getCatalogForId(PARENT_CATALOG_ID)).thenReturn(parentCatalog);
		Mockito.when(parentCatalog.getActiveCatalogVersion()).thenReturn(parentCatalogVersion);
		Mockito.when(parentCatalogVersion.getVersion()).thenReturn(ONLINE_CATALOG_VERSION);

		//when
		final List<CatalogVersionWsDTO> catalogs = replaceComponentWithContainerExecutor.getCatalogsForReadAccess(params);

		//then
		Assert.assertNotNull(catalogs);
		Assert.assertTrue(catalogs.size() == 2);
		CatalogVersionWsDTO catalogVersion = catalogs.get(0);
		Assert.assertEquals(CATALOG_ID, catalogVersion.getCatalog());
		Assert.assertEquals(CATALOG_VERSION_ID, catalogVersion.getVersion());
		catalogVersion = catalogs.get(1);
		Assert.assertEquals(PARENT_CATALOG_ID, catalogVersion.getCatalog());
		Assert.assertEquals(ONLINE_CATALOG_VERSION, catalogVersion.getVersion());
	}

	private Map<String, String> createParams(final String componentId, final String slotId, final String catalogId,
			final String catalogVersionId)
	{
		final Map<String, String> params = new HashMap<String, String>();
		params.put(OLD_ID, componentId);
		params.put(SLOT_ID, slotId);
		params.put(CATALOG, catalogId);
		params.put(CATALOG_VERSION, catalogVersionId);
		return params;

	}

}
