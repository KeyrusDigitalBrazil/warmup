/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.passport;

import com.google.common.base.Preconditions;
import com.sap.jdsr.passport.DSRPassport;

public class SapPassportBuilder
{
	private Integer version;
	private Integer traceFlag;
	private String systemId;
	private Integer service;
	private String user;
	private String action;
	private Integer actionType;
	private String prevSystemId;
	private String transId;
	private String clientNumber;
	private Integer systemType;
	private byte[] rootContextId;
	private byte[] connectionId;
	private Integer connectionCounter;

	private SapPassportBuilder()
	{
		// not instantiable
	}

	public static SapPassportBuilder newSapPassportBuilder()
	{
		return new SapPassportBuilder();
	}

	public SapPassportBuilder withVersion(final Integer version)
	{
		this.version = version;
		return this;
	}

	public SapPassportBuilder withTraceFlag(final Integer traceFlag)
	{
		this.traceFlag = traceFlag;
		return this;
	}

	public SapPassportBuilder withSystemId(final String systemId)
	{
		this.systemId = systemId;
		return this;
	}

	public SapPassportBuilder withService(final Integer service)
	{
		this.service = service;
		return this;
	}

	public SapPassportBuilder withUser(final String user)
	{
		this.user = user;
		return this;
	}

	public SapPassportBuilder withAction(final String action)
	{
		this.action = action;
		return this;
	}

	public SapPassportBuilder withActionType(final Integer actionType)
	{
		this.actionType = actionType;
		return this;
	}

	public SapPassportBuilder withPrevSystemId(final String prevSystemId)
	{
		this.prevSystemId = prevSystemId;
		return this;
	}

	public SapPassportBuilder withTransId(final String transId)
	{
		this.transId = transId;
		return this;
	}

	public SapPassportBuilder withClientNumber(final String clientNumber)
	{
		this.clientNumber = clientNumber;
		return this;
	}

	public SapPassportBuilder withSystemType(final Integer systemType)
	{
		this.systemType = systemType;
		return this;
	}

	public SapPassportBuilder withRootContextId(final byte[] rootContextId)
	{
		this.rootContextId = rootContextId;
		return this;
	}

	public SapPassportBuilder withConnectionId(final byte[] connectionId)
	{
		this.connectionId = connectionId;
		return this;
	}

	public SapPassportBuilder withConnectionCounter(final Integer connectionCounter)
	{
		this.connectionCounter = connectionCounter;
		return this;
	}

	public DSRPassport build()
	{
		Preconditions.checkArgument(version != null, "version cannot be null");
		Preconditions.checkArgument(traceFlag != null, "traceFlag cannot be null");
		Preconditions.checkArgument(systemId != null, "systemId cannot be null");
		Preconditions.checkArgument(service != null, "service cannot be null");
		Preconditions.checkArgument(user != null, "user cannot be null");
		Preconditions.checkArgument(action != null, "action cannot be null");
		Preconditions.checkArgument(actionType != null, "actionType cannot be null");
		Preconditions.checkArgument(prevSystemId != null, "prevSystemId cannot be null");
		Preconditions.checkArgument(transId != null, "transId cannot be null");
		Preconditions.checkArgument(clientNumber != null, "clientNumber cannot be null");
		Preconditions.checkArgument(systemType != null, "systemType cannot be null");
		Preconditions.checkArgument(rootContextId != null, "rootContextId cannot be null");
		Preconditions.checkArgument(connectionId != null, "connectionId cannot be null");
		Preconditions.checkArgument(connectionCounter != null, "connectionCounter cannot be null");

		return new DSRPassport(version, traceFlag, systemId, service, user, action, actionType,
				prevSystemId, transId, clientNumber, systemType, rootContextId, connectionId, connectionCounter);
	}
}
