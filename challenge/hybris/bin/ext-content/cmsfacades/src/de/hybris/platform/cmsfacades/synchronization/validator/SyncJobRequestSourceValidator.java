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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_DOES_NOT_EXIST;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cmsfacades.common.predicate.CatalogExistsPredicate;
import de.hybris.platform.cmsfacades.common.predicate.CatalogVersionExistsPredicate;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationFacade;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Provides additional validation to the DTO of {@link SyncJobData} for {@link SynchronizationFacade}.
 * <p>
 * Rules:</br>
 * <ul>
 * <li>Source Catalog Version cannot be null</li>
 * <li>A catalog version should exist with Source Catalog Version</li>
 * </ul>
 * </p>
 */
public class SyncJobRequestSourceValidator implements Validator
{
	public static final String SOURCE_VERSION = "SourceVersionId";

	private CatalogVersionService catalogVersionService;
	private CatalogExistsPredicate catalogExistsPredicate;
	private CatalogVersionExistsPredicate catalogVersionExistsPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return clazz.isAssignableFrom(SyncRequestData.class);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final SyncRequestData syncJobData = (SyncRequestData) obj;
		if (!Objects.isNull(syncJobData.getCatalogId()) && getCatalogExistsPredicate().test(syncJobData.getCatalogId()))
		{
			if (Objects.isNull(syncJobData.getSourceVersionId()))
			{
				errors.rejectValue(SOURCE_VERSION, FIELD_REQUIRED);
			}
			else
			{
				if (!catalogVersionExistsPredicate.test(syncJobData.getCatalogId(), syncJobData.getSourceVersionId()))
				{
					errors.rejectValue(SOURCE_VERSION, FIELD_DOES_NOT_EXIST);
				}
			}
		}
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

	protected CatalogVersionExistsPredicate getCatalogVersionExistsPredicate()
	{
		return catalogVersionExistsPredicate;
	}

	@Required
	public void setCatalogVersionExistsPredicate(final CatalogVersionExistsPredicate catalogVersionExistsPredicate)
	{
		this.catalogVersionExistsPredicate = catalogVersionExistsPredicate;
	}

	protected CatalogExistsPredicate getCatalogExistsPredicate()
	{
		return catalogExistsPredicate;
	}

	@Required
	public void setCatalogExistsPredicate(final CatalogExistsPredicate catalogExistsPredicate)
	{
		this.catalogExistsPredicate = catalogExistsPredicate;
	}
}
