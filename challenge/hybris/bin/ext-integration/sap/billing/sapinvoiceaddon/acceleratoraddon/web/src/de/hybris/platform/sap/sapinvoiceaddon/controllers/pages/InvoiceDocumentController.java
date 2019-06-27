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
package de.hybris.platform.sap.sapinvoiceaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.sap.sapinvoiceaddon.breadcrumb.impl.InvoiceDetailsMyCompanyBreadcrumbBuilder;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.sap.sapinvoiceaddon.facade.B2BInvoiceFacade;
import de.hybris.platform.sap.sapinvoiceaddon.exception.SapInvoiceException;

import java.lang.Exception;





import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.sap.sapinvoicefacades.facade.InvoiceFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import de.hybris.platform.sap.sapinvoiceaddon.constants.SapinvoiceaddonConstants;
import de.hybris.platform.sap.sapinvoicefacades.exception.UnableToRetrieveInvoiceException;

import java.io.IOException;

import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.sap.sapinvoiceaddon.utils.SapInvoiceAddonUtils;

/**
 * Controller for invoice download and invoice details.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-company/organization-management/invoicedocument")
public class InvoiceDocumentController extends AbstractSearchPageController {
	private static final Logger LOG = Logger
			.getLogger(InvoiceDocumentController.class.getName());

	@Resource(name = "b2BInvoiceFacade")
	private B2BInvoiceFacade b2BInvoiceFacade;

	@Resource(name = "invoiceFacade")
	private InvoiceFacade invoiceFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "invoiceDetailsMyCompanyBreadcrumbBuilder")
	protected InvoiceDetailsMyCompanyBreadcrumbBuilder invoiceDetailsMyCompanyBreadcrumbBuilder;

	
	final String unexpedErrorMsg = "Unexpected Error Occured while trying to fetch invoice data ::";

	/**
	 * Invoice download functionality.
	 */

	@SuppressWarnings("boxing")
	@ResponseBody
	@RequestMapping(value = "/invoicedownload", method = { RequestMethod.GET,
			RequestMethod.POST })
	@RequireHardLogIn
	public void invoiceDownload(
			@RequestParam("invoiceCode") final String invoiceCode,
			final Model model, final HttpServletRequest request,
			final HttpServletResponse response) {
		
		try {
			boolean haveToSetPDFData = false;
			byte[] pdfData = null;
			final String invoiceNumber =  SapInvoiceAddonUtils.filter(invoiceCode);
			
			final String invoiceName = invoiceNumber.concat("_invoice.pdf");

			// try to get byte arrary from current session if requested invoice
			// is present
			// Validation Check
			b2BInvoiceFacade.getOrderForCode(invoiceNumber);
			if (sessionService.getAttribute(invoiceNumber) != null) {
				pdfData = (byte[]) (sessionService.getAttribute(invoiceNumber));
				haveToSetPDFData = true;
			} else {
				pdfData = (byte[]) (invoiceFacade.generatePdf(invoiceNumber));

			}

			if (null != pdfData && pdfData.length > 0) {
				if(!StringUtils.isEmpty(invoiceName)){
   				response.setHeader("Content-Disposition", "inline;filename="
   						+ invoiceName);
				}
				response.setDateHeader("Expires", -1);
				response.setContentType("application/pdf");
				response.getOutputStream().write(pdfData);
				response.getOutputStream().close();
				response.getOutputStream().flush();
				if (!haveToSetPDFData) {
					sessionService.setAttribute(invoiceNumber, pdfData);	
				}
			} else {
				LOG.debug("Invoice for Document Number " + invoiceNumber
						+ " is not present");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}

		catch (final SapInvoiceException e) {
			LOG.error("Authorization Issue::", e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		// catching UnableToRetrieveInvoiceException exception which come from
		// facade and IOException which can come for repose write
		catch (UnableToRetrieveInvoiceException | IOException e) {
			LOG.error("Error from backend::", e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);

		}
		// Catching any other excaption
		catch (Exception e) {
			LOG.error(unexpedErrorMsg, e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/invoicedetails", method = RequestMethod.GET)
	@RequireHardLogIn
	public String invoiceDetails(
			@RequestParam("invoiceCode") final String invoiceCode,
			final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request, final HttpServletResponse response)
			throws CMSItemNotFoundException {
		try {
			final String invoiceNumber =  SapInvoiceAddonUtils.filter(invoiceCode);
			SapB2BDocumentModel invoice = b2BInvoiceFacade
					.getOrderForCode(invoiceNumber);
			B2BDocumentData invoiceData = b2BInvoiceFacade
					.convertInvoiceData(invoice);
			model.addAttribute("invoiceData", invoiceData);
			model.addAttribute("backLink", request.getHeader("Referer"));
			model.addAttribute("breadcrumbs",
					invoiceDetailsMyCompanyBreadcrumbBuilder
							.createInvoiceDetailsBreadcrumbs(invoiceNumber,
									invoiceData.getB2bUnit()));
		}

		catch (final SapInvoiceException e) {
			LOG.error("SapInvoiceException", e); 
			GlobalMessages.addFlashMessage(redirectModel,
					GlobalMessages.ERROR_MESSAGES_HOLDER,
					"invoice.page.details.error.message");
			return REDIRECT_PREFIX + request.getHeader("Referer");
		} catch (final Exception e) {
			LOG.warn("Failed to load invoice",e);
			GlobalMessages.addFlashMessage(redirectModel,
					GlobalMessages.ERROR_MESSAGES_HOLDER,
					"invoice.page.details.error.message");
			return REDIRECT_PREFIX + request.getHeader("Referer");
		}
		storeCmsPageInModel(
				model,
				getContentPageForLabelOrId(SapinvoiceaddonConstants.MYCOMPANY_INVOICE_DETAILS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS,
				ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		setUpMetaDataForContentPage(
				model,
				getContentPageForLabelOrId(SapinvoiceaddonConstants.MYCOMPANY_INVOICE_DETAILS_PAGE));
		return getViewForPage(model);

	}
}
