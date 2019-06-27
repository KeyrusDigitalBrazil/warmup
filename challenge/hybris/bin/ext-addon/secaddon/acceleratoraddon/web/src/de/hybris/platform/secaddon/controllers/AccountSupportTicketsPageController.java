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
package de.hybris.platform.secaddon.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.data.TicketData;
import de.hybris.platform.secaddon.data.TicketPriority;
import de.hybris.platform.secaddon.data.TicketType;
import de.hybris.platform.secaddon.data.Transcript;
import de.hybris.platform.secaddon.facades.TicketServiceFacade;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for Customer Support tickets.
 */
@Controller
@RequestMapping("/my-account")
public class AccountSupportTicketsPageController extends AbstractPageController
{
    private static final Logger LOG = Logger.getLogger(AccountSupportTicketsPageController.class);

    private static final String SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN = "{ticketId:.*}";

    @Resource(name = "accountBreadcrumbBuilder")
    private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;
    @Resource(name = "ticketServiceFacade")
    private TicketServiceFacade ticketServiceFacade;
    @Resource
    private TicketDataValidator ticketDataValidator;

    @InitBinder
    protected void initBinder(final WebDataBinder binder)
    {
        if (binder.getTarget() != null && TicketData.class.isAssignableFrom(binder.getTarget().getClass()))
        {
            binder.setValidator(ticketDataValidator);
        }
    }

    //First call to return the JSP include component.
    @RequestMapping(value =
            {"/support-tickets", "/support-ticket/" + SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN, "/add-support-ticket"})
    public String supportTickets(
            @RequestParam(value = "ticketAdded", required = false, defaultValue = "false") final boolean ticketAdded,
            final Model model) throws CMSItemNotFoundException
    {
        storeCmsPageInModel(model, getContentPageForLabelOrId(SecaddonControllerConstants.SUPPORT_TICKETS_PAGE));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                accountBreadcrumbBuilder.getBreadcrumbs(SecaddonControllerConstants.TEXT_SUPPORT_TICKETING_HISTORY));

        return getViewForPage(model);
    }

    //get ticket details
    @RequestMapping(value = "/json/support-ticket/"
            + SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public TicketData getSupportTicket(final HttpServletResponse response, @PathVariable("ticketId") final String ticketId)
            throws CMSItemNotFoundException
    {
        return ticketServiceFacade.getTicketDetails(ticketId);
    }

    /**
     * Return list of tickets
     *
     * @param response - we need to pass the Hybris-Count header
     */
    @RequestMapping(value = "/json/support-ticket", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TicketData> getSupportTickets(final HttpServletResponse response, @RequestParam("sort") final String sort,
                                              @RequestParam(value = "pageNumber", defaultValue = "1") final Integer pageNumber,
                                              @RequestParam(value = "pageSize", defaultValue = "5") final Integer pageSize)
    {

        final PageableData pageableData = getPageableData(sort, pageNumber, pageSize);
        final SearchPageData<TicketData> tickets = ticketServiceFacade.getTickets(pageableData);
        final List<TicketData> dataList = tickets.getResults();

        response.setHeader(SecaddonConstants.HYBRIS_COUNT, String.valueOf(tickets.getPagination().getTotalNumberOfResults()));
        return dataList;
    }

    /**
     * Creates the ticket and returns the proper response status
     */
    @RequestMapping(value = "/json/support-ticket/add-support-ticket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addSupportTicket(@RequestBody @Valid final TicketData ticketData)
    {
        try
        {
            ticketServiceFacade.createTicket(ticketData);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (final Exception exp)
        {
            LOG.warn("Couldn't create support ticket [" + ticketData + "]", exp);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * Adds a ticket message and returns the proper response status
     */
    @RequestMapping(value = "/json/support-ticket/" + SUPPORT_TICKET_CODE_PATH_VARIABLE_PATTERN
            + "/conversations", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addMessage(@PathVariable final String ticketId, @RequestBody final Transcript transcript)
    {
        ticketServiceFacade.addMessage(ticketId, transcript);
    }

    /**
     * Return List of ticket types
     */
    @RequestMapping(value = "/json/support-ticket/ticketTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TicketType> getSupportTicketTypes()
    {
        final List<TicketType> ticketTypes = ticketServiceFacade.getTicketTypes();
        return ticketTypes;
    }
    
    /**
     * Return ticket priorities list
     */
    @RequestMapping(value = "/json/support-ticket/ticketPriorities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TicketPriority> getSupportTicketPriorities()
    {
        final List<TicketPriority> ticketPriorities = ticketServiceFacade.getTicketPriorities();
        return ticketPriorities;
    }

    protected PageableData getPageableData(final String sort, final Integer pageNumber, final Integer pageSize)
    {
        final PageableData pageableData = new PageableData();
        pageableData.setSort(sort);
        pageableData.setCurrentPage(pageNumber);
        pageableData.setPageSize(pageSize);
        return pageableData;
    }
}
