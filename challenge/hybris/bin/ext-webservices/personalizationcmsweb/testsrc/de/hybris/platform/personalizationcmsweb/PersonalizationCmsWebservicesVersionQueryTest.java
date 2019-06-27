/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.personalizationcmsweb;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.personalizationwebservices.BaseWebServiceTest;
import de.hybris.platform.personalizationwebservices.constants.PersonalizationwebservicesConstants;
import de.hybris.platform.personalizationwebservices.data.QueryParamsWsDTO;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
@NeedsEmbeddedServer(webExtensions =
{ PersonalizationwebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
public class PersonalizationCmsWebservicesVersionQueryTest extends BaseWebServiceTest
{
	private static final String PATH = "/v1/query/cxCmsPageVersionCheck";

	private static final String VERSION_ID = "versionId";
	private static final String CATALOG = "testCatalog";
	private static final String CATALOG_VERSION = "Online";
	private static final String PAGE_ID = "homepage";
	private static final String SLOT_ID = "Section1Slot-Homepage";
	private static final String SLOT_2_ID = "Section2Slot-Homepage";

	private static final String componentToPersonalize = "bannerHomePage1";



	@Resource
	private CMSPageService cmsPageService;
	@Resource
	private CMSContentSlotService cmsContentSlotService;

	@Resource
	private ModelService modelService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private CMSVersionService cmsVersionService;

	@Resource
	private CMSAdminSiteService cmsAdminSiteService;

	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		importData(new ClasspathImpExResource("/personalizationcmsweb/test/personalizationcmsweb_testdata.impex", "UTF-8"));
	}

	@Test
	public void shouldAllowVersionChange() throws IOException, JAXBException, CMSItemNotFoundException
	{
		//given
		catalogIsSet();
		final ContentSlotModel slot = slotIsSelected(SLOT_ID);
		componentIsAddedToSlot(slot);
		final CMSVersionModel version = versionIsCreated();

		//when
		final Response response = requestIsMade(version);

		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final Results data = unmarshallResult(response, Results.class);

		Assert.assertNotNull(data);
		Assert.assertTrue(data.result);
	}



	@Test
	public void shouldWarnForVersionChange() throws IOException, JAXBException, CMSItemNotFoundException
	{
		//given
		catalogIsSet();
		final ContentSlotModel slot = slotIsSelected(SLOT_2_ID);
		final List<AbstractCMSComponentModel> components = containerIsRemovedFromSlot(slot);
		final CMSVersionModel version = versionIsCreated();
		containerIsAddedToSlot(slot, components);

		//when
		final Response response = requestIsMade(version);

		//then
		WebservicesAssert.assertResponse(Status.OK, Optional.empty(), response);
		final Results data = unmarshallResult(response, Results.class);

		Assert.assertNotNull(data);
		Assert.assertFalse(data.result);
	}



	private void catalogIsSet()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG, CATALOG_VERSION);
		cmsAdminSiteService.setActiveCatalogVersion(catalogVersion);
		catalogVersionService.setSessionCatalogVersions(Collections.singleton(catalogVersion));
	}

	private ContentSlotModel slotIsSelected(final String slot2Id)
	{
		return cmsContentSlotService.getContentSlotForId(slot2Id);
	}


	private void componentIsAddedToSlot(final ContentSlotModel slot)
	{
		List<AbstractCMSComponentModel> components = slot.getCmsComponents();
		components = new ArrayList<>(components);
		components.add(components.get(0));
		slot.setCmsComponents(components);
		modelService.save(slot);
	}

	private List<AbstractCMSComponentModel> containerIsRemovedFromSlot(final ContentSlotModel slot)
	{
		final List<AbstractCMSComponentModel> components = slot.getCmsComponents();
		slot.setCmsComponents(new ArrayList<>());
		modelService.save(slot);
		return components;
	}

	private CMSVersionModel versionIsCreated() throws CMSItemNotFoundException
	{
		final AbstractPageModel page = cmsPageService.getPageForId(PAGE_ID);
		final CMSVersionModel version = cmsVersionService.createVersionForItem(page, "version1", "version1");
		return version;
	}

	private void containerIsAddedToSlot(final ContentSlotModel slot, final List<AbstractCMSComponentModel> components)
	{
		slot.setCmsComponents(components);
		modelService.save(slot);
	}

	private Response requestIsMade(final CMSVersionModel version) throws IOException, JAXBException
	{

		final QueryParamsWsDTO params = new QueryParamsWsDTO();
		params.setParams(new HashMap<>());
		params.getParams().put(VERSION_ID, version.getUid());
		params.getParams().put("catalog", CATALOG);
		params.getParams().put("catalogVersion", CATALOG_VERSION);

		return getWsSecuredRequestBuilderForCmsManager()//
				.path(PATH)//
				.build()//
				.post(Entity.entity(marshallDto(params, QueryParamsWsDTO.class), MediaType.APPLICATION_JSON));
	}


	public static class Results
	{
		public boolean result;

		public boolean isResult()
		{
			return result;
		}

		public void setResult(final boolean result)
		{
			this.result = result;
		}
	}

}
