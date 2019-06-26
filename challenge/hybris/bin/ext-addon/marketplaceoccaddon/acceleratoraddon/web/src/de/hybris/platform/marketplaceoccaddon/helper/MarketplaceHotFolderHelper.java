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
package de.hybris.platform.marketplaceoccaddon.helper;

import de.hybris.platform.marketplaceoccaddon.constants.ErrorMessageConstants;
import de.hybris.platform.marketplaceoccaddon.exceptions.FileDownloadException;
import de.hybris.platform.marketplaceoccaddon.exceptions.FileUploadException;
import de.hybris.platform.marketplaceservices.model.VendorUserModel;
import de.hybris.platform.marketplacewebservices.hotfolder.dto.FileUploadWsDTO;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class MarketplaceHotFolderHelper
{

	private static final long MAX_FILE_LENGTH = 512L * 1024 * 1024;
	private static final String FILE_EXTENSION_CSV = "csv";
	private static final String FILE_EXTENSION_ZIP = "zip";
	private static final String FILE_EXTENSION_LOG = "log";
	private static final String UPLOAD_SUCCESS = "Upload success";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "marketplaceBaseDirectory")
	private String marketplaceHotfolderDir;

	@Resource(name = "exportDataBaseDirectory")
	private String exportDataBaseDirectory;

	/**
	 * @param request
	 *           http servlet request
	 *
	 * @return FileUploadWsDTO if process success, return successful message
	 */
	public FileUploadWsDTO processUpload(final HttpServletRequest request)
	{
		final String filename = StringUtils.defaultString(request.getHeader("Filename")).replaceAll("/", StringUtils.EMPTY)
				.replaceAll("\\\\", StringUtils.EMPTY);
		final String extension = FilenameUtils.getExtension(filename);

		switch (extension)
		{
			case FILE_EXTENSION_CSV:
				processCSVFile(request, filename);
				break;

			case FILE_EXTENSION_ZIP:
				processZIPFile(request);
				break;

			default:
				throw new FileUploadException(ErrorMessageConstants.UNSUPPORTED_FILE_TYPE);
		}

		final FileUploadWsDTO dto = new FileUploadWsDTO();
		dto.setStatus(UPLOAD_SUCCESS);
		dto.setFilename(filename);

		return dto;
	}

	/**
	 * Process download vendor orders
	 *
	 * @param response
	 *           http servlet response
	 */
	public void processOrdersDownload(final HttpServletResponse response)
	{
		download(response, "text/csv;charset=" + StandardCharsets.UTF_8.name(), getOrderExportFile());
	}

	/**
	 * Process download logs
	 *
	 * @param response
	 *           http servlet response
	 */
	public void processLogsDownload(final HttpServletResponse response)
	{
		final File vendorLogDirectory = new File(getOrCreateDir(getMarketplaceHotfolderDir(), getVendorCode()), FILE_EXTENSION_LOG);
		final File[] logFiles = searchFilesInDirectory(vendorLogDirectory, FILE_EXTENSION_LOG);
		if (ArrayUtils.isEmpty(logFiles))
		{
			throw new FileDownloadException(ErrorMessageConstants.FILE_NOT_EXIST);
		}

		final File zipLog = new File(FILE_EXTENSION_LOG + FilenameUtils.EXTENSION_SEPARATOR + FILE_EXTENSION_ZIP);
		zipLogFiles(zipLog, logFiles);
		download(response, "application/x-zip-compressed", zipLog);
	}


	protected void processZIPFile(final HttpServletRequest request)
	{
		File tempFile = null;
		ZipFile zipFile = null;
		try //NOSONAR
		{
			tempFile = checkFileLength(request);
			checkEntryFiles(tempFile);
			zipFile = new ZipFile(tempFile);
			final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

			while (entries.hasMoreElements())
			{
				final ZipArchiveEntry entry = entries.nextElement();
				final File vendorFolder = getOrCreateDir(getMarketplaceHotfolderDir(), getVendorCode());
				final File entryFile = new File(vendorFolder, entry.getName());

				if (entry.isDirectory())
				{
					createEntryFile(entryFile);
					continue;
				}
				FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), entryFile);
			}
		}
		catch (final IOException e)
		{
			throw new FileUploadException(ErrorMessageConstants.UNZIP_FILE_ERROR, e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(zipFile);
			FileUtils.deleteQuietly(tempFile);
		}
	}

	protected void checkEntryFiles(final File file) throws IOException
	{
		try (ZipFile zipFile = new ZipFile(file))
		{
			final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
			while (entries.hasMoreElements())
			{
				final ZipArchiveEntry entry = entries.nextElement();
				final File vendorFolder = getOrCreateDir(getMarketplaceHotfolderDir(), getVendorCode());
				final String canonicalVendorDirPath = vendorFolder.getCanonicalPath();
				final File entryFile = new File(vendorFolder, entry.getName());
				final String canonicalEntryFile = entryFile.getCanonicalPath();
				if (!canonicalEntryFile.startsWith(canonicalVendorDirPath + File.separator))
				{
					throw new FileUploadException(ErrorMessageConstants.UNZIP_FILE_NAME_FORMAT_INVALID);
				}
			}
		}
	}

	protected void createEntryFile(final File entryFile)
	{
		if (!entryFile.exists())
		{
			entryFile.mkdir();
		}
	}

	protected void processCSVFile(final HttpServletRequest request, final String filename)
	{
		File tempFile = null;
		try
		{
			tempFile = checkFileLength(request);
			final File csvFile = new File(getOrCreateDir(getMarketplaceHotfolderDir(), getVendorCode()),
					FilenameUtils.normalize(filename));
			FileUtils.copyFile(tempFile, csvFile);
		}
		catch (final IOException e)
		{
			throw new FileUploadException(ErrorMessageConstants.PROCESS_FILE_ERROR, e.getMessage(), e);
		}
		finally
		{
			FileUtils.deleteQuietly(tempFile);
		}
	}

	protected File checkFileLength(final HttpServletRequest request) throws IOException
	{
		final File tempFile = new File(getMarketplaceHotfolderDir(), UUID.randomUUID().toString() + ".tmp");
		FileUtils.copyInputStreamToFile(request.getInputStream(), tempFile);
		if (tempFile.length() > MAX_FILE_LENGTH)
		{
			FileUtils.deleteQuietly(tempFile);
			throw new FileUploadException(ErrorMessageConstants.SIZE_EXCEED_LIMIT);
		}
		return tempFile;
	}

	protected File getOrderExportFile()
	{
		final File exportDir = getOrCreateDir(getExportDataBaseDirectory(), getVendorCode());
		final File[] files = searchFilesInDirectory(exportDir, FILE_EXTENSION_CSV);
		if (files.length == 0)
		{
			throw new FileDownloadException(ErrorMessageConstants.FILE_NOT_EXIST);
		}

		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

		return files[0];
	}

	protected File[] searchFilesInDirectory(final File dir, final String extension)
	{
		return dir.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(final File file, final String name)
			{
				return StringUtils.equalsIgnoreCase(extension, FilenameUtils.getExtension(name));
			}
		});
	}

	protected void download(final HttpServletResponse response, final String contentType, final File file)
	{
		if (MAX_FILE_LENGTH < file.length())
		{
			throw new FileDownloadException(ErrorMessageConstants.SIZE_EXCEED_LIMIT);
		}

		try (final OutputStream output = response.getOutputStream())
		{
			response.setContentType(contentType);
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			setHeaderDisposition(response, file.getName());
			FileUtils.copyFile(file, output);
		}
		catch (final IOException e)
		{
			throw new FileDownloadException(ErrorMessageConstants.PROCESS_FILE_ERROR, e.getMessage(), e);
		}
	}

	protected void setHeaderDisposition(final HttpServletResponse response, final String filename)
	{
		String headerValue = "attachment;";
		headerValue += " filename=\"" + encodeURIComponent(filename) + "\";";
		headerValue += " filename*=" + StandardCharsets.UTF_8.name() + "''" + encodeURIComponent(filename);
		response.setHeader("Content-Disposition", headerValue);
	}

	protected String encodeURIComponent(final String value)
	{
		try
		{
			return URLEncoder.encode(value, StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
		}
		catch (final UnsupportedEncodingException e)
		{
			throw new FileDownloadException(ErrorMessageConstants.PROCESS_FILE_ERROR, null, e);
		}
	}

	protected void zipLogFiles(final File zipFiles, final File[] logFiles)
	{
		try (final ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(zipFiles))
		{
			final List<File> toBeDeletedFiles = new ArrayList<>();
			for (final File file : logFiles)
			{
				if (MAX_FILE_LENGTH < file.length())
				{
					throw new FileDownloadException(ErrorMessageConstants.SIZE_EXCEED_LIMIT);
				}

				final ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getName());
				zipOutputStream.putArchiveEntry(zipArchiveEntry);
				FileUtils.copyFile(file, zipOutputStream);
				zipOutputStream.closeArchiveEntry();
				toBeDeletedFiles.add(file);
			}
			zipOutputStream.finish();

			if (MAX_FILE_LENGTH > zipFiles.length())
			{
				toBeDeletedFiles.forEach(file -> FileUtils.deleteQuietly(file));
			}
		}
		catch (final IOException e)
		{
			throw new FileDownloadException(ErrorMessageConstants.COMPRESS_FILE_ERROR, null, e);
		}
	}

	protected File getOrCreateDir(final String path, final String child)
	{
		final File file = new File(FilenameUtils.normalize(path), FilenameUtils.normalize(child));
		if (!file.exists())
		{
			file.mkdirs();
		}
		return file;
	}

	protected String getVendorCode()
	{
		return ((VendorUserModel) getUserService().getCurrentUser()).getVendor().getCode();
	}

	protected UserService getUserService()
	{
		return userService;
	}

	protected String getMarketplaceHotfolderDir()
	{
		return marketplaceHotfolderDir;
	}

	protected String getExportDataBaseDirectory()
	{
		return exportDataBaseDirectory;
	}

}
