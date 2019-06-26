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
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.DescriptorModel;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Predicate to verify that ComposedTypeModel contains two fields (external and urlLink) at the same time.
 */
public class ComposedTypeContainsLinkTogglePredicate implements Predicate<ComposedTypeModel>
{
	@Override
	public boolean test(ComposedTypeModel composedTypeModel)
	{
			final List<String> attributes =
					Stream.concat(
							composedTypeModel.getDeclaredattributedescriptors().stream(),
							composedTypeModel.getInheritedattributedescriptors().stream()
					).map(DescriptorModel::getQualifier).collect(Collectors.toList());

			return attributes
					.containsAll(Arrays.asList(CmsfacadesConstants.FIELD_URL_LINK_NAME, CmsfacadesConstants.FIELD_EXTERNAL_NAME));
	}
}
