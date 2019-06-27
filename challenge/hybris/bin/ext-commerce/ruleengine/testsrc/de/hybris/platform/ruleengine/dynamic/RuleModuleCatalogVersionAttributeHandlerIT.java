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
package de.hybris.platform.ruleengine.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class RuleModuleCatalogVersionAttributeHandlerIT extends ServicelayerTest
{

	@Resource
	private RulesModuleDao rulesModuleDao;
	@Resource
	private RuleModuleCatalogVersionAttributeHandler AbstractRulesModule_catalogVersionsAttributeHandler;      // NOSONAR

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		importCsv("/ruleengine/test/mappings/mappings-test-data.impex", "UTF-8");
	}

	@Test
	public void testGet()
	{
		final AbstractRulesModuleModel previewModule = rulesModuleDao.findByName("catmap01-preview-module");
		final Collection<CatalogVersionModel> catalogVersionModels = AbstractRulesModule_catalogVersionsAttributeHandler.get(previewModule);

		assertThat(catalogVersionModels).hasSize(1);
		final CatalogVersionModel catalogVersion = catalogVersionModels.iterator().next();
		assertThat(catalogVersion.getCatalog().getId()).isEqualTo("catmap01");
		assertThat(catalogVersion.getVersion()).isEqualToIgnoringCase("staged");
	}

	@Test
	public void testGetNoMapping()
	{
		final AbstractRulesModuleModel previewModule = rulesModuleDao.findByName("catmap03-live-module");
		final Collection<CatalogVersionModel> catalogVersionModels = AbstractRulesModule_catalogVersionsAttributeHandler.get(previewModule);

		assertThat(catalogVersionModels).isEmpty();
	}

}
