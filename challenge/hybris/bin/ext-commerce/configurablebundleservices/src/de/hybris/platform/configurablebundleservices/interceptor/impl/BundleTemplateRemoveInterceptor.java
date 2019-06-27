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
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * If a parent {@link BundleTemplateModel} is deleted all child {@link BundleTemplateModel}s are also removed
 */
public class BundleTemplateRemoveInterceptor implements RemoveInterceptor
{

	@Override
	public void onRemove(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof BundleTemplateModel)
		{
			final BundleTemplateModel bundleTemplate = (BundleTemplateModel) model;

			if (bundleTemplate.getParentTemplate() == null)
			{
				if (CollectionUtils.isNotEmpty(bundleTemplate.getChildTemplates()))
				{
					removeChildTemplates(ctx, bundleTemplate);
				}

				if (bundleTemplate.getStatus() != null)
				{
					removeStatus(ctx, bundleTemplate);
				}
			}
		}
	}

	protected void removeStatus(final InterceptorContext ctx, final BundleTemplateModel bundleTemplate)
	{
		ctx.getModelService().remove(bundleTemplate.getStatus());
		ctx.getModelService().refresh(bundleTemplate);
	}

	protected void removeChildTemplates(final InterceptorContext ctx, final BundleTemplateModel bundleTemplate)
	{
		final List<BundleTemplateModel> childTemplates = bundleTemplate.getChildTemplates();
		for (final BundleTemplateModel childTemplate : childTemplates)
		{
			ctx.getModelService().remove(childTemplate);
			ctx.getModelService().refresh(bundleTemplate);
		}
	}
}
