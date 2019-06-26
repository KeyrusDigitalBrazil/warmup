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
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSPageDaoIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private CMSPageDao cmsPageDao;
	@Resource
	private TypeService typeService;
	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		catalogVersion = catalogVersionService.getCatalogVersion("testCatalog", "Online");

		final PageTemplateModel pageTemplate = modelService.create(PageTemplateModel.class);
		pageTemplate.setUid("pageTemplate");
		pageTemplate.setCatalogVersion(catalogVersion);

		final ContentPageModel contentPage = modelService.create(ContentPageModel.class);
		contentPage.setUid("homepage");
		contentPage.setCatalogVersion(catalogVersion);
		contentPage.setMasterTemplate(pageTemplate);
		contentPage.setDefaultPage(Boolean.TRUE);

		modelService.saveAll();
	}

	@Test
	public void shouldFindDefaultPage()
	{
		final ComposedTypeModel type = typeService.getComposedTypeForCode(ContentPageModel._TYPECODE);

		final Collection<AbstractPageModel> pages = //
				cmsPageDao.findDefaultPageByTypeAndCatalogVersions(type, Arrays.asList(catalogVersion));

		assertThat(pages, not(empty()));
	}

}
