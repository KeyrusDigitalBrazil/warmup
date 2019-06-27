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
package de.hybris.platform.customerticketingc4cintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * Class for mapping ODataErrors.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ODataError
{
	@JsonProperty
	private String code; //NOSONAR

	@JsonProperty
	private Message message; //NOSONAR

	public String getCode()
	{
		return code;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	public Message getMessage()
	{
		return message;
	}

	public void setMessage(final Message message)
	{
		this.message = message;
	}

	@JsonRootName(value = "message")
	public static class Message
	{
		@JsonProperty
		private String lang;
		@JsonProperty
		private String value;

		public String getLang()
		{
			return lang;
		}

		public void setLang(final String lang)
		{
			this.lang = lang;
		}

		public String getValue()
		{
			return value;
		}

		public void setValue(final String value)
		{
			this.value = value;
		}
	}
}
