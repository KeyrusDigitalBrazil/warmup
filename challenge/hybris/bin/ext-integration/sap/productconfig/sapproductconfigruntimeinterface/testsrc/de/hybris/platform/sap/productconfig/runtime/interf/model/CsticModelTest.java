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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class CsticModelTest
{

	private final CsticModel classUnderTest = new CsticModelStable();

	@Test
	public void testGetMessagesDefault()
	{
		assertNotNull(classUnderTest.getMessages());
		assertTrue(classUnderTest.getMessages().isEmpty());
	}

	@Test(expected = NotImplementedException.class)
	public void testSetMessagesDefault()
	{
		classUnderTest.setMessages(Collections.emptySet());
	}


	private static final class CsticModelStable implements CsticModel
	{

		@Override
		public String getName()
		{
			// dummy
			return null;
		}

		@Override
		public void setName(final String name)
		{
			// dummy

		}

		@Override
		public String getLanguageDependentName()
		{
			// dummy
			return null;
		}

		@Override
		public void setLanguageDependentName(final String languageDependentName)
		{
			// dummy

		}

		@Override
		public String getLongText()
		{
			// dummy
			return null;
		}

		@Override
		public void setLongText(final String longText)
		{
			// dummy

		}

		@Override
		public List<CsticValueModel> getAssignedValues()
		{
			// dummy
			return null;
		}

		@Override
		public void setAssignedValuesWithoutCheckForChange(final List<CsticValueModel> assignedValues)
		{
			// dummy

		}

		@Override
		public void setAssignedValues(final List<CsticValueModel> assignedValues)
		{
			// dummy

		}

		@Override
		public List<CsticValueModel> getAssignableValues()
		{
			// dummy
			return null;
		}

		@Override
		public void setAssignableValues(final List<CsticValueModel> assignableValues)
		{
			// dummy

		}

		@Override
		public int getValueType()
		{
			// dummy
			return 0;
		}

		@Override
		public void setValueType(final int valueType)
		{
			// dummy

		}

		@Override
		public int getTypeLength()
		{
			// dummy
			return 0;
		}

		@Override
		public void setTypeLength(final int typeLength)
		{
			// dummy

		}

		@Override
		public int getNumberScale()
		{
			// dummy
			return 0;
		}

		@Override
		public void setNumberScale(final int numberScale)
		{
			// dummy

		}

		@Override
		public boolean isVisible()
		{
			// dummy
			return false;
		}

		@Override
		public void setVisible(final boolean visble)
		{
			// dummy

		}

		@Override
		public boolean isConsistent()
		{
			// dummy
			return false;
		}

		@Override
		public void setConsistent(final boolean consistent)
		{
			// dummy

		}

		@Override
		public boolean isComplete()
		{
			// dummy
			return false;
		}

		@Override
		public void setComplete(final boolean complete)
		{
			// dummy

		}

		@Override
		public boolean isReadonly()
		{
			// dummy
			return false;
		}

		@Override
		public void setReadonly(final boolean readonly)
		{
			// dummy

		}

		@Override
		public boolean isRequired()
		{
			// dummy
			return false;
		}

		@Override
		public void setRequired(final boolean required)
		{
			// dummy

		}

		@Override
		public boolean isMultivalued()
		{
			// dummy
			return false;
		}

		@Override
		public void setMultivalued(final boolean multivalued)
		{
			// dummy

		}

		@Override
		public boolean isChangedByFrontend()
		{
			// dummy
			return false;
		}

		@Override
		public void setChangedByFrontend(final boolean changedByFrontend)
		{
			// dummy

		}

		@Override
		public CsticModel clone()
		{
			throw new NotImplementedException();
		}

		@Override
		public String getAuthor()
		{
			// dummy
			return null;
		}

		@Override
		public void setAuthor(final String author)
		{
			// dummy

		}

		@Override
		public void setSingleValue(final String valueName)
		{
			// dummy

		}

		@Override
		public void addValue(final String valueName)
		{
			// dummy

		}

		@Override
		public void removeValue(final String valueName)
		{
			// dummy

		}

		@Override
		public String getSingleValue()
		{
			// dummy
			return null;
		}

		@Override
		public void clearValues()
		{
			// dummy

		}

		@Override
		public void setAllowsAdditionalValues(final boolean booleanValue)
		{
			// dummy

		}

		@Override
		public String getEntryFieldMask()
		{
			// dummy
			return null;
		}

		@Override
		public boolean isAllowsAdditionalValues()
		{
			// dummy
			return false;
		}

		@Override
		public void setEntryFieldMask(final String csticEntryFieldMask)
		{
			// dummy

		}

		@Override
		public boolean isIntervalInDomain()
		{
			// dummy
			return false;
		}

		@Override
		public void setIntervalInDomain(final boolean intervalInDomain)
		{
			// dummy

		}

		@Override
		public boolean isConstrained()
		{
			// dummy
			return false;
		}

		@Override
		public void setConstrained(final boolean constrained)
		{
			// dummy

		}

		@Override
		public int getStaticDomainLength()
		{
			// dummy
			return 0;
		}

		@Override
		public void setStaticDomainLength(final int staticDomainLength)
		{
			// dummy

		}

		@Override
		public String getPlaceholder()
		{
			// dummy
			return null;
		}

		@Override
		public void setPlaceholder(final String placeHolder)
		{
			// dummy

		}

		@Override
		public void setInstanceId(final String instanceId)
		{
			// dummy

		}

		@Override
		public String getInstanceId()
		{
			// dummy
			return null;
		}

		@Override
		public void setRetractTriggered(final boolean b)
		{
			// dummy

		}

		@Override
		public boolean isRetractTriggered()
		{
			// dummy
			return false;
		}

		@Override
		public boolean removeAssignableValue(final String valueName)
		{
			// dummy
			return false;
		}

		@Override
		public void setInstanceName(final String instanceName)
		{
			//
		}

		@Override
		public String getInstanceName()
		{

			return null;
		}
	}
}
