/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.platform.sapcpconfiguration.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import com.sap.platform.factory.SapCharonFactory;

import org.springframework.beans.factory.annotation.Required;

import com.sap.platform.model.CecServiceModel;
import com.sap.platform.model.CecTechnicalUserModel;
/**
 * 
 * This class is used to clear the cache if CecService or CecTechncialUser model modified
 *
 */
public class CecModelInterceptor implements RemoveInterceptor, ValidateInterceptor {
	private SapCharonFactory sapCharonFactory;

	private ModelService modelService;

	@Override
	public void onValidate(final Object model, final InterceptorContext arg1) throws InterceptorException {

		inValidateClient(model);

	}

	@Override
	public void onRemove(final Object model, final InterceptorContext arg1) throws InterceptorException {

		inValidateClient(model);

	}

	private void inValidateClient(final Object model) {
		if (model instanceof CecServiceModel) {

			final CecServiceModel cecServiceModel = (CecServiceModel) model;
			sapCharonFactory.inValidateCache(cecServiceModel.getIdentifier().toString());

		} else if (model instanceof CecTechnicalUserModel) {

			final CecTechnicalUserModel cecTechnicalUserModel = (CecTechnicalUserModel) model;
			sapCharonFactory.inValidateCache(cecTechnicalUserModel.getTenantName());
		}
	}

	public SapCharonFactory getSapCharonFactory() {
		return sapCharonFactory;
	}

	@Required
	public void setSapCharonFactory(final SapCharonFactory sapCharonFactory) {
		this.sapCharonFactory = sapCharonFactory;
	}

	public ModelService getModelService() {
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

}
