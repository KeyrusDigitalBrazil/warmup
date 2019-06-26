/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.warehousing.model.RestockConfigModel;
import de.hybris.platform.warehousing.util.builder.RestockConfigModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import org.springframework.beans.factory.annotation.Required;


public class RestockConfigs extends AbstractItems<RestockConfigModel>
{
	public static final String CODE_RETURNED_BIN = "returned_bin";

	private WarehousingDao<RestockConfigModel> restockConfigDao;

	public RestockConfigModel RestockAfterReturn()
	{
		return getOrSaveAndReturn(() -> getRestockConfigDao().getByCode(""),
				() -> RestockConfigModelBuilder.aModel()
						.withDelayDaysBeforeRestock(0)
						.withIsUpdateStockAfterReturn(true)
						.withReturnedBin(CODE_RETURNED_BIN)
				.build());
	}

	public WarehousingDao<RestockConfigModel> getRestockConfigDao()
	{
		return restockConfigDao;
	}

	@Required
	public void setRestockConfigDao(final WarehousingDao<RestockConfigModel> restockConfigDao)
	{
		this.restockConfigDao = restockConfigDao;
	}
}
