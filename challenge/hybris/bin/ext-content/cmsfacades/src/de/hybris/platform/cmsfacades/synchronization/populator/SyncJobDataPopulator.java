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
package de.hybris.platform.cmsfacades.synchronization.populator;

import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;


/**
 * This populator will populate the {@link SyncJobData} from the {@link CronJobModel}.
 */
public class SyncJobDataPopulator implements Populator<Optional<CronJobModel>, SyncJobData>
{

	@Override
	public void populate(final Optional<CronJobModel> source, final SyncJobData target) throws ConversionException
	{

		source.ifPresent(src -> {
			target.setEndDate(src.getEndTime());
			target.setStartDate(src.getStartTime());
			target.setSyncStatus(src.getStatus().name());
			target.setSyncResult(src.getResult().name());
			target.setLastModifiedDate(src.getModifiedtime());
			target.setCreationDate(src.getCreationtime());
		});
	}
}
