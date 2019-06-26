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
package de.hybris.platform.personalizationcms.service;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.misc.CMSFilter;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationcms.data.CxCmsActionResult;
import de.hybris.platform.personalizationservices.RecalculateAction;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class MulticountyCxCmsRecalculationServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String TOP_CATALOG = "multiCatalog1";
	private static final String MID_CATALOG = "multiCatalog2";
	private static final String LEAF_1_CATALOG = "multiCatalog3a";
	private static final String VERSION = "Online";

	private static final String TICKET_ID = "previewTicketId";

	@Resource
	private CxCmsRecalculationService cxCmsRecalculationService;
	@Resource
	private CxService cxService;
	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private SessionService sessionService;
	@Resource
	private CMSPreviewService cmsPreviewService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private ModelService modelService;


	UserModel user;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/personalizationcms/test/testdata_personalizationcms_multicountry.impex", "utf-8");

		//		cmsAdminSiteService.setActiveSiteForId(SITE_ID);

		sessionService.setAttribute(CMSFilter.PREVIEW_TICKET_ID_PARAM, TICKET_ID);
		user = new UserModel();
	}

	private CatalogVersionModel getCatalogVersion(final String catalog)
	{
		return catalogVersionService.getCatalogVersion(catalog, VERSION);
	}

	private CxVariationModel getVariarion(final String code, final CatalogVersionModel catalogVersion)
	{
		final CxVariationModel example = new CxVariationModel();
		example.setCode(code);
		example.setCatalogVersion(catalogVersion);
		return flexibleSearchService.getModelByExample(example);
	}

	private CxCmsActionResult buildActionResult(final String cust, final String var, final String code)
	{
		final CxCmsActionResult result = new CxCmsActionResult();
		result.setActionCode(code);
		result.setCustomizationCode(cust);
		result.setVariationCode(var);
		return result;
	}

	private CMSPreviewTicketModel createPreviewTicket(final Collection<CatalogVersionModel> catalogs,
			final Collection<CxVariationModel> variations)
	{
		final PreviewDataModel dataModel = new PreviewDataModel();
		dataModel.setCatalogVersions(catalogs);
		dataModel.setVariations(variations);
		dataModel.setLiveEdit(Boolean.FALSE);

		final CMSPreviewTicketModel result = new CMSPreviewTicketModel();
		result.setId(TICKET_ID);
		result.setPreviewData(dataModel);

		modelService.save(result);


		return result;
	}

	@Test
	public void checkActionsForSingleVariation()
	{
		//given
		final CatalogVersionModel topCatalog = getCatalogVersion(TOP_CATALOG);
		final CxVariationModel variarion = getVariarion("variation1", topCatalog);
		createPreviewTicket(Arrays.asList(topCatalog), Arrays.asList(variarion));

		catalogVersionService.setSessionCatalogVersions(Arrays.asList(topCatalog));

		final List<CxCmsActionResult> expected = Arrays.asList(buildActionResult("customization1", "variation1", "cmsaction1"));

		//when
		cxCmsRecalculationService.recalculate(user, Arrays.asList(RecalculateAction.RECALCULATE));

		//then
		final List<CxAbstractActionResult> actual = cxService.getActionResultsFromSession(user);

		assertEquals(expected, actual);
	}

	@Test
	public void checkActionsForMultipleVariations()
	{
		//given
		final CatalogVersionModel topCatalog = getCatalogVersion(TOP_CATALOG);
		final CxVariationModel variarion1 = getVariarion("variation1", topCatalog);
		final CxVariationModel variarion2 = getVariarion("variation2", topCatalog);
		createPreviewTicket(Arrays.asList(topCatalog), Arrays.asList(variarion1, variarion2));

		catalogVersionService.setSessionCatalogVersions(Arrays.asList(topCatalog));

		final List<CxCmsActionResult> expected = Arrays.asList(buildActionResult("customization1", "variation1", "cmsaction1"),
				buildActionResult("customization2", "variation2", "cmsaction2"));

		//when
		cxCmsRecalculationService.recalculate(user, Arrays.asList(RecalculateAction.RECALCULATE));

		//then
		final List<CxAbstractActionResult> actual = cxService.getActionResultsFromSession(user);

		assertEquals(expected, actual);
	}

	@Test
	public void checkActionsForMultipleVariationsMulticountry()
	{
		//given
		final CatalogVersionModel topCatalog = getCatalogVersion(TOP_CATALOG);
		final CatalogVersionModel midCatalog = getCatalogVersion(MID_CATALOG);
		final CatalogVersionModel leafCatalog = getCatalogVersion(LEAF_1_CATALOG);

		final CxVariationModel variarion1 = getVariarion("variation1", topCatalog);
		final CxVariationModel variarion2 = getVariarion("variation2", topCatalog);

		final CxVariationModel variarion3 = getVariarion("variation3", leafCatalog);
		final CxVariationModel variarion4 = getVariarion("variation4", leafCatalog);

		createPreviewTicket(Arrays.asList(midCatalog, topCatalog, leafCatalog),
				Arrays.asList(variarion3, variarion1, variarion2, variarion4));

		catalogVersionService.setSessionCatalogVersions(Arrays.asList(topCatalog, midCatalog, leafCatalog));

		final List<CxCmsActionResult> expected = Arrays.asList(buildActionResult("customization3", "variation3", "cmsaction5"),
				buildActionResult("customization4", "variation4", "cmsaction8"),
				buildActionResult("customization1", "variation1", "cmsaction1"),
				buildActionResult("customization2", "variation2", "cmsaction2"));

		//when
		cxCmsRecalculationService.recalculate(user, Arrays.asList(RecalculateAction.RECALCULATE));

		//then
		final List<CxAbstractActionResult> actual = cxService.getActionResultsFromSession(user);

		assertEquals(expected, actual);
	}

	private void assertEquals(final List<CxCmsActionResult> expected, final List<CxAbstractActionResult> actual)
	{
		final List<String> expectedCode = expected.stream()
				.map(ar -> ar.getCustomizationCode() + ":" + ar.getVariationCode() + ":" + ar.getActionCode())
				.collect(Collectors.toList());
		final List<String> actualCode = actual.stream()
				.map(ar -> ar.getCustomizationCode() + ":" + ar.getVariationCode() + ":" + ar.getActionCode())
				.collect(Collectors.toList());

		Assert.assertEquals(expectedCode, actualCode);

	}
}
