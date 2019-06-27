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
package de.hybris.platform.sap.ysapomsfulfillment.actions.consignment;

import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import org.apache.log4j.Logger;


/**
 * Redirects to the proper wait node depending on whether a consignment is for ship or pickup.
 */
public class SapRedirectConsignmentByDeliveryModeAction extends AbstractProceduralAction<SapConsignmentProcessModel>
{
	
	private static final Logger LOG = Logger.getLogger(SapRedirectConsignmentByDeliveryModeAction.class);
	@Override
	public void executeAction(SapConsignmentProcessModel process) throws Exception {
		LOG.info(String.format("Process: %s in step %s",process.getCode(), getClass().getSimpleName()));
			
	}
	
}
