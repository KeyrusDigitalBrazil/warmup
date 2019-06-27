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
package de.hybris.platform.sap.core.runtime;



/**
 * Event listener interface for SAP hybris session.
 */
public interface SAPHybrisSessionEventListener
{

	/**
	 * Event called after the SAP hybris session has been destroyed.
	 * 
	 * @param sapHybrisSession
	 *           SAP hybris session
	 */
	public void onAfterDestroy(SAPHybrisSession sapHybrisSession);

}
