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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.warehousing.model.SourcingConfigModel;
import de.hybris.platform.warehousing.util.builder.SourcingConfigModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import org.springframework.beans.factory.annotation.Required;


public class SourcingConfigs extends AbstractItems<SourcingConfigModel>
{
	public static final String SOURCING_CONFIG_NAME = "hybris";

	private WarehousingDao<SourcingConfigModel> sourcingConfigDao;

	public SourcingConfigModel HybrisConfig()
	{
		return getOrSaveAndReturn(() -> getSourcingConfigDao().getByCode(SOURCING_CONFIG_NAME),
				() -> SourcingConfigModelBuilder.aModel()
				.withCode(SOURCING_CONFIG_NAME)
				.withSourcingFactorsWeight(40,30,20,10)
				.build());
	}

	public WarehousingDao<SourcingConfigModel> getSourcingConfigDao()
	{
		return sourcingConfigDao;
	}

	@Required
	public void setSourcingConfigDao(final WarehousingDao<SourcingConfigModel> sourcingConfigDao)
	{
		this.sourcingConfigDao = sourcingConfigDao;
	}

}
