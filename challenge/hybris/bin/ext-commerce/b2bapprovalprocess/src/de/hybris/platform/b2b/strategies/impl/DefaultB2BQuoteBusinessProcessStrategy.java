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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.constants.B2BApprovalConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.BusinessProcessStrategy;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.constants.ProcessengineConstants;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BQuoteBusinessProcessStrategy implements BusinessProcessStrategy
{
	private BusinessProcessService businessProcessService;
	private KeyGenerator processCodeGenerator;
	private String processName;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private static final Logger log = Logger.getLogger(DefaultB2BQuoteBusinessProcessStrategy.class);
	private String processCode;
	private ModelService modelService;

	@Override
	public void createB2BBusinessProcess(final OrderModel order)
	{
		final Map<String, Object> contextParams = new HashMap<String, Object>();
		contextParams.put(ProcessengineConstants.EVENT_AFTER_WORKFLOW_PARAM_NAME,
				B2BApprovalConstants.APPROVAL_WORKFLOW_COMPLETE_EVENT);
		final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();

		final B2BApprovalProcessModel quoteProcess = (B2BApprovalProcessModel) getBusinessProcessService().createProcess(
				String.valueOf(processCodeGenerator.generate()), getProcessCode(), contextParams);
		quoteProcess.setOrder(order);
		this.getModelService().save(quoteProcess);
		getBusinessProcessService().startProcess(quoteProcess);
		if (log.isDebugEnabled())
		{
			log.debug(String.format("Started Business process '%s' code: %s for order %s placed by %s", getProcessCode(),
					quoteProcess.getCode(), order.getCode(), orderUser.getUid()));
		}
	}


	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}


	protected KeyGenerator getProcessCodeGenerator()
	{
		return processCodeGenerator;
	}

	@Required
	public void setProcessCodeGenerator(final KeyGenerator processCodeGenerator)
	{
		this.processCodeGenerator = processCodeGenerator;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public String getProcessName()
	{
		return processName;
	}

	public void setProcessName(final String processName)
	{
		this.processName = processName;
	}

	protected String getProcessCode()
	{
		return processCode;
	}

	public void setProcessCode(final String processCode)
	{
		this.processCode = processCode;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
