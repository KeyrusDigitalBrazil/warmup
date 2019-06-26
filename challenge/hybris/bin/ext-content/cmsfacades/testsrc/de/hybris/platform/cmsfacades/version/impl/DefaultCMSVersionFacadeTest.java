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
package de.hybris.platform.cmsfacades.version.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.data.CMSVersionSearchData;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.CMSVersionNotFoundException;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.service.CMSVersionSearchService;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.ItemDataPopulatorProvider;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.exception.ValidationError;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.transaction.PlatformTransactionManager;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionFacadeTest
{
	private static final String VALID_ITEM_UID = "valid-item-uid";
	private static final String VALID_ITEM_UUID = "valid-item-uuid";
	private static final String INVALID_ITEM_UUID = "invalid-item-uuid";
	private static final String VALID_VERSION_UID = "valid-version-uid";
	private static final String INVALID_VERSION_UID = "invalid-version-uid";
	private static final String LABEL = "test-label-";
	private static final String DESCRIPTION = "test-description-123";
	public static final String LABEL_KEY = "1234567890";

	@Spy
	@InjectMocks
	private DefaultCMSVersionFacade cmsVersionFacade;

	@Mock
	private ObjectFactory<CMSVersionData> cmsVersionDataDataFactory;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CMSVersionService cmsVersionService;
	@Mock
	private CMSVersionSearchService cmsVersionSearchService;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<CMSVersionModel, CMSVersionData> cmsVersionDataConverter;
	@Mock
	private ObjectFactory<CMSVersionSearchData> cmsVersionSearchDataFactory;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	@Mock
	private CMSItemConverter cmsItemConverter;
	@Mock
	private ItemDataPopulatorProvider itemDataPopulatorProvider;
	@Mock
	private Populator<CMSVersionModel, Map<String, Object>> cmsVersionItemCustomAttributesPopulator;
	@Mock
	private CMSItemModel cmsItemModel;
	@Mock
	private CMSVersionModel cmsVersionModel;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private SearchResult<CMSVersionModel> searchResult;
	@Mock
	private CMSVersionSearchData cmsVersionSearchData;
	@Mock
	private PageableData pageableData;
	@Mock
	private Populator<CMSItemModel, Map<String, Object>> itemDataPopulator;
	@Mock
	private CMSVersionData cmsversionData;
	@Mock
	private PlatformTransactionManager transactionManager;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Before
	public void setUp()
	{
		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier supplier = (Supplier) args[0];
			return supplier.get();
		}).when(sessionSearchRestrictionsDisabler).execute(any());

		doThrow(new TypePermissionException("invalid")).when(cmsVersionFacade).throwTypePermissionException(anyString(),
				anyString());
		when(permissionCRUDService.canChangeType(CMSVersionModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void shouldFindVersionsForItem() throws CMSItemNotFoundException
	{
		doNothing().when(facadeValidationService).validate(Mockito.any(), Mockito.any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionSearchService.findVersions(cmsVersionSearchData, pageableData)).thenReturn(searchResult);
		when(searchResult.getResult()).thenReturn(Collections.singletonList(cmsVersionModel));
		when(cmsVersionSearchDataFactory.getObject()).thenReturn(cmsVersionSearchData);
		when(cmsItemModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(cmsVersionDataDataFactory.getObject()).thenReturn(cmsversionData);
		doReturn(cmsversionData).when(cmsVersionDataDataFactory).getObject();

		cmsVersionFacade.findVersionsForItem(VALID_ITEM_UUID, LABEL, pageableData);

		verify(cmsVersionDataConverter).convert(cmsVersionModel);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailFindVersionsForItem_CMSItemDoesNotExist() throws CMSItemNotFoundException
	{
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());
		when(cmsVersionSearchDataFactory.getObject()).thenReturn(cmsVersionSearchData);
		when(cmsVersionDataDataFactory.getObject()).thenReturn(cmsversionData);

		cmsVersionFacade.findVersionsForItem(INVALID_ITEM_UUID, LABEL, pageableData);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowExceptionWhenUpdateVersionIfUSerDoesNotHaveChangePermission()
	{
		// GIVEN
		when(permissionCRUDService.canChangeType(CMSVersionModel._TYPECODE)).thenReturn(false);

		// WHEN
		cmsVersionFacade.updateVersion(cmsversionData);
	}

	@Test
	public void shouldGetVersionForItem() throws CMSVersionNotFoundException
	{
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionModel.getItemUid()).thenReturn(VALID_ITEM_UID);

		cmsVersionFacade.getVersion(VALID_VERSION_UID);

		verify(cmsVersionDataConverter).convert(cmsVersionModel);
	}

	@Test(expected = CMSVersionNotFoundException.class)
	public void shouldFailGetVersion_CMSVersionDoesNotExist() throws CMSVersionNotFoundException
	{
		when(cmsVersionService.getVersionByUid(INVALID_VERSION_UID)).thenReturn(Optional.empty());

		cmsVersionFacade.getVersion(INVALID_VERSION_UID);
	}

	@Test
	public void shouldCreateVersionForItem()
	{
		doNothing().when(facadeValidationService).validate(Mockito.any(), Mockito.any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));

		cmsVersionFacade.createVersion(createCMSVersionData(VALID_ITEM_UUID, LABEL, DESCRIPTION));

		verify(cmsVersionService).createVersionForItem(cmsItemModel, LABEL, DESCRIPTION);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailCreateVersionForItem_ValidationError()
	{
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());

		cmsVersionFacade.createVersion(createCMSVersionData(INVALID_ITEM_UUID, LABEL, DESCRIPTION));
	}

	@Test
	public void shouldUpdateVersionForItem()
	{
		doNothing().when(facadeValidationService).validate(Mockito.any(), Mockito.any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));

		cmsVersionFacade.updateVersion(createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, LABEL, DESCRIPTION));

		verify(modelService).save(cmsVersionModel);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailUpdateVersionForItem_ValidationError()
	{
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());

		cmsVersionFacade.updateVersion(createCMSVersionData(VALID_VERSION_UID, INVALID_ITEM_UUID, LABEL, DESCRIPTION));
	}

	@Test
	public void shouldCreateVersionWithSpecificKeyGeneratedLabel() throws ParseException
	{
		doNothing().when(facadeValidationService).validate(any(), any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(keyGenerator.generate()).thenReturn(LABEL_KEY);
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		doReturn(DESCRIPTION).when(cmsVersionFacade).getLocalizedDescription(Mockito.any());
		when(permissionCRUDService.canReadType(cmsItemModel.getItemtype())).thenReturn(true);
		when(permissionCRUDService.canChangeType(cmsItemModel.getItemtype())).thenReturn(true);

		cmsVersionFacade.createRollbackAutoVersion(createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null));

		final ArgumentCaptor<String> labelCaptor = ArgumentCaptor.forClass(String.class);
		verify(cmsVersionService).createVersionForItem(any(), labelCaptor.capture(), anyString());
		assertThat(labelCaptor.getValue(), equalTo(CmsfacadesConstants.VERSION_ROLLBACK_LABEL_PREFIX + LABEL_KEY));
	}

	@Test(expected = ValidationException.class)
	public void shouldFailRollbackVersionForItem_ValidationError()
	{
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());

		cmsVersionFacade.rollbackVersion(createCMSVersionData(VALID_VERSION_UID, INVALID_ITEM_UUID, LABEL, DESCRIPTION));
	}

	@Test
	public void shouldRollbackVersionForItem()
	{
		// GIVEN
		doNothing().when(facadeValidationService).validate(any(), any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(keyGenerator.generate()).thenReturn(LABEL_KEY);
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		doReturn(DESCRIPTION).when(cmsVersionFacade).getLocalizedDescription(Mockito.any());
		when(permissionCRUDService.canReadType(cmsItemModel.getItemtype())).thenReturn(true);
		when(permissionCRUDService.canChangeType(cmsItemModel.getItemtype())).thenReturn(true);

		// WHEN
		cmsVersionFacade.rollbackVersion(createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null));

		// THEN
		verify(cmsVersionService).createVersionForItem(any(), anyString(), anyString());
		verify(cmsVersionService).rollbackVersionForUid(any());
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowExceptionWhenRollbackVersionForItemIfUserDoesNotHaveReadPermissionOnItem()
	{
		// GIVEN
		doNothing().when(facadeValidationService).validate(any(), any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(keyGenerator.generate()).thenReturn(LABEL_KEY);
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		doReturn(DESCRIPTION).when(cmsVersionFacade).getLocalizedDescription(Mockito.any());
		when(permissionCRUDService.canReadType(cmsItemModel.getItemtype())).thenReturn(false);
		when(permissionCRUDService.canChangeType(cmsItemModel.getItemtype())).thenReturn(true);

		// WHEN
		cmsVersionFacade.rollbackVersion(createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null));
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowExceptionWhenRollbackVersionForItemIfUserDoesNotHaveChangePermissionOnItem()
	{
		// GIVEN
		doNothing().when(facadeValidationService).validate(any(), any());
		when(uniqueItemIdentifierService.getItemModel(VALID_ITEM_UUID, CMSItemModel.class)).thenReturn(Optional.of(cmsItemModel));
		when(keyGenerator.generate()).thenReturn(LABEL_KEY);
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		doReturn(DESCRIPTION).when(cmsVersionFacade).getLocalizedDescription(Mockito.any());
		when(permissionCRUDService.canReadType(cmsItemModel.getItemtype())).thenReturn(true);
		when(permissionCRUDService.canChangeType(cmsItemModel.getItemtype())).thenReturn(false);

		// WHEN
		cmsVersionFacade.rollbackVersion(createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null));
	}

	@Test
	public void shouldDeleteVersionForItem()
	{
		final CMSVersionData versionData = createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null);
		doNothing().when(facadeValidationService).validate(any(), any());
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));

		cmsVersionFacade.deleteVersion(versionData);

		verify(modelService).remove(any(CMSVersionModel.class));
	}

	@Test(expected = ValidationException.class)
	public void shouldFailDeleteVersionForItemWithValidationError()
	{
		final CMSVersionData versionData = createCMSVersionData(INVALID_VERSION_UID, VALID_ITEM_UUID, null, null);
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());

		cmsVersionFacade.deleteVersion(versionData);

		verifyZeroInteractions(cmsVersionService, modelService);
	}

	@Test
	public void shouldNotDeleteVersionForItemWhenVersionModelNotFound()
	{
		final CMSVersionData versionData = createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, null, null);
		doNothing().when(facadeValidationService).validate(any(), any());
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.empty());

		cmsVersionFacade.deleteVersion(versionData);

		verifyZeroInteractions(modelService);
	}

	@Test
	public void shouldGetItemByVersionWhereVersionModelFound() throws CMSVersionNotFoundException
	{

		final Map<String, Object> map = new HashMap<>();
		final CMSVersionData versionData = createCMSVersionData(VALID_VERSION_UID, VALID_ITEM_UUID, LABEL, DESCRIPTION);
		when(cmsVersionService.getVersionByUid(VALID_VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(cmsItemModel);
		when(cmsItemConverter.convert(cmsItemModel)).thenReturn(map);
		when(itemDataPopulatorProvider.getItemDataPopulators(cmsItemModel)).thenReturn(Arrays.asList(itemDataPopulator));

		cmsVersionFacade.getItemByVersion(versionData);

		verify(cmsVersionService).getVersionByUid(VALID_VERSION_UID);
		verify(cmsItemConverter).convert(cmsItemModel);
		verify(itemDataPopulator).populate(cmsItemModel, map);
		verify(cmsVersionItemCustomAttributesPopulator).populate(cmsVersionModel, map);

	}

	@Test(expected = ValidationException.class)
	public void shouldThrowVersionNotFoundExceptionIfVersionNotFount() throws CMSVersionNotFoundException
	{
		final CMSVersionData versionData = createCMSVersionData(INVALID_VERSION_UID, VALID_ITEM_UUID, LABEL, DESCRIPTION);
		doThrow(new ValidationException(new ValidationError(""))).when(facadeValidationService).validate(Mockito.any(),
				Mockito.any());

		cmsVersionFacade.getItemByVersion(versionData);
	}

	protected CMSVersionData createCMSVersionData(final String uid, final String itemUUID, final String label,
			final String description)
	{
		final CMSVersionData cmsVersionData = new CMSVersionData();
		cmsVersionData.setUid(uid);
		cmsVersionData.setItemUUID(itemUUID);
		cmsVersionData.setLabel(label);
		cmsVersionData.setDescription(description);

		return cmsVersionData;
	}

	protected CMSVersionData createCMSVersionData(final String itemUUID, final String label, final String description)
	{
		return createCMSVersionData(null, itemUUID, label, description);
	}
}
