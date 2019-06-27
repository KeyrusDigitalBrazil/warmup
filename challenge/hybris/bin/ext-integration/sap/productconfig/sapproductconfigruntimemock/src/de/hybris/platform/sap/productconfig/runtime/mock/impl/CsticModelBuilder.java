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
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * helper class to build cstics for Mock implemntations<br>
 * <b>create a new instance for every model object you want to build</b>
 */
public class CsticModelBuilder
{
	private static final String X = "X";
	private static final String SELECTED = "Selected";

	protected final CsticModel cstic;

	public CsticModelBuilder()
	{
		cstic = new CsticModelImpl();
	}


	public CsticModel build()
	{
		return cstic;
	}



	public CsticModelBuilder withNoValuesAndOptions()
	{
		cstic.setAssignedValuesWithoutCheckForChange(Collections.emptyList());
		cstic.setAssignableValues(Collections.emptyList());
		return this;
	}

	public CsticModelBuilder withInstance(final String instanceId, final String instanceName)
	{
		cstic.setInstanceId(instanceId);
		cstic.setInstanceName(instanceName);
		return this;
	}

	public CsticModelBuilder withName(final String name, final String languageDependentName)
	{
		cstic.setLanguageDependentName(languageDependentName);
		cstic.setName(name);
		return this;
	}

	/**
	 * editable (not readonly), visible, optional (not required), consistent, complete, no author
	 */
	public CsticModelBuilder withDefaultUIState()
	{
		// ui flags
		cstic.setVisible(true);
		cstic.setReadonly(false);
		cstic.setRequired(false);

		// state
		cstic.setComplete(true);
		cstic.setConsistent(true);
		cstic.setAuthor(CsticModel.AUTHOR_NOAUTHOR);

		return this;
	}

	public CsticModelBuilder numericType(final int numScale, final int typeLength)
	{
		if (numScale > 0)
		{
			cstic.setValueType(CsticModel.TYPE_FLOAT);
		}
		else
		{
			cstic.setValueType(CsticModel.TYPE_INTEGER);
		}
		cstic.setTypeLength(typeLength);
		cstic.setNumberScale(numScale);
		return this;
	}

	public CsticModelBuilder stringType()
	{
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(30);
		cstic.setNumberScale(0);
		return this;
	}

	public CsticModelBuilder simpleInput()
	{
		cstic.setMultivalued(false);
		cstic.setConstrained(false);
		cstic.setAllowsAdditionalValues(false);
		cstic.setIntervalInDomain(false);
		cstic.setStaticDomainLength(0);
		return this;
	}

	public CsticModelBuilder singleSelection()
	{
		simpleInput();
		cstic.setConstrained(true);
		cstic.setStaticDomainLength(0);
		return this;
	}

	public CsticModelBuilder multiSelection()
	{
		singleSelection();
		cstic.setMultivalued(true);
		return this;
	}

	public SimpleFlagBuilder simpleFlag()
	{
		return simpleFlag(SELECTED);
	}

	public SimpleFlagBuilder simpleFlag(final String lName)
	{
		multiSelection();
		withOptions(Collections.singletonList(buildCsticValue(X, lName)));
		cstic.setValueType(CsticModel.TYPE_STRING);
		cstic.setTypeLength(1);
		cstic.setNumberScale(0);
		return new SimpleFlagBuilder(this);
	}

	public class SimpleFlagBuilder extends CsticModelBuilder
	{
		private final CsticModelBuilder builder;

		private SimpleFlagBuilder(final CsticModelBuilder builder)
		{
			this.builder = builder;
		}

		public final CsticModelBuilder selected()
		{
			builder.withValue(builder.cstic.getAssignableValues().get(0));
			return builder;
		}
	}

	public CsticModelBuilder withValue(final CsticValueModel value)
	{
		cstic.setAssignedValuesWithoutCheckForChange(Collections.singletonList(value));
		return this;
	}

	public CsticModelBuilder withValues(final List<CsticValueModel> values)
	{
		cstic.setAssignedValuesWithoutCheckForChange(values);
		cstic.setAuthor(CsticModel.AUTHOR_DEFAULT);
		return this;
	}

	public CsticModelBuilder addOption(final CsticValueModel option)
	{
		final List<CsticValueModel> options = new ArrayList(cstic.getAssignableValues());
		options.add(option);
		withOptions(options);
		return this;
	}

	public CsticModelBuilder withOptions(final List<CsticValueModel> options)
	{
		cstic.setAssignableValues(options);
		cstic.setStaticDomainLength(options.size());
		return this;
	}


	public CsticModelBuilder required()
	{
		cstic.setRequired(true);
		if (null == cstic.getSingleValue())
		{
			cstic.setComplete(false);
		}
		return this;
	}

	public CsticModelBuilder readOnly()
	{
		cstic.setReadonly(true);
		return this;
	}

	public CsticModelBuilder allowsAdditionalValues()
	{
		cstic.setAllowsAdditionalValues(true);
		return this;
	}

	public CsticModelBuilder withLongText(final String longText)
	{
		cstic.setLongText(longText);
		return this;
	}

	public CsticModelBuilder addOption(final String name, final String langDepName)
	{
		addOption(buildCsticValue(name, langDepName));
		return this;
	}

	public CsticModelBuilder addOption(final String name, final String langDepName, final boolean selected)
	{
		if (selected)
		{
			addSelectedOption(name, langDepName);
		}
		else
		{
			addOption(buildCsticValue(name, langDepName));
		}
		return this;
	}

	protected CsticValueModel buildCsticValue(final String name, final String langDepName)
	{
		return new CsticValueModelBuilder().withName(name, langDepName).build();
	}

	public CsticModelBuilder addSelectedOption(final String name, final String langDepName)
	{
		final CsticValueModel option = buildCsticValue(name, langDepName);
		addOption(option);
		if (cstic.isMultivalued())
		{
			addValue(option);
		}
		else
		{
			withValue(option);
		}
		return this;
	}

	public CsticModelBuilder addValue(final CsticValueModel option)
	{
		final List<CsticValueModel> values = new ArrayList(cstic.getAssignedValues());
		values.add(option);
		withValues(values);
		return this;
	}

	public CsticModelBuilder hidden()
	{
		cstic.setVisible(false);
		return this;
	}

	public CsticModelBuilder typeLength(final int length)
	{
		cstic.setTypeLength(length);
		return this;
	}
}
