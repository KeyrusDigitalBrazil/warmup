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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.constants.ProcessengineConstants;
import de.hybris.platform.processengine.helpers.ProcessParameterHelper;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import org.springframework.beans.factory.annotation.Required;


public class B2BAfterWorkflowAction extends B2BAbstractWorkflowAutomatedAction
{
	private ProcessParameterHelper processParameterHelper;
	private BusinessProcessService businessProcessService;

	@Override
	public void performAction(final WorkflowActionModel action)
	{

		for (final ItemModel attachment : action.getAttachmentItems())
		{
			if (attachment instanceof BusinessProcessModel)
			{
				final BusinessProcessModel process = (BusinessProcessModel) attachment;
				final String eventName = (String) this.getProcessParameterHelper()
						.getProcessParameterByName(process, ProcessengineConstants.EVENT_AFTER_WORKFLOW_PARAM_NAME).getValue();
				if (eventName != null)
				{
					this.getBusinessProcessService().triggerEvent(process.getCode() + "_" + eventName);
				}
			}
		}
	}

	protected ProcessParameterHelper getProcessParameterHelper()
	{
		return processParameterHelper;
	}

	@Required
	public void setProcessParameterHelper(final ProcessParameterHelper processParameterHelper)
	{
		this.processParameterHelper = processParameterHelper;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
