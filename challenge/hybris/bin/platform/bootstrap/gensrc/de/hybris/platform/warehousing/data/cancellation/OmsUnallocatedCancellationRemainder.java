/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:05
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
package de.hybris.platform.warehousing.data.cancellation;

import java.io.Serializable;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;

public  class OmsUnallocatedCancellationRemainder  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>OmsUnallocatedCancellationRemainder.orderEntryCancellationRecord</code> property defined at extension <code>warehousing</code>. */
		
	private OrderEntryCancelRecordEntryModel orderEntryCancellationRecord;

	/** <i>Generated property</i> for <code>OmsUnallocatedCancellationRemainder.remainingQuantity</code> property defined at extension <code>warehousing</code>. */
		
	private Integer remainingQuantity;
	
	public OmsUnallocatedCancellationRemainder()
	{
		// default constructor
	}
	
		
	
	public void setOrderEntryCancellationRecord(final OrderEntryCancelRecordEntryModel orderEntryCancellationRecord)
	{
		this.orderEntryCancellationRecord = orderEntryCancellationRecord;
	}

		
	
	public OrderEntryCancelRecordEntryModel getOrderEntryCancellationRecord() 
	{
		return orderEntryCancellationRecord;
	}
	
		
	
	public void setRemainingQuantity(final Integer remainingQuantity)
	{
		this.remainingQuantity = remainingQuantity;
	}

		
	
	public Integer getRemainingQuantity() 
	{
		return remainingQuantity;
	}
	


}
