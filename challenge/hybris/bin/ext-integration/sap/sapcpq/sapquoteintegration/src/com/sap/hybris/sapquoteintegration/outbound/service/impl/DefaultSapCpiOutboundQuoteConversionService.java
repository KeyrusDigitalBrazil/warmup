/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.sapquoteintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteCommentModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteItemModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteStatusModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteConversionService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteCommentMapperService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteEntryMapperService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteMapperService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiQuoteStatusMapperService;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;

/**
 *
 */
public class DefaultSapCpiOutboundQuoteConversionService implements SapCpiOutboundQuoteConversionService {

    protected static final Logger LOG = Logger.getLogger(DefaultSapCpiOutboundQuoteConversionService.class);

    private List<SapCpiQuoteMapperService<QuoteModel, SAPCpiOutboundQuoteModel>> sapCpiQuoteMappers;
    private List<SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>> sapCpiQuoteEntryMappers;
    private List<SapCpiQuoteCommentMapperService<CommentModel, SAPCpiOutboundQuoteCommentModel>> sapCpiQuoteCommentMappers;
    private List<SapCpiQuoteStatusMapperService<QuoteModel, SAPCpiOutboundQuoteStatusModel>> sapCpiQuoteStatusMappers;
    private static String UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    private QuoteService quoteService;

    @Override
    public SAPCpiOutboundQuoteModel convertQuoteToSapCpiQuote(final QuoteModel quoteModel) {
        final SAPCpiOutboundQuoteModel scpiQuoteModel = new SAPCpiOutboundQuoteModel();
        getSapCpiQuoteMappers().forEach(mapper -> mapper.map(quoteModel, scpiQuoteModel));

        // Mapping header comments
        final List<CommentModel> comments = quoteModel.getComments();
        if (comments != null && !comments.isEmpty()) {
            final List<SAPCpiOutboundQuoteCommentModel> scpiQuoteComments = new ArrayList<>();
            comments.forEach(comment -> {
                final SAPCpiOutboundQuoteCommentModel scpiQuoteComment = new SAPCpiOutboundQuoteCommentModel();
                getSapCpiQuoteCommentMappers().forEach(mapper -> mapper.map(comment, scpiQuoteComment));
                scpiQuoteComments.add(scpiQuoteComment);
            });
            scpiQuoteModel.setSapCpiOutboundQuoteComments(scpiQuoteComments);

        }

        // Mapping quote entries
        final List<SAPCpiOutboundQuoteItemModel> scpiQuoteItems = new ArrayList<>();
        quoteModel.getEntries().forEach(entry -> {
            final SAPCpiOutboundQuoteItemModel scpiQuoteItem = new SAPCpiOutboundQuoteItemModel();
            getSapCpiQuoteEntryMappers().forEach(mapper -> mapper.map(entry, scpiQuoteItem));

            // Mapping entry comments
            final List<CommentModel> entryComments = entry.getComments();
            if (entryComments != null && !entryComments.isEmpty()) {
                final List<SAPCpiOutboundQuoteCommentModel> scpiQuoteItemComments = new ArrayList<>();
                entryComments.forEach(entryComment -> {
                    final SAPCpiOutboundQuoteCommentModel scpiQuoteComment = new SAPCpiOutboundQuoteCommentModel();
                    getSapCpiQuoteCommentMappers().forEach(mapper -> mapper.map(entryComment, scpiQuoteComment));
                    scpiQuoteComment.setEntryNumber(entry.getEntryNumber().toString());
                    scpiQuoteItemComments.add(scpiQuoteComment);
                });
                scpiQuoteItem.setSapCpiOutboundQuoteItemComments(scpiQuoteItemComments);

            }
            scpiQuoteItems.add(scpiQuoteItem);

        });

        scpiQuoteModel.setSapCpiOutboundQuoteItems(new HashSet<>(scpiQuoteItems));

        return scpiQuoteModel;
    }

    @Override
    public SAPCpiOutboundQuoteStatusModel convertQuoteToSapCpiQuoteStatus(QuoteModel quote) {

        final SAPCpiOutboundQuoteStatusModel scpiQuoteStatus = new SAPCpiOutboundQuoteStatusModel();
        getSapCpiQuoteStatusMappers().forEach(mapper -> mapper.map(quote, scpiQuoteStatus));

        if (QuoteState.CANCELLED.equals(quote.getState())) {

            // Mapping header comments
            final List<CommentModel> comments = getDeltaComments(quote);
            if (comments != null && !comments.isEmpty()) {
                final List<SAPCpiOutboundQuoteCommentModel> scpiQuoteComments = new ArrayList<>();
                comments.forEach(comment -> {
                    final SAPCpiOutboundQuoteCommentModel scpiQuoteComment = new SAPCpiOutboundQuoteCommentModel();
                    getSapCpiQuoteCommentMappers().forEach(mapper -> mapper.map(comment, scpiQuoteComment));
                    scpiQuoteComments.add(scpiQuoteComment);
                });
                scpiQuoteStatus.setHeaderComments(scpiQuoteComments);
            }

            // Mapping quote entries
            final List<SAPCpiOutboundQuoteCommentModel> scpiQuoteItemComments = new ArrayList<>();
            quote.getEntries().forEach(entry -> {

                // Mapping entry comments
                final List<CommentModel> entryComments = getDeltaCommentsForEntry(entry);
                if (entryComments != null && !entryComments.isEmpty()) {
                    entryComments.forEach(entryComment -> {
                        final SAPCpiOutboundQuoteCommentModel scpiQuoteComment = new SAPCpiOutboundQuoteCommentModel();
                        getSapCpiQuoteCommentMappers().forEach(mapper -> mapper.map(entryComment, scpiQuoteComment));
                        QuoteEntryModel quoteEntry = (QuoteEntryModel) entry;
                        scpiQuoteComment.setEntryNumber(quoteEntry.getExternalQuoteEntryId() != null ?
                                            quoteEntry.getExternalQuoteEntryId() : quoteEntry.getEntryNumber().toString());
                        scpiQuoteItemComments.add(scpiQuoteComment);
                    });
                }
            });
            scpiQuoteStatus.setItemComments(scpiQuoteItemComments);
        }

        return scpiQuoteStatus;
    }

    /**
     *
     */
    protected List<CommentModel> getDeltaComments(final QuoteModel quoteModel) {
        List<CommentModel> comments = quoteModel.getComments();
        if (comments != null && quoteModel.getVersion() != 1) {
        	comments = new ArrayList<>();
            for(CommentModel c : quoteModel.getComments()) {
            	if(!c.getCode().matches(UUID_REGEX)) {
            		comments.add(c);
            	}
            }
        }
        return comments;
    }

    /**
     *
     */
    protected List<CommentModel> getDeltaCommentsForEntry(final AbstractOrderEntryModel item) {
        List<CommentModel> comments = item.getComments();
        final QuoteModel quoteModel = (QuoteModel) item.getOrder();
        if (comments != null && quoteModel.getVersion() != 1) {
            comments = new ArrayList<>();
            for(CommentModel c : item.getComments()) {
            	if(!c.getCode().matches(UUID_REGEX)) {
            		comments.add(c);
            	}
            }
        }
        return comments;
    }

    /**
     * @return the sapCpiQuoteEntryMappers
     */
    public List<SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>> getSapCpiQuoteEntryMappers() {
        return sapCpiQuoteEntryMappers;
    }

    /**
     * @param sapCpiQuoteEntryMappers
     *            the sapCpiQuoteEntryMappers to set
     */
    @Required
    public void setSapCpiQuoteEntryMappers(
            final List<SapCpiQuoteEntryMapperService<AbstractOrderEntryModel, SAPCpiOutboundQuoteItemModel>> sapCpiQuoteEntryMappers) {
        this.sapCpiQuoteEntryMappers = sapCpiQuoteEntryMappers;
    }

    /**
     * @return the sapCpiQuoteCommentMappers
     */
    public List<SapCpiQuoteCommentMapperService<CommentModel, SAPCpiOutboundQuoteCommentModel>> getSapCpiQuoteCommentMappers() {
        return sapCpiQuoteCommentMappers;
    }

    /**
     * @param sapCpiQuoteCommentMappers
     *            the sapCpiQuoteCommentMappers to set
     */
    @Required
    public void setSapCpiQuoteCommentMappers(
            final List<SapCpiQuoteCommentMapperService<CommentModel, SAPCpiOutboundQuoteCommentModel>> sapCpiQuoteCommentMappers) {
        this.sapCpiQuoteCommentMappers = sapCpiQuoteCommentMappers;
    }

    public List<SapCpiQuoteMapperService<QuoteModel, SAPCpiOutboundQuoteModel>> getSapCpiQuoteMappers() {
        return sapCpiQuoteMappers;
    }

    @Required
    public void setSapCpiQuoteMappers(
            final List<SapCpiQuoteMapperService<QuoteModel, SAPCpiOutboundQuoteModel>> sapCpiQuoteMappers) {
        this.sapCpiQuoteMappers = sapCpiQuoteMappers;
    }

    public QuoteService getQuoteService() {
        return quoteService;
    }

    @Required
    public void setQuoteService(final QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    public List<SapCpiQuoteStatusMapperService<QuoteModel, SAPCpiOutboundQuoteStatusModel>> getSapCpiQuoteStatusMappers() {
        return sapCpiQuoteStatusMappers;
    }

    @Required
    public void setSapCpiQuoteStatusMappers(
            List<SapCpiQuoteStatusMapperService<QuoteModel, SAPCpiOutboundQuoteStatusModel>> sapCpiQuoteStatusMappers) {
        this.sapCpiQuoteStatusMappers = sapCpiQuoteStatusMappers;
    }

}
