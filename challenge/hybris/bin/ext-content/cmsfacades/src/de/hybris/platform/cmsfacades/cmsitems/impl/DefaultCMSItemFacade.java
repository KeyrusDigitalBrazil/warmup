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

import static com.google.common.base.Preconditions.checkArgument;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CATALOG_VERSION;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_URI_CONTEXT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService;
import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade;
import de.hybris.platform.cmsfacades.cmsitems.ItemDataPopulatorProvider;
import de.hybris.platform.cmsfacades.cmsitems.ItemTypePopulatorProvider;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.cmsfacades.exception.RequiredRollbackException;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.Validator;


/**
 * Default implementation of the {@link CMSItemFacade}.
 */
public class DefaultCMSItemFacade implements CMSItemFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCMSItemFacade.class);
	private static final String MODEL_SAVING_EXCEPTION_REGEX = "\\[([\\w,\\s]+)\\](?!\\.)*";
	private static final String COMMA = ",";

	private CMSItemConverter cmsItemConverter;

	private ModelService modelService;

	private ItemTypePopulatorProvider itemTypePopulatorProvider;

	private CMSItemSearchService cmsItemSearchService;

	private Validator cmsItemSearchDataValidator;

	private FacadeValidationService facadeValidationService;

	private UniqueItemIdentifierService uniqueItemIdentifierService;

	private CMSAdminSiteService cmsAdminSiteService;

	private CatalogVersionService catalogVersionService;

	private Converter<CMSItemSearchData, de.hybris.platform.cms2.data.CMSItemSearchData> cmsItemSearchDataConverter;

	private ValidationErrorsProvider validationErrorsProvider;

	private PlatformTransactionManager transactionManager;

	private TypeService typeService;

	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	private OriginalClonedItemProvider originalClonedItemProvider;

	private ItemDataPopulatorProvider itemDataPopulatorProvider;

	private PermissionCRUDService permissionCRUDService;

	@Override
	public SearchResult<Map<String, Object>> findCMSItems(final CMSItemSearchData cmsItemSearchData,
			final PageableData pageableData)
	{
		getFacadeValidationService().validate(getCmsItemSearchDataValidator(), cmsItemSearchData);

		return getSessionSearchRestrictionsDisabler().execute(() -> {
			final SearchResult<CMSItemModel> searchResults = getCmsItemSearchService()
					.findCMSItems(getCmsItemSearchDataConverter().convert(cmsItemSearchData), pageableData);

			final List<Map<String, Object>> convertedResults = searchResults.getResult().stream() //
					.map(this::convertAndPopulate) //
					.collect(Collectors.toList());
			return new SearchResultImpl<>(convertedResults, searchResults.getTotalCount(), searchResults.getRequestedCount(),
					searchResults.getRequestedStart());
		});
	}

	@Override
	public List<Map<String, Object>> findCMSItems(final List<String> uuids) throws CMSItemNotFoundException
	{
		return getSessionSearchRestrictionsDisabler().execute(() -> uuids.stream() //
				.map(uuid -> getCMSItemByUuid(uuid, false)) //
				.filter(Objects::nonNull) //
				.collect(toList()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws TypePermissionException
	 *            when user does not have permission to read an instance of the given item type.
	 */
	@Override
	public Map<String, Object> getCMSItemByUuid(final String uuid) throws CMSItemNotFoundException
	{
		return getCMSItemByUuid(uuid, true);
	}


	/**
	 * Get one single CMSItem by its uuid (Universal Unique Identifier) <br>
	 * For more information about Unique Identifiers, see
	 * {@link de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService}
	 *
	 * @param uuid
	 *           the universal unique identifier
	 * @param throwException
	 *           determines if an exception is thrown or not if an item is not found for the given uuid
	 * @return The CMS Item matching the provided uuid; <br>
	 *         Can be {@code NULL} when user does not have READ permission for the type of the CMS Item and
	 *         {@code throwException} is {@code FALSE}
	 * @throws CMSItemNotFoundException
	 *            when a CMS Item can not be found for a given uui
	 * @throws TypePermissionException
	 *            when user does not have READ permission for the type of the CMS Item.
	 */
	@SuppressWarnings("squid:S00112")
	protected Map<String, Object> getCMSItemByUuid(final String uuid, final boolean throwException)
	{
		return getSessionSearchRestrictionsDisabler().execute(() -> {
			try
			{
				final CMSItemModel cmsItemModel = getUniqueItemIdentifierService() //
						.getItemModel(uuid, CMSItemModel.class) //
						.orElseThrow(() -> createCMSItemNotFoundException(uuid));

				if (getPermissionCRUDService().canReadType(cmsItemModel.getItemtype()))
				{
					return convertAndPopulate(cmsItemModel);
				}
				else if (throwException)
				{
					throw createTypePermissionException(PermissionsConstants.READ, cmsItemModel.getItemtype());
				}
				else
				{
					return null;
				}
			}
			catch (final CMSItemNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws TypePermissionException
	 *            when user does not have permission to create an instance of the given item type.
	 */
	@Override
	public Map<String, Object> createItem(final Map<String, Object> itemMap) throws CMSItemNotFoundException
	{
		setCatalogInSession(itemMap);

		final Predicate<String> typePermissionPredicate = itemType -> getPermissionCRUDService().canCreateTypeInstance(itemType);
		return saveItem(itemMap, typePermissionPredicate, PermissionsConstants.CREATE);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws TypePermissionException
	 *            when user does not have permission to update an instance of the given item type.
	 */
	@Override
	public Map<String, Object> updateItem(final String uuid, final Map<String, Object> itemMap) throws CMSItemNotFoundException
	{
		initialUpdateValidation(uuid, itemMap);
		setCatalogInSession(itemMap);

		final Predicate<String> typePermissionPredicate = itemType -> getPermissionCRUDService().canChangeType(itemType);
		return saveItem(itemMap, typePermissionPredicate, PermissionsConstants.CHANGE);
	}

	/**
	 * Checks if an item exists. If the item exists, then stores it in the local session for further validation.
	 *
	 * @param uuid
	 *           the item unique identifier
	 * @param itemMap
	 *           the itemMap representation of the item model.
	 * @throws CMSItemNotFoundException
	 *            when the item does not exist.
	 */
	@SuppressWarnings("squid:S2201")
	protected void initialUpdateValidation(final String uuid, final Map<String, Object> itemMap) throws CMSItemNotFoundException
	{
		// checks if the item exists
		getUniqueItemIdentifierService() //
				.getItemModel(uuid, CMSItemModel.class) //
				.orElseThrow(() -> createCMSItemNotFoundException(uuid));

		if (!StringUtils.equals(uuid, (String) itemMap.get(FIELD_UUID)))
		{
			throw new CMSItemNotFoundException("Inconsistent CMS Item [" + uuid + "] - [" + itemMap.get(FIELD_UUID) + "].");
		}
	}

	/**
	 * Saves Item using a local transaction. Any CMS items created or modified during this save operation will be
	 * versioned.
	 *
	 * @param itemMap
	 *           the itemMap to be saved
	 * @param typePermissionPredicate
	 *           the predicate to evaluate the type permissions for the current user
	 * @param permissionName
	 *           the operation user wants to perform on the item; possible values can be found in
	 *           {@link PermissionsConstants}
	 * @return the item Map representation after saving.
	 * @throws CMSItemNotFoundException
	 *            when an item for a given uid does not exist
	 */
	/*
	 * Suppress warnings:[squid:S2259] - Null pointer should not be dereferenced
	 */
	@SuppressWarnings("squid:S2259")
	protected Map<String, Object> saveItem(final Map<String, Object> itemMap, final Predicate<String> typePermissionPredicate,
			final String permissionName)
	{
		checkArgument(nonNull(itemMap), "map should not be null");
		final String itemType = (String) itemMap.get(ItemModel.ITEMTYPE);
		checkArgument(nonNull(itemType), "map should contain a value for key " + ItemModel.ITEMTYPE);

		if (!typePermissionPredicate.test(itemType))
		{
			throw createTypePermissionException(permissionName, itemType);
		}

		return getSessionSearchRestrictionsDisabler().execute(() -> {
			try
			{
				setCloneContext(itemMap);
				setRestoreContext(itemMap);
				return new TransactionTemplate(getTransactionManager()).execute(status -> {
					final ItemModel itemModel = convertAndPopulate(itemMap);
					getModelService().saveAll();
					return convertAndPopulate((CMSItemModel) itemModel);
				});
			}
			catch (final ModelSavingException e)
			{
				LOG.info("Failed to save the item model", e);
				try
				{
					getValidationErrorsProvider().initializeValidationErrors();
					transformValidationException(e);
					throw new ValidationException(getValidationErrorsProvider().getCurrentValidationErrors());
				}
				finally
				{
					getValidationErrorsProvider().finalizeValidationErrors();
				}
			}
		});
	}

	/**
	 * Parses the ModelSavingException and transforms it into validation error(s).
	 *
	 * @param error
	 *           the ModelSavingException
	 */
	protected void transformValidationException(final ModelSavingException error)
	{
		if (error.getMessage() != null)
		{
			final Pattern pattern = Pattern.compile(MODEL_SAVING_EXCEPTION_REGEX);
			final Matcher matcher = pattern.matcher(error.getMessage());

			if (matcher.find())
			{
				final String match = matcher.group().replaceAll("\\[", "").replaceAll("\\]", "");

				Arrays.stream(match.split(COMMA)).map(String::trim)
						.forEach(qualifier -> getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
								.field(qualifier) //
								.errorCode(FIELD_REQUIRED) //
								.exceptionMessage(error.getMessage()) //
								.build()));
			}
			else
			{
				getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
						.errorCode(FIELD_REQUIRED) //
						.exceptionMessage(error.getMessage()) //
						.build());
			}
		}
	}


	/**
	 * Converts and populates model to save
	 *
	 * @param itemMap
	 *           the Map representing the ItemModel to be converted and saved
	 * @return the model ready to be saved
	 */
	protected ItemModel convertAndPopulate(final Map<String, Object> itemMap)
	{
		final ItemModel itemModel = getCmsItemConverter().convert(itemMap);

		getItemTypePopulatorProvider().getItemTypePopulator(itemModel.getItemtype()) //
				.ifPresent(populator -> populator.populate(itemMap, itemModel));

		return itemModel;
	}

	/**
	 * Converts and populates Map to return to the frontend.
	 *
	 * @param itemModel
	 *           the itemModel to be converted to the Map.
	 * @return the itemMap ready to be consumed by frontend.
	 */
	protected Map<String, Object> convertAndPopulate(final CMSItemModel itemModel)
	{
		final Map<String, Object> itemMap = getCmsItemConverter().convert(itemModel);
		getItemDataPopulatorProvider().getItemDataPopulators(itemModel)
				.forEach(populator -> populator.populate(itemModel, itemMap));
		return itemMap;
	}

	/**
	 * {@inheritDoc} <br>
	 * The deleted CMS item will be versioned.
	 *
	 * @throws TypePermissionException
	 *            when user does not have permission to remove an instance of the given item type.
	 */
	@Override
	public void deleteCMSItemByUuid(final String uuid) throws CMSItemNotFoundException
	{
		final ItemModel cmsItem = getUniqueItemIdentifierService() //
				.getItemModel(uuid, CMSItemModel.class) //
				.orElseThrow(() -> createCMSItemNotFoundException(uuid));

		if (getPermissionCRUDService().canRemoveTypeInstance(cmsItem.getItemtype()))
		{
			getModelService().remove(cmsItem);
		}
		else
		{
			throw createTypePermissionException(PermissionsConstants.REMOVE, cmsItem.getItemtype());
		}
	}

	@Override
	public Map<String, Object> validateItemForUpdate(final String uuid, final Map<String, Object> itemMap)
			throws CMSItemNotFoundException
	{
		initialUpdateValidation(uuid, itemMap);
		return validateItem(itemMap);
	}

	@Override
	public Map<String, Object> validateItemForCreate(final Map<String, Object> itemMap) throws CMSItemNotFoundException
	{
		return validateItem(itemMap);
	}

	/**
	 * Thread safe temporary storage of a convertedItem just before explicitly rollbacking the transaction in dryRun mode
	 */
	protected ThreadLocal<Map<String, Object>> convertedItem = new ThreadLocal<>();


	/**
	 * Validates and convert the item for the sole purpose of validation. The transaction will be rolled back at the end.
	 *
	 * @param itemMap
	 *           the item model representation as a map
	 * @return the converted item model into its representation after validation and conversion.
	 * @throws CMSItemNotFoundException
	 *            when any item in its map does not exist.
	 */
	@SuppressWarnings(
	{ "squid:S1166", "squid:S00112" })
	protected Map<String, Object> validateItem(final Map<String, Object> itemMap)
	{
		final Boolean uuidProvided = itemMap.containsKey(CmsfacadesConstants.FIELD_UUID);
		final Boolean uidProvided = itemMap.containsKey(CmsfacadesConstants.FIELD_UID);
		return getSessionSearchRestrictionsDisabler().execute(() -> {
			try
			{
				setCatalogInSession(itemMap);
				setCloneContext(itemMap);
				setRestoreContext(itemMap);
				new TransactionTemplate(getTransactionManager()).execute(status -> {
					final ItemModel item = convertAndPopulate(itemMap);
					getModelService().saveAll();
					getModelService().refresh(item);
					convertedItem.set(convertAndPopulate((CMSItemModel) item));
					throw new RequiredRollbackException();
				});

			}
			catch (final CMSItemNotFoundException e)
			{
				throw new RuntimeException(e);
			}
			catch (final ModelSavingException e)
			{
				LOG.info("Failed to save the item model", e);
				try
				{
					getValidationErrorsProvider().initializeValidationErrors();
					transformValidationException(e);
					throw new ValidationException(getValidationErrorsProvider().getCurrentValidationErrors());
				}
				finally
				{
					getValidationErrorsProvider().finalizeValidationErrors();
				}
			}
			catch (final RequiredRollbackException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("required rollback in validation mode");
				}
			}
			finally
			{
				if (convertedItem.get() != null)
				{
					if (!uuidProvided)
					{
						convertedItem.get().remove(CmsfacadesConstants.FIELD_UUID);
					}
					if (!uidProvided)
					{
						convertedItem.get().remove(CmsfacadesConstants.FIELD_UID);
					}
				}
				getModelService().detachAll();
			}

			return convertedItem.get();
		});
	}


	/**
	 * Creates a new {@link CMSItemNotFoundException}.
	 *
	 * @param uuid
	 *           The string representing the UUID of the item not found.
	 * @return the new exception.
	 */
	protected CMSItemNotFoundException createCMSItemNotFoundException(final String uuid)
	{
		return new CMSItemNotFoundException("CMS Item [" + uuid + "] does not exist.");
	}

	/**
	 * Creates a new {@link TypePermissionException} with a localized error message
	 *
	 * @param permissionName
	 *           The permission name defined by {@link PermissionsConstants}
	 * @param itemType
	 *           The type code of the item
	 * @return a new {@link TypePermissionException}
	 */
	protected TypePermissionException createTypePermissionException(final String permissionName, final String itemType)
	{
		return PermissionExceptionUtils.createTypePermissionException(permissionName, itemType);
	}

	protected CMSItemConverter getCmsItemConverter()
	{
		return cmsItemConverter;
	}

	@Required
	public void setCmsItemConverter(final CMSItemConverter cmsItemConverter)
	{
		this.cmsItemConverter = cmsItemConverter;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	/**
	 * Sets the catalogVersion in the current session.
	 *
	 * @param source
	 * @throws CMSItemNotFoundException
	 */
	protected void setCatalogInSession(final Map<String, Object> source) throws CMSItemNotFoundException
	{
		if (source == null)
		{
			return;
		}
		final String catalogVersionUUID = (String) source.get(FIELD_CATALOG_VERSION);
		final Optional<CatalogVersionModel> catalogVersionOpt = //
				getUniqueItemIdentifierService().getItemModel(catalogVersionUUID, CatalogVersionModel.class);
		if (catalogVersionOpt.isPresent())
		{
			final CatalogVersionModel catalogVersion = catalogVersionOpt.get();
			getCmsAdminSiteService().setActiveCatalogVersion(catalogVersion.getCatalog().getId(), catalogVersion.getVersion());
			getCatalogVersionService().setSessionCatalogVersion(catalogVersion.getCatalog().getId(), catalogVersion.getVersion());
		}
	}

	/**
	 * Sets the clone context in the current session. The clone context contains information about original catalog
	 * version.
	 *
	 * @param source
	 */
	protected void setCloneContext(final Map<String, Object> source)
	{
		final Map<String, String> cloneContext = (HashMap<String, String>) source.get(FIELD_URI_CONTEXT);
		getCmsAdminSiteService().setCloneContext(cloneContext);
	}

	/**
	 * Sets the restore context in the current session. The restore context contains information about whether to
	 * override while restore or not.
	 *
	 * @param source
	 */
	protected void setRestoreContext(final Map<String, Object> source)
	{
		final Map<String, Object> restoreContext = new HashMap<>();
		restoreContext.put(CmsfacadesConstants.FIELD_PAGE_REPLACE, source.get(CmsfacadesConstants.FIELD_PAGE_REPLACE));

		getCmsAdminSiteService().setRestoreContext(restoreContext);
	}

	protected ItemTypePopulatorProvider getItemTypePopulatorProvider()
	{
		return itemTypePopulatorProvider;
	}

	@Required
	public void setItemTypePopulatorProvider(final ItemTypePopulatorProvider itemTypePopulatorProvider)
	{
		this.itemTypePopulatorProvider = itemTypePopulatorProvider;
	}

	protected CMSItemSearchService getCmsItemSearchService()
	{
		return cmsItemSearchService;
	}

	@Required
	public void setCmsItemSearchService(final CMSItemSearchService cmsItemSearchService)
	{
		this.cmsItemSearchService = cmsItemSearchService;
	}

	protected Validator getCmsItemSearchDataValidator()
	{
		return cmsItemSearchDataValidator;
	}

	@Required
	public void setCmsItemSearchDataValidator(final Validator cmsItemSearchDataValidator)
	{
		this.cmsItemSearchDataValidator = cmsItemSearchDataValidator;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Converter<CMSItemSearchData, de.hybris.platform.cms2.data.CMSItemSearchData> getCmsItemSearchDataConverter()
	{
		return cmsItemSearchDataConverter;
	}

	@Required
	public void setCmsItemSearchDataConverter(
			final Converter<CMSItemSearchData, de.hybris.platform.cms2.data.CMSItemSearchData> cmsItemSearchDataConverter)
	{
		this.cmsItemSearchDataConverter = cmsItemSearchDataConverter;
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	protected PlatformTransactionManager getTransactionManager()
	{
		return transactionManager;
	}

	@Required
	public void setTransactionManager(final PlatformTransactionManager transactionManager)
	{
		this.transactionManager = transactionManager;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	public SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}

	protected OriginalClonedItemProvider getOriginalClonedItemProvider()
	{
		return originalClonedItemProvider;
	}

	@Required
	public void setOriginalClonedItemProvider(final OriginalClonedItemProvider originalClonedItemProvider)
	{
		this.originalClonedItemProvider = originalClonedItemProvider;
	}

	protected ItemDataPopulatorProvider getItemDataPopulatorProvider()
	{
		return itemDataPopulatorProvider;
	}

	@Required
	public void setItemDataPopulatorProvider(final ItemDataPopulatorProvider itemDataPopulatorProvider)
	{
		this.itemDataPopulatorProvider = itemDataPopulatorProvider;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	@Required
	public void setPermissionCRUDService(final PermissionCRUDService permissionCRUDService)
	{
		this.permissionCRUDService = permissionCRUDService;
	}
}
