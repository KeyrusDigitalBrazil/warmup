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
package de.hybris.platform.cmsfacades.rendering.converter;

import static de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother.TemplateSite.ELECTRONICS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.containers.ABTestCMSComponentContainerModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2lib.model.components.FlashComponentModel;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.ABTestCMSComponentContainerModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotForPageModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotModelMother;
import de.hybris.platform.cmsfacades.util.models.ContentSlotNameModelMother;
import de.hybris.platform.cmsfacades.util.models.FlashComponentModelMother;
import de.hybris.platform.cmsfacades.util.models.MediaModelMother;
import de.hybris.platform.cmsfacades.util.models.PageTemplateModelMother;
import de.hybris.platform.cmsfacades.util.models.ParagraphComponentModelMother;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class PageModelToDataRenderingConverterIntegrationTest extends BaseIntegrationTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String STAGED_CATALOG_VERSION = "ID_APPLE/staged";
	private final String MEDIA_KEY = "media";
	private final String COMPONENTS_KEY = "components";

	@Resource
	private I18NService i18NService;

	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;

	@Resource
	private CMSSiteModelMother cmsSiteModelMother;

	@Resource
	private ContentPageModelMother contentPageModelMother;

	@Resource
	private ContentSlotForPageModelMother contentSlotForPageModelMother;

	@Resource
	private Converter<AbstractPageModel, AbstractPageData> cmsPageModelToDataRenderingConverter;

	@Resource
	private CatalogVersionService catalogVersionService;

	private CatalogVersionModel catalogVersion;
	private ContentPageModel contentPageModel;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	protected void createElectronicsSite()
	{
		cmsSiteModelMother.createSiteWithTemplate(ELECTRONICS, catalogVersion);
	}

	protected void createEmptyAppleCatalog()
	{
		catalogVersion = catalogVersionModelMother.createAppleStagedCatalogVersionModel();
		catalogVersionService.setSessionCatalogVersion(catalogVersion.getCatalog().getId(), catalogVersion.getVersion());
	}

	@Before
	public void setUp()
	{
		i18NService.setCurrentLocale(Locale.ENGLISH);
		setCurrentUserCmsManager();

		createEmptyAppleCatalog();
		createElectronicsSite();

		contentSlotForPageModelMother.HeaderHomePage_ContainerWithParagraphs(catalogVersion);
		contentSlotForPageModelMother.FooterHomepage_FlashComponentOnly(catalogVersion);
		contentPageModel = contentPageModelMother.homePage(catalogVersion);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void whenPageIsConvertedForRendering_ThenAnAbstractPageDataIsReturnedWithTheRightInformation()
	{
		// WHEN
		final AbstractPageData pageData = cmsPageModelToDataRenderingConverter.convert(contentPageModel);

		// THEN
		hasValidPageData(pageData);
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void hasValidPageData(final AbstractPageData pageData)
	{
		assertThat(pageData.getUid(), is(contentPageModelMother.UID_HOMEPAGE));
		assertThat(pageData.getTypeCode(), is(ContentPageModel._TYPECODE));
		assertThat(pageData.getDefaultPage(), is(false));
		assertThat(pageData.getName(), is(contentPageModelMother.NAME_HOMEPAGE));
		assertThat(pageData.getTemplate(), is(PageTemplateModelMother.UID_HOME_PAGE));
		assertThat(pageData.getLocalizedTitle(), is(contentPageModelMother.NAME_HOMEPAGE + contentPageModelMother.TITLE_SUFFIX));

		hasValidHeaderSlotData(pageData.getContentSlots().get(0));
		hasValidFooterSlotData(pageData.getContentSlots().get(1));
	}

	protected void hasValidHeaderSlotData(final PageContentSlotData contentSlotData)
	{
		assertThat(contentSlotData.getSlotId(), is(ContentSlotModelMother.UID_HEADER));
		assertThat(contentSlotData.getPosition(), is(ContentSlotNameModelMother.NAME_HEADER));
		assertThat(contentSlotData.getCatalogVersion(), is(STAGED_CATALOG_VERSION));
		assertThat(contentSlotData.isSlotShared(), is(false));

		hasValidABTestComponentData(contentSlotData.getComponents().get(0));
	}

	protected void hasValidFooterSlotData(final PageContentSlotData contentSlotData)
	{
		assertThat(contentSlotData.getSlotId(), is(ContentSlotModelMother.UID_FOOTER));
		assertThat(contentSlotData.getPosition(), is(ContentSlotNameModelMother.NAME_FOOTER));
		assertThat(contentSlotData.getCatalogVersion(), is(STAGED_CATALOG_VERSION));
		assertThat(contentSlotData.isSlotShared(), is(false));

		hasValidMediaComponentData(contentSlotData.getComponents().get(0));
	}

	@SuppressWarnings("unchecked")
	protected void hasValidABTestComponentData(final AbstractCMSComponentData componentData)
	{
		assertThat(componentData.getModifiedtime(), notNullValue());
		assertThat(componentData.getCatalogVersion(), is(STAGED_CATALOG_VERSION));
		assertThat(componentData.getUid(), is(ABTestCMSComponentContainerModelMother.UID_HEADER));
		assertThat(componentData.getTypeCode(), is(ABTestCMSComponentContainerModel._TYPECODE));
		assertThat(componentData.getOtherProperties().isEmpty(), is(false));


		final List<String> nestedComponents = (List<String>) componentData.getOtherProperties().get(COMPONENTS_KEY);
		assertThat(nestedComponents.get(0), is(ParagraphComponentModelMother.UID_HEADER));
		assertThat(nestedComponents.get(1), is(ParagraphComponentModelMother.UID_FOOTER));
	}

	@SuppressWarnings("unchecked")
	protected void hasValidMediaComponentData(final AbstractCMSComponentData componentData)
	{
		assertThat(componentData.getModifiedtime(), notNullValue());
		assertThat(componentData.getCatalogVersion(), is(STAGED_CATALOG_VERSION));
		assertThat(componentData.getUid(), is(FlashComponentModelMother.UID_FOOTER));
		assertThat(componentData.getName(), is(FlashComponentModelMother.NAME_FOOTER));
		assertThat(componentData.getTypeCode(), is(FlashComponentModel._TYPECODE));
		assertThat(componentData.getOtherProperties().isEmpty(), is(false));

		final MediaModelMother.MediaTemplate template = MediaModelMother.MediaTemplate.LOGO;

		final MediaData media = (MediaData) componentData.getOtherProperties().get(MEDIA_KEY);
		assertThat(media.getUrl(), is(template.getUrl()));
		assertThat(media.getCode(), is(template.getCode()));
		assertThat(media.getMime(), is(template.getMimetype()));
		assertThat(media.getAltText(), is(template.getAltText()));
		assertThat(media.getDownloadUrl(), is(template.getUrl()));
		assertThat(media.getDescription(), is(template.getDescription()));
	}
}
