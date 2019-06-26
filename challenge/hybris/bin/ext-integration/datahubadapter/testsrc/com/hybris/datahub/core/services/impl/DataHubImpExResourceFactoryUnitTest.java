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

package com.hybris.datahub.core.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;

import com.hybris.datahub.core.dto.ItemImportTaskData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UnitTest
public class DataHubImpExResourceFactoryUnitTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DataHubImpExResourceFactoryUnitTest.class);

	private DataHubImpExResourceFactory factory;

	@Before
	public void setUp()
	{
		factory = new DataHubImpExResourceFactory();
	}

	@Test
	public void testNoDefaultReaderIsSetWhenNoReaderWasExplicitlyAssigned()
	{
		final FragmentReader reader = factory.getFragmentReader();

		assertThat(reader).isNull();
	}

	@Test
	public void testFragmentReaderCanBeSpecifiedInsteadOfUsingTheDefaultOne()
	{
		final FragmentReader specific = someFragmentReader();
		factory.setFragmentReader(specific);

		final FragmentReader reader = factory.getFragmentReader();

		assertThat(reader).isSameAs(specific);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThrowsExcpetionWhenNullIsPassedForTheReaderToUse()
	{
		factory.setFragmentReader(null);
	}

	@Test
	public void testUsesExplicitlyAssignedReader() throws ImpExException
	{
		factory.setFragmentReader(someFragmentReader());

		final List<ImpExFragment> blocks = factory.extractFragments(scriptCtx());

		assertThat(blocks).isEmpty();
	}

	private FragmentReader someFragmentReader()
	{
		final List<ImpExFragment> noFragments = Collections.emptyList();
		return simulateFragmentReader(noFragments);
	}

	private ItemImportTaskData scriptCtx()
	{
		final ItemImportTaskData ctx = new ItemImportTaskData();
		final String script = "# this is a very short sample ImpEx script";
		ctx.setImpexMetaData(script.getBytes());
		return ctx;
	}

	@Test
	public void testCreatesAnInputStreamThatReadsAllFragmentsSequentiallyFromOriginalInputStream() throws IOException,
			ImpExException
	{
		final String[] fragments = {"Fragment 1", "Fragment 2"};
		prepareFragmentsParsedByTheReader(fragments);

		try (final InputStream in = factory.createScriptStream(someCtx()))
		{
			final String script = IOUtils.toString(in);

			assertThat(script).isEqualTo(StringUtils.join(fragments));
		}
	}

	private ItemImportTaskData someCtx()
	{
		final ItemImportTaskData ctx = new ItemImportTaskData();
		ctx.setImpexMetaData(new byte[0]);
		return ctx;
	}

	private void prepareFragmentsParsedByTheReader(final String... contents) throws IOException
	{
		final List<ImpExFragment> fragments = new ArrayList<>(contents.length);
		for (final String content : contents)
		{
			fragments.add(simulateFragment(content));
		}
		factory.setFragmentReader(simulateFragmentReader(fragments));
	}

	private ImpExFragment simulateFragment(final String content) throws IOException
	{
		final ImpExFragment frag = Mockito.mock(ImpExFragment.class);
		doReturn(content).when(frag).getContent();
		doReturn(new ByteArrayInputStream(content.getBytes())).when(frag).getContentAsInputStream();
		return frag;
	}

	private ImpExFragment crashingFragment() throws IOException
	{
		final ImpExFragment frag = Mockito.mock(ImpExFragment.class);
		doThrow(new IOException()).when(frag).getContent();
		doThrow(new IOException()).when(frag).getContentAsInputStream();
		return frag;
	}

	private FragmentReader simulateFragmentReader(final List<ImpExFragment> fragments)
	{
		final FragmentReader reader = Mockito.mock(FragmentReader.class);
		try
		{
			doReturn(fragments).when(reader).readScriptFragments(any(ItemImportTaskData.class));
		}
		catch (final ImpExException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
		return reader;
	}

	private FragmentReader simulateFragmentReader(final ImpExFragment... fragments)
	{
		return simulateFragmentReader(Arrays.asList(fragments));
	}

	@Test(expected = Exception.class)
	public void testThrowsExceptionWhenFailedToReadFragmentContent() throws ImpExException, IOException
	{
		final FragmentReader reader = simulateFragmentReader(crashingFragment());
		factory.setFragmentReader(reader);

		factory.createResource(scriptCtx());
	}
}
