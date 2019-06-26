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
package de.hybris.platform.cmsfacades.languages.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link LanguageFacade} to be used when Product Catalogs only are in the session and we don't want to 
 * use the languages from the current Site stored in session, but use the languages coming from the CatalogVersions stored in session. 
 * 
 */
public class DefaultCatalogVersionCentricLanguageFacade implements LanguageFacade
{
	private CatalogVersionService catalogVersionService;
	private Converter<LanguageModel, LanguageData> languageConverter;

	@Override
	public List<LanguageData> getLanguages()
	{
		return getCatalogVersionService().getSessionCatalogVersions()
				.stream()
				.map(CatalogVersionModel::getLanguages)
				.flatMap(languageModels -> languageModels.stream())
				.map(languageModel -> getLanguageConverter().convert(languageModel))
				.collect(Collectors.toList());
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

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	@Required
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}
}
