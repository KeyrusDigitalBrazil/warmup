/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:12
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
package de.hybris.platform.commercefacades.order.data;

import de.hybris.platform.acceleratorservices.enums.ImportStatus;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import java.util.Date;
import java.util.List;

public  class CartData extends AbstractOrderData 
{

 

	/** <i>Generated property</i> for <code>CartData.potentialOrderPromotions</code> property defined at extension <code>commercefacades</code>. */
		
	private List<PromotionResultData> potentialOrderPromotions;

	/** <i>Generated property</i> for <code>CartData.potentialProductPromotions</code> property defined at extension <code>commercefacades</code>. */
		
	private List<PromotionResultData> potentialProductPromotions;

	/** <i>Generated property</i> for <code>CartData.saveTime</code> property defined at extension <code>commercefacades</code>. */
		
	private Date saveTime;

	/** <i>Generated property</i> for <code>CartData.savedBy</code> property defined at extension <code>commercefacades</code>. */
		
	private PrincipalData savedBy;

	/** <i>Generated property</i> for <code>CartData.quoteData</code> property defined at extension <code>commercefacades</code>. */
		
	private QuoteData quoteData;

	/** <i>Generated property</i> for <code>CartData.importStatus</code> property defined at extension <code>acceleratorfacades</code>. */
		
	private ImportStatus importStatus;
	
	public CartData()
	{
		// default constructor
	}
	
		
	
	public void setPotentialOrderPromotions(final List<PromotionResultData> potentialOrderPromotions)
	{
		this.potentialOrderPromotions = potentialOrderPromotions;
	}

		
	
	public List<PromotionResultData> getPotentialOrderPromotions() 
	{
		return potentialOrderPromotions;
	}
	
		
	
	public void setPotentialProductPromotions(final List<PromotionResultData> potentialProductPromotions)
	{
		this.potentialProductPromotions = potentialProductPromotions;
	}

		
	
	public List<PromotionResultData> getPotentialProductPromotions() 
	{
		return potentialProductPromotions;
	}
	
		
	
	public void setSaveTime(final Date saveTime)
	{
		this.saveTime = saveTime;
	}

		
	
	public Date getSaveTime() 
	{
		return saveTime;
	}
	
		
	
	public void setSavedBy(final PrincipalData savedBy)
	{
		this.savedBy = savedBy;
	}

		
	
	public PrincipalData getSavedBy() 
	{
		return savedBy;
	}
	
		
	
	public void setQuoteData(final QuoteData quoteData)
	{
		this.quoteData = quoteData;
	}

		
	
	public QuoteData getQuoteData() 
	{
		return quoteData;
	}
	
		
	
	public void setImportStatus(final ImportStatus importStatus)
	{
		this.importStatus = importStatus;
	}

		
	
	public ImportStatus getImportStatus() 
	{
		return importStatus;
	}
	


}
