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
package de.hybris.platform.cms2.version.service.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.data.CMSItemData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSVersionDao;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionServiceTest
{
	private static final String VERSION_UID = "someVersionUid";
	private static final String INVALID_VERSION_UID = "someVersionUidDoesNotExist";
	private static final String TRANSACTION_ID = "someVersionId";
	private static final String ITEM_UID = "someItemUid";
	private static final String LABEL = "someLabel";
	private static final String DESCRIPTION = "description";
	private static final String PAYLOAD = "somePayload";
	private static final String CATALOG_ID = "someCatalogId";
	private static final String CATALOG_VERSION_NAME = "someCatalogVersion";
	private PK CATALOG_VERSION_PK;

	@Spy
	@InjectMocks
	private DefaultCMSVersionService versionService;

	@Mock
	private ModelService modelService;
	@Mock
	private TypeService typeService;
	@Mock
	private CMSItemModel itemModel;
	@Mock
	private CMSParagraphComponentModel paragraphModel;
	@Mock
	private CMSVersionModel cmsVersionModel;
	@Mock
	private CatalogModel catalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private PersistentKeyGenerator versionUidGenerator;
	@Mock
	private PersistentKeyGenerator versionIdGenerator;
	@Mock
	private SessionService sessionService;
	@Mock
	private CMSVersionDao cmsVersionDao;
	@Mock
	private CMSAdminItemService cmsAdminItemService;
	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;
	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private ObjectFactory<CMSItemData> cmsItemDataFactory;
	@Mock
	private Converter<CMSItemModel, String> cmsVersionToDataConverter;
	@Mock
	private Converter<CMSVersionModel, ItemModel> cmsVersionToModelRollbackConverter;
	@Mock
	private ItemModelContext contentPageItemModelContext;
	@Mock
	private ContentPageModel contentPageModel;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Before
	public void setup()
	{
		when(versionUidGenerator.generate()).thenReturn(VERSION_UID);
		when(versionIdGenerator.generate()).thenReturn(TRANSACTION_ID);
		when(paragraphModel.getUid()).thenReturn(ITEM_UID);
		when(paragraphModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(itemModel.getUid()).thenReturn(ITEM_UID);
		when(itemModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		when(catalogModel.getId()).thenReturn(CATALOG_ID);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogVersionModel.getVersion()).thenReturn(CATALOG_VERSION_NAME);
		CATALOG_VERSION_PK = PK.fromLong(10l);
		when(catalogVersionModel.getPk()).thenReturn(CATALOG_VERSION_PK);
		when(permissionCRUDService.canReadType(anyString())).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(anyString())).thenReturn(true);
		when(permissionCRUDService.canRemoveTypeInstance(anyString())).thenReturn(true);
		when(permissionCRUDService.canChangeType(anyString())).thenReturn(true);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(cmsSessionSearchRestrictionsDisabler).execute(any());
	}

	@Test
	public void testGetVersionByUid()
	{
		//GIVEN
		when(cmsVersionDao.findByUid(VERSION_UID)).thenReturn(Optional.of(cmsVersionModel));

		//WHEN
		final Optional<CMSVersionModel> cmsVersion = versionService.getVersionByUid(VERSION_UID);

		//THEN
		assertTrue(cmsVersion.isPresent());
	}

	@Test
	public void testGetVersionByLabel()
	{
		//GIVEN
		when(cmsVersionDao.findByItemUidAndLabel(ITEM_UID, LABEL, catalogVersionModel)).thenReturn(Optional.of(cmsVersionModel));

		//WHEN
		final Optional<CMSVersionModel> cmsVersion = versionService.getVersionByLabel(itemModel, LABEL);

		//THEN
		assertTrue(cmsVersion.isPresent());
		assertThat(cmsVersion.get(), is(cmsVersionModel));
	}

	@Test
	public void testGetSessionTransactionIdWhenIdAlreadySet()
	{
		//GIVEN
		when(sessionService.getAttribute(Cms2Constants.SESSION_VERSION_TRANSACTION_ID)).thenReturn(TRANSACTION_ID);

		//WHEN
		final String transactionId = versionService.getTransactionId();

		//THEN
		assertThat(transactionId, is(TRANSACTION_ID));
	}

	@Test
	public void testGetSessionVersionIdWhenIdNotSet()
	{
		//WHEN
		final String transactionId = versionService.getTransactionId();

		//THEN
		verify(versionIdGenerator).generate();
		assertThat(transactionId, is(TRANSACTION_ID));
	}

	@Test
	public void testCreatePartialVersionForItemPopulatesAllMandatoryFields()
	{
		// GIVEN
		when(sessionService.getAttribute(Cms2Constants.SESSION_VERSION_TRANSACTION_ID)).thenReturn(TRANSACTION_ID);
		when(modelService.create(CMSVersionModel._TYPECODE)).thenReturn(cmsVersionModel);

		// WHEN
		cmsVersionModel = versionService.createPartialVersionForItem(itemModel);

		// THEN
		verify(cmsVersionModel).setTransactionId(TRANSACTION_ID);
		verify(cmsVersionModel).setItemCatalogVersion(catalogVersionModel);
		verify(cmsVersionModel).setItemUid(ITEM_UID);
		verify(modelService).save(cmsVersionModel);
	}

	@Test
	public void testCreateVersionForItem() throws CMSItemNotFoundException
	{
		// GIVEN
		when(sessionService.getAttribute(Cms2Constants.SESSION_VERSION_TRANSACTION_ID)).thenReturn(TRANSACTION_ID);
		when(modelService.create(CMSVersionModel._TYPECODE)).thenReturn(cmsVersionModel);
		when(cmsVersionToDataConverter.convert(itemModel)).thenReturn(PAYLOAD);
		when(cmsAdminItemService.findByUid(ITEM_UID, catalogVersionModel)).thenReturn(itemModel);
		when(cmsVersionModel.getItemCatalogVersion()).thenReturn(catalogVersionModel);
		when(cmsVersionModel.getItemUid()).thenReturn(ITEM_UID);

		// WHEN
		cmsVersionModel = versionService.createRevisionForItem(itemModel);

		// THEN
		verify(cmsVersionModel).setUid(VERSION_UID);
		verify(cmsVersionModel).setTransactionId(TRANSACTION_ID);
		verify(cmsVersionModel).setItemCatalogVersion(catalogVersionModel);
		verify(cmsVersionModel).setItemUid(ITEM_UID);
		verify(cmsVersionModel).setPayload(PAYLOAD);
	}

	@Test
	public void testIsVersionableWhenParagraphBelongsToSessionCatalogVersion()
	{
		// GIVEN
		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(cmsAdminContentSlotService.getAllRelationsForSlot(any())).thenReturn(Collections.emptyList());

		// WHEN
		final boolean isVersionable = versionService.isVersionable(paragraphModel);

		// THEN
		assertThat(isVersionable, equalTo(true));
	}

	@Test
	public void testIsNotVersionableWhenParagraphBelongsToParentCatalogVersion()
	{
		// GIVEN
		final CatalogVersionModel otherCatalogVersion = mock(CatalogVersionModel.class);
		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(paragraphModel.getCatalogVersion()).thenReturn(otherCatalogVersion);

		// WHEN
		final boolean isVersionable = versionService.isVersionable(paragraphModel);

		// THEN
		assertThat(isVersionable, equalTo(false));
	}

	@Test
	public void testIsNotVersionableWhenContentSlotIsShared()
	{
		// GIVEN
		final ContentSlotModel sharedContentSlotModel = mock(ContentSlotModel.class);
		final ContentSlotForTemplateModel slotForTemplateModel = mock(ContentSlotForTemplateModel.class);

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(cmsAdminContentSlotService.getAllRelationsForSlot(sharedContentSlotModel))
				.thenReturn(Collections.singletonList(slotForTemplateModel));
		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(Boolean.FALSE);

		// WHEN
		final boolean isVersionable = versionService.isVersionable(sharedContentSlotModel);

		// THEN
		assertThat(isVersionable, equalTo(false));
	}

	@Test
	public void testIsVersionableWhenContentSlotBelongsToSessionCatalogVersion()
	{
		// GIVEN
		final ContentSlotModel contentSlotModel = mock(ContentSlotModel.class);
		when(contentSlotModel.getCatalogVersion()).thenReturn(catalogVersionModel);
		final ContentSlotForPageModel slotForPageModel = mock(ContentSlotForPageModel.class);

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(cmsAdminContentSlotService.getAllRelationsForSlot(contentSlotModel))
				.thenReturn(Collections.singletonList(slotForPageModel));
		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(Boolean.TRUE);

		// WHEN
		final boolean isVersionable = versionService.isVersionable(contentSlotModel);

		// THEN
		assertThat(isVersionable, equalTo(true));
	}

	@Test
	public void testIsNotVersionableWhenContentSlotBelongsToParentCatalogVersion()
	{
		// GIVEN
		final CatalogVersionModel otherCatalogVersion = mock(CatalogVersionModel.class);
		final ContentSlotModel contentSlotModel = mock(ContentSlotModel.class);
		when(contentSlotModel.getCatalogVersion()).thenReturn(otherCatalogVersion);
		final ContentSlotForPageModel slotForPageModel = mock(ContentSlotForPageModel.class);

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
		when(cmsAdminContentSlotService.getAllRelationsForSlot(contentSlotModel))
				.thenReturn(Collections.singletonList(slotForPageModel));
		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(Boolean.TRUE);

		// WHEN
		final boolean isVersionable = versionService.isVersionable(contentSlotModel);

		// THEN
		assertThat(isVersionable, equalTo(false));
	}

	@Test
	public void testRollbackVersion()
	{
		// GIVEN
		doReturn(Optional.of(cmsVersionModel)).when(versionService).getVersionByUid(VERSION_UID);
		doReturn(cmsVersionToModelRollbackConverter).when(versionService).getCmsVersionToModelRollbackConverter();
		doReturn(itemModel).when(cmsVersionToModelRollbackConverter).convert(any());

		// WHEN
		final Optional<ItemModel> rollbackVersion = versionService.rollbackVersionForUid(VERSION_UID);

		// THEN
		assertThat(rollbackVersion.get(), equalTo(itemModel));
		verify(cmsVersionToModelRollbackConverter).convert(any());
		verify(modelService).saveAll();
	}

	@Test
	public void testIsNotRolledBackInvalidVersionId()
	{
		// GIVEN
		doReturn(Optional.empty()).when(versionService).getVersionByUid(INVALID_VERSION_UID);

		// WHEN
		final Optional<ItemModel> rollbackVersion = versionService.rollbackVersionForUid(INVALID_VERSION_UID);

		// THEN
		assertThat(rollbackVersion, equalTo(Optional.empty()));
		verifyZeroInteractions(cmsVersionToModelRollbackConverter, modelService);
	}

	@Test
	public void testGetAllVersionsForCMSItemModel()
	{
		// GIVEN
		final CMSVersionModel versionModel = mock(CMSVersionModel.class);
		final List<CMSVersionModel> versionModelList = Arrays.asList(versionModel);
		doReturn(versionModelList).when(cmsVersionDao).findAllByItemUidAndItemCatalogVersion(ITEM_UID, catalogVersionModel);

		// WHEN
		final List<CMSVersionModel> allVersions = versionService.getVersionsForItem(itemModel);

		//THEN
		verify(cmsVersionDao).findAllByItemUidAndItemCatalogVersion(ITEM_UID, catalogVersionModel);
		assertThat(allVersions, hasSize(1));
		assertThat(allVersions.get(0), is(versionModel));
	}

	@Test
	public void testWillDeleteAllTaggedVersionsForACMSItemModel()
	{
		// GIVEN
		final CMSVersionModel versionModel = mock(CMSVersionModel.class);
		final List<CMSVersionModel> versionModelList = Arrays.asList(versionModel);
		doReturn(versionModelList).when(versionService).getVersionsForItem(itemModel);

		// WHEN
		versionService.deleteVersionsForItem(itemModel);

		// THEN
		verify(modelService).remove(versionModel);
		verify(modelService).saveAll();
	}

	@Test
	public void testRollbackedContentPageShouldBeAHomepgaeIfOriginalIsAHomepage()
	{

		// GIVEN
		doReturn(Optional.of(cmsVersionModel)).when(versionService).getVersionByUid(VERSION_UID);
		doReturn(cmsVersionToModelRollbackConverter).when(versionService).getCmsVersionToModelRollbackConverter();
		doReturn(contentPageModel).when(cmsVersionToModelRollbackConverter).convert(any());

		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(Boolean.TRUE);
		when(contentPageModel.getItemModelContext()).thenReturn(contentPageItemModelContext);
		when(contentPageItemModelContext.getOriginalValue(ContentPageModel.HOMEPAGE)).thenReturn(Boolean.TRUE);

		// WHEN
		final Optional<ItemModel> rollbackVersion = versionService.rollbackVersionForUid(VERSION_UID);

		// THEN
		verify(contentPageModel).setHomepage(Boolean.TRUE);

	}

	@Test
	public void testRollbackedContentPageShouldNotBeAHomepgaeIfOriginalIsNotAHomepage()
	{
		// GIVEN
		doReturn(Optional.of(cmsVersionModel)).when(versionService).getVersionByUid(VERSION_UID);
		doReturn(cmsVersionToModelRollbackConverter).when(versionService).getCmsVersionToModelRollbackConverter();
		doReturn(contentPageModel).when(cmsVersionToModelRollbackConverter).convert(any());

		when(typeService.isAssignableFrom(anyString(), anyString())).thenReturn(Boolean.TRUE);
		when(contentPageModel.getItemModelContext()).thenReturn(contentPageItemModelContext);
		when(contentPageItemModelContext.getOriginalValue(ContentPageModel.HOMEPAGE)).thenReturn(Boolean.FALSE);

		// WHEN
		final Optional<ItemModel> rollbackVersion = versionService.rollbackVersionForUid(VERSION_UID);

		// THEN
		verify(contentPageModel).setHomepage(Boolean.FALSE);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileCreatingItemFromVersionIfUserDoesNotHaveReadPermissionForCMSVersion()
	{
		// GIVEN
		noReadPermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.createItemFromVersion(cmsVersionModel);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileGettingItemFromVersionIfUserDoesNotHaveReadPermissionForCMSVersion()
	{
		// GIVEN
		noReadPermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.createItemFromVersion(cmsVersionModel);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileGettingVersionByUidIfUserDoesNotHaveReadPermissionForCMSVersion()
	{
		// GIVEN
		noReadPermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.getVersionByUid(VERSION_UID);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileGettingVersionByLabelIfUserDoesNotHaveReadPermissionForCMSVersion()
	{
		// GIVEN
		noReadPermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.getVersionByLabel(itemModel, LABEL);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileCreatingVersionForItemIfUserDoesNotHaveCreatePermissionForCMSVersion()
	{
		// GIVEN
		noCreatePermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.createVersionForItem(itemModel, LABEL, DESCRIPTION);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileCreatingVersionForItemIfUserDoesNotHaveReadPermissionForCMSItem()
	{
		// GIVEN
		createPermission(CMSVersionModel._TYPECODE);
		noReadPermission(CMSItemModel._TYPECODE);

		// WHEN
		versionService.createVersionForItem(itemModel, LABEL, DESCRIPTION);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldThrowTypePermissionExceptionWhileDeletingVersionsForItemIfUserDoesNotHaveRemovePermissionForCMSVersion()
	{
		// GIVEN
		noRemovePermission(CMSVersionModel._TYPECODE);

		// WHEN
		versionService.deleteVersionsForItem(itemModel);
	}

	@Test
	public void givenPageVersionedIsNotCached_WhenFindPageVersionedByTransactionIdIsCalled_ThenThePageIsQueriedAndStoredInTheCache()
	{
		// GIVEN
		doReturn(false).when(cmsVersionSessionContextProvider).isPageVersionedInTransactionCached();
		doReturn(Optional.of(contentPageModel)).when(cmsVersionDao).findPageVersionedByTransactionId(TRANSACTION_ID);

		// WHEN
		Optional<AbstractPageModel> pageModel = versionService.findPageVersionedByTransactionId(TRANSACTION_ID);

		// THEN
		verify(cmsVersionSessionContextProvider).addPageVersionedInTransactionToCache(Optional.of(contentPageModel));
		verify(cmsVersionDao, times(1)).findPageVersionedByTransactionId(TRANSACTION_ID);
		assertTrue(pageModel.isPresent());
		assertThat(pageModel.get(), is(contentPageModel));
	}

	@Test
	public void givenPageVersionedIsCached_WhenFindPageVersionedByTransactionIdIsCalled_ThenThePageIsReturnedFromCache()
	{
		// GIVEN
		doReturn(true).when(cmsVersionSessionContextProvider).isPageVersionedInTransactionCached();
		doReturn(Optional.of(contentPageModel)).when(cmsVersionSessionContextProvider).getPageVersionedInTransactionFromCache();

		// WHEN
		Optional<AbstractPageModel> pageModel = versionService.findPageVersionedByTransactionId(TRANSACTION_ID);

		// THEN
		verify(cmsVersionSessionContextProvider, never()).addPageVersionedInTransactionToCache(Optional.of(contentPageModel));
		verify(cmsVersionDao, never()).findPageVersionedByTransactionId(TRANSACTION_ID);
		assertTrue(pageModel.isPresent());
		assertThat(pageModel.get(), is(contentPageModel));
	}

	protected void noReadPermission(final String typeCode)
	{
		when(permissionCRUDService.canReadType(typeCode)).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(versionService).throwTypePermissionException(PermissionsConstants.READ,
				typeCode);
	}

	protected void noCreatePermission(final String typeCode)
	{
		when(permissionCRUDService.canCreateTypeInstance(typeCode)).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(versionService)
				.throwTypePermissionException(PermissionsConstants.CREATE, typeCode);
	}

	protected void noRemovePermission(final String typeCode)
	{
		when(permissionCRUDService.canRemoveTypeInstance(typeCode)).thenReturn(false);
		doThrow(new TypePermissionException("invalid")).when(versionService)
				.throwTypePermissionException(PermissionsConstants.REMOVE, typeCode);
	}

	protected void readPermission(final String typeCode)
	{
		when(permissionCRUDService.canReadType(typeCode)).thenReturn(true);
	}

	protected void createPermission(final String typeCode)
	{
		when(permissionCRUDService.canCreateTypeInstance(typeCode)).thenReturn(true);
	}

	protected void removePermission(final String typeCode)
	{
		when(permissionCRUDService.canRemoveTypeInstance(typeCode)).thenReturn(true);
	}

}
