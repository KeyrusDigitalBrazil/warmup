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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mockito.InOrder;

@SuppressWarnings("javadoc")
public class FragmentedImpExInputStreamUnitTest
{
	@Test
	public void testReadsDataFromFragments() throws IOException
	{
		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc", "123"));
		final String content = IOUtils.toString(in);

		assertThat(content).isEqualTo("abc123");
	}

	@Test
	public void testHandlesNoDataInSomeFragments() throws IOException
	{
		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("", "b", "c"));
		final String content = IOUtils.toString(in);

		assertThat(content).isEqualTo("bc");
	}

	@Test
	public void testHandlesNoDataInAllFragments() throws IOException
	{
		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithStreams(mockStream(), mockStream()));
		final String content = IOUtils.toString(in);

		assertThat(content).isEmpty();
	}

	@Test
	public void testReadDataWithNoFragments() throws IOException
	{
		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithStreams());
		final String content = IOUtils.toString(in);

		assertThat(content).isEmpty();
	}

	@Test
	public void testHandlesNullForTheFragments() throws IOException
	{
		final String content = IOUtils.toString(new FragmentedImpExInputStream(null));

		assertThat(content).isEmpty();
	}

	@Test
	public void testClosesFragmentInputStreamBeforeReadingNextOne() throws IOException
	{
		final InputStream streamOne = mockStream();
		final InputStream streamTwo = mockStream();
		final InOrder callSeq = inOrder(streamOne, streamTwo);

		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithStreams(streamOne, streamTwo));
		IOUtils.toString(in);

		callSeq.verify(streamOne).close();
		callSeq.verify(streamTwo).read();
	}

	@Test
	public void testClosesAllFragmentStreamsWhenReadingIsDone() throws IOException
	{
		final InputStream streamOne = mockStream();
		final InputStream streamTwo = mockStream();

		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithStreams(streamOne, streamTwo));
		IOUtils.toString(in);

		verify(streamOne).close();
		verify(streamTwo).close();
	}

	@Test
	public void testCannotBeReadAfterClosing() throws IOException
	{
		final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc"));
		in.close();
		assertThat(IOUtils.toString(in)).isEmpty();
	}

	@Test
	public void testMarkNotSupported() throws IOException
	{
		try (final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc")))
		{
			assertThat(in.markSupported()).isFalse();
		}
	}

	@Test
	public void testAvailableBeforeReadingIsDone() throws IOException
	{
		try (final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc")))
		{
			assertThat(in.available()).isEqualTo(0);
		}
	}

	@Test
	public void testAvailableAfterReadingStarted() throws IOException
	{
		try (final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc")))
		{
			in.read();
			assertThat(in.available()).isEqualTo(2);
		}
	}

	@Test
	public void testSkipsBytes() throws IOException
	{
		try (final FragmentedImpExInputStream in = new FragmentedImpExInputStream(fragmentsWithData("abc")))
		{
			in.skip(2);
			assertThat(IOUtils.toString(in)).isEqualTo("c");
		}
	}

	private InputStream mockStream() throws IOException
	{
		final InputStream in = mock(InputStream.class);
		doReturn(-1).when(in).read();
		return in;
	}

	private List<ImpExFragment> fragmentsWithData(final String... contents) throws IOException
	{
		final ArrayList<ImpExFragment> fragments = new ArrayList<>(contents.length);
		for (final String fragContent : contents)
		{
			fragments.add(toFragment(fragContent));
		}
		return fragments;
	}

	private List<ImpExFragment> fragmentsWithStreams(final InputStream... streams) throws IOException
	{
		final ArrayList<ImpExFragment> fragments = new ArrayList<>(streams.length);
		for (final InputStream in : streams)
		{
			fragments.add(toFragment(in));
		}
		return fragments;
	}

	private ImpExFragment toFragment(final String content) throws IOException
	{
		return toFragment(new ByteArrayInputStream(content.getBytes()));
	}

	private ImpExFragment toFragment(final InputStream stream) throws IOException
	{
		final ImpExFragment f = mock(ImpExFragment.class);
		doReturn(stream).when(f).getContentAsInputStream();
		return f;
	}
}
