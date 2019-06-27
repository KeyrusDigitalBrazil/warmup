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
package de.hybris.platform.cmsfacades.util.models;

import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_APPLE;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentCatalogDao;
import de.hybris.platform.cmsfacades.util.builder.ContentCatalogModelBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Required;


public class ContentCatalogModelMother extends AbstractModelMother<ContentCatalogModel>
{

	public enum CatalogTemplate
	{
		ID_APPLE("Apple's Content Catalog"),
		ID_ONLINE("online content catalog"),
		ID_STAGED("staged content catalog"),
		ID_READONLY("read-only Content Catalog"),

		ID_PHONES("Phone Product Catalog"),
		ID_PHONE_ONLINE("online phone catalog"),
		ID_PHONE_STAGED_1("staged1 phone catalog"),
		ID_PHONE_STAGED_2("staged1 phone catalog"),

		ID_LAPTOPS("Laptop Product Catalog"),
		ID_LAPTOP_ONLINE("online laptop catalog"),
		ID_LAPTOP_STAGED_1("staged1 laptop catalog"),
		ID_LAPTOP_STAGED_2("staged1 laptop catalog"),

		//Multicountry
		MULTI_COUNTRY_ID_CARS("Car's Content Catalog"),
		MULTI_COUNTRY_ID_EUROPE_CARS("Europe Cars's Content Catalog"),
		MULTI_COUNTRY_ID_DE_CARS("DE Car's Content Catalog"),
		MULTI_COUNTRY_ID_UK_CARS("UK Car's Content Catalog");

		private final Map<String, String> humanName;

		CatalogTemplate(final String humanName)
		{
			this.humanName = buildName(Locale.ENGLISH, humanName);
		}

		CatalogTemplate(final Map<String, String> humanName)
		{
			this.humanName = humanName;
		}

		public Map<String, String> getHumanName()
		{
			return humanName;
		}

		public String getFirstInstanceOfHumanName()
		{
			final Map<String, String> map = getHumanName();
			final String key = map.keySet().stream().findFirst().get();
			return map.get(key);
		}

		public static Map<String, String> buildName(final Locale loc, final String name)
		{
			final Map<String, String> map = new HashMap<>();
			map.put(loc.getLanguage(), name);
			return map;
		}
	}

	private CMSContentCatalogDao cmsContentCatalogDao;
	private CatalogVersionModelMother catalogVersionModelMother;

	protected ContentCatalogModel defaultCatalog()
	{
		return ContentCatalogModelBuilder.aModel().withDefault(Boolean.TRUE).build();
	}

	public ContentCatalogModel createAppleContentCatalogModel()
	{
		return createContentCatalogModelWithIdAndName(ID_APPLE.name(), ID_APPLE.getFirstInstanceOfHumanName());
	}

	public ContentCatalogModel createContentCatalogModelFromTemplate(final CatalogTemplate template)
	{
		return createContentCatalogModelWithIdAndName(template.name(), template.getFirstInstanceOfHumanName());
	}

	public ContentCatalogModel createContentCatalogModelWithIdAndName(final String id, final String name)
	{
		return getOrSaveAndReturn(() -> getCmsContentCatalogDao().findContentCatalogById(id),
				() -> ContentCatalogModelBuilder.fromModel(defaultCatalog()).withName(name, Locale.ENGLISH).withId(id).build());
	}

	public ContentCatalogModel createAppleStagedAndOnlineContentCatalogModel()
	{
		// Create the catalogVersions only when a content catalog is created by ObjectModelMother (not by impex)
		final Supplier<Set<CatalogVersionModel>> catalogVersionsSupplier = () ->
		{
			final Set<CatalogVersionModel> catalogVersions = new HashSet<>();
			catalogVersions.add(getCatalogVersionModelMother().createAppleStagedCatalogVersionModel());
			catalogVersions.add(getCatalogVersionModelMother().createAppleOnlineCatalogVersionModel());
			return catalogVersions;
		};

		return getOrSaveAndReturn(() -> getCmsContentCatalogDao().findContentCatalogById(ID_APPLE.name()),
				() -> ContentCatalogModelBuilder.fromModel(defaultCatalog())
						.withName(ID_APPLE.getFirstInstanceOfHumanName(), Locale.ENGLISH) //
						.withId(ID_APPLE.name()) //
						.withCatalogVersions(catalogVersionsSupplier.get()).build());
	}

	public ContentCatalogModel createAppleContentCatalogModel(final CatalogVersionModel... catalogVersions)
	{
		return getOrSaveAndReturn(() -> getCmsContentCatalogDao().findContentCatalogById(ID_APPLE.name()),
				() -> ContentCatalogModelBuilder.fromModel(defaultCatalog())
						.withName(ID_APPLE.getFirstInstanceOfHumanName(), Locale.ENGLISH).withId(ID_APPLE.name())
						.withCatalogVersions(new HashSet(Arrays.asList(catalogVersions))).build());
	}

	protected CMSContentCatalogDao getCmsContentCatalogDao()
	{
		return cmsContentCatalogDao;
	}

	@Required
	public void setCmsContentCatalogDao(final CMSContentCatalogDao cmsContentCatalogDao)
	{
		this.cmsContentCatalogDao = cmsContentCatalogDao;
	}

	protected CatalogVersionModelMother getCatalogVersionModelMother()
	{
		return catalogVersionModelMother;
	}

	@Required
	public void setCatalogVersionModelMother(final CatalogVersionModelMother catalogVersionModelMother)
	{
		this.catalogVersionModelMother = catalogVersionModelMother;
	}
}
