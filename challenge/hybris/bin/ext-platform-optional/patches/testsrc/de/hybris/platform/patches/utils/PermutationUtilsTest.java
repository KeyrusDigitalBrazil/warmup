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
package de.hybris.platform.patches.utils;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.patches.data.ImpexHeaderOption;

import java.util.Arrays;

import org.junit.Test;


/**
 * Test for {@link de.hybris.platform.patches.utils.PermutationUtils}.
 */
@UnitTest
public class PermutationUtilsTest
{
	// test1
	static String[][] SOURCE_1 = new String[][]
	{
			{ "A1", "A2" },
			{ "B1", "B2" },
			{ "C1", "C2", "C3" } };
	static String[][] RESULT_1 = new String[][]
	{
			{ "A1", "B1", "C1" },
			{ "A2", "B1", "C1" },
			{ "A1", "B2", "C1" },
			{ "A2", "B2", "C1" },
			{ "A1", "B1", "C2" },
			{ "A2", "B1", "C2" },
			{ "A1", "B2", "C2" },
			{ "A2", "B2", "C2" },
			{ "A1", "B1", "C3" },
			{ "A2", "B1", "C3" },
			{ "A1", "B2", "C3" },
			{ "A2", "B2", "C3" } };

	//test2
	static String[][] SOURCE_2 = new String[][]
	{
			{ "A1", "A2", "A3" },
			{ "B1", "B2" } };


	static String[][] RESULT_2 = new String[][]
	{
			{ "A1", "B1" },
			{ "A2", "B1" },
			{ "A3", "B1" },
			{ "A1", "B2" },
			{ "A2", "B2" },
			{ "A3", "B2" } };

	//test3
	static String[][] SOURCE_3 = new String[][]
	{
			{ "A1", "A2" },
			{ "B1" },
			{ "C1", "C2", "C3" },
			{ "D1", "D2", "D3", "D4" } };

	static String[][] RESULT_3 = new String[][]
	{
			{ "A1", "B1", "C1", "D1" },
			{ "A2", "B1", "C1", "D1" },
			{ "A1", "B1", "C2", "D1" },
			{ "A2", "B1", "C2", "D1" },
			{ "A1", "B1", "C3", "D1" },
			{ "A2", "B1", "C3", "D1" },
			{ "A1", "B1", "C1", "D2" },
			{ "A2", "B1", "C1", "D2" },
			{ "A1", "B1", "C2", "D2" },
			{ "A2", "B1", "C2", "D2" },
			{ "A1", "B1", "C3", "D2" },
			{ "A2", "B1", "C3", "D2" },
			{ "A1", "B1", "C1", "D3" },
			{ "A2", "B1", "C1", "D3" },
			{ "A1", "B1", "C2", "D3" },
			{ "A2", "B1", "C2", "D3" },
			{ "A1", "B1", "C3", "D3" },
			{ "A2", "B1", "C3", "D3" },
			{ "A1", "B1", "C1", "D4" },
			{ "A2", "B1", "C1", "D4" },
			{ "A1", "B1", "C2", "D4" },
			{ "A2", "B1", "C2", "D4" },
			{ "A1", "B1", "C3", "D4" },
			{ "A2", "B1", "C3", "D4" } };

	final static String ERROR_MSG = "Generated permutation is not the same as expected (check if this is only about different order of elements)";

	@Test
	public void test1()
	{
		assertThat(test(RESULT_1, SOURCE_1)).as(ERROR_MSG).isTrue();
	}

	@Test
	public void test2()
	{
		assertThat(test(RESULT_2, SOURCE_2)).as(ERROR_MSG).isTrue();
	}

	@Test
	public void test3()
	{
		assertThat(test(RESULT_3, SOURCE_3)).as(ERROR_MSG).isTrue();
	}

	private boolean test(final String[][] expected, final String[][] entry)
	{
		final ImpexHeaderOption[][] options = convertToImpexHeaderOptions(entry);
		final ImpexHeaderOption[][] result = PermutationUtils.permutate(options);
		final String[][] stringResults = convertStrings(result);
		return Arrays.deepEquals(expected, stringResults);
	}

	/**
	 * Help method that will convert Strings array of arrays to ImpexHeaderOption arrays. Useful for testing.
	 *
	 * @param input
	 *
	 */
	protected ImpexHeaderOption[][] convertToImpexHeaderOptions(final String[][] input)
	{

		final ImpexHeaderOption[][] result = new ImpexHeaderOption[input.length][];

		for (int i = 0; i < input.length; i++)
		{
			result[i] = new ImpexHeaderOption[input[i].length];
			for (int j = 0; j < input[i].length; j++)
			{
				final ImpexHeaderOption option = new ImpexHeaderOption();
				option.setMacro(input[i][j]);
				result[i][j] = option;
			}
		}
		return result;
	}

	/**
	 * Help method that will convert ImpexHeaderOption array of arrays to Strings arrays. Useful for testing.
	 *
	 * @param input
	 *
	 */
	protected String[][] convertStrings(final ImpexHeaderOption[][] input)
	{
		final String[][] result = new String[input.length][];
		for (int i = 0; i < input.length; i++)
		{
			result[i] = new String[input[i].length];
			for (int j = 0; j < input[i].length; j++)
			{
				result[i][j] = input[i][j].getMacro();
			}
		}
		return result;
	}
}
