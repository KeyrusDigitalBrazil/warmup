/**
 *
 */
package de.hybris.platform.sap.productconfig.testdata.impl;

import static org.junit.Assert.fail;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


public class ConfigurationValueHelperImpl
{

	private static final Logger LOG = Logger.getLogger(ConfigurationValueHelperImpl.class);

	public CsticData getCstic(final ConfigurationData configData, final String name)
	{
		//find first regular group
		UiGroupData uiGroup = null;
		int i = 0;
		do
		{
			uiGroup = configData.getGroups().get(i++);
		}
		while (uiGroup.getGroupType().equals(GroupType.CONFLICT) || uiGroup.getGroupType().equals(GroupType.CONFLICT_HEADER));
		final List<CsticData> cstics = uiGroup.getCstics();
		for (final CsticData cstic : cstics)
		{
			if (name.equalsIgnoreCase(cstic.getName()))
			{
				return cstic;
			}
		}

		return null;
	}

	public CsticData getCstic(final ConfigurationData configData, final String groupId, final String name)
	{
		final List<CsticData> cstics = getGroupById(configData, groupId).getCstics();

		for (final CsticData cstic : cstics)
		{
			if (name.equalsIgnoreCase(cstic.getName()))
			{
				return cstic;
			}
		}

		return null;
	}

	public CsticData getCstic(final ConfigurationData configData, final String groupId, final String subGroupId, final String name)
	{
		final UiGroupData group = getGroupById(configData, groupId);
		try
		{
			final UiGroupData subGroup = getSubGroupById(group, subGroupId);

			if (CollectionUtils.isNotEmpty(subGroup.getCstics()))
			{
				for (final CsticData cstic : subGroup.getCstics())
				{
					if (name.equalsIgnoreCase(cstic.getName()))
					{
						return cstic;
					}
				}
			}
		}
		catch (final NullPointerException e)
		{
			return null;
		}
		return null;
	}

	protected UiGroupData getSubGroupById(final UiGroupData group, final String subGroupId)
	{
		final List<UiGroupData> subGroups = group.getSubGroups();
		if (CollectionUtils.isNotEmpty(subGroups))
		{
			for (final UiGroupData subGroup : subGroups)
			{
				if (subGroup.getId().equalsIgnoreCase(subGroupId))
				{
					return subGroup;
				}
			}
		}
		return null;
	}

	public CsticValueData getDomainValue(final CsticData cstic, final String name)
	{
		final List<CsticValueData> domainValues = cstic.getDomainvalues();

		for (final CsticValueData value : domainValues)
		{
			if (name.equalsIgnoreCase(value.getName()))
			{
				return value;
			}
		}

		return null;
	}

	public void setCstic(final ConfigurationData configData, final String name, final String value)
	{
		final UiGroupData uiGroupData = configData.getGroups().get(0);
		setCstic(uiGroupData, name, value);
	}

	public void setCstic(final ConfigurationData configData, final String groupId, final String name, final String value)
	{
		final UiGroupData uiGroup = getGroupById(configData, groupId);
		setCstic(uiGroup, name, value);
	}

	public void setCstic(final UiGroupData uiGroup, final String name, final String value)
	{
		final List<CsticData> cstics = uiGroup.getCstics();

		for (final CsticData cstic : cstics)
		{
			if (name.equalsIgnoreCase(cstic.getName()))
			{
				LOG.debug("setting " + cstic.getLangdepname() + "(" + cstic.getName() + ") to " + value);
				cstic.setValue(value);
				cstic.setFormattedValue(value);
				return;
			}
		}
		fail("value '" + value + "' not set, because cstic '" + name + "' not found!");
	}

	public UiGroupData getGroupById(final ConfigurationData configData, final String groupId)
	{
		final boolean fullQualifiedGroupId = groupId.contains(".");
		final List<UiGroupData> csticGroups = configData.getGroups();
		for (final UiGroupData csticGroup : csticGroups)
		{
			final String currentGroupId = csticGroup.getId();
			if ((fullQualifiedGroupId && groupId.equals(currentGroupId)
					|| (!fullQualifiedGroupId && currentGroupId.endsWith(groupId))
					|| (!fullQualifiedGroupId && lastPartStartsWith(currentGroupId, groupId))))
			{
				return csticGroup;
			}
		}

		return null;
	}

	protected boolean lastPartStartsWith(final String expression, final String groupId)
	{
		final String lastPart = expression.substring(expression.lastIndexOf(".") + 1);
		return lastPart.startsWith(groupId) || lastPart.startsWith("0" + groupId);
	}

	public void setCsticValue(final ConfigurationData configData, final String name, final String csticValueName,
			final boolean selected)
	{
		final UiGroupData uiGroupData = configData.getGroups().get(0);
		setCsticValue(uiGroupData, name, csticValueName, selected);

	}

	public void setCsticValue(final ConfigurationData configData, final String groupId, final String name,
			final String csticValueName, final boolean selected)
	{
		final UiGroupData uiGroup = getGroupById(configData, groupId);
		setCsticValue(uiGroup, name, csticValueName, selected);
	}

	public void setCsticValue(final UiGroupData uiGroup, final String name, final String csticValueName, final boolean selected)
	{
		final List<CsticData> cstics = uiGroup.getCstics();

		for (final CsticData cstic : cstics)
		{
			if (name.equalsIgnoreCase(cstic.getName()))
			{
				setCsticValue(cstic, csticValueName, selected);
				return;
			}
		}
		fail("value '" + csticValueName + "' not set, because cstic '" + name + "' not found!");
	}

	public void setCsticValue(final CsticData cstic, final String csticValueName, final boolean selected)
	{
		final List<CsticValueData> csticValues = cstic.getDomainvalues();
		for (final CsticValueData csticValueData : csticValues)
		{
			if (csticValueName.equalsIgnoreCase(csticValueData.getName()))
			{
				LOG.debug("setting " + cstic.getLangdepname() + "(" + cstic.getName() + ") to " + csticValueData.getLangdepname()
						+ "(" + csticValueData.getName() + ") to " + selected);
				csticValueData.setSelected(selected);
				return;
			}
		}
		fail("value '" + csticValueName + "' not set, because valueName was not found in cstic '" + cstic.getName() + "'");
	}
}
