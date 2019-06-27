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
import java.util.LinkedHashSet;


/**
 * Example of shop organisation that consist from country organisations and is used for imports.
 */
public enum ShopOrganisation implements ImportOrganisationUnit<CountryOrganisation, ShopOrganisation>
{
	// @formatter:off
	NA("NA", "North America", new CountryOrganisation[]
	{ CountryOrganisation.US, CountryOrganisation.CA }, StructureState.V1), EU("EU", "Europe", new CountryOrganisation[]
	{ CountryOrganisation.DE, CountryOrganisation.FR }, StructureState.V2);
	// @formatter:on

	private static final String FOLDER_NAME = "shops";
	private static final String COMMON_FOLDER_NAME = "_commonShops";

	private String code;
	private String name;
	private Collection<CountryOrganisation> children;
	private Collection<ImportLanguage> languages;
	private StructureState structureState;

	ShopOrganisation(final String code, final String name, final CountryOrganisation[] children, final StructureState structureState)
	{
		this.code = code;
		this.name = name;
		this.children = Arrays.asList(children);
		this.structureState = structureState;
		final Collection<ImportLanguage> childLanguages = new LinkedHashSet<>();
		for (final CountryOrganisation child : children)
		{
			child.setParent(this);
			childLanguages.addAll(child.getLanguages());
		}
		this.languages = childLanguages;
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
		return FOLDER_NAME;
	}

	@Override
	public String getCommonFolderName()
	{
		return COMMON_FOLDER_NAME;
	}

	@Override
	public Collection<CountryOrganisation> getChildren()
	{
		return this.children;
	}

	@Override
	public Collection<ImportLanguage> getLanguages()
	{
		return this.languages;
	}

	@Override
	public ShopOrganisation getParent()
	{
		return null;
	}

	@Override
	public void setParent(final ShopOrganisation parent)
	{
		throw new UnsupportedOperationException("Parent can't be set for ShopOrganisation");

	}

	@Override
	public StructureState getStructureState()
	{
		return this.structureState;
	}
}
