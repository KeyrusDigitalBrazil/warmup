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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSSiteDao;
import de.hybris.platform.cmsfacades.util.builder.SiteModelBuilder;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;


public class SiteModelMother extends AbstractModelMother<CMSSiteModel>
{
	public static final String ELECTRONICS = "electronics";
	public static final String APPAREL = "test-apparel";
	public static final String POWERTOOL = "test-powertools";
	public static final String MULTI_COUNTRY_CAR_SITE = "multiCountryCarSite";
	public static final String MULTI_COUNTRY_EUROPE_CARS_SITE = "multiCountryCarEuropeSite";
	public static final String MULTI_COUNTRY_DE_CARS_SITE = "multiCountryCarDeSite";
	public static final String MULTI_COUNTRY_UK_CARS_SITE = "multiCountryCarUkSite";


	public static final String PREVIEW_URL = "/yacceleratorstorefront/?site=electronics";
	public static final String REDIRECT_URL = "/dummy/redirect/path";

	private CMSSiteDao cmsSiteDao;
	private LanguageModelMother languageModelMother;
	private ContentCatalogModelMother contentCatalogModelMother;
	private ProductCatalogModelMother productCatalogModelMother;
	private BaseStoreModelMother baseStoreModelMother;
	private CatalogVersionModelMother catalogVersionModelMother;

	protected CMSSiteModel defaultSite()
	{
		return SiteModelBuilder.aModel().withActive(Boolean.TRUE).build();
	}

	public CMSSiteModel createElectronicsWithAppleCatalog()
	{
		return getFromCollectionOrSaveAndReturn(() -> getCmsSiteDao().findCMSSitesById(ELECTRONICS),
				() -> SiteModelBuilder.fromModel(defaultSite()) //
						.withUid(ELECTRONICS) //
						.withPreviewRUL(PREVIEW_URL) //
						.withRedirectUrl(REDIRECT_URL) //
						.withName(ELECTRONICS, Locale.ENGLISH) //
						.withLanguage(getLanguageModelMother().createEnglish()) //
						.withDefaultCatalog(getContentCatalogModelMother().createAppleContentCatalogModel()) //
						.build());
	}

	public CMSSiteModel createElectronicsWithAppleStagedAndOnlineCatalog()
	{
		final ContentCatalogModel appleCatalog = getContentCatalogModelMother().createAppleStagedAndOnlineContentCatalogModel();
		
		return getFromCollectionOrSaveAndReturn(() -> getCmsSiteDao().findCMSSitesById(ELECTRONICS),
				() -> SiteModelBuilder.fromModel(defaultSite()) //
						.withUid(ELECTRONICS) //
						.withPreviewRUL(PREVIEW_URL) //
						.withRedirectUrl(REDIRECT_URL) //
						.withName(ELECTRONICS, Locale.ENGLISH) //
						.withLanguage(getLanguageModelMother().createEnglish()) //
						.withDefaultCatalog(appleCatalog) //
						.withContentCatalogs(Arrays.asList(appleCatalog)) //
						.build());
	}


	public CMSSiteModel createElectronics(ContentCatalogModel catalog)
	{
		return getFromCollectionOrSaveAndReturn(() -> getCmsSiteDao().findCMSSitesById(ELECTRONICS),
				() -> SiteModelBuilder.fromModel(defaultSite()) //
						.withUid(ELECTRONICS) //
						.withPreviewRUL(PREVIEW_URL) //
						.withRedirectUrl(REDIRECT_URL) //
						.withName(ELECTRONICS, Locale.ENGLISH) //
						.withLanguage(getLanguageModelMother().createEnglish()) //
						.withDefaultCatalog(catalog) //
						.withContentCatalogs(Arrays.asList(catalog)) //
						.build());
	}

	public CMSSiteModel createNorthAmericaElectronicsWithAppleStagedCatalog()
	{
		final CatalogModel phoneCatalog = getProductCatalogModelMother().createStaged1And2AndOnlinePhoneProductCatalogModel();
		final CatalogModel laptopCatalog = getProductCatalogModelMother().createStaged1And2AndOnlineLaptopProductCatalogModel();

		Set<CatalogVersionModel> catalogVersions = Sets.newHashSet();
		catalogVersions.addAll(phoneCatalog.getCatalogVersions());
		catalogVersions.addAll(laptopCatalog.getCatalogVersions());
		
		catalogVersions.add(getCatalogVersionModelMother().createAppleStagedCatalogVersionModel());
		
		BaseStoreModel northAmericaStore = getBaseStoreModelMother().createNorthAmerica(catalogVersions.toArray(new CatalogVersionModel[]{}));

		ContentCatalogModel contentCatalogModel = getContentCatalogModelMother().createAppleContentCatalogModel();

		return getFromCollectionOrSaveAndReturn(
				() -> getCmsSiteDao()
						.findCMSSitesById(ELECTRONICS),
				() -> SiteModelBuilder.fromModel(defaultSite()) //
						.withUid(ELECTRONICS) //
						.withPreviewRUL(PREVIEW_URL) //
						.withRedirectUrl(REDIRECT_URL) //
						.withName(ELECTRONICS, Locale.ENGLISH) //
						.withLanguage(getLanguageModelMother().createEnglish()) //
						.withDefaultCatalog(contentCatalogModel) //
						.withContentCatalogs(Arrays.asList(contentCatalogModel))
						.withStores(Arrays.asList(northAmericaStore))
						.build());
	}

	protected LanguageModelMother getLanguageModelMother()
	{
		return languageModelMother;
	}

	@Required
	public void setLanguageModelMother(final LanguageModelMother languageModelMother)
	{
		this.languageModelMother = languageModelMother;
	}

   public ContentCatalogModelMother getContentCatalogModelMother()
   {
   	return contentCatalogModelMother;
   }
   
   public void setContentCatalogModelMother(ContentCatalogModelMother contentCatalogModelMother)
   {
   	this.contentCatalogModelMother = contentCatalogModelMother;
   }
   
   public ProductCatalogModelMother getProductCatalogModelMother()
   {
   	return productCatalogModelMother;
   }
   
   public void setProductCatalogModelMother(ProductCatalogModelMother productCatalogModelMother)
   {
   	this.productCatalogModelMother = productCatalogModelMother;
   }

	protected CMSSiteDao getCmsSiteDao()
	{
		return cmsSiteDao;
	}

	@Required
	public void setCmsSiteDao(final CMSSiteDao cmsSiteDao)
	{
		this.cmsSiteDao = cmsSiteDao;
	}

	protected BaseStoreModelMother getBaseStoreModelMother()
	{
		return baseStoreModelMother;
	}

	@Required
	public void setBaseStoreModelMother(final BaseStoreModelMother baseStoreModelMother)
	{
		this.baseStoreModelMother = baseStoreModelMother;
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
