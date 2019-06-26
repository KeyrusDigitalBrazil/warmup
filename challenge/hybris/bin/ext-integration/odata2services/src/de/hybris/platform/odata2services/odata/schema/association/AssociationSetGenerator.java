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
package de.hybris.platform.odata2services.odata.schema.association;

import static de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils.toFullQualifiedName;

import org.apache.olingo.odata2.api.edm.provider.Association;
import org.apache.olingo.odata2.api.edm.provider.AssociationEnd;
import org.apache.olingo.odata2.api.edm.provider.AssociationSet;
import org.apache.olingo.odata2.api.edm.provider.AssociationSetEnd;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

import de.hybris.platform.odata2services.odata.schema.SchemaElementGenerator;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

/**
 * A default implementation of the {@link AssociationSet} generator.
 */
public class AssociationSetGenerator implements SchemaElementGenerator<AssociationSet, Association>
{
	private EntitySetNameGenerator nameGenerator;

	private static String generateName(final Association association)
	{
		return association.getEnd1().getType().getName() + "_" + English.plural(association.getEnd2().getType().getName());
	}

	@Override
	public AssociationSet generate(final Association assoc)
	{
		Preconditions.checkArgument(assoc != null,
				"Cannot generate an AssociationSet with a null Association.");

		return new AssociationSet()
				.setName(generateName(assoc))
				.setAssociation(toFullQualifiedName(assoc.getName()))
				.setEnd1(generateSetEnd(assoc.getEnd1()))
				.setEnd2(generateSetEnd(assoc.getEnd2()));
	}

	private AssociationSetEnd generateSetEnd(final AssociationEnd end)
	{
		return new AssociationSetEnd()
				.setEntitySet(nameGenerator.generate(end.getType().getName()))
				.setRole(end.getRole());
	}

	@Required
	public void setNameGenerator(final EntitySetNameGenerator generator)
	{
		nameGenerator = generator;
	}
}