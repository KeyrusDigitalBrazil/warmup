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
package de.hybris.platform.messagecentercsoccaddon.controllers.messagecenter;

import de.hybris.platform.messagecentercsfacades.ConversationFacade;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData;
import de.hybris.platform.messagecentercsoccaddon.constants.ErrorConstants;
import de.hybris.platform.messagecentercsoccaddon.dto.conversation.ConversationListWsDTO;
import de.hybris.platform.messagecentercsoccaddon.dto.conversation.ConversationMessageListWsDTO;
import de.hybris.platform.messagecentercsoccaddon.dto.conversation.ConversationWsDTO;
import de.hybris.platform.messagecentercsoccaddon.exceptions.MessageCenterCSException;
import de.hybris.platform.messagecentercsoccaddon.validation.MessageCenterCSValidator;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller for message center.
 */
@Controller
@RequestMapping("/{baseSiteId}/messagecenter/im/conversations")
@Api(tags = "Message Center")
public class MessageCenterCSController
{
	private static final String STATUS_OPEN = "open";
	private static final String STATUS_UNASSIGNED = "unassigned";

	@Resource(name = "conversationFacade")
	private ConversationFacade conversationFacade;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "messageCenterCSValidator")
	private MessageCenterCSValidator messageCenterCSValidator;

	@Resource(name = "conversationMessageListValidator")
	private Validator conversationMessageListValidator;
	

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(new String[] {});
	}


	@RequestMapping(value = "/customerconversations", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Gets conversations of current customer", notes = "Returns the conversation list of current customer.")
	@ApiBaseSiteIdParam
	public ConversationListWsDTO getConversationsForCustomer()
	{
		final List<ConversationData> conversations = conversationFacade.getConversationsForCustomer();
		return mapConversationList(conversations);
	}



	@RequestMapping(value = "/agentconversations", method = RequestMethod.GET)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@ApiOperation(value = "Gets unassigned or open conversations for current CSA", notes = "Returns unassigned or open conversation list for current CSA.")
	@ApiBaseSiteIdParam
	public ConversationListWsDTO getConversationsForAgent(
			@ApiParam(value = "the conversation status", allowableValues = "open, unassigned", required = true) @RequestParam(value = "status", required = true) final String status)
	{
		messageCenterCSValidator.checkIfStatusCorrect(status);
		if (STATUS_UNASSIGNED.equals(status))
		{
			final List<ConversationData> conversations = conversationFacade.getUnassignedConversations();
			return mapConversationList(conversations);
		}
		else if (STATUS_OPEN.equals(status))
		{
			final List<ConversationData> conversations = conversationFacade.getOpenConversations();
				return mapConversationList(conversations);
		}
		return null;

	}

	@RequestMapping(value = "/{conversationId}/pick", method = RequestMethod.PATCH)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@ApiOperation(value = "Picks an unassigned conversation", notes = "Picks an unassigned conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public synchronized ConversationWsDTO pickConversation(
			@ApiParam(value = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		messageCenterCSValidator.checkIfConversationClosed(conversation);
		messageCenterCSValidator.checkIfConversationAssigned(conversation);
		return dataMapper.map(conversationFacade.pickConversation(conversationId), ConversationWsDTO.class);
	}


	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@RequestMapping(value = "/{conversationId}/close", method = RequestMethod.PATCH)
	@ApiOperation(value = "Closes an open conversation", notes = "Closes an open conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public ConversationWsDTO closeConversation(
			@ApiParam(value = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		messageCenterCSValidator.checkIfConversationAccessible(conversation);
		messageCenterCSValidator.checkIfConversationClosed(conversation);

		return dataMapper.map(conversationFacade.closeConversation(conversationId), ConversationWsDTO.class);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(
	{ MessageCenterCSException.class })
	public ErrorListWsDTO handleExceptions(final MessageCenterCSException e)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(e.getType());
		error.setMessage(e.getMessage());
		error.setErrorCode(e.getErrorCode());
		errorListDto.setErrors(Collections.singletonList(error));
		return errorListDto;
	}

	@RequestMapping(method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERSUPPORTAGENTGROUP", "ROLE_CUSTOMERGROUP" })
	@ApiOperation(value = "Sends messages", notes = "Sends messages in a conversation and returns the conversation data.")
	@ApiBaseSiteIdParam
	public ConversationWsDTO sendMessage(
			@ApiParam(value = "the list of messages", required = true) @RequestBody final ConversationMessageListWsDTO conversationMessageList)
	{
		validate(conversationMessageList.getMessages(), "messages", conversationMessageListValidator);

		final ConversationMessageListData conversationMessageListData = dataMapper.map(conversationMessageList,
				ConversationMessageListData.class);

		final String conversationId = conversationMessageListData.getConversationId();
		if (StringUtils.isNotBlank(conversationId))
		{
			conversationMessageListData.setConversationId(YSanitizer.sanitize(conversationId));
			final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
			messageCenterCSValidator.checkIfConversationAccessible(conversation);
			messageCenterCSValidator.checkIfConversationClosed(conversation);
		}
		else
		{
			messageCenterCSValidator.checkIfConversationCreatable();
		}

		try
		{
			return dataMapper.map(conversationFacade.sendMessage(conversationMessageListData), ConversationWsDTO.class);
		}
		catch (final ModelSavingException e)//NOSONAR
		{
			throw new MessageCenterCSException(ErrorConstants.MESSAGE_SAVE_ERROR, ErrorConstants.MESSAGE_SAVE_MESSAGE);
		}
	}


	protected ConversationListWsDTO mapConversationList(final List<ConversationData> conversations)
	{
		final ConversationDataList conversationList = conversationFacade.getConversationDataList(conversations);

		return dataMapper.map(conversationList, ConversationListWsDTO.class);
	}

	@ResponseBody
	@RequestMapping(value = "/{conversationId}/messages", method = RequestMethod.GET)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERSUPPORTAGENTGROUP" })
	@ApiOperation(value = "Gets conversation messages for current customer or current CSA", notes = "Returns all messages of a specific conversation.")
	@ApiBaseSiteIdParam
	public ConversationMessageListWsDTO getMessagesForConversation(
			@ApiParam(value = "the uid of conversation", required = true) @PathVariable final String conversationId)
	{
		final ConversationData conversation = messageCenterCSValidator.checkIfConversationExists(conversationId);
		if (conversationFacade.isCustomer())
		{
			messageCenterCSValidator.checkIfConversationAccessible(conversation);
		}

		final List<ConversationMessageData> messages = conversationFacade.getMessagesForConversation(conversationId);
		final ConversationMessageListData messageList = conversationFacade.getConversationMessageList(messages);
		return dataMapper.map(messageList, ConversationMessageListWsDTO.class);
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}
}
