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
package de.hybris.platform.cmswebservices.media.controller;

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.API_VERSION;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.header.LocationHeaderResource;
import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.cmswebservices.security.IsAuthorizedCmsManager;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * Controller that provides media.
 */
@Controller
@IsAuthorizedCmsManager
@RequestMapping(API_VERSION + "/catalogs/{catalogId}/versions/{versionId}" + CatalogVersionMediaController.MEDIA_URI_PATH)
public class CatalogVersionMediaController
{
	private static Logger LOGGER = LoggerFactory.getLogger(CatalogVersionMediaController.class);
	public static final String MEDIA_URI_PATH = "/media";
	private static final String UTF_8 = "UTF-8";

	@Resource
	private MediaFacade mediaFacade;

	@Resource
	private LocationHeaderResource locationHeaderResource;

	@Resource
	private DataMapper dataMapper;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(value = "Upload multipart media", notes = "Upload a media.")
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When an error occurs parsing the MultipartFile (IOException) or when the media query parameters provided contain validation errors (WebserviceValidationException)"),
			@ApiResponse(code = 200, message = "The newly created Media item", response = MediaData.class) })
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "code", value = "The code to use for the newly created media.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "altText", value = "The alternative text to use for the newly created media.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "description", value = "The description to use for the newly created media.", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "multiPart", value = "The file that was uploaded for the new media.", required = false, dataType = "string", paramType = "query") })
	public MediaData uploadMultipartMedia(
			@ApiParam(value = "The unique identifier of the catalog for which to link the new media.", required = true) //
			@PathVariable("catalogId")
			final String catalogId,
			@ApiParam(value = "The specific catalog version to which the new media will be associated to.", required = true) //
			@PathVariable("versionId")
			final String versionId,
			@ApiParam(value = "The MediaData containing the data for the associated media item to be created.", required = true) //
			@ModelAttribute("media")
			final MediaData media,
			@ApiParam(value = "The file representing the actual binary contents of the media to be created.", required = true) //
			@RequestParam("file")
			final MultipartFile multiPart, //
			final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		media.setCatalogId(catalogId);
		media.setCatalogVersion(versionId);

		try
		{
			final de.hybris.platform.cmsfacades.data.MediaData convertedMediaData = //
					getDataMapper().map(media, de.hybris.platform.cmsfacades.data.MediaData.class);
			final de.hybris.platform.cmsfacades.data.MediaData newMedia = //
					getMediaFacade().addMedia(convertedMediaData, getFile(multiPart, multiPart.getInputStream()));

			response.addHeader(CmswebservicesConstants.HEADER_LOCATION,
					getLocationHeaderResource().createLocationForChildResource(request, newMedia.getCode()));
			return getDataMapper().map(newMedia, MediaData.class);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	/**
	 * With Spring security 4.2, this resource does not support media codes containing spaces.
	 * <p>
	 * The resource will accept any GET ../media/**, which includes paths and image extensions at the end. To allow this
	 * resource to accept any image extension like .jpg, .gif, .png, Spring MVC needs to be configured accordingly. See
	 * spring mvc configuration <mvc:annotation-driven content-negotiation-manager=\"contentNegotiationManager\"> where
	 * org.springframework.web.accept.ContentNegotiationManagerFactoryBean.favorPathExtension = false
	 *
	 * @deprecated since 6.7, please use {@code MediaController.getMediaByUuid()} instead.
	 */
	@RequestMapping(value = "/**", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation( //
			value = "Get media by code", notes = "Get a media by code. Deprecated since 6.7, "
					+ "please use GET /media/{uuid} to retrieve a media by a universally unique item identifier.\n"
					+ "The resource will accept any GET ../media/**, which includes paths and image extensions at the end.")
	@ApiImplicitParams(
	{ //
			@ApiImplicitParam(name = "catalogId", value = "The catalog name", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "versionId", value = "The catalog version identifier", required = true, dataType = "string", paramType = "path"),
			@ApiImplicitParam(name = "code", value = "The unique code of the Media item.", required = true, dataType = "string", paramType = "query"), })
	@ApiResponses(
	{ //
			@ApiResponse(code = 400, message = "When the media code requested cannot be found."),
			@ApiResponse(code = 200, message = "The Media item matching the code", response = MediaData.class) })
	@HybrisDeprecation(sinceVersion = "6.7")
	@Deprecated
	public MediaData getMediaByCode(
			@ApiParam(value = "The HttpServletRequest containing the unique code of the Media item", required = true) //
			final HttpServletRequest request)
	{
		final Optional<String> optionalMediaCode = parseMediaCode(request);

		final de.hybris.platform.cmsfacades.data.MediaData media = getMediaFacade() //
				.getMediaByCode(optionalMediaCode.orElseThrow(() -> new MediaNotFoundException("Media code cannot be empty")));

		return getDataMapper().map(media, MediaData.class);
	}

	/**
	 * Parses the Request URI after the Media code, which is defined by everything after the /media less the first '/',
	 * if present.
	 *
	 * @param request
	 *           the http servlet request
	 * @return an optional object to hold the parsed media code
	 */
	@SuppressWarnings("squid:S1166")
	protected Optional<String> parseMediaCode(final HttpServletRequest request)
	{
		// we know that the URI will always have the MEDIA_URI_PATH at the end
		final String uri = StringUtils.substringAfter(request.getRequestURI(), MEDIA_URI_PATH);
		if (Strings.isNotBlank(uri))
		{
			// returns the remaining path without the first '/'
			try
			{
				return Optional.of(java.net.URLDecoder.decode(uri.substring(1), UTF_8));
			}
			catch (final UnsupportedEncodingException e)
			{
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	/**
	 * Create a new media file DTO from the {@code MultipartFile}.
	 *
	 * @param file
	 *           - a Spring {@code MultipartFile}
	 * @param inputStream
	 *           - an input stream used to read the file
	 * @return a media file DTO
	 */
	public MediaFileDto getFile(final MultipartFile file, final InputStream inputStream)
	{
		final MediaFileDto mediaFile = new MediaFileDto();
		mediaFile.setInputStream(inputStream);
		mediaFile.setName(file.getOriginalFilename());
		mediaFile.setSize(file.getSize());
		mediaFile.setMime(file.getContentType());
		return mediaFile;
	}

	protected MediaFacade getMediaFacade()
	{
		return mediaFacade;
	}

	public void setMediaFacade(final MediaFacade mediaFacade)
	{
		this.mediaFacade = mediaFacade;
	}

	protected LocationHeaderResource getLocationHeaderResource()
	{
		return locationHeaderResource;
	}

	public void setLocationHeaderResource(final LocationHeaderResource locationHeaderResource)
	{
		this.locationHeaderResource = locationHeaderResource;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}
