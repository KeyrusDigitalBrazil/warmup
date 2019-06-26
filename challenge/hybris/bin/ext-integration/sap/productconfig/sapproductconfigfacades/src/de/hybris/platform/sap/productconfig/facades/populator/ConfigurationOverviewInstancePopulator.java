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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.ConfigOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicValue;
import de.hybris.platform.sap.productconfig.facades.overview.ValuePositionTypeEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Transforms an {@link InstanceModel} into into a list of {@link CharacteristicGroup} data as required by the
 * configuration overview page.<br>
 * This class will use the {@link ConfigurationOverviewValuePopulator} to handle the individual cstic values, while
 * itself is typically called by the {@link ConfigurationOverviewPopulator}.
 */
public class ConfigurationOverviewInstancePopulator extends AbstractOverviewPopulator implements
		ConfigurablePopulator<InstanceModel, List<CharacteristicGroup>, Map>
{
	private ClassificationSystemCPQAttributesProvider nameProvider;
	private ProductDao productDao;

	private ConfigurationOverviewValuePopulator configurationOverviewValuePopulator;
	private ConfigOverviewFilter visibleValueFilter;
	private ConfigOverviewGroupFilter overviewGroupFilter;
	static final String HAS_ONLY_ONE_CSTIC_GROUP = "HAS_ONLY_ONE_CSTIC_GROUP";
	static final String APPLIED_CSTIC_FILTERS = "APPLIED_FILTERS";
	static final String APPLIED_GROUP_FILTERS = "APPLIED_GROUP_FILTERS";
	static final String HYBRIS_NAME_MAP = "HYBRIS_NAME_MAP";

	/**
	 * @return the overviewGroupFilter
	 */
	public ConfigOverviewGroupFilter getOverviewGroupFilter()
	{
		return overviewGroupFilter;
	}

	/**
	 * @param overviewGroupFilter
	 *           the overviewGroupFilter to set
	 */
	public void setOverviewGroupFilter(final ConfigOverviewGroupFilter overviewGroupFilter)
	{
		this.overviewGroupFilter = overviewGroupFilter;
	}

	/**
	 * @return the visibleValueFilter
	 */
	public ConfigOverviewFilter getVisibleValueFilter()
	{
		return visibleValueFilter;
	}

	/**
	 * @param visibleValueFilter
	 *           the visibleValueFilter to set
	 */
	public void setVisibleValueFilter(final ConfigOverviewFilter visibleValueFilter)
	{
		this.visibleValueFilter = visibleValueFilter;
	}

	/**
	 * @return the configurationOverviewValuePopulator
	 */
	public ConfigurationOverviewValuePopulator getConfigurationOverviewValuePopulator()
	{
		return configurationOverviewValuePopulator;
	}

	/**
	 * @param configurationOverviewValuePopulator
	 *           the configurationOverviewValuePopulator to set
	 */
	public void setConfigurationOverviewValuePopulator(
			final ConfigurationOverviewValuePopulator configurationOverviewValuePopulator)
	{
		this.configurationOverviewValuePopulator = configurationOverviewValuePopulator;
	}

	@Override
	public void populate(final InstanceModel source, final List<CharacteristicGroup> target, final Collection<Map> options)

	{
		final HashMap optionsMap = (HashMap) options.iterator().next();
		final Set<String> filteredOutGroups = (Set<String>) optionsMap.get(APPLIED_GROUP_FILTERS);
		final Set<String> groupsToBeDisplayed = getOverviewGroupFilter().getGroupsToBeDisplayed(source, filteredOutGroups);
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = (Map<String, ClassificationSystemCPQAttributesContainer>) optionsMap
				.get(HYBRIS_NAME_MAP);

		createUIGroupsFromCsticGroups(source, target, optionsMap, groupsToBeDisplayed, nameMap);
		createUIGroupsFromSubInstances(source, target, optionsMap, groupsToBeDisplayed, nameMap);
	}

	protected void createUIGroupsFromSubInstances(final InstanceModel source, final List<CharacteristicGroup> target,
			final Map optionsMap, final Set<String> groupsToBeDisplayed,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final List<InstanceModel> subInstances = source.getSubInstances();
		for (final InstanceModel subInstance : subInstances)
		{
			if (groupsToBeDisplayed.contains(subInstance.getName()))
			{
				final CharacteristicGroup subGroup = createSubInstanceGroup(subInstance, nameMap,
						(List) optionsMap.get(APPLIED_CSTIC_FILTERS));
				if ((subGroup.getSubGroups() == null || subGroup.getSubGroups().isEmpty())
						&& (subGroup.getCharacteristicValues() == null || subGroup.getCharacteristicValues().isEmpty()))
				{
					continue;
				}
				target.add(subGroup);
			}
		}
	}

	protected void createUIGroupsFromCsticGroups(final InstanceModel source, final List<CharacteristicGroup> target,
			final Map optionsMap, final Set<String> groupsToBeDisplayed,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final List<CsticGroup> csticModelGroups = source.retrieveCsticGroupsWithCstics();
		for (final CsticGroup csticModelGroup : csticModelGroups)
		{
			if (groupsToBeDisplayed.contains(csticModelGroup.getName()))
			{
				final CharacteristicGroup csticGroup = createCsticGroup(csticModelGroup, nameMap,
						(List) optionsMap.get(APPLIED_CSTIC_FILTERS));
				if (csticGroup.getCharacteristicValues() == null || csticGroup.getCharacteristicValues().isEmpty())
				{
					continue;
				}
				target.add(csticGroup);
			}

		}
		if (target.size() == 1)
		{
			optionsMap.put(HAS_ONLY_ONE_CSTIC_GROUP, Boolean.TRUE);
		}
	}

	protected CharacteristicGroup createCsticGroup(final CsticGroup csticModelGroup,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap, final List<ConfigOverviewFilter> filters)
	{
		final CharacteristicGroup group = new CharacteristicGroup();

		final String groupDescription = getGroupDescription(csticModelGroup.getDescription(), csticModelGroup.getName());
		group.setId(csticModelGroup.getName());
		group.setGroupDescription(groupDescription);

		final List<CharacteristicValue> values = new ArrayList<>();
		if (!csticModelGroup.getCstics().isEmpty())
		{
			for (final CsticModel cstic : csticModelGroup.getCstics())
			{
				if (!cstic.getAssignedValues().isEmpty())
				{
					final List<CsticValueModel> filteredAssignedValues = applyFilters(cstic, filters);
					createCsticValues(values, cstic, filteredAssignedValues, nameMap);
				}
			}
		}

		group.setCharacteristicValues(values);

		return group;
	}

	protected void createCsticValues(final List<CharacteristicValue> values, final CsticModel cstic,
			final List<CsticValueModel> filteredAssignedValues, final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		final ClassificationSystemCPQAttributesContainer cpqAttributes = getNameProvider().getCPQAttributes(cstic.getName(),
				nameMap);

		final Collection<Map> options = new ArrayList<>();
		final HashMap<String, Object> optionsMap = new HashMap<>();
		options.add(optionsMap);
		optionsMap.put(ConfigurationOverviewValuePopulator.CSTIC_MODEL, cstic);
		optionsMap.put(ConfigurationOverviewValuePopulator.HYBRIS_NAMES, cpqAttributes);

		for (int i = 0; i < filteredAssignedValues.size(); i++)
		{
			final CsticValueModel csticValue = filteredAssignedValues.get(i);
			final CharacteristicValue value = new CharacteristicValue();
			final ValuePositionTypeEnum valuePositionType = determineValuePositionType(filteredAssignedValues.size(), i);
			optionsMap.put(ConfigurationOverviewValuePopulator.VALUE_POSITION_TYPE, valuePositionType);
			getConfigurationOverviewValuePopulator().populate(csticValue, value, options);
			values.add(value);
		}
	}

	protected List<CsticValueModel> applyFilters(final CsticModel cstic, final List<ConfigOverviewFilter> filters)
	{

		// First reduce list of assigned values to the list of visible assigned values.
		final List<CsticValueModel> visibleAssignedValues = getVisibleValueFilter().filter(cstic.getAssignedValues(), cstic);

		// Filters of the overviewFilterList are connected via logical OR. This is implemented by using negated filters
		// (noMatch method) and passing the result values of one filter to the other (i.e. implementing logical AND).
		// The final result values are then deleted from the visible assigned values.
		List<CsticValueModel> filteredOutValues = new ArrayList<>(visibleAssignedValues);
		if (!filters.isEmpty())
		{
			for (final ConfigOverviewFilter currentFilter : filters)
			{
				filteredOutValues = currentFilter.noMatch(filteredOutValues, cstic);
			}
			visibleAssignedValues.removeAll(filteredOutValues);
		}
		return visibleAssignedValues;
	}


	protected String getGroupDescription(final String languageDependentName, final String name)
	{
		String groupDescription = name;
		if (languageDependentName != null && !languageDependentName.isEmpty())
		{
			groupDescription = languageDependentName;
		}
		return groupDescription;
	}

	protected CharacteristicGroup createSubInstanceGroup(final InstanceModel instance,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap, final List<ConfigOverviewFilter> filters)
	{
		final CharacteristicGroup group = new CharacteristicGroup();
		group.setId(instance.getName());

		final String hybrisProductLanguageDependentName = retrieveHibrisProductName(instance.getName());
		if (hybrisProductLanguageDependentName != null && !hybrisProductLanguageDependentName.trim().isEmpty())
		{
			group.setGroupDescription(hybrisProductLanguageDependentName);
		}
		else
		{
			group.setGroupDescription(instance.getLanguageDependentName());
		}

		final List<CharacteristicGroup> groups = new ArrayList<>();
		final Collection<Map> options = new ArrayList<>();
		final HashMap optionsMap = new HashMap();
		final Set<String> filteredOutGroups = new HashSet<>();
		optionsMap.put(APPLIED_CSTIC_FILTERS, filters);
		optionsMap.put(APPLIED_GROUP_FILTERS, filteredOutGroups);
		optionsMap.put(HYBRIS_NAME_MAP, nameMap);
		options.add(optionsMap);

		this.populate(instance, groups, options);
		if (!groups.isEmpty())
		{
			if (optionsMap.containsKey(HAS_ONLY_ONE_CSTIC_GROUP))
			{
				group.setCharacteristicValues(groups.get(0).getCharacteristicValues());
				groups.remove(0);
			}
			group.setSubGroups(groups);
		}

		return group;
	}

	protected String retrieveHibrisProductName(final String name)
	{
		String productName = null;
		//retrieve (sub)instance product description from catalog if available
		final List<ProductModel> products = getProductDao().findProductsByCode(name);
		if (products != null && products.size() == 1)
		{
			final ProductModel product = products.get(0);
			productName = product.getName();
		}
		return productName;
	}

	/**
	 * @return the hybris characteristic and value name provider
	 */
	protected ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param nameProvider
	 *           hybris characteristic and value name provider
	 */
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

	/**
	 * @return Product access object
	 */
	protected ProductDao getProductDao()
	{
		return productDao;
	}

	/**
	 * Setter for product data access object
	 *
	 * @param productDao
	 */
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

}
