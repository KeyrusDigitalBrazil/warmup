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
package de.hybris.platform.ruleengine.dao.impl;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleMediaModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultDroolsKIEModuleMediaDaoIT extends ServicelayerTest
{
	private static final String RELEASE_ID = "groupId:artifactId:version";
	private static final String KIE_MODULE_NAME = "kieModuleName";

	@Resource(name = "defaultDroolsKIEModuleMediaDao")
	private DefaultDroolsKIEModuleMediaDao defaultDroolsKIEModuleMediaDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		final DroolsKIEModuleMediaModel droolsKIEModuleMedia = modelService.create(DroolsKIEModuleMediaModel.class);
		droolsKIEModuleMedia.setCode("code");
		droolsKIEModuleMedia.setKieModuleName(KIE_MODULE_NAME);
		droolsKIEModuleMedia.setReleaseId(RELEASE_ID);
		modelService.saveAll(droolsKIEModuleMedia);
	}

	@Test
	public void testFindKIEModuleMedia()
	{
		final Optional<DroolsKIEModuleMediaModel> droolsKIEModuleMediaOptional = defaultDroolsKIEModuleMediaDao.findKIEModuleMedia(
				KIE_MODULE_NAME, RELEASE_ID);
		assertThat(droolsKIEModuleMediaOptional.isPresent()).isTrue();
		final DroolsKIEModuleMediaModel droolsKIEModuleMedia = droolsKIEModuleMediaOptional.get();
		assertThat(droolsKIEModuleMedia.getKieModuleName()).isEqualTo(KIE_MODULE_NAME);
		assertThat(droolsKIEModuleMedia.getReleaseId()).isEqualTo(RELEASE_ID);
	}

	@Test
	public void testFindKIEMMediaNotFound()
	{
		assertThat(defaultDroolsKIEModuleMediaDao.findKIEModuleMedia("invalid", RELEASE_ID).isPresent()).isFalse();
		assertThat(defaultDroolsKIEModuleMediaDao.findKIEModuleMedia(KIE_MODULE_NAME, "invalid").isPresent()).isFalse();
	}
}