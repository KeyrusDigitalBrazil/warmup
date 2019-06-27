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
package de.hybris.platform.cmswebservices.pagetemplates.contoller;

import static de.hybris.platform.cmswebservices.pagetemplates.contoller.PageTemplateControllerWebServiceTest.PageTemplateField.FRONTEND_NAME;
import static de.hybris.platform.cmswebservices.pagetemplates.contoller.PageTemplateControllerWebServiceTest.PageTemplateField.UID;
import static de.hybris.platform.cmsfacades.util.models.CMSPageTypeModelMother.CODE_CONTENT_PAGE;
import static de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother.TEMPLATE_NAME_HOME_PAGE;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.PageTemplateListData;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.BaseStoreModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.SiteModelMother;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;


@NeedsEmbeddedServer(webExtensions =
{ CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageTemplateControllerWebServiceTest extends ApiBaseIntegrationTest
{
	private static final String PAGE_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagetemplates";
	private static final String PAGE_TYPE_CODE = "pageTypeCode";
	private static final String INVALID = "invalid";
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private PageTemplateModelMother pageTemplateModelMother;
	@Resource
	private SiteModelMother siteModelMother;
	@Resource
	private BaseStoreModelMother baseStoreModelMother;
	private CatalogVersionModel catalogVersion;

	@Before
	public void setup()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		siteModelMother.createElectronicsWithAppleCatalog();
		pageTemplateModelMother.homepageTemplate(catalogVersion);
		//still needed ?
		baseStoreModelMother.createNorthAmerica(catalogVersion);
	}

	@Test
	public void willRetrieveAllActivetemplatesForTheGivenPageType() throws Exception {
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(PAGE_TYPE_CODE, CODE_CONTENT_PAGE).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageTemplateListData entity = response.readEntity(PageTemplateListData.class);
		assertThat(entity.getTemplates().size(), is(1));
		assertThat(
				entity.getTemplates().get(0),
				allOf(hasProperty(UID.property, is(PageTemplateModelMother.UID_HOME_PAGE)),
						hasProperty(FRONTEND_NAME.property, is(TEMPLATE_NAME_HOME_PAGE))
				//, hasProperty(PREVIEW_ICON.property, is(LOGO.getUrl())) dynamic attribute in ORM so getter returns null
						));
	}

	@Test
	public void willRetrieveEmptyCollectionIfNoMatchingTemplateForGivenPageType() throws Exception {
		final Response response = getCmsManagerWsSecuredRequestBuilder() //
				.path(replaceUriVariablesWithDefaults(PAGE_ENDPOINT, new HashMap<>()))
				.queryParam(PAGE_TYPE_CODE, INVALID).build() //
				.accept(MediaType.APPLICATION_JSON) //
				.get();

		assertResponse(Status.OK, response);

		final PageTemplateListData entity = response.readEntity(PageTemplateListData.class);
		assertThat(entity.getTemplates(), empty());
	}

	enum PageTemplateField
	{
		UID("uid"), NAME("name"), PREVIEW_ICON("previewIcon"), FRONTEND_NAME("frontEndName");
		String property;

		private PageTemplateField(final String property)
		{
			this.property = property;
		}
	}
}
