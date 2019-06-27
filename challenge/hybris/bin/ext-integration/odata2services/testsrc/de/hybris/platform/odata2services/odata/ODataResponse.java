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

package de.hybris.platform.odata2services.odata;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.core.edm.provider.EdmImplProv;

/**
 * A helper class for exploring and evaluating content of an {@code ODataResponse}
 */
public class ODataResponse
{
	private final Edm edm;

	private ODataResponse(final Edm e)
	{
		edm = e;
	}

	public static ODataResponse createFrom(final org.apache.olingo.odata2.api.processor.ODataResponse r) throws ODataException
	{
		final Edm edm = EntityProvider.readMetadata(r.getEntityAsStream(), true);
		return new ODataResponse(edm);
	}

	/**
	 * Returns namespaces of the schemas present in the response.
	 * @return a collection of all schema names in this response or an empty collection, if this response contains no schemas.
	 * @throws ODataException if failed to obtain schemas from the response.
	 */
	public Collection<String> getSchemaNames() throws ODataException
	{
		return getSchemas().stream().map(Schema::getNamespace).collect(Collectors.toSet());
	}

	/**
	 * Searches for the specified schema in the content of this response.
	 * @param namespace namespace of the EDMX schema to select.
	 * @return the schema in the content of this response, which the namespace matching the specified value.
	 * @throws ODataException if schemas could not be accessed in the response.
	 * @throws IllegalArgumentException if the schema does not exist
	 */
	public ODataSchema getSchema(final String namespace) throws ODataException
	{
		final Schema schema = getSchemas().stream()
				.filter(s -> Objects.equals(namespace, s.getNamespace()))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("'" + namespace + "' schema not found"));
		return new ODataSchema(schema);
	}

	private Collection<Schema> getSchemas() throws ODataException
	{
		return ((EdmImplProv) edm).getEdmProvider().getSchemas();
	}
}
