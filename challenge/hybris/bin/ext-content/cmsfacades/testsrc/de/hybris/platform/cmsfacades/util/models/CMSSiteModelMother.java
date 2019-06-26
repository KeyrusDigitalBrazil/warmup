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

import static de.hybris.platform.cmsfacades.util.builder.CMSSiteModelBuilder.fromModel;
import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.APPAREL;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.joda.time.DateTime.now;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentCatalogDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSSiteDao;
import de.hybris.platform.cmsfacades.util.builder.CMSSiteModelBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class CMSSiteModelMother extends AbstractModelMother<CMSSiteModel>
{

	public enum TemplateSite
	{
		APPAREL(buildName(Locale.ENGLISH, "Apparel"), SiteModelMother.APPAREL, "http://apparel.com", "url-media-thumbnail"), //
		POWER_TOOLS(buildName(Locale.ENGLISH, "Power Tools"), SiteModelMother.POWERTOOL, "http://power.com", "url-media-thumbnail"), //
		ELECTRONICS(buildName(Locale.ENGLISH, "Electronics"), SiteModelMother.ELECTRONICS, "http://electric.com",
				"url-media-thumbnail");

		private final String uid;
		private final Map<String, String> name;
		private final String baseUrl;
		private final String thumbnailUri;

		TemplateSite(final Map<String, String> name, final String uid, final String baseUrl, final String thumbnailUri)
		{
			this.name = name;
			this.uid = uid;
			this.baseUrl = baseUrl;
			this.thumbnailUri = thumbnailUri;
		}

		public String getUid()
		{
			return uid;
		}

		public Map<String, String> getNames()
		{
			return name;
		}

		public String getName(final Locale locale)
		{
			return name.get(locale.getLanguage());
		}

		public String getFirstInstanceOfName()
		{
			final Map<String, String> value = getNames();
			final String firstKey = value.keySet().stream().findFirst().get();
			return value.get(firstKey);
		}

		public String getBaseUrl()
		{
			return baseUrl;
		}

		public String getThumbnailUri()
		{
			return thumbnailUri;
		}

		public static Map<String, String> buildName(final Locale locale, final String value)
		{
			final Map<String, String> map = new HashMap<>();
			map.put(locale.getLanguage(), value);
			return map;
		}
	}

	private CMSSiteDao cmsSiteDao;

	private CMSContentCatalogDao cmsContentCatalogDao;

	public CMSSiteModel createSiteWithTemplate(final TemplateSite site, final CatalogVersionModel... catalogs)
	{
		return createSiteWithMinimumParameters(site.getFirstInstanceOfName(), site.getUid(), site.getBaseUrl(), catalogs);
	}

	protected CMSSiteModel createSiteWithMinimumParameters(final String name, final String uid, final String url,
			final CatalogVersionModel[] catalogs)
	{

		final List<ContentCatalogModel> contentCatalogs = asList(catalogs).stream().map(this::getContentCatalogFromCatalogVersion)
				.collect(toList());
		return getFromCollectionOrSaveAndReturn(() -> getCmsSiteDao().findCMSSitesById(uid), () -> fromModel(defaultSite())
				.withEnglishName(name).withUid(uid).withRedirectUrl(url).usingCatalogs(contentCatalogs).build());
	}

	protected ContentCatalogModel getContentCatalogFromCatalogVersion(final CatalogVersionModel catalogVersionModel)
	{
		return getCmsContentCatalogDao().findContentCatalogById(catalogVersionModel.getCatalog().getId());
	}

	protected CMSSiteModel defaultSite()
	{
		return CMSSiteModelBuilder.aModel().withEnglishName(APPAREL.getFirstInstanceOfName()).active()
				.from(now().minusDays(5).withTimeAtStartOfDay().toDate()).until(now().plusDays(5).withTimeAtStartOfDay().toDate())
				.withUid(APPAREL.getUid()).withRedirectUrl(APPAREL.getBaseUrl()).build();
	}

	public CMSSiteDao getCmsSiteDao()
	{
		return cmsSiteDao;
	}

	@Required
	public void setCmsSiteDao(final CMSSiteDao cmsSiteDao)
	{
		this.cmsSiteDao = cmsSiteDao;
	}

	@Required
	public void setCmsContentCatalogDao(final CMSContentCatalogDao cmsContentCatalogDao)
	{
		this.cmsContentCatalogDao = cmsContentCatalogDao;
	}

	public CMSContentCatalogDao getCmsContentCatalogDao()
	{
		return cmsContentCatalogDao;
	}
}
