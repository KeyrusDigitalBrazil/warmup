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
package de.hybris.platform.odata2services.odata.processor.writer;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.processor.reader.EntityReader;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

/**
 * Registry that manages and provides a collection of {@link ResponseWriterPropertyPopulator}
 */
public class ResponseWriterPropertyPopulatorRegistry
{
	private Collection<ResponseWriterPropertyPopulator> populators;

	/**
	 * Iterate over the populators and return the ones that apply based on the ItemLookupRequest information.
	 *
	 * @param itemLookupRequest Use to determine what {@link ResponseWriterPropertyPopulator} will be used
	 * @return An {@link EntityReader}
	 * @throws RuntimeException or a derivative of it if no {@link EntityReader} is found
	 */
	public Collection<ResponseWriterPropertyPopulator> getPopulators(final ItemLookupRequest itemLookupRequest)
	{
		return populators.stream()
				.filter(populator -> populator.isApplicable(itemLookupRequest))
				.collect(Collectors.toList());
	}

	protected Collection<ResponseWriterPropertyPopulator> getPopulators()
	{
		return populators;
	}

	@Required
	public void setPopulators(final Collection<ResponseWriterPropertyPopulator> populators)
	{
		this.populators = populators;
	}
}
