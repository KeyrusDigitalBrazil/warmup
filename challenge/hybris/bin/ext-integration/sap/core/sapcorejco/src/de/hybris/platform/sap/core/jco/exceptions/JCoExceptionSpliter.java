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
package de.hybris.platform.sap.core.jco.exceptions;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoException;

/**
 * Splits Exceptions from the JCo Library in exception classes of this package.
 */
public class JCoExceptionSpliter {
    /**
     * Logger.
     */
    private static final Logger LOG = Logger
            .getLogger(JCoExceptionSpliter.class.getName());

    private JCoExceptionSpliter() {

    }

    /**
     * Splits the general purpose Exception <code>JCoException</code> used by
     * JCo into some more meaningful and JCo independent exceptions.
     * 
     * 
     * @param jcoEx
     *            The JCo exception to be split
     * @throws BackendCommunicationException
     *             BackendCommunicationException
     * @throws BackendLogonException
     *             BackendLogonException
     * @throws BackendSystemFailureException
     *             BackendSystemFailureException
     * @throws BackendServerStartupException
     *             BackendServerStartupException
     * @throws BackendException
     *             BackendException
     */
    public static void splitAndThrowException(final JCoException jcoEx)
            throws BackendException {

        LOG.debug(jcoEx);
        checkBackendOrBackendRuntimeException(jcoEx);

        switch (jcoEx.getGroup()) {

        case JCoException.JCO_ERROR_COMMUNICATION:
            throwBackendCommunicationException(jcoEx);
            break;

        case JCoException.JCO_ERROR_LOGON_FAILURE:
            throwBackendLogonException(jcoEx);
            break;

        case JCoException.JCO_ERROR_SYSTEM_FAILURE:
            throwCoreBaseRuntimeSystemFailureException(jcoEx);
            break;

        case JCoException.JCO_ERROR_SERVER_STARTUP:
            throwBackendServerStartupException(jcoEx);
            break;

        case JCoException.JCO_ERROR_RESOURCE:
            throwCoreBaseRuntimeException(jcoEx);
            break;

        case JCoException.JCO_ERROR_DESTINATION_DATA_INVALID:
            String msg = "Destination data was changed at runtime for the currently used destination : "
                    + jcoEx.getMessage();
            throw new DestinationChangedRuntimeException(msg, jcoEx);

        default:
            msg = "Backend Runtime Exception: Unknown exception \""
                    + jcoEx.getMessage() + "\" occurs. (Key: " + jcoEx.getKey()+ ")";
            throw new BackendRuntimeException(msg, jcoEx);

        }
    }

    private static void checkBackendOrBackendRuntimeException(JCoException jcoEx)
            throws BackendException, BackendRuntimeException {
        if (isBackendException(jcoEx.getGroup())) {
            throwBackendException(jcoEx);
        } else if (isBackendRuntimeException(jcoEx.getGroup())) {
            throwBackendRuntimeException(jcoEx);
        }

    }

    private static boolean isBackendRuntimeException(int group) {
        switch (group) {
        case JCoException.JCO_ERROR_CONVERSION:
        case JCoException.JCO_ERROR_FIELD_NOT_FOUND:
        case JCoException.JCO_ERROR_FUNCTION_NOT_FOUND:
        case JCoException.JCO_ERROR_NULL_HANDLE:
        case JCoException.JCO_ERROR_UNSUPPORTED_CODEPAGE:
        case JCoException.JCO_ERROR_XML_PARSER:
            return true;
        default:
            break;
        }
        return false;
    }

    private static boolean isBackendException(int group) {
        switch (group) {
        case JCoException.JCO_ERROR_APPLICATION_EXCEPTION:
        case JCoException.JCO_ERROR_CANCELLED:
        case JCoException.JCO_ERROR_ILLEGAL_TID:
        case JCoException.JCO_ERROR_INTERNAL:
        case JCoException.JCO_ERROR_NOT_SUPPORTED:
        case JCoException.JCO_ERROR_PROGRAM:
        case JCoException.JCO_ERROR_PROTOCOL:
        case JCoException.JCO_ERROR_STATE_BUSY:
            return true;
        default:
            break;
        }
        return false;
    }

    private static void throwBackendCommunicationException(
            final JCoException jcoEx) throws BackendCommunicationException {
        String msg = "Backend Communication Error: " + jcoEx.getMessage()
                + " occurs. (Group: JCO_ERROR_COMMUNICATION)";
        LOG.log(Level.DEBUG, msg);
        throw new BackendCommunicationException(msg, jcoEx);
    }

    private static void throwCoreBaseRuntimeException(final JCoException jcoEx)
            throws CoreBaseRuntimeException {
        String msg = "JCO_ERROR_RESOURCE: " + jcoEx.getMessage()
                + " occurs. (Group: JCO_ERROR_RESOURCE)";
        LOG.log(Level.DEBUG, msg);
        throw new CoreBaseRuntimeException(msg, jcoEx);
    }

    private static void throwBackendException(final JCoException jcoEx)
            throws BackendException {
        String msg = "Backend Exception: " + jcoEx.getMessage()
                + " occurs. (Group: " + jcoEx.getGroup() + ")";
        LOG.log(Level.DEBUG, msg);
        throw new BackendException(msg, jcoEx);
    }

    private static void throwBackendRuntimeException(final JCoException jcoEx)
            throws BackendRuntimeException {
        String msg = "Backend Runtime Exception: " + jcoEx.getMessage()
                + " occurs. (Group: " + jcoEx.getGroup() + ")";
        LOG.log(Level.DEBUG, msg);
        throw new BackendRuntimeException(msg, jcoEx);
    }

    private static void throwBackendLogonException(final JCoException jcoEx)
            throws BackendLogonException {
        String msg = "Backend Logon Exception: " + jcoEx.getMessage()
                + " occurs. (Group: JCO_ERROR_LOGON_FAILURE)";
        LOG.log(Level.DEBUG, msg);
        throw new BackendLogonException(msg, jcoEx);
    }

    private static void throwCoreBaseRuntimeSystemFailureException(
            final JCoException jcoEx) throws CoreBaseRuntimeException {
        String msg = "Backend System Failure Exception: " + jcoEx.getMessage()
                + " occurs. (Group: JCO_ERROR_SYSTEM_FAILURE)";
        LOG.log(Level.DEBUG, msg);
        throw new CoreBaseRuntimeException(msg, jcoEx);
    }

    private static void throwBackendServerStartupException(
            final JCoException jcoEx) throws BackendServerStartupException {
        String msg = "Backend Server Startup Exception: " + jcoEx.getMessage()
                + " occurs. (Group: JCO_ERROR_SERVER_STARTUP)";
        LOG.log(Level.DEBUG, msg);
        throw new BackendServerStartupException(msg, jcoEx);
    }
}
