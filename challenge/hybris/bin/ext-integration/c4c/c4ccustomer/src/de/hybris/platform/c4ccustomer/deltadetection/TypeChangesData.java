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
package de.hybris.platform.c4ccustomer.deltadetection;

import de.hybris.y2ysync.deltadetection.collector.BatchingCollector;

import javax.annotation.concurrent.Immutable;


/**
 * Helper structure to group item changes by type.
 */
@Immutable
public class TypeChangesData
{
	private final String impexHeader;
	private final String dataHubColumns;
	private final String dataHubType;
	private final BatchingCollector collector;

	/**
	 * Full constructor.
	 *
	 * @param impexColumns semicolon-delimited source columns of impex
	 * @param dataHubColumns semicolon-delimited target columns of datahub processor
	 * @param collector collector user to store records
	 * @param dataHubType dataHub type
	 */
	public TypeChangesData(final String impexColumns, final String dataHubColumns, final BatchingCollector collector,
			final String dataHubType)
	{
		impexHeader = impexColumns;
		this.dataHubColumns = dataHubColumns;
		this.collector = collector;
		this.dataHubType = dataHubType;
	}

	/**
	 * @return impex header.
	 */
	public String getImpexHeader()
	{
		return impexHeader;
	}

	/**
	 * @return datahub column names.
	 */
	public String getDataHubColumns()
	{
		return dataHubColumns;
	}

	/**
	 * @return collector storing data.
	 */
	public BatchingCollector getCollector()
	{
		return collector;
	}

	/**
	 * @return dataHub type.
	 */
	public String getDataHubType()
	{
		return dataHubType;
	}
}
