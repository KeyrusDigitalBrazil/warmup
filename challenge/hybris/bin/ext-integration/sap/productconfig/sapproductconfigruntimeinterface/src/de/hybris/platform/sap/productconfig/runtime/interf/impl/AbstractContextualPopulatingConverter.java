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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.PopulatorList;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulaterList;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Compared to it's parent the {@link AbstractPopulatingConverter}, this abstract converter can handle both ordinary
 * {@link Populator}s and {@link ContextualPopulator}s.<br>
 * So it manages two lists. The {@link PopulatorList} contains ordinary {@link Populator}s, which are called without a
 * context during the convert process, while the {@link ContextualPopulaterList} contains {@link ContextualPopulator}s,
 * which are called with a context during the convert process.<br>
 *
 * @param <SOURCE>
 *           type of source object
 * @param <TARGET>
 *           type of target object
 * @param <CONTEXT>
 *           type of populating context
 */
public class AbstractContextualPopulatingConverter<SOURCE, TARGET, CONTEXT> extends AbstractPopulatingConverter<SOURCE, TARGET>
		implements ContextualPopulaterList<SOURCE, TARGET, CONTEXT>, ContextualConverter<SOURCE, TARGET, CONTEXT>
{

	private static final Logger LOG = Logger.getLogger(AbstractContextualPopulatingConverter.class);
	private List<ContextualPopulator<SOURCE, TARGET, CONTEXT>> contextualPopulators;



	@Override
	public List<ContextualPopulator<SOURCE, TARGET, CONTEXT>> getContextualPopulators()
	{
		return contextualPopulators;
	}

	@Override
	public void setContextualPopulators(final List<ContextualPopulator<SOURCE, TARGET, CONTEXT>> contextualPopulators)
	{
		this.contextualPopulators = contextualPopulators;
	}

	/**
	 * Populate the target instance from the source instance. Calls a list of injected populators to populate the
	 * instance.
	 *
	 * @param source
	 *           the source item
	 * @param target
	 *           the target item to populate
	 * @param context
	 *           populating context
	 */
	public void populate(final SOURCE source, final TARGET target, final CONTEXT context)
	{
		// handle NON-contextual populators
		super.populate(source, target);

		// handle contextual populators
		final List<ContextualPopulator<SOURCE, TARGET, CONTEXT>> list = getContextualPopulators();

		if (list == null)
		{
			return;
		}

		for (final ContextualPopulator<SOURCE, TARGET, CONTEXT> populator : list)
		{
			if (populator != null)
			{
				populator.populate(source, target, context);
			}
		}
	}



	@Override
	public TARGET convertWithContext(final SOURCE source, final CONTEXT context)
	{
		final TARGET target = createFromClass();
		populate(source, target, context);
		return target;
	}



	// execute when BEAN name is known
	@Override
	@PostConstruct
	public void removePopulatorsDuplicates()
	{
		// CHECK for populators duplicates
		if (CollectionUtils.isNotEmpty(contextualPopulators))
		{
			final LinkedHashSet<ContextualPopulator<SOURCE, TARGET, CONTEXT>> distinctPopulators = new LinkedHashSet<>();

			for (final ContextualPopulator<SOURCE, TARGET, CONTEXT> populator : contextualPopulators)
			{
				if (!distinctPopulators.add(populator))
				{
					LOG.warn("Duplicate populator entry [" + populator.getClass().getName() + "] found for converter "
							+ getMyBeanName() + "! The duplication has been removed.");
				}
			}
			this.contextualPopulators = new ArrayList<>(distinctPopulators);
		}
		else
		{
			LOG.warn("Empty populators list found for converter " + getMyBeanName() + "!");
		}
	}




}
