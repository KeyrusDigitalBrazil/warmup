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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VERSION_ROLLBACK_DESC;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VERSION_ROLLBACK_LABEL_PREFIX;

import de.hybris.platform.cms2.common.exceptions.PermissionExceptionUtils;
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
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.version.CMSVersionFacade;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.util.localization.Localization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.Validator;


/**
 * Default implementation of the {@link CMSVersionFacade}.
 */
public class DefaultCMSVersionFacade implements CMSVersionFacade
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	private CMSVersionService cmsVersionService;

	private CMSVersionSearchService cmsVersionSearchService;

	private FacadeValidationService facadeValidationService;

	private ModelService modelService;

	private Converter<CMSVersionModel, CMSVersionData> cmsVersionDataConverter;

	private Validator getCMSVersionsForItemValidator;

	private Validator createCMSVersionValidator;

	private Validator updateCMSVersionValidator;

	private Validator rollbackCMSVersionValidator;

	private Validator deleteCMSVersionValidator;

	private ObjectFactory<CMSVersionSearchData> cmsVersionSearchDataFactory;

	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	private CMSItemConverter cmsItemConverter;

	private ItemDataPopulatorProvider itemDataPopulatorProvider;

	private Populator<CMSVersionModel, Map<String, Object>> cmsVersionItemCustomAttributesPopulator;

	private ObjectFactory<CMSVersionData> cmsVersionDataDataFactory;

	private KeyGenerator versionLabelKeyGenerator;

	private PlatformTransactionManager transactionManager;

	private PermissionCRUDService permissionCRUDService;

	@Override
	public SearchResult<CMSVersionData> findVersionsForItem(final String itemUUID, final String mask,
			final PageableData pageableData) throws CMSItemNotFoundException
	{
		final CMSVersionData cmsVersionData = getCmsVersionDataDataFactory().getObject();
		cmsVersionData.setItemUUID(itemUUID);
		getFacadeValidationService().validate(getGetCMSVersionsForItemValidator(), cmsVersionData);

		final CMSItemModel cmsItemModel = getUniqueItemIdentifierService().getItemModel(itemUUID, CMSItemModel.class).get();

		final CMSVersionSearchData cmsVersionSearchData = getCmsVersionSearchDataFactory().getObject();
		cmsVersionSearchData.setMask(mask);
		cmsVersionSearchData.setItemUid(cmsItemModel.getUid());
		cmsVersionSearchData.setItemCatalogId(cmsItemModel.getCatalogVersion().getCatalog().getId());
		cmsVersionSearchData.setItemCatalogVersion(cmsItemModel.getCatalogVersion().getVersion());

		final SearchResult<CMSVersionModel> searchResult = getCmsVersionSearchService().findVersions(cmsVersionSearchData,
				pageableData);

		final List<CMSVersionData> cmsVersionDataList = searchResult.getResult().stream() //
				.map(cmsVersionModel -> getCmsVersionDataConverter().convert(cmsVersionModel)).collect(Collectors.toList());

		return new SearchResultImpl<>(cmsVersionDataList, //
				searchResult.getTotalCount(), //
				searchResult.getRequestedCount(), //
				searchResult.getRequestedStart());
	}

	@Override
	public CMSVersionData getVersion(final String versionUid) throws CMSVersionNotFoundException
	{
		final CMSVersionModel cmsVersionModel = getCmsVersionService().getVersionByUid(versionUid)
				.orElseThrow(() -> new CMSVersionNotFoundException("CMS Version [" + versionUid + "] does not exist."));

		return getCmsVersionDataConverter().convert(cmsVersionModel);
	}

	@Override
	public CMSVersionData createVersion(final CMSVersionData cmsVersionData)
	{
		return new TransactionTemplate(getTransactionManager()).execute(status -> {
			getFacadeValidationService().validate(getCreateCMSVersionValidator(), cmsVersionData);

			final CMSItemModel cmsItemModel = getUniqueItemIdentifierService()
					.getItemModel(cmsVersionData.getItemUUID(), CMSItemModel.class).orElse(null);

			final CMSVersionModel newVersionModel = getCmsVersionService().createVersionForItem(cmsItemModel,
					cmsVersionData.getLabel(), cmsVersionData.getDescription());

			return getCmsVersionDataConverter().convert(newVersionModel);
		});
	}

	@Override
	public CMSVersionData updateVersion(final CMSVersionData cmsVersionData)
	{

		if (!getPermissionCRUDService().canChangeType(CMSVersionModel._TYPECODE))
		{
			throwTypePermissionException(PermissionsConstants.CHANGE, CMSVersionModel._TYPECODE);
		}

		return new TransactionTemplate(getTransactionManager()).execute(status -> {
			getFacadeValidationService().validate(getUpdateCMSVersionValidator(), cmsVersionData);

			return getCmsVersionService().getVersionByUid(cmsVersionData.getUid()).map(cmsVersionModel -> {
				cmsVersionModel.setLabel(cmsVersionData.getLabel());
				cmsVersionModel.setDescription(cmsVersionData.getDescription());

				getModelService().save(cmsVersionModel);

				return getCmsVersionDataConverter().convert(cmsVersionModel);
			}).orElse(null);
		});


	}

	@Override
	@SuppressWarnings("squid:S3655")
	public void rollbackVersion(final CMSVersionData cmsVersionData)
	{
		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				getFacadeValidationService().validate(getRollbackCMSVersionValidator(), cmsVersionData);
				createRollbackAutoVersion(cmsVersionData);
				getCmsVersionService().rollbackVersionForUid(cmsVersionData.getUid());
			}
		});
	}


	@Override
	public Map<String, Object> getItemByVersion(final CMSVersionData cmsVersionData)
	{
		getFacadeValidationService().validate(getGetCMSVersionsForItemValidator(), cmsVersionData);

		final CMSVersionModel cmsVersionModel = getCmsVersionService().getVersionByUid(cmsVersionData.getUid()).get();
		final CMSItemModel itemModel = (CMSItemModel) getCmsVersionService().createItemFromVersion(cmsVersionModel);

		return getSessionSearchRestrictionsDisabler().execute(() -> {
			final Map<String, Object> itemMap = getCmsItemConverter().convert(itemModel);
			getItemDataPopulatorProvider().getItemDataPopulators(itemModel)
					.forEach(populator -> populator.populate(itemModel, itemMap));
			getCmsVersionItemCustomAttributesPopulator().populate(cmsVersionModel, itemMap);
			return itemMap;
		});

	}

	/**
	 * Creates a CMSVersionModel of the current page with a preset version label and description
	 *
	 * @param cmsVersionData
	 *           the version data object containing information of the page to be versioned
	 */
	protected void createRollbackAutoVersion(final CMSVersionData cmsVersionData)
	{
		final Optional<CMSItemModel> itemOptional = getUniqueItemIdentifierService().getItemModel(cmsVersionData.getItemUUID(),
				CMSItemModel.class);
		final Optional<CMSVersionModel> versionOptional = getCmsVersionService().getVersionByUid(cmsVersionData.getUid());

		if (itemOptional.isPresent() && versionOptional.isPresent())
		{

			final CMSItemModel itemModel = itemOptional.get();
			final CMSVersionModel versionModel = versionOptional.get();

			if (!getPermissionCRUDService().canReadType(itemModel.getItemtype()))
			{
				throwTypePermissionException(PermissionsConstants.READ, itemModel.getItemtype());
			}

			if (!getPermissionCRUDService().canChangeType(itemModel.getItemtype()))
			{
				throwTypePermissionException(PermissionsConstants.CHANGE, itemModel.getItemtype());
			}

			final String label = VERSION_ROLLBACK_LABEL_PREFIX + getVersionLabelKeyGenerator().generate().toString();
			final String description = getLocalizedDescription(versionModel.getLabel());

			getCmsVersionService().createVersionForItem(itemModel, label, description);
		}
	}

	/**
	 * Returns the localized string for the version description. This is used to auto fill the description for an
	 * automatic version created during a rollback operation.
	 *
	 * @param versionLabel
	 * @return The localized description
	 */
	protected String getLocalizedDescription(final String versionLabel)
	{
		return Localization.getLocalizedString(VERSION_ROLLBACK_DESC, new Object[]
		{ versionLabel });
	}

	@Override
	public void deleteVersion(final CMSVersionData cmsVersionData)
	{
		new TransactionTemplate(getTransactionManager()).execute(new TransactionCallbackWithoutResult()
		{
			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus status)
			{
				getFacadeValidationService().validate(getDeleteCMSVersionValidator(), cmsVersionData);

				final Optional<CMSVersionModel> versionOptional = getCmsVersionService().getVersionByUid(cmsVersionData.getUid());
				if (versionOptional.isPresent())
				{
					getModelService().remove(versionOptional.get());
				}
			}
		});
	}

	/**
	 * Throws {@link TypePermissionException} if current user does not have permission for typeCode.
	 *
	 * @param permissionName
	 * @param typeCode
	 */
	protected void throwTypePermissionException(final String permissionName, final String typeCode)
	{
		throw PermissionExceptionUtils.createTypePermissionException(permissionName, typeCode);
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

	protected CMSVersionService getCmsVersionService()
	{
		return cmsVersionService;
	}

	@Required
	public void setCmsVersionService(final CMSVersionService cmsVersionService)
	{
		this.cmsVersionService = cmsVersionService;
	}

	protected CMSVersionSearchService getCmsVersionSearchService()
	{
		return cmsVersionSearchService;
	}

	@Required
	public void setCmsVersionSearchService(final CMSVersionSearchService cmsVersionSearchService)
	{
		this.cmsVersionSearchService = cmsVersionSearchService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Converter<CMSVersionModel, CMSVersionData> getCmsVersionDataConverter()
	{
		return cmsVersionDataConverter;
	}

	@Required
	public void setCmsVersionDataConverter(final Converter<CMSVersionModel, CMSVersionData> cmsVersionDataConverter)
	{
		this.cmsVersionDataConverter = cmsVersionDataConverter;
	}

	protected Validator getCreateCMSVersionValidator()
	{
		return createCMSVersionValidator;
	}

	@Required
	public void setCreateCMSVersionValidator(final Validator createCMSVersionValidator)
	{
		this.createCMSVersionValidator = createCMSVersionValidator;
	}

	protected Validator getUpdateCMSVersionValidator()
	{
		return updateCMSVersionValidator;
	}

	@Required
	public void setUpdateCMSVersionValidator(final Validator updateCMSVersionValidator)
	{
		this.updateCMSVersionValidator = updateCMSVersionValidator;
	}

	protected Validator getRollbackCMSVersionValidator()
	{
		return rollbackCMSVersionValidator;
	}

	@Required
	public void setRollbackCMSVersionValidator(final Validator rollbackCMSVersionValidator)
	{
		this.rollbackCMSVersionValidator = rollbackCMSVersionValidator;
	}

	protected ObjectFactory<CMSVersionSearchData> getCmsVersionSearchDataFactory()
	{
		return cmsVersionSearchDataFactory;
	}

	@Required
	public void setCmsVersionSearchDataFactory(final ObjectFactory<CMSVersionSearchData> cmsVersionSearchDataFactory)
	{
		this.cmsVersionSearchDataFactory = cmsVersionSearchDataFactory;
	}

	protected Validator getDeleteCMSVersionValidator()
	{
		return deleteCMSVersionValidator;
	}

	@Required
	public void setDeleteCMSVersionValidator(final Validator deleteCMSVersionValidator)
	{
		this.deleteCMSVersionValidator = deleteCMSVersionValidator;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
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

	protected ItemDataPopulatorProvider getItemDataPopulatorProvider()
	{
		return itemDataPopulatorProvider;
	}

	@Required
	public void setItemDataPopulatorProvider(final ItemDataPopulatorProvider itemDataPopulatorProvider)
	{
		this.itemDataPopulatorProvider = itemDataPopulatorProvider;
	}

	protected Populator<CMSVersionModel, Map<String, Object>> getCmsVersionItemCustomAttributesPopulator()
	{
		return cmsVersionItemCustomAttributesPopulator;
	}

	@Required
	public void setCmsVersionItemCustomAttributesPopulator(
			final Populator<CMSVersionModel, Map<String, Object>> cmsVersionItemCustomAttributesPopulator)
	{
		this.cmsVersionItemCustomAttributesPopulator = cmsVersionItemCustomAttributesPopulator;
	}

	protected Validator getGetCMSVersionsForItemValidator()
	{
		return getCMSVersionsForItemValidator;
	}

	@Required
	public void setGetCMSVersionsForItemValidator(final Validator getCMSVersionsForItemValidator)
	{
		this.getCMSVersionsForItemValidator = getCMSVersionsForItemValidator;
	}

	protected ObjectFactory<CMSVersionData> getCmsVersionDataDataFactory()
	{
		return cmsVersionDataDataFactory;
	}

	@Required
	public void setCmsVersionDataDataFactory(final ObjectFactory<CMSVersionData> cmsVersionDataDataFactory)
	{
		this.cmsVersionDataDataFactory = cmsVersionDataDataFactory;
	}

	protected KeyGenerator getVersionLabelKeyGenerator()
	{
		return versionLabelKeyGenerator;
	}

	@Required
	public void setVersionLabelKeyGenerator(final KeyGenerator versionLabelKeyGenerator)
	{
		this.versionLabelKeyGenerator = versionLabelKeyGenerator;
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
