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
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "metaData", "extensionDate", "unlimited" })
public class ExtendSubscription
{

	@JsonProperty("metaData")
	private MetaData metaData;
	@JsonProperty("extensionDate")
	private String extensionDate;
	@JsonProperty("unlimited")
	private String unlimited;

	@JsonProperty("metaData")
	public MetaData getMetaData() {
		return metaData;
	}

	@JsonProperty("metaData")
	public void setMetaData(final MetaData metaData) {
		this.metaData = metaData;
	}

	@JsonProperty("extensionDate")
	public String getExtensionDate()
	{
		return extensionDate;
	}

	@JsonProperty("extensionDate")
	public void setExtensionDate(final String extensionDate)
	{
		this.extensionDate = extensionDate;
	}

	@JsonProperty("unlimited")
	public String getUnlimited()
	{
		return unlimited;
	}

	@JsonProperty("unlimited")
	public void setUnlimited(final String unlimited)
	{
		this.unlimited = unlimited;
	}
}
