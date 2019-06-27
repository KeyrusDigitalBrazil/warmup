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

package de.hybris.platform.configurablebundleservices.interceptor.impl;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.configurablebundleservices.enums.BundleTemplateStatusEnum;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateStatusModel;


/**
 * Prepare interceptor that
 * <ul>
 * <li>removes the older {@link BundleSelectionCriteriaModel} from {@link BundleTemplateModel} when it is updated
 * <li>creates and/or assigns the {@link BundleTemplateStatusModel} with status {@link BundleTemplateStatusEnum}
 * .UNAPPROVED for new {@link BundleTemplateModel}s
 * </ul>
 */
public class BundleTemplatePrepareInterceptor implements PrepareInterceptor
{

	@Override
	public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof BundleTemplateModel)
		{
			final BundleTemplateModel bundleTemplate = (BundleTemplateModel) model;
			final ModelService modelService = ctx.getModelService();
			if (bundleTemplate.getPk() != null)
			{
				final BundleTemplateModel dbBundleTemplate = modelService.get(bundleTemplate.getPk());
				final BundleSelectionCriteriaModel dbSelectionCriteria = dbBundleTemplate.getBundleSelectionCriteria();
				if (dbSelectionCriteria != null && !dbSelectionCriteria.equals(bundleTemplate.getBundleSelectionCriteria()))
				{
					modelService.remove(dbSelectionCriteria);
				}
			}

			if (bundleTemplate.getStatus() == null)
			{
				if (bundleTemplate.getParentTemplate() == null)
				{
					final BundleTemplateStatusModel bundleStatus = modelService.create(BundleTemplateStatusModel.class);
					bundleStatus.setCatalogVersion(bundleTemplate.getCatalogVersion());
					bundleStatus.setStatus(BundleTemplateStatusEnum.UNAPPROVED);
					modelService.save(bundleStatus);
					bundleTemplate.setStatus(bundleStatus);
				}
				else
				{
					final BundleTemplateModel parentTemplate = bundleTemplate.getParentTemplate();
					bundleTemplate.setStatus(parentTemplate.getStatus());
				}
			}
		}
	}

}
