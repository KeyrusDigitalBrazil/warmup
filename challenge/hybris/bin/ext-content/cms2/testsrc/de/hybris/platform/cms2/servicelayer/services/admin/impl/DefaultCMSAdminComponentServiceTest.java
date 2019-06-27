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
package de.hybris.platform.cms2.servicelayer.services.admin.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.registry.CMSComponentContainerRegistry;
import de.hybris.platform.cms2.servicelayer.daos.CMSComponentDao;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.jalo.type.Type;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultCMSAdminComponentServiceTest
{
	private static final String CODE = "CODE";
	private static final String CONTAINER = "container";
	private static final String TYPE_CODE = "test_paragraph_typecode";
	private static final String TYPE_NAME = "Test Paragraph Type";
	private static final String TEST_UID = "my-paragraph-uid";
	private static final String TEST_NAME = "My Test Paragraph Component";
	private static final String UID = "132456";
	private static final String GENERATED_UID = "comp_" + UID;
	private static final String INVALID_UID = "invalid-uid";

	@InjectMocks
	@Spy
	private DefaultCMSAdminComponentService cmsAdminComponentService;

	@Mock
	private ModelService modelService;
	@Mock
	private SessionService sessionService;
	@Mock
	private TypeService typeService;
	@Mock
	private PersistentKeyGenerator componentUidGenerator;
	@Mock
	private CMSComponentContainerRegistry cmsComponentContainerRegistry;
	@Mock
	private Comparator<AbstractCMSComponentModel> cmsItemCatalogLevelComparator;

	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogVersionModel parentCatalogVersion;
	@Mock
	private AbstractCMSComponentContainerModel containerModel;
	@Mock
	private ContentSlotModel contentSlotModel;
	@Mock
	private CMSComponentContainerStrategy cmsComponentContainerStrategy;
	@Mock
	private TypeModel typeModel;
	@Mock
	private Type typeSource;
	@Mock
	private ComposedTypeModel componentType;
	@Mock
	private CMSComponentDao cmsComponentDao;

	private AbstractCMSComponentModel component;

	@Before
	public void setUp()
	{
		component = new AbstractCMSComponentModel();

		when(composedType.getName()).thenReturn(TYPE_NAME);
		when(composedType.getCode()).thenReturn(TYPE_CODE);

		doReturn(catalogVersion).when(cmsAdminComponentService).getActiveCatalogVersion();

		when(typeService.getComposedTypeForCode(TYPE_CODE)).thenReturn(composedType);
		when(componentUidGenerator.generate()).thenReturn(UID);
		when(modelService.create(TYPE_CODE)).thenReturn(component);
		doNothing().when(modelService).save(component);
		doNothing().when(modelService).refresh(component);

		when(cmsComponentContainerRegistry.getStrategy(containerModel)).thenReturn(cmsComponentContainerStrategy);
		when(cmsComponentContainerStrategy.getDisplayComponentsForContainer(containerModel))
				.thenReturn(Collections.singletonList(component));

		when(typeService.getComposedTypeForClass(Mockito.any())).thenReturn(composedType);
		when(composedType.getCode()).thenReturn(CODE);
		when(typeService.getTypeForCode(CODE)).thenReturn(typeModel);
		when(modelService.getSource(typeModel)).thenReturn(typeSource);
		when(typeSource.getProperty(CONTAINER)).thenReturn(Boolean.FALSE);
	}

	@Test
	public void shouldCreateComponenFromUIDAndNameAndType()
	{
		cmsAdminComponentService.createCmsComponent(null, TEST_UID, TEST_NAME, TYPE_CODE);

		verify(modelService).create(TYPE_CODE);
		verify(modelService).save(component);
		verify(modelService).refresh(component);
		assertEquals(TEST_NAME, component.getName());
		assertEquals(TEST_UID, component.getUid());
		assertEquals(catalogVersion, component.getCatalogVersion());
	}

	@Test
	public void shouldCreateComponentFromUIDAndType()
	{
		cmsAdminComponentService.createCmsComponent(null, TEST_UID, null, TYPE_CODE);

		verify(typeService).getComposedTypeForCode(TYPE_CODE);
		verify(modelService).create(TYPE_CODE);
		verify(modelService).save(component);
		verify(modelService).refresh(component);

		assertEquals(TYPE_NAME, component.getName());
		assertEquals(TEST_UID, component.getUid());
		assertEquals(catalogVersion, component.getCatalogVersion());
	}

	@Test
	public void shouldCreateComponentFromNameAndType()
	{
		cmsAdminComponentService.createCmsComponent(null, null, TEST_NAME, TYPE_CODE);

		verify(modelService).create(TYPE_CODE);
		verify(componentUidGenerator).generate();
		verify(modelService).save(component);
		verify(modelService).refresh(component);

		assertEquals(TEST_NAME, component.getName());
		assertEquals(GENERATED_UID, component.getUid());
		assertEquals(catalogVersion, component.getCatalogVersion());
	}

	@Test
	public void shouldCreateComponentFromType()
	{
		cmsAdminComponentService.createCmsComponent(null, null, null, TYPE_CODE);

		verify(typeService).getComposedTypeForCode(TYPE_CODE);
		verify(modelService).create(TYPE_CODE);
		verify(componentUidGenerator).generate();
		verify(modelService).save(component);
		verify(modelService).refresh(component);

		assertEquals(TYPE_NAME, component.getName());
		assertEquals(GENERATED_UID, component.getUid());
		assertEquals(catalogVersion, component.getCatalogVersion());
	}

	@Test
	public void shouldGetComponentsForContainer()
	{
		final Collection<AbstractCMSComponentModel> components = cmsAdminComponentService
				.getCMSComponentsForContainer(containerModel);
		assertThat(components, contains(component));
	}

	@Test
	public void shouldGetDisplayedComponentsForContentSlot_ContainerPropertyFalse()
	{
		when(contentSlotModel.getCmsComponents()).thenReturn(Collections.singletonList(component));

		final Collection<AbstractCMSComponentModel> components = cmsAdminComponentService
				.getDisplayedComponentsForContentSlot(contentSlotModel);

		assertThat(components, contains(component));
	}

	@Test
	public void shouldGetDisplayedComponentsForContentSlot_ContainerPropertyNull()
	{
		when(contentSlotModel.getCmsComponents()).thenReturn(Collections.singletonList(component));
		when(typeSource.getProperty(CONTAINER)).thenReturn(null);

		final Collection<AbstractCMSComponentModel> components = cmsAdminComponentService
				.getDisplayedComponentsForContentSlot(contentSlotModel);

		assertThat(components, contains(component));
	}

	@Test
	public void shouldGetDisplayedComponentsForContentSlot_ContainerPropertyTrue()
	{
		when(contentSlotModel.getCmsComponents()).thenReturn(Collections.singletonList(containerModel));
		when(typeSource.getProperty(CONTAINER)).thenReturn(Boolean.TRUE);

		final Collection<AbstractCMSComponentModel> components = cmsAdminComponentService
				.getDisplayedComponentsForContentSlot(contentSlotModel);

		assertThat(components, contains(component));
	}

	@Test
	public void shouldGetComponentByIdAndCatalogVersions()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(parentCatalogVersion, catalogVersion);
		when(cmsComponentDao.findCMSComponentsByIdAndCatalogVersions(TEST_UID, catalogVersions))
				.thenReturn(Arrays.asList(component));

		cmsAdminComponentService.getCMSComponentForIdAndCatalogVersions(TEST_UID, catalogVersions);

		verify(cmsComponentDao).findCMSComponentsByIdAndCatalogVersions(TEST_UID, catalogVersions);
	}

	@Test
	public void givenSlotHasContainers_shouldGetAllContainersInSlot()
	{
		// GIVEN
		when(contentSlotModel.getCmsComponents()).thenReturn(Collections.singletonList(containerModel));
		when(typeSource.getProperty(CONTAINER)).thenReturn(Boolean.TRUE);

		// WHEN
		final Collection<AbstractCMSComponentContainerModel> containersList = cmsAdminComponentService
				.getContainersForContentSlot(contentSlotModel);

		// THEN
		assertEquals(containersList.size(), 1);
		assertThat(containersList, contains(containerModel));
	}

	@Test
	public void givenSlotHasNoContainers_shouldGetEmptyList()
	{
		// GIVEN
		when(contentSlotModel.getCmsComponents()).thenReturn(Collections.singletonList(containerModel));
		when(typeSource.getProperty(CONTAINER)).thenReturn(Boolean.FALSE);

		// WHEN
		final Collection<AbstractCMSComponentContainerModel> containersList = cmsAdminComponentService
				.getContainersForContentSlot(contentSlotModel);

		// THEN
		assert (containersList.isEmpty());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldNotGetComponentByIdAndCatalogVersions_InvalidId()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(parentCatalogVersion, catalogVersion);
		when(cmsComponentDao.findCMSComponentsByIdAndCatalogVersions(INVALID_UID, catalogVersions))
				.thenReturn(Collections.emptyList());

		cmsAdminComponentService.getCMSComponentForIdAndCatalogVersions(INVALID_UID, catalogVersions);
	}

	@Test
	public void shouldGetComponentByIdAndCatalogVersions_ManyComponentsFoundWithSameId()
	{
		final List<CatalogVersionModel> catalogVersions = Arrays.asList(parentCatalogVersion, catalogVersion);
		when(cmsComponentDao.findCMSComponentsByIdAndCatalogVersions(TEST_UID, catalogVersions))
				.thenReturn(Arrays.asList(component, component));
		when(cmsItemCatalogLevelComparator.compare(component, component)).thenReturn(0);

		cmsAdminComponentService.getCMSComponentForIdAndCatalogVersions(TEST_UID, catalogVersions);

		verify(cmsComponentDao).findCMSComponentsByIdAndCatalogVersions(TEST_UID, catalogVersions);
		verify(cmsItemCatalogLevelComparator).compare(component, component);
	}
}
