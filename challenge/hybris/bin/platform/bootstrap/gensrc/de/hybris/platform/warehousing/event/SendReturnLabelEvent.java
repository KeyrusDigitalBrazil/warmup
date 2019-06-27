/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:08
 * ----------------------------------------------------------------
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.event;

import java.io.Serializable;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import de.hybris.platform.returns.model.ReturnRequestModel;

public  class SendReturnLabelEvent  extends AbstractEvent {


	/** <i>Generated property</i> for <code>SendReturnLabelEvent.returnRequest</code> property defined at extension <code>warehousing</code>. */
		
	private ReturnRequestModel returnRequest;
	
	public SendReturnLabelEvent()
	{
		super();
	}

	public SendReturnLabelEvent(final Serializable source)
	{
		super(source);
	}
	
	
	
	public void setReturnRequest(final ReturnRequestModel returnRequest)
	{
		this.returnRequest = returnRequest;
	}
	
	
	
	public ReturnRequestModel getReturnRequest() 
	{
		return returnRequest;
	}
	


}
