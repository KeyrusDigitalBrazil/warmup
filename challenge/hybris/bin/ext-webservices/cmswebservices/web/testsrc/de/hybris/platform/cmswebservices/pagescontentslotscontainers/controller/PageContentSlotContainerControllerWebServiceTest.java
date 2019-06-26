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
package de.hybris.platform.cmswebservices.pagescontentslotscontainers.controller;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.util.models.*;
import de.hybris.platform.cmsfacades.util.models.ABTestCMSComponentContainerModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerListWsDTO;
import de.hybris.platform.cmswebservices.dto.PageContentSlotContainerWsDTO;
import de.hybris.platform.cmswebservices.util.ApiBaseIntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.HashMap;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static de.hybris.platform.cmsfacades.util.models.ContentPageModelMother.UID_HOMEPAGE;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_PAGE_ID;
import static de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert.assertResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@NeedsEmbeddedServer(webExtensions =
        { CmswebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class PageContentSlotContainerControllerWebServiceTest extends ApiBaseIntegrationTest
{
    private static final String PAGE_CONTENT_SLOT_CONTAINERS_ENDPOINT = "/v1/sites/{siteId}/catalogs/{catalogId}/versions/{versionId}/pagescontentslotscontainers";
    private static final String INVALID_PAGE_ID = "INVALID_PAGE_ID";
    private static final String SLOT_ID = ContentSlotModelMother.UID_HEADER;
    private static final String CONTAINER_ID = ABTestCMSComponentContainerModelMother.UID_HEADER;

    @Resource
    private CMSSiteModelMother cmsSiteModelMother;
    @Resource
    private CatalogVersionModelMother catalogVersionModelMother;
    @Resource
    private ContentSlotForPageModelMother contentSlotForPageModelMother;

    private CatalogVersionModel catalogVersion;

    @Before
    public void setUp()
    {
        catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
        cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
    }

    @Test
    public void shouldGetContainersByPage() throws Exception
    {
        // GIVEN
        setupTestData();

        // WHEN
        final Response response = getCmsManagerWsSecuredRequestBuilder() //
                .path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_CONTAINERS_ENDPOINT,
                        new HashMap<>()))
                .queryParam(URI_PAGE_ID, UID_HOMEPAGE).build() //
                .accept(MediaType.APPLICATION_JSON) //
                .get();
        assertResponse(Status.OK, response);

        // THEN
        final PageContentSlotContainerListWsDTO entity = response.readEntity(PageContentSlotContainerListWsDTO.class);
        assertThat(entity.getPageContentSlotContainerList().size(), Matchers.is(1));

        PageContentSlotContainerWsDTO container = entity.getPageContentSlotContainerList().get(0);
        assertThat(container.getPageId(), Matchers.is(UID_HOMEPAGE));
        assertThat(container.getSlotId(), Matchers.is(SLOT_ID));
        assertThat(container.getContainerId(), Matchers.is(CONTAINER_ID));
        assertThat(container.getComponents().size(), Matchers.is(2));
    }

    @Test
    public void shouldReturnEmptyList_OnException() throws Exception
    {
        // GIVEN

        // WHEN
        final Response response = getCmsManagerWsSecuredRequestBuilder() //
                .path(replaceUriVariablesWithDefaults(PAGE_CONTENT_SLOT_CONTAINERS_ENDPOINT,
                        new HashMap<>()))
                .queryParam(URI_PAGE_ID, INVALID_PAGE_ID).build() //
                .accept(MediaType.APPLICATION_JSON) //
                .get();

        // THEN
        assertResponse(Status.OK, response);
        final PageContentSlotContainerListWsDTO entity = response.readEntity(PageContentSlotContainerListWsDTO.class);
        assertThat(entity.getPageContentSlotContainerList(), empty());
    }



    protected void setupTestData()
    {
        contentSlotForPageModelMother.HeaderHomePage_ContainerWithParagraphs(catalogVersion);
    }
}