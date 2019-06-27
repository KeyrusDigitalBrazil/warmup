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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.util.BaseIntegrationTest;
import de.hybris.platform.cmsfacades.util.models.CMSNavigationNodeModelMother;
import de.hybris.platform.cmsfacades.util.models.CMSSiteModelMother;
import de.hybris.platform.cmsfacades.util.models.CatalogVersionModelMother;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ComponentModelToDataRenderingConverterIntegrationTest extends BaseIntegrationTest
{
	private CatalogVersionModel catalogVersion;

	@Resource
	private Converter<CMSNavigationNodeModel, NavigationNodeData> cmsRenderingNavigationNodeToDataContentConverter;
	@Resource
	private CMSNavigationNodeModelMother navigationNodeModelMother;
	@Resource
	private I18NService i18NService;
	@Resource
	private CatalogVersionModelMother catalogVersionModelMother;
	@Resource
	private CMSSiteModelMother cmsSiteModelMother;
	@Resource
	private CatalogVersionService catalogVersionService;

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

		createEmptyAppleCatalog();
		createElectronicsSite();
	}

	@Test
	public void test()
	{
		final CMSNavigationNodeModel rootNode = navigationNodeModelMother.createNavigationNodeTree(catalogVersion);

		final NavigationNodeData navigationNodeData = cmsRenderingNavigationNodeToDataContentConverter.convert(rootNode);

		assertThat(navigationNodeData.getUid(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_ROOT_UID));
		assertThat(navigationNodeData.getName(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_ROOT_NAME));
		assertThat(navigationNodeData.getLocalizedTitle(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_ROOT_TITLE));
		assertThat(navigationNodeData.getEntries(), empty());
		assertThat(navigationNodeData.getChildren(), not(empty()));

		final NavigationNodeData childNode = navigationNodeData.getChildren().get(0);
		assertThat(childNode.getUid(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_CHILD_UID));
		assertThat(childNode.getName(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_CHILD_NAME));
		assertThat(childNode.getLocalizedTitle(), equalTo(CMSNavigationNodeModelMother.NAVIGATION_NODE_CHILD_TITLE));
		assertThat(childNode.getEntries(), not(empty()));
		assertThat(childNode.getChildren(), empty());
	}

}
