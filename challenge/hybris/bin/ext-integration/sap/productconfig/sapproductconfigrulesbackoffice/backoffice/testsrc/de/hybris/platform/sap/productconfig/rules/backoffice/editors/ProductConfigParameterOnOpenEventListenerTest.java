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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import com.hybris.cockpitng.editors.EditorListener;

@UnitTest
public class ProductConfigParameterOnOpenEventListenerTest
{
	@Mock
	private Combobox comboBox;

	@Mock
	private EditorListener<Object> listener;

	private ProductConfigParameterOnOpenEventListener classUnderTest;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ProductConfigParameterOnOpenEventListener(listener, comboBox);
	}

	@Test
	public void testOnEventWithoutSelectedValue()
	{
		OpenEvent event = Mockito.mock(OpenEvent.class);
		when(event.isOpen()).thenReturn(false);
		classUnderTest.onEvent(event);
	}

	@Test
	public void testOnEventWithSelectedValue()
	{
		OpenEvent event = Mockito.mock(OpenEvent.class);
		Comboitem comboItem = Mockito.mock(Comboitem.class);
		Object selectedObject = new Object();

		when(event.isOpen()).thenReturn(false);
		when(comboBox.getSelectedItem()).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(selectedObject);

		classUnderTest.onEvent(event);

		verify(listener, times(1)).onValueChanged(selectedObject);
	}

	@Test
	public void testOnEventWithSelectedValueCalledTwice()
	{
		OpenEvent event = Mockito.mock(OpenEvent.class);
		Comboitem comboItem = Mockito.mock(Comboitem.class);
		Object selectedObject = new Object();

		when(event.isOpen()).thenReturn(true);
		when(comboBox.getSelectedItem()).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(selectedObject);

		classUnderTest.onEvent(event);

		when(event.isOpen()).thenReturn(true);
		classUnderTest.onEvent(event);

		verify(listener, times(0)).onValueChanged(selectedObject);
	}
}
