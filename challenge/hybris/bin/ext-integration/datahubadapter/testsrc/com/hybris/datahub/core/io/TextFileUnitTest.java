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

package com.hybris.datahub.core.io;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

@UnitTest
public class TextFileUnitTest
{
	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private static final String FILE_NAME = "TestFile.txt";
	private static final String FULL_PATH = TMP_DIR.endsWith(File.separator) ? TMP_DIR + FILE_NAME : TMP_DIR + File.separator + FILE_NAME ;
	private static final String LS = System.lineSeparator();

	@After
	public void tearDown()
	{
		try
		{
			FileUtils.cleanDirectory(new File(TMP_DIR));
		}
		catch (final IOException ioe)
		{
			// nothing
		}
	}

	@Test
	public void testCreatesTextFileWithASpecificPath()
	{
		final String path = FULL_PATH;
		final TextFile file = new TextFile(path);

		assertThat(file.getPath()).isEqualTo(path);
		assertThat(file.getFilePath().toString()).isEqualTo(path);
	}

	@Test
	public void testCreatesTextFileWithASpecifiedDirectoryAndFileName()
	{
		final TextFile file = new TextFile(TMP_DIR, FILE_NAME);

		final String expectedPath = FULL_PATH;
		assertThat(file.getPath()).isEqualTo(expectedPath);
		assertThat(file.getFilePath().toString()).isEqualTo(expectedPath);
	}

	@Test
	public void testCreatesTextFileWithASpecificFile()
	{
		final String path = FULL_PATH;
		final TextFile file = new TextFile(new File(path));

		assertThat(file.getPath()).isEqualTo(path);
		assertThat(file.getFilePath().toString()).isEqualTo(path);
	}

	@Test
	public void testDoesNotSaveFileFileBeforeContentWasWritten()
	{
		assertThat(testFile().getFilePath()).doesNotExist();
	}

	@Test
	public void testSaveCreatesFileIfItDoesNotExist() throws IOException
	{
		final TextFile file = testFile();
		assert !file.getFilePath().exists() : "The test file not expected to exist yet";

		file.save("file content");

		assertThat(file.getFilePath()).exists();
	}

	@Test
	public void testSavePersistsTheContent() throws IOException
	{
		testFile().save("file content");
		final String content = testFile().read();

		assertThat(content).isEqualTo("file content");
	}

	@Test
	public void testSaveReplacesPreviousContent() throws IOException
	{
		final TextFile file = testFile();
		file.save("original content");
		file.save("new content");

		assertThat(file.read()).isEqualTo("new content");
	}

	@Test
	public void testDeleteDoesNothingIfTheFileWasNeverSaved() throws IOException
	{
		final TextFile file = testFile();
		assert !file.getFilePath().exists() : "Test file should not exist on the file system";

		file.delete();

		assertThat(file.getFilePath()).doesNotExist();
	}

	@Test
	public void testDeleteRemovesAPreviouslySavedFile() throws IOException
	{
		final TextFile file = testFile();
		file.save("this saves the file on the file system");
		assert file.getFilePath().exists() : "Test file should exist on the file system";

		file.delete();

		assertThat(file.getFilePath()).doesNotExist();
	}

	@Test
	public void testReadReturnsEmptyTextBeforeTheFileWasEverSaved() throws IOException
	{
		assertThat(testFile().read()).isEmpty();
	}

	@Test
	public void testAppendAddsToPreviousContentAndDoesNotReplaceIt() throws IOException
	{
		final TextFile file = testFile();
		file.append("this is ");
		file.append("file content");

		assertThat(file.read()).isEqualTo("this is file content");
	}

	@Test
	public void testAppendLineAddsLineOfTextToThePreviousContent() throws IOException
	{
		final TextFile file = testFile();
		file.appendLine("line 1");
		file.appendLine("line 2");

		assertThat(file.read()).isEqualTo("line 1" + LS + "line 2" + LS);
	}

	@Test
	public void testReadLineReturnsTextOnTheLineSpecifiedByItsNumber() throws IOException
	{
		testFile().appendLine("line 1").appendLine("line 2").appendLine("line 3");
		assertThat(testFile().readLine(3)).isEqualTo("line 3");
	}

	@Test
	public void testReadLineReturnsNullForLineNumber0() throws IOException
	{
		testFile().save("line 1");
		assertThat(testFile().readLine(0)).isNull();
	}

	@Test
	public void testReadLineReturnsNullWhenLineNumberExceedsNumberOfLinesInTheFile() throws IOException
	{
		testFile().appendLine("line 1");
		assertThat(testFile().readLine(2)).isNull();
	}

	@Test
	public void testReadLineReturnsNullWhenNothingHasBeenSavedIntoTheFileYet() throws IOException
	{
		assertThat(testFile().readLine(1)).isNull();
	}

	@Test
	public void testReadLineTreatsTextWithoutLineBreaksAsSingleLine() throws IOException
	{
		testFile().save("line 1");
		assertThat(testFile().readLine(1)).isEqualTo("line 1");
	}

	@Test
	public void testReadLineReturnsEmptyStringForLinesWithNoTextPresent() throws IOException
	{
		testFile().save(LS);
		assertThat(testFile().readLine(1)).isEmpty();
	}

	@Test
	public void testToStringContainsTheFilePath()
	{
		final TextFile file = testFile();
		assertThat(file.toString()).contains(file.getPath());
	}

	@Test
	public void testFilesAreEqualWhenTheyAreBasedOnTheSamePath()
	{
		final TextFile aFile = new TextFile(FULL_PATH);
		final TextFile anotherFile = new TextFile(TMP_DIR, FILE_NAME);

		assertThat(aFile).isEqualTo(anotherFile);
	}

	@Test
	public void testFilesAreNotEqualWhenTheyAreNotOfTheSameClass()
	{
		final TextFile textFile = new TextFile(TMP_DIR, FILE_NAME);
		final File ioFile = new File(TMP_DIR, FILE_NAME);

		assertThat(textFile).isNotEqualTo(ioFile);
	}

	@Test
	public void testFilesAreNotEqualWhenTheirPathesAreDifferent()
	{
		final TextFile aFile = new TextFile(TMP_DIR, "file1.txt");
		final TextFile anotherFile = new TextFile(TMP_DIR, "file2.txt");

		assertThat(aFile).isNotEqualTo(anotherFile);
	}

	@Test
	public void testHashCodeIsTheSameWhenFilesAreEqual()
	{
		final TextFile aFile = new TextFile(FULL_PATH);
		final TextFile anotherFile = new TextFile(TMP_DIR, FILE_NAME);

		assertThat(aFile.hashCode()).isEqualTo(anotherFile.hashCode());
	}

	@Test
	public void testHashCodeIsDifferentWhenFilesAreNotEqual()
	{
		final TextFile aFile = new TextFile(TMP_DIR, "file1.txt");
		final TextFile anotherFile = new TextFile(TMP_DIR, "file2.txt");

		assertThat(aFile.hashCode()).isNotEqualTo(anotherFile.hashCode());
	}

	private TextFile testFile()
	{
		return new TextFile(TMP_DIR, FILE_NAME);
	}

	@Test
	public void testCreatesNonExistingPathForTheFileName() throws IOException
	{
		final TextFile file = new TextFile(TMP_DIR, "junit/test/file.txt");
		assert !file.getFilePath().getParentFile().exists() : "directory should not exist for this test";

		file.save("some text to write");
		assertThat(file.getFilePath()).exists();
	}
}
