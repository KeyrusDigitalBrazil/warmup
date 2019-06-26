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
package de.hybris.platform.previewwebservices.populators;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.previewwebservices.dto.CatalogVersionWsDTO;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.impex.impl.ClasspathImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class PreviewTicketReversePopulatorIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String SITE_URL = "https://127.0.0.1:9002/yacceleratorstorefront?site=";
	private static final String TEST_GLOBAL_SITE = "testSite";
	private static final String TEST_REGION_SITE = "testSite-region";
	private static final String TEST_LOCAL_SITE = "testSite-local";
	private static final String TEST_SITE_2 = "testSite2";
	private static final String TEST_GLOBAL_CATALOG = "testContentCatalog";
	private static final String TEST_REGION_CATALOG = "testContentCatalog-region";
	private static final String TEST_LOCAL_CATALOG = "testContentCatalog-local";
	private static final String TEST_PRODUCT_CATALOG = "testProductCatalog";
	private static final String TEST_CATALOG_VERSION_ONLINE = "Online";
	private static final String TEST_CATALOG_VERSION_STAGED = "Staged";
	private static final String TEST_GLOBAL_PAGE = "homepage";
	private static final String TEST_REGION_PAGE = "homepage-region";
	private static final String TEST_LOCAL_PAGE = "homepage-local";
	private static final String TEST_VERSION = "homepage-version";
	private static final Date TEST_DATE = new Date();

	@Resource
	private PreviewTicketReversePopulator previewTicketReversePopulator;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	@Before
	public void importCatalogs() throws Exception
	{
		importData(new ClasspathImpExResource("/previewwebservices/test/previewwebservices_testcatalogs.impex", "UTF-8"));
	}

	public void importTypePermissionsAndMakeCmsManagerAsUser() throws Exception
	{
		importCsv("/test/cmsTypePermissionTestData.impex", "UTF-8");
		final UserModel cmsmanager = userService.getUserForUID("cmsmanager");
		userService.setCurrentUser(cmsmanager);
	}

	@Test
	public void shouldHaveOnlineProductAndStagedContentCatalogs()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_STAGED)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertNotNull(target.getPage());
		Assert.assertEquals(target.getPage().getUid(), TEST_GLOBAL_PAGE);
	}

	@Test
	public void shouldPopulateTargetWithPageFromRegionLevelContentCatalog()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForRegionTestSite();
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals(target.getCatalogVersions().size(), 3);
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_REGION_CATALOG, TEST_CATALOG_VERSION_STAGED)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertNotNull(target.getPage());
		Assert.assertEquals(target.getPage().getUid(), TEST_REGION_PAGE);
	}

	@Test
	public void shouldPopulateTargetWithPageFromLocalLevelContentCatalog()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForLocalTestSite();
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals(target.getCatalogVersions().size(), 4);
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_REGION_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_LOCAL_CATALOG, TEST_CATALOG_VERSION_STAGED)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertNotNull(target.getPage());
		Assert.assertEquals(target.getPage().getUid(), TEST_LOCAL_PAGE);
	}

	@Test
	public void shouldNotPopulateTargetWithPageWhenPageIsNotProvided()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setPageId(null);
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);
		Assert.assertNull(target.getPage());
	}

	@Test
	public void shouldNotPopulateTargetWithVersionWhenVersionIsNotProvided()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);
		Assert.assertNull(target.getVersion());
	}

	@Test
	public void shouldPopulateTargetWithVersionWhenVersionIsProvided() throws Exception
	{
		// GIVEN
		importTypePermissionsAndMakeCmsManagerAsUser();
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSiteWithVersion();
		final PreviewDataModel target = new PreviewDataModel();

		// WHEN
		previewTicketReversePopulator.populate(source, target);

		// THEN
		Assert.assertNotNull(target.getVersion());
	}

	@Test(expected = ConversionException.class)
	public void shouldThrowConversionExceptionIfSiteNotFound()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setResourcePath("/cart");
		final PreviewDataModel target = new PreviewDataModel();
		previewTicketReversePopulator.populate(source, target);
	}

	@Test
	public void shouldGetActiveBaseSiteFromTargetIfSourceDTOContainsMalformedResourcePathUrl()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setResourcePath("/cart");
		final PreviewDataModel target = new PreviewDataModel();

		try
		{
			target.setActiveSite(cmsSiteService.getSiteForURL(new URL(getResourcePath(TEST_GLOBAL_SITE))));
		}
		catch (final CMSItemNotFoundException | MalformedURLException e)
		{
			fail("Should NOT throw MalformedURLException");
		}

		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_STAGED)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertNotNull(target.getPage());
	}

	@Test
	public void shouldPopulateModelWithNewBaseSiteForProperResourcePath()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setResourcePath(getResourcePath(TEST_SITE_2));
		final PreviewDataModel target = new PreviewDataModel();

		try
		{
			target.setActiveSite(cmsSiteService.getSiteForURL(new URL(getResourcePath(TEST_GLOBAL_SITE))));
		}
		catch (final CMSItemNotFoundException | MalformedURLException e)
		{
			fail("Should NOT throw MalformedURLException");
		}

		previewTicketReversePopulator.populate(source, target);

		Assert.assertEquals(target.getCatalogVersions().size(), 2);
		Assert.assertEquals(TEST_SITE_2, target.getActiveSite().getUid());
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_STAGED)));
		Assert.assertTrue(target.getCatalogVersions()
				.contains(catalogVersionService.getCatalogVersion(TEST_PRODUCT_CATALOG, TEST_CATALOG_VERSION_ONLINE)));
		Assert.assertNotNull(target.getPage());
	}

	@Test
	public void shouldPopulateModelWithSimpleLanguageCode()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setLanguage("en");
		final PreviewDataModel target = new PreviewDataModel();

		previewTicketReversePopulator.populate(source, target);

		assertThat(target.getLanguage(), not(nullValue()));
		assertThat(target.getLanguage().getIsocode(), equalTo("en"));
	}

	@Test
	public void shouldPopulateModelWithComplexLanguageCode()
	{
		final LanguageModel language = modelService.create(LanguageModel.class);
		language.setIsocode("en_US");
		language.setActive(Boolean.TRUE);
		modelService.save(language);

		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setLanguage("en_US");
		final PreviewDataModel target = new PreviewDataModel();

		previewTicketReversePopulator.populate(source, target);

		assertThat(target.getLanguage(), not(nullValue()));
		assertThat(target.getLanguage().getIsocode(), equalTo("en_US"));
	}

	@Test
	public void shouldSetPreviewTimeValue()
	{
		// GIVEN
		final PreviewTicketWsDTO source = getPreviewTicketForGlobalTestSite();
		source.setTime(TEST_DATE);
		final PreviewDataModel target = new PreviewDataModel();

		// WHEN
		previewTicketReversePopulator.populate(source, target);

		// THEN
		assertThat(target.getTime(), equalTo(TEST_DATE));
	}

	@Test
	public void shouldPopulateModelWithRegionPageWhenNotFoundInLocalCatalog()
	{
		final PreviewTicketWsDTO source = getPreviewTicketForLocalTestSite();
		source.setPageId(TEST_REGION_PAGE);
		final PreviewDataModel target = new PreviewDataModel();

		previewTicketReversePopulator.populate(source, target);

		assertThat(target.getPage(), not(nullValue()));
		assertThat(target.getPage().getUid(), equalTo(TEST_REGION_PAGE));
	}

	private PreviewTicketWsDTO getPreviewTicketForGlobalTestSiteWithVersion()
	{
		return createPreviewTicketDTO(TEST_GLOBAL_SITE, TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_STAGED, TEST_GLOBAL_PAGE,
				TEST_VERSION);
	}

	private PreviewTicketWsDTO getPreviewTicketForGlobalTestSite()
	{
		return createPreviewTicketDTO(TEST_GLOBAL_SITE, TEST_GLOBAL_CATALOG, TEST_CATALOG_VERSION_STAGED, TEST_GLOBAL_PAGE, null);
	}

	private PreviewTicketWsDTO getPreviewTicketForRegionTestSite()
	{
		return createPreviewTicketDTO(TEST_REGION_SITE, TEST_REGION_CATALOG, TEST_CATALOG_VERSION_STAGED, TEST_REGION_PAGE, null);
	}

	private PreviewTicketWsDTO getPreviewTicketForLocalTestSite()
	{
		return createPreviewTicketDTO(TEST_LOCAL_SITE, TEST_LOCAL_CATALOG, TEST_CATALOG_VERSION_STAGED, TEST_LOCAL_PAGE, null);
	}

	private PreviewTicketWsDTO createPreviewTicketDTO(final String siteId, final String catalogId, final String catalogVersion,
			final String pageId, final String versionId)
	{
		final PreviewTicketWsDTO source = new PreviewTicketWsDTO();

		final CatalogVersionWsDTO cv = new CatalogVersionWsDTO();
		cv.setCatalog(catalogId);
		cv.setCatalogVersion(catalogVersion);

		source.setCatalogVersions(Collections.singletonList(cv));

		source.setResourcePath(getResourcePath(siteId));
		source.setPageId(pageId);
		source.setVersionId(versionId);
		return source;
	}

	private String getResourcePath(final String siteId)
	{
		return SITE_URL + siteId;
	}
}
