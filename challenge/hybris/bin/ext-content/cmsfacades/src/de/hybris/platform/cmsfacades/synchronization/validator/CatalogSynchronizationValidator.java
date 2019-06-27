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
package de.hybris.platform.cmsfacades.synchronization.validator;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.ACTIVE_SYNC_JOB_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates the request of synchronization between two catalog versions by verifying synchronization permissions
 * using {@link CatalogSynchronizationService#canSynchronize(SyncItemJobModel, PrincipalModel)}
 *
 * @see SyncRequestData
 */
public class CatalogSynchronizationValidator implements Validator
{
	private static final String SOURCE_NAME_ATTR = "sourceVersionId";

	private CatalogVersionService catalogVersionService;
	private UserService userService;
	private CatalogSynchronizationService catalogSynchronizationService;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return SyncRequestData.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final SyncRequestData syncJobRequestData = (SyncRequestData) target;

		try
		{
			final Optional<SyncItemJobModel> syncItemJobModelOptional = getSyncItemJob(syncJobRequestData);
			final boolean activeJobExists = syncItemJobModelOptional.isPresent();

			if (!activeJobExists)
			{
				errors.rejectValue(SOURCE_NAME_ATTR, ACTIVE_SYNC_JOB_REQUIRED, new Object[]
						{ syncJobRequestData.getCatalogId(), syncJobRequestData.getSourceVersionId(),
								syncJobRequestData.getTargetVersionId() }, null);
			}
			else
			{
				final UserModel principal = getUserService().getCurrentUser();
				final boolean canSync = getCatalogSynchronizationService().canSynchronize(syncItemJobModelOptional.get(), principal);
				if (!canSync)
				{
					errors.rejectValue(SOURCE_NAME_ATTR, UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS, new Object[]
							{ principal.getName() }, null);
				}
			}
		}
		catch (final UnknownIdentifierException e)
		{
			/**
			 * catalogId, sourceVersionId and/or targetVersionId could be invalid. No need to log this error again because
			 * it should be caught by the {@link SyncJobRequestValidator} already.
			 */
		}
	}

	/**
	 * Returns first active synchronization job from source catalog.
	 *
	 * @param syncJobRequestData
	 * 		the {@link SyncRequestData} object
	 * @return {@link Optional} synchronization job
	 */
	protected Optional<SyncItemJobModel> getSyncItemJob(final SyncRequestData syncJobRequestData)
	{
		final CatalogVersionModel sourceCatalog = getCatalogVersionModel(syncJobRequestData.getCatalogId(),
				syncJobRequestData.getSourceVersionId());

		return sourceCatalog.getSynchronizations().stream()
				.filter(syncItemJob -> syncItemJob.getTargetVersion().getVersion().equals(syncJobRequestData.getTargetVersionId()))
				.filter(SyncItemJobModel::getActive).findFirst();
	}

	/**
	 * Gets the catalogVersionModel
	 *
	 * @param catalog
	 * 		the catalog name
	 * @param catalogVersion
	 * 		the catalog version name
	 * @return the catalogVersionModel
	 */
	protected CatalogVersionModel getCatalogVersionModel(final String catalog, final String catalogVersion)
	{
		return getCatalogVersionService().getCatalogVersion(catalog, catalogVersion);
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CatalogSynchronizationService getCatalogSynchronizationService()
	{
		return catalogSynchronizationService;
	}

	@Required
	public void setCatalogSynchronizationService(
			final CatalogSynchronizationService catalogSynchronizationService)
	{
		this.catalogSynchronizationService = catalogSynchronizationService;
	}
}
