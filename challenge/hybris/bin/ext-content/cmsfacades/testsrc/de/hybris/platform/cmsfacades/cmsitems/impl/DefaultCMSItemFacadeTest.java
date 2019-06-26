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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminSiteService;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.ItemDataPopulatorProvider;
import de.hybris.platform.cmsfacades.cmsitems.ItemTypePopulatorProvider;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.validation.Validator;

import com.google.common.collect.Lists;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSItemFacadeTest
{
	private static final String VALID_UID = "valid-item-uid";
	private static final String INVALID_UID = "invalid-item-uid";
	private static final String FIELD_REQUIRED = "field.required";
	private static final String CONTENT1 = "content1";
	private static final String CONTENT2 = "content2";

	@Spy
	@InjectMocks
	private DefaultCMSItemFacade facade;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	@Mock
	private CMSItemConverter CMSItemConverter;

	@Mock
	private ModelService modelService;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Mock
	private CMSItemModel itemModel;

	@Mock
	private ItemData invalidItemData;

	@Mock
	private ItemData validItemData;

	@Mock
	private ItemTypePopulatorProvider itemTypePopulatorProvider;

	@Mock
	private ItemDataPopulatorProvider itemDataPopulatorProvider;

	@Mock
	private Populator<Map<String, Object>, ItemModel> itemTypePopulator;

	@Mock
	private Populator<CMSItemModel, Map<String, Object>> itemDataPopulator;

	@Mock
	private CMSItemSearchService cmsItemSearchService;

	@Mock
	private Validator cmsItemSearchParamsDataValidator;

	@Mock
	private FacadeValidationService facadeValidationService;

	@Mock
	private DefaultCMSAdminSiteService defaultCMSAdminSiteService;

	@Mock
	private SessionSearchRestrictionsDisabler searchRestrictionsDisabler;

	@Mock
	private Converter<CMSItemSearchData, de.hybris.platform.cms2.data.CMSItemSearchData> cmsItemSearchDataConverter;

	private final ValidationErrors validationErrors = new DefaultValidationErrors();

	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Before
	public void setup() throws CMSItemNotFoundException
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(uniqueItemIdentifierService.getItemModel(invalidItemData)).thenReturn(Optional.empty());
		when(uniqueItemIdentifierService.getItemModel(validItemData)).thenReturn(Optional.of(itemModel));
		when(uniqueItemIdentifierService.getItemModel(any(), any())).thenReturn(Optional.empty());

		when(uniqueItemIdentifierService.getItemModel(INVALID_UID, CMSItemModel.class)).thenReturn(Optional.empty());
		when(uniqueItemIdentifierService.getItemModel(VALID_UID, CMSItemModel.class)).thenReturn(Optional.of(itemModel));

		when(itemTypePopulatorProvider.getItemTypePopulator(any())).thenReturn(Optional.of(itemTypePopulator));
		when(itemDataPopulatorProvider.getItemDataPopulators(any())).thenReturn(Arrays.asList(itemDataPopulator));

		when(CMSItemConverter.convert(any(Map.class))).thenReturn(itemModel);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier supplier = (Supplier) args[0];
			return supplier.get();
		}).when(searchRestrictionsDisabler).execute(any());

		facade.setCmsAdminSiteService(defaultCMSAdminSiteService);
	}

	@Test(expected = RuntimeException.class)
	public void shouldFailFindByIdInvalidUid() throws CMSItemNotFoundException
	{
		facade.getCMSItemByUuid(INVALID_UID);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailFindByIdInsufficientTypePermission() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canReadType(anyString())).thenReturn(Boolean.FALSE);
		doThrow(new TypePermissionException("Failure!")).when(facade).createTypePermissionException(any(), any());

		facade.getCMSItemByUuid(VALID_UID);
	}

	@Test
	public void shouldFindByIdValidUid() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canReadType(anyString())).thenReturn(Boolean.TRUE);

		facade.getCMSItemByUuid(VALID_UID);

		verify(CMSItemConverter).convert(itemModel);
	}

	@Test
	public void shouldDeleteValidUid() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canRemoveTypeInstance(anyString())).thenReturn(Boolean.TRUE);

		facade.deleteCMSItemByUuid(VALID_UID);

		verify(modelService).remove(itemModel);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailDeleteItemInsufficientTypePermission() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canRemoveTypeInstance(anyString())).thenReturn(Boolean.FALSE);
		doThrow(new TypePermissionException("Failure!")).when(facade).createTypePermissionException(any(), any());

		facade.deleteCMSItemByUuid(VALID_UID);
	}

	@Test
	public void shouldCreateItem() throws CMSItemNotFoundException
	{
		final Map<String, Object> map = new HashMap<>();
		map.put(CMSItemModel.ITEMTYPE, CMSItemModel._TYPECODE);

		when(CMSItemConverter.convert(itemModel)).thenReturn(map);
		when(permissionCRUDService.canCreateTypeInstance(anyString())).thenReturn(Boolean.TRUE);

		facade.createItem(map);

		verify(CMSItemConverter).convert(map);
		verify(itemTypePopulator).populate(map, itemModel);
		verify(CMSItemConverter).convert(itemModel);
		verify(transactionManager, never()).rollback(any(TransactionStatus.class));
		verify(itemDataPopulator).populate(itemModel, map);
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailCreateItemInsufficientTypePermission() throws CMSItemNotFoundException
	{
		final Map<String, Object> map = new HashMap<>();
		map.put(CMSItemModel.ITEMTYPE, CMSItemModel._TYPECODE);

		when(permissionCRUDService.canCreateTypeInstance(anyString())).thenReturn(Boolean.FALSE);
		doThrow(new TypePermissionException("Failure!")).when(facade).createTypePermissionException(any(), any());

		facade.createItem(map);
	}

	@Test
	public void shouldValidateItemForCreateAndRollbackTransaction() throws CMSItemNotFoundException
	{
		final Map<String, Object> map = new HashMap<>();
		facade.validateItemForCreate(map);
		verify(CMSItemConverter).convert(map);
		verify(itemTypePopulator).populate(map, itemModel);
		verify(CMSItemConverter).convert(itemModel);
		verify(itemDataPopulator).populate(itemModel, map);
		verify(modelService).saveAll();
		verify(modelService).refresh(itemModel);
		verify(modelService).detachAll();

		verify(transactionManager).rollback(any(TransactionStatus.class));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailUpdateWhenUuidIsInvalid() throws CMSItemNotFoundException
	{
		facade.updateItem(INVALID_UID, new HashMap<>());
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailUpdateWhenItemHasInconsistentValue() throws CMSItemNotFoundException
	{
		final Map<String, Object> map = new HashMap<>();
		facade.updateItem(VALID_UID, map);
		verify(CMSItemConverter).convert(map);
		verify(itemTypePopulator).populate(map, itemModel);
		verify(CMSItemConverter).convert(itemModel);
	}

	@Test
	public void shouldUpdateItem() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canChangeType(anyString())).thenReturn(Boolean.TRUE);

		final Map<String, Object> map = new HashMap<>();
		map.put(CMSItemModel.ITEMTYPE, CMSItemModel._TYPECODE);
		map.put(FIELD_UUID, VALID_UID);

		facade.updateItem(VALID_UID, map);

		verify(CMSItemConverter).convert(map);
		verify(itemTypePopulator).populate(map, itemModel);
		verify(CMSItemConverter).convert(itemModel);
		verify(transactionManager, never()).rollback(any(TransactionStatus.class));
	}

	@Test(expected = TypePermissionException.class)
	public void shouldFailUpdateItemInsufficientTypePermission() throws CMSItemNotFoundException
	{
		when(permissionCRUDService.canChangeType(anyString())).thenReturn(Boolean.FALSE);
		doThrow(new TypePermissionException("Failure!")).when(facade).createTypePermissionException(any(), any());

		final Map<String, Object> map = new HashMap<>();
		map.put(CMSItemModel.ITEMTYPE, CMSItemModel._TYPECODE);
		map.put(FIELD_UUID, VALID_UID);

		facade.updateItem(VALID_UID, map);
	}

	@Test
	public void shouldValidateItemForUpdateAndRollbackTransaction() throws CMSItemNotFoundException
	{
		final Map<String, Object> map = new HashMap<>();
		map.put(FIELD_UUID, VALID_UID);
		facade.validateItemForUpdate(VALID_UID, map);
		verify(CMSItemConverter).convert(map);
		verify(itemTypePopulator).populate(map, itemModel);
		verify(CMSItemConverter).convert(itemModel);
		verify(modelService).saveAll();
		verify(modelService).refresh(itemModel);
		verify(modelService).detachAll();

		verify(transactionManager).rollback(any(TransactionStatus.class));
	}

	@Test
	public void shouldSearchForItems()
	{
		// Input
		final CMSItemSearchData cmsItemSearchData = new CMSItemSearchData();
		final PageableData pageableData = new PageableData();

		// Intermediary data
		final SearchResult<CMSItemModel> modelResults = mock(SearchResult.class);
		final CMSItemModel mockItem1 = mock(CMSItemModel.class);
		final CMSItemModel mockItem2 = mock(CMSItemModel.class);
		final List<CMSItemModel> mockedResuls = Lists.newArrayList(mockItem1, mockItem2);

		final de.hybris.platform.cms2.data.CMSItemSearchData cms2SearchData = mock(
				de.hybris.platform.cms2.data.CMSItemSearchData.class);
		when(cmsItemSearchDataConverter.convert(cmsItemSearchData)).thenReturn(cms2SearchData);

		when(cmsItemSearchService.findCMSItems(cms2SearchData, pageableData)).thenReturn(modelResults);
		when(modelResults.getResult()).thenReturn(mockedResuls);
		when(permissionCRUDService.canReadType(anyString())).thenReturn(Boolean.TRUE);

		facade.findCMSItems(cmsItemSearchData, pageableData);

		verify(facadeValidationService).validate(cmsItemSearchParamsDataValidator, cmsItemSearchData);
		verify(cmsItemSearchService).findCMSItems(cms2SearchData, pageableData);
		verify(CMSItemConverter).convert(mockItem1);
		verify(CMSItemConverter).convert(mockItem2);
	}

	@Test
	public void shouldTransformValidationExceptionWithNoQualifier()
	{
		final ModelSavingException error = new ModelSavingException("any error message []");

		facade.transformValidationException(error);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
		assertThat(errors, hasSize(1));
		assertThat(errors.get(0).getField(), is(nullValue()));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED));
		assertThat(errors.get(0).getExceptionMessage(), is("any error message []"));
	}

	@Test
	public void shouldTransformValidationExceptionWithOneQualifier()
	{
		final ModelSavingException error = new ModelSavingException(String.format("[any.value.here.AnyValidator@123456z]:missing value for "
				+ "[ %s ] in model ComponentModel (<unsaved>) to create a new Component", CONTENT1));

		facade.transformValidationException(error);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
		assertThat(errors, hasSize(1));
		assertThat(errors.get(0).getField(), is(CONTENT1));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED));
	}

	@Test
	public void shouldTransformValidationExceptionWithTwoQualifiers()
	{
		final ModelSavingException error = new ModelSavingException(String.format("[any.value.AnyValidator]:missing values for "
				+ "[ %s, %s] in model ComponentModel (<unsaved>) to create a new Component", CONTENT1, CONTENT2));

		facade.transformValidationException(error);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();
		assertThat(errors, hasSize(2));
		assertThat(errors.stream().map(ValidationError::getField).collect(Collectors.toList()),
				containsInAnyOrder(CONTENT1, CONTENT2));
		assertTrue(errors.stream().map(ValidationError::getErrorCode).allMatch(code->code.equalsIgnoreCase(FIELD_REQUIRED)));
	}
}
