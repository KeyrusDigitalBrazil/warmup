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
package de.hybris.platform.cms2.servicelayer.interceptor.impl;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import javax.annotation.Resource;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class CMSRelationInitDefaultsInterceptorIntegrationTest extends ServicelayerTest // NOPMD: Junit4 allows any name for test method
{
	@Resource
	ModelService modelService;

	@Test
	public void shouldCreateCMSRelationModelWithGeneratedUid()
	{
		// given
		final CMSRelationModel cmsRelation = modelService.create(CMSRelationModel.class);

		// when
		final String uid = cmsRelation.getUid();

		// then
		assertThat(uid).isNotNull();
		assertThat(uid).isNotEmpty();
	}

}
