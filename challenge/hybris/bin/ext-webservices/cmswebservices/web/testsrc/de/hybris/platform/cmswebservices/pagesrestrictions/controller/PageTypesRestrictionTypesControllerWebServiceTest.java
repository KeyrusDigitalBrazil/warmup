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
package de.hybris.platform.cmswebservices.pagesrestrictions.controller;

import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.util.models.CMSPageTypeModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite;
import de.hybris.platform.cmsfacades.util.models.CMSTimeRestrictionModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.RestrictionTypeModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageTypeRestrictionTypeData;
import de.hybris.platform.cmswebservices.data.PageTypeRestrictionTypeListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageTypesRestrictionTypesControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String PAGE_TYPE_RESTRICTION_TYPE_ENDPOINT = "/v1/pagetypesrestrictiontypes";

	@Resource
	private CMSTimeRestrictionModelMother timeRestrictionModelMother;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private ContentPageModelMother contentPageModelMother;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		cmsSiteModelMother.createSiteWithTemplate(TemplateSite.ELECTRONICS, catalogVersion);

		final AbstractPageModel homePage = contentPageModelMother.homePage(catalogVersion);
		final AbstractPageModel searchPage = contentPageModelMother.searchPage(catalogVersion);

		timeRestrictionModelMother.today(catalogVersion, homePage, searchPage);
		timeRestrictionModelMother.tomorrow(catalogVersion, homePage);
	}

	@Test
	public void shouldGetPageTypesRestrictionTypes()
	{
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_TYPE_RESTRICTION_TYPE_ENDPOINT, new HashMap<>())).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageTypeRestrictionTypeListData entity = response.readEntity(PageTypeRestrictionTypeListData.class);

		// check that we have a body
		assertNotNull(entity);

		final List<PageTypeRestrictionTypeData> relations = entity.getPageTypeRestrictionTypeList();
		//check that we have a couple of entries
		assertThat(relations.size(), greaterThan(1));

		PageTypeRestrictionTypeData contentPageRestrictionTypes = new PageTypeRestrictionTypeData();

		for (final PageTypeRestrictionTypeData data : relations)
		{
			if (data.getPageType().equals(CMSPageTypeModelMother.CODE_CONTENT_PAGE)
					&& data.getRestrictionType().equals(RestrictionTypeModelMother.CODE_CMS_TIME_RESTRICTION))
			{
				contentPageRestrictionTypes = data;
			}
		}

		// check that it contains a restriction type for content page
		assertNotNull(contentPageRestrictionTypes);
	}

}
