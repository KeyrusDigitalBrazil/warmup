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
package de.hybris.platform.integrationservices.service;

import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;

import java.io.InputStream;
import java.util.List;

/**
 * Service that persists a payload
 */
public interface MediaPersistenceService
{
	/**
	 * Persists the payloads.
	 *
	 * @param payloads request payloads to be persisted
	 * @param mediaType implementation type to use for the generated medias
	 * @return persisted payloads as medias in the order corresponding to the order of the submitted {@code payloads}.
	 * Number of medias returned is equal to number of payload input streams passed in.
	 * If certain medias failed to persist, there will be {@code null} in the corresponding place of the returned
	 * list.
	 */
	<T extends IntegrationApiMediaModel> List<T> persistMedias(final List<InputStream> payloads,
			final Class<T> mediaType);
}
