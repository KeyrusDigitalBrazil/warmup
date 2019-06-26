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
package de.hybris.platform.importcockpit.services.mapping.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.importcockpit.model.ImportCockpitCronJobModel;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.model.mappingview.impl.DefaultMappingModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultImportCockpitMappingServiceTest extends ServicelayerTransactionalTest
{
	@Resource
	private DefaultImportCockpitMappingService defaultImportCockpitMappingService;
	@Resource
	private ModelService modelService;

	@Test
	public void saveMappingToProcessModel() throws Exception
	{
		//given
		final CatalogVersionModel catalogVersionModel = createNewCatalogVersion();

		final MappingModel mappingModel = new DefaultMappingModel();
		mappingModel.setCatalogVersion(catalogVersionModel);
		final ImportCockpitCronJobModel jobModel = new ImportCockpitCronJobModel();

		//when
		defaultImportCockpitMappingService.saveMappingToProcessModel(mappingModel, "mappingName", jobModel, false);

		//then
		assertThat(jobModel.getMapping()).isNotNull();
		assertThat(jobModel.getMapping().getCatalogVersion()).isNotNull();

		final CatalogVersionModel savedCatVersion = jobModel.getMapping().getCatalogVersion();
		assertThat(savedCatVersion.getCatalog().getId()).isEqualTo(catalogVersionModel.getCatalog().getId());
		assertThat(savedCatVersion.getActive()).isFalse();
		assertThat(savedCatVersion.getPk()).isEqualTo(catalogVersionModel.getPk());
	}

	private CatalogVersionModel createNewCatalogVersion()
	{
		final CatalogModel newCatalog = modelService.create(CatalogModel.class);
		newCatalog.setId("testCatalogId");
		newCatalog.setName("TestCatalog");
		modelService.save(newCatalog);

		final CatalogVersionModel newCatalogVersion = modelService.create(CatalogVersionModel.class);
		newCatalogVersion.setCatalog(newCatalog);
		newCatalogVersion.setVersion("version");
		modelService.save(newCatalogVersion);

		return newCatalogVersion;
	}
}
