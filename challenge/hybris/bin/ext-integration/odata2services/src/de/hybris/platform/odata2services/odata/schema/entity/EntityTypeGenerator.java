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
import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;

import java.util.List;

import org.apache.olingo.odata2.api.edm.provider.EntityType;

/**
 * A marker interface for the convenience of encapsulating the generics and finding all implementors, which determines a
 * generator responsible for EDMX entity types generation from a single {@link IntegrationObjectItemModel}
 */
public interface EntityTypeGenerator extends SchemaElementGenerator<List<EntityType>, IntegrationObjectItemModel>
{
}
