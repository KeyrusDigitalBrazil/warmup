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
package de.hybris.platform.ordermanagementwebservices.dto.fraud;

import java.io.Serializable;
import de.hybris.platform.ordermanagementwebservices.dto.fraud.FraudReportWsDTO;
import java.util.List;

public  class FraudReportListWsDTO  implements Serializable 
{

 	/** Default serialVersionUID value. */
 
 	private static final long serialVersionUID = 1L;

	/** <i>Generated property</i> for <code>FraudReportListWsDTO.reports</code> property defined at extension <code>ordermanagementwebservices</code>. */
		
	private List<FraudReportWsDTO> reports;
	
	public FraudReportListWsDTO()
	{
		// default constructor
	}
	
		
	
	public void setReports(final List<FraudReportWsDTO> reports)
	{
		this.reports = reports;
	}

		
	
	public List<FraudReportWsDTO> getReports() 
	{
		return reports;
	}
	


}
