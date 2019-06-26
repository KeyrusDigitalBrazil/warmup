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

package de.hybris.platform.yaasconfiguration.service.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.yaasconfiguration.CharonFactory;
import de.hybris.platform.yaasconfiguration.model.YaasClientCredentialModel;
import de.hybris.platform.yaasconfiguration.model.YaasServiceModel;

import org.springframework.beans.factory.annotation.Required;


public class YaasInterceptor implements RemoveInterceptor, ValidateInterceptor
{

	private CharonFactory charonFactory;

	private ModelService modelService;

	@Override
	public void onRemove(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		notifyClient(model);
	}


	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (!modelService.isNew(model))
		{
			notifyClient(model);
		}

	}

	protected void notifyClient(final Object model)
	{
		if (model instanceof YaasServiceModel)
		{
			final YaasServiceModel yaasServiceModel = (YaasServiceModel) model;

			// notify to invalidate all the client associated to given yaasService.
			charonFactory.inValidateCache(yaasServiceModel.getIdentifier());
		}
		else if (model instanceof YaasClientCredentialModel)
		{
			final YaasClientCredentialModel yaasClientCredentialModel = (YaasClientCredentialModel) model;

			// notify to invalidate the specific yaas client.
			charonFactory.inValidateCache(yaasClientCredentialModel.getIdentifier());
		}
	}


	@Required
	public void setCharonFactory(final CharonFactory charonFactory)
	{
		this.charonFactory = charonFactory;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
