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

import static de.hybris.platform.cmsfacades.util.builder.ProductCatalogModelBuilder.fromModel;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_LAPTOPS;
import static de.hybris.platform.cmsfacades.util.models.ContentCatalogModelMother.CatalogTemplate.ID_PHONES;

import de.hybris.platform.catalog.daos.CatalogDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.builder.ProductCatalogModelBuilder;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


public class ProductCatalogModelMother extends AbstractModelMother<CatalogModel>
{


	private CatalogDao catalogDao;
	private CatalogVersionModelMother catalogVersionModelMother;

	protected CatalogModel defaultProductCatalog()
	{
		return ProductCatalogModelBuilder.aModel().withDefault(Boolean.TRUE).build();
	}

	public CatalogModel createPhoneProductCatalogModel()
	{
		return createProductCatalogModelWithIdAndName(ID_PHONES.name(), ID_PHONES.getFirstInstanceOfHumanName());
	}

	public CatalogModel createProductCatalogModelWithIdAndName(final String id, final String name)
	{
		return getOrSaveAndReturn(() -> getCatalogDao().findCatalogById(id),
				() -> ProductCatalogModelBuilder.fromModel(defaultProductCatalog()).withName(name, Locale.ENGLISH).withId(id).build());
	}

	public CatalogModel createStaged1And2AndOnlinePhoneProductCatalogModel()
	{
		final Set<CatalogVersionModel> catalogVersions = new HashSet<>();
		catalogVersions.add(getCatalogVersionModelMother().createPhoneStaged1CatalogVersionModel());
		catalogVersions.add(getCatalogVersionModelMother().createPhoneStaged2CatalogVersionModel());
		catalogVersions.add(getCatalogVersionModelMother().createPhoneOnlineCatalogVersionModel());
		return getOrSaveAndReturn(() -> getCatalogDao().findCatalogById(ID_PHONES.name()),
				() -> fromModel(defaultProductCatalog())
						.withName(ID_PHONES.getFirstInstanceOfHumanName(), Locale.ENGLISH).withId(ID_PHONES.name())
						.withCatalogVersions(catalogVersions).build());
	}

	public CatalogModel createStaged1And2AndOnlineLaptopProductCatalogModel()
	{
		final Set<CatalogVersionModel> catalogVersions = new HashSet<>();
		catalogVersions.add(getCatalogVersionModelMother().createLaptopStaged1CatalogVersionModel());
		catalogVersions.add(getCatalogVersionModelMother().createLaptopStaged2CatalogVersionModel());
		catalogVersions.add(getCatalogVersionModelMother().createLaptopOnlineCatalogVersionModel());
		return getOrSaveAndReturn(() -> getCatalogDao().findCatalogById(ID_LAPTOPS.name()),
				() -> fromModel(defaultProductCatalog())
						.withName(ID_LAPTOPS.getFirstInstanceOfHumanName(), Locale.ENGLISH).withId(ID_PHONES.name())
						.withCatalogVersions(catalogVersions).build());
	}

	public CatalogDao getCatalogDao()
	{
		return catalogDao;
	}
	
	@Required
	public void setCatalogDao(CatalogDao catalogDao)
	{
		this.catalogDao = catalogDao;
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
