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
package de.hybris.platform.marketplaceservices.cronjob;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.marketplaceservices.strategies.VendorOrderExportStrategy;
import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;


/**
 * Abstract base job for exporting vendor orders into csv file
 */
public class VendorOrderExportJob extends AbstractJobPerformable<CronJobModel>
{
	private VendorDao vendorDao;
	private TaskExecutor taskExecutor;
	private VendorOrderExportStrategy vendorOrderExportStrategy;

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		final List<VendorModel> activeVendors = getVendorDao().findActiveVendors();
		activeVendors.forEach(vendor -> {
			final String vendorCode = vendor.getCode();
			if (getVendorOrderExportStrategy().readyToExportOrdersForVendor(vendorCode))
			{
				taskExecutor.execute(new VendorOrderExportTask(getVendorOrderExportStrategy(), vendorCode));
			}
		});
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected VendorDao getVendorDao()
	{
		return vendorDao;
	}

	@Required
	public void setVendorDao(final VendorDao vendorDao)
	{
		this.vendorDao = vendorDao;
	}

	protected TaskExecutor getTaskExecutor()
	{
		return taskExecutor;
	}

	@Required
	public void setTaskExecutor(final TaskExecutor taskExecutor)
	{
		this.taskExecutor = taskExecutor;
	}

	protected VendorOrderExportStrategy getVendorOrderExportStrategy()
	{
		return vendorOrderExportStrategy;
	}

	@Required
	public void setVendorOrderExportStrategy(final VendorOrderExportStrategy vendorOrderExportStrategy)
	{
		this.vendorOrderExportStrategy = vendorOrderExportStrategy;
	}

}
