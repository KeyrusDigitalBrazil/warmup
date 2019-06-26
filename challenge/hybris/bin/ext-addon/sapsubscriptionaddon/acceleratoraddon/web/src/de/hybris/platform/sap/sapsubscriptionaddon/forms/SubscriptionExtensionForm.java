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
package de.hybris.platform.sap.sapsubscriptionaddon.forms;

public class SubscriptionExtensionForm {

	private String version;
	private String extensionPeriod;
	private String ratePlanId;
	private String validTilldate;
	private String contractFrequency;
	private boolean unlimited;
	
	public String getContractFrequency() {
		return contractFrequency;
	}

	public void setContractFrequency(String contractFrequency) {
		this.contractFrequency = contractFrequency;
	}

	public String getValidTilldate() {
		return validTilldate;
	}

	public void setValidTilldate(String validTilldate) {
		this.validTilldate = validTilldate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getExtensionPeriod() {
		return extensionPeriod;
	}

	public void setExtensionPeriod(String extensionPeriod) {
		this.extensionPeriod = extensionPeriod;
	}

	public String getRatePlanId() {
		return ratePlanId;
	}

	public void setRatePlanId(String ratePlanId) {
		this.ratePlanId = ratePlanId;
	}
	
	public boolean isUnlimited()
	{
		return unlimited;
	}

	public void setUnlimited(final boolean unlimited)
	{
		this.unlimited = unlimited;
	}


}
