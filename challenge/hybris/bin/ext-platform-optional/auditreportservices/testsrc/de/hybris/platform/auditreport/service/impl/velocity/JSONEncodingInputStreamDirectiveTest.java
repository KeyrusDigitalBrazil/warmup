/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.auditreport.service.impl.velocity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.directive.DirectiveConstants;
import org.apache.velocity.runtime.parser.node.Node;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JSONEncodingInputStreamDirectiveTest
{

	public static final String TO_ENCODE = "_&<>a!";
	public static final String ENCODED = "_&amp;&lt;&gt;a!";
	@Spy
	private JSONEncodingInputStreamDirective directive;

	@Test
	public void shouldReturnTrueOnRenderValidInput() throws IOException
	{
		//given
		final StringWriter writer = new StringWriter();
		final Node node = mock(Node.class);
		final InternalContextAdapter context = mock(InternalContextAdapter.class);
		final Node isNode = mock(Node.class);
		final Node shouldEncodeNode = mock(Node.class);

		when(node.jjtGetChild(0)).thenReturn(isNode);
		when(node.jjtGetChild(1)).thenReturn(shouldEncodeNode);
		when(isNode.value(context)).thenReturn(IOUtils.toInputStream(TO_ENCODE));
		when(shouldEncodeNode.value(context)).thenReturn(null);

		//when
		final boolean success = directive.render(context, writer, node);

		//then
		assertThat(writer.toString()).isEqualTo(ENCODED);
		assertThat(success).isTrue();
	}

	@Test
	public void shouldReturnFalseOnRenderInvalidInput() throws IOException
	{
		//given
		final Node node = mock(Node.class);
		final InternalContextAdapter context = mock(InternalContextAdapter.class);
		final Node internalNode = mock(Node.class);

		when(node.jjtGetChild(0)).thenReturn(internalNode);
		when(internalNode.value(context)).thenReturn(null);

		//when
		final boolean success = directive.render(context, null, node);

		//then
		assertThat(success).isFalse();
	}

	@Test
	public void shouldEncodeHTMLEntities()
	{
		//when
		final String encoded = directive.encodeHTMLEntities(TO_ENCODE);

		//then
		assertThat(encoded).isEqualTo(ENCODED);
	}

	@Test
	public void shouldReturnNameUsedInImpex()
	{
		//when
		final String name = directive.getName();

		//then
		assertThat(name).isEqualTo("encodeJsonIS");
	}

	@Test
	public void shouldReturnLinearDirectiveType()
	{
		//when
		final int type = directive.getType();

		//then
		assertThat(type).isEqualTo(DirectiveConstants.LINE);
	}
}
