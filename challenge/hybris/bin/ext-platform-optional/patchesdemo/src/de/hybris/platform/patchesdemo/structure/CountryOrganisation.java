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
package de.hybris.platform.patchesdemo.structure;

import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patches.organisation.ImportOrganisationUnit;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;


/**
 * Example of country organisation that is used for imports.
 */
public enum CountryOrganisation implements ImportOrganisationUnit<CountryOrganisation, ShopOrganisation>
{
	// @formatter:off
	US("US", "United States", new ImportLanguage[]
	{ Language.EN_US }), CA("CA", "Canada", new ImportLanguage[]
	{ Language.EN_CA, Language.FR_CA }, StructureState.V2), FR("FR", "France", new ImportLanguage[]
	{ Language.FR_FR }, StructureState.V2), DE("DE", "Germany", new ImportLanguage[]
	{ Language.DE_DE });
	// @formatter:on

	private static final Logger LOG = Logger.getLogger(CountryOrganisation.class.getName());

	static
	{
		// thanks to that there is guarantee that constructor of ShopOrganisation is used and these objects are set as
		// parents for CountryOrganisations.
		ShopOrganisation.values();
	}

	private static final String COMMON_FOLDER_NAME = "_commonCountries";
	private static final String FOLDER_NAME = "countries";
	private String code;
	private String name;
	private Collection<ImportLanguage> languages;
	private ShopOrganisation parent;
	private de.hybris.platform.patches.organisation.StructureState structureState;

	CountryOrganisation(final String code, final String name, final ImportLanguage[] languages)
	{
		this.code = code;
		this.name = name;
		this.languages = Arrays.asList(languages);
	}

	CountryOrganisation(final String code, final String name, final ImportLanguage[] languages,
			final StructureState structureState)
	{
		this.code = code;
		this.name = name;
		this.languages = Arrays.asList(languages);
		this.structureState = structureState;
	}

	@Override
	public void setParent(final ShopOrganisation parent)
	{
		if (this.parent != null)
		{
			throw new IllegalArgumentException("Try to set parent for CountryOrganisation second time");
		}
		this.parent = parent;
		final de.hybris.platform.patches.organisation.StructureState parentStructureState = parent.getStructureState();
		if (this.structureState == null)
		{
			this.structureState = parentStructureState;
		}
		if (parentStructureState.isAfter(this.structureState))
		{
			this.structureState = parentStructureState;
			LOG.warn("CountryOrganization with name " + this.getName()
					+ " have structureState set that is before store where this country belongs too");
		}
	}

	@Override
	public ShopOrganisation getParent()
	{
		return this.parent;
	}

	@Override
	public String getCode()
	{
		return this.code;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getFolderName()
	{
		return CountryOrganisation.FOLDER_NAME;
	}

	@Override
	public String getCommonFolderName()
	{
		return CountryOrganisation.COMMON_FOLDER_NAME;
	}

	@Override
	public Collection<CountryOrganisation> getChildren()
	{
		return null;
	}

	@Override
	public Collection<ImportLanguage> getLanguages()
	{
		return this.languages;
	}

	@Override
	public de.hybris.platform.patches.organisation.StructureState getStructureState()
	{
		return this.structureState;
	}

}
