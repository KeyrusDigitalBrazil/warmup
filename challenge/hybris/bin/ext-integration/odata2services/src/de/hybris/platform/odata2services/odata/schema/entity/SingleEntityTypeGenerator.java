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

package de.hybris.platform.odata2services.odata.schema.entity;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.odata2services.odata.schema.KeyGenerator;
import de.hybris.platform.odata2services.odata.schema.property.AbstractPropertyListGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.provider.EntityType;
import org.apache.olingo.odata2.api.edm.provider.Key;
import org.apache.olingo.odata2.api.edm.provider.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Base implementation class for entity type generators, which generate either a single entity type or no entity types depending
 * on certain conditions.
 */
public abstract class SingleEntityTypeGenerator implements EntityTypeGenerator
{
	private static final Logger LOG = LoggerFactory.getLogger(ComposedEntityTypeGenerator.class);
	private AbstractPropertyListGenerator propertiesGenerator;
	private KeyGenerator keyGenerator;

	/**
	 * {@inheritDoc}
	 * Implementation checks whether this generator is applicable to the specified item by calling {@link #isApplicable(IntegrationObjectItemModel)}
	 * and depending on the result proceeds to {@link #generateEntityType(IntegrationObjectItemModel)} or returns an empty list.
	 * @param item an item, for which an EDMX entity type has to be generated
	 * @return a list with a single generated entity type or an empty list, if this generator is not {@code isApplicable()}.
	 */
	@Override
	public List<EntityType> generate(final IntegrationObjectItemModel item)
	{
		Preconditions.checkArgument(item != null, "An EntityType cannot be generated from a null IntegrationObjectItemModel");

		return isApplicable(item)
				? Collections.singletonList(generateEntityType(item))
				: Collections.emptyList();
	}

	/**
	 * Determines whether this generator is applicable to the specified item and can generate at least a single EDMX entity type.
	 * @param item an item, based on which the decision has to be made.
	 * @return {@code true}, if at least one entity type can be generated for the given item; {@code false}, otherwise.
	 */
	protected abstract boolean isApplicable(IntegrationObjectItemModel item);

	/**
	 * Generates a single entity type for the specified item delegating the entity parts creation to:
	 * <ul>
	 *     <li>{@link #generateEntityTypeName(IntegrationObjectItemModel)}</li> for the entity type name generation
	 *     <li>{@link #propertiesGenerator}</li> for the entity type properties generation
	 *     <li>{@link #keyGenerator}</li> for the entity type key generation. If the key is not generated, i.e. {@code !Optional<Key>.isPresent()},
	 *     an {@code IllegalStateException} is thrown.
	 * </ul>
	 * @param item an item to generate the EDMX entity type for.
	 * @return the generated entity type.<br/>
	 * <b>Subclasses</b> make sure never return {@code null} from this method. If item cannot be generated, then
	 * {@link #isApplicable(IntegrationObjectItemModel)} should return {@code false} instead.
	 */
	protected EntityType generateEntityType(final IntegrationObjectItemModel item)
	{
		final String name = generateEntityTypeName(item);
		LOG.debug("Generating {} EntityType", name);

		final List<Property> properties = getPropertiesGenerator().generate(item);
		final Key key = generateKey(name, properties);

		return new EntityType()
				.setName(name)
				.setProperties(properties)
				.setKey(key);
	}

	protected Key generateKey(final String name, final List<Property> properties)
	{
		final Optional<Key> key = getKeyGenerator().generate(properties);
		if (! key.isPresent())
		{
			LOG.warn("{} missing key. This will cause problems when the schema is loaded to SCPI.", name);
		}
		return key.orElse(null);
	}

	/**
	 * Generates name for the entity type being generated.
	 * @param item item, for which entity type is being generated.
	 * @return a valid EDMX entity type name.
	 */
	protected abstract String generateEntityTypeName(IntegrationObjectItemModel item);

	@Required
	public void setPropertiesGenerator(final AbstractPropertyListGenerator generator)
	{
		propertiesGenerator = generator;
	}

	@Required
	public void setKeyGenerator(final KeyGenerator generator)
	{
		keyGenerator = generator;
	}

	protected AbstractPropertyListGenerator getPropertiesGenerator()
	{
		return propertiesGenerator;
	}

	protected KeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}
}
