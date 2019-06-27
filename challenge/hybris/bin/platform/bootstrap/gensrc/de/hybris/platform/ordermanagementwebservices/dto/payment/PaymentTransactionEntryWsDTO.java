/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:07
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
package de.hybris.platform.ordermanagementwebservices.dto.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public  class PaymentTransactionEntryWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.amount</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private BigDecimal amount;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.code</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String code;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.currencyIsocode</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String currencyIsocode;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.requestId</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String requestId;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.requestToken</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String requestToken;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.subscriptionID</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String subscriptionID;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.time</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private Date time;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.transactionStatus</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String transactionStatus;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.transactionStatusDetails</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String transactionStatusDetails;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.type</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String type;

	/** <i>Generated property</i> for <code>PaymentTransactionEntryWsDTO.versionID</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private String versionID;
	
	public PaymentTransactionEntryWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setAmount(final BigDecimal amount)
	{
		this.amount = amount;
	}

		
	
	public BigDecimal getAmount() 
	{
		return amount;
	}
	
		
	
	public void setCode(final String code)
	{
		this.code = code;
	}

		
	
	public String getCode() 
	{
		return code;
	}
	
		
	
	public void setCurrencyIsocode(final String currencyIsocode)
	{
		this.currencyIsocode = currencyIsocode;
	}

		
	
	public String getCurrencyIsocode() 
	{
		return currencyIsocode;
	}
	
		
	
	public void setRequestId(final String requestId)
	{
		this.requestId = requestId;
	}

		
	
	public String getRequestId() 
	{
		return requestId;
	}
	
		
	
	public void setRequestToken(final String requestToken)
	{
		this.requestToken = requestToken;
	}

		
	
	public String getRequestToken() 
	{
		return requestToken;
	}
	
		
	
	public void setSubscriptionID(final String subscriptionID)
	{
		this.subscriptionID = subscriptionID;
	}

		
	
	public String getSubscriptionID() 
	{
		return subscriptionID;
	}
	
		
	
	public void setTime(final Date time)
	{
		this.time = time;
	}

		
	
	public Date getTime() 
	{
		return time;
	}
	
		
	
	public void setTransactionStatus(final String transactionStatus)
	{
		this.transactionStatus = transactionStatus;
	}

		
	
	public String getTransactionStatus() 
	{
		return transactionStatus;
	}
	
		
	
	public void setTransactionStatusDetails(final String transactionStatusDetails)
	{
		this.transactionStatusDetails = transactionStatusDetails;
	}

		
	
	public String getTransactionStatusDetails() 
	{
		return transactionStatusDetails;
	}
	
		
	
	public void setType(final String type)
	{
		this.type = type;
	}

		
	
	public String getType() 
	{
		return type;
	}
	
		
	
	public void setVersionID(final String versionID)
	{
		this.versionID = versionID;
	}

		
	
	public String getVersionID() 
	{
		return versionID;
	}
	


}
