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
package de.hybris.platform.sap.core.jco.rec.version100;

import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.version100.jaxb.FieldType;

import com.sap.conn.jco.JCoMetaData;

/**
 * Constants and methods used for parsing and saving of the JCoRecorder
 * repository files.
 */
public class Utils100 {

    /**
     * Key-word used for {@link RepositoryPlayback#getRecord(String)} if an
     * import-parameter is requested.
     */
    public static final String PARAMETERLIST_IMPORT_NAME = "INPUT";
    /**
     * Key-word used for {@link RepositoryPlayback#getRecord(String)} if an
     * export-parameter is requested.
     */
    public static final String PARAMETERLIST_EXPORT_NAME = "OUPUT";
    /**
     * Key-word used for {@link RepositoryPlayback#getRecord(String)} if an
     * changing-parameter is requested.
     */
    public static final String PARAMETERLIST_CHANGING_NAME = "CHANGING";
    /**
     * Key-word used for {@link RepositoryPlayback#getRecord(String)} if an
     * table-parameter is requested.
     */
    public static final String PARAMETERLIST_TABLES_NAME = "TABLES";

    /**
     * This byte length is used by the recorder as a default value for BCD
     * elements.
     */
    public static final int STANDARD_LENGTH_BCD = 16;
    /**
     * This byte length is used by the recorder as a default value for elements
     * representing character strings.
     */
    public static final int STANDARD_LENGTH_SYMBOLCHAIN = 16;

    private Utils100() {

    }

    /**
     * Converts a {@link FieldType} value to an integer value used by JCo.
     * 
     * @param type
     *            the {@link FieldType} value that shall be converted.
     * @return Returns the corresponding integer value.
     */
    public static int fieldTypeToInt(final FieldType type) {
        int integerValue=0;
        integerValue=numToInt(type);
        if(integerValue==0)
        {
            switch (type)
            {
                case BCD:
                    integerValue = JCoMetaData.TYPE_BCD; // 2
                    break;
                case BYTE:
                    integerValue = JCoMetaData.TYPE_BYTE; // 4
                    break;
                case CHAR:
                    integerValue = JCoMetaData.TYPE_CHAR; // 0
                    break;
                case DATE:
                    integerValue = JCoMetaData.TYPE_DATE; // 1
                    break;
                case STRING:
                    integerValue = JCoMetaData.TYPE_STRING; // 29
                    break;
                case TIME:
                    integerValue = JCoMetaData.TYPE_TIME; // 3
                    break;
                case XSTRING:
                    integerValue = JCoMetaData.TYPE_XSTRING; // 30
                    break;
            // STRUCTURE 17
            // TABLE 99
                default:
                    throw new UnsupportedOperationException("The Fieldtype value "
                            + type + " does not exist!");
            }
        }
        return integerValue;
    }

    private static int numToInt(FieldType type) {
        int integerValue=0;
        switch(type)
        {
            case DECF_16:
                integerValue = JCoMetaData.TYPE_DECF16; // 23
                break;
            case DECF_34:
                integerValue = JCoMetaData.TYPE_DECF34; // 24
                break;
            case FLOAT:
                integerValue = JCoMetaData.TYPE_FLOAT; // 7
                break;
            case INT:
                integerValue = JCoMetaData.TYPE_INT; // 8
                break;
            case INT_1:
                integerValue = JCoMetaData.TYPE_INT1; // 10
                break;
            case INT_2:
                integerValue = JCoMetaData.TYPE_INT2; // 9
                break;
            case NUM:
                integerValue = JCoMetaData.TYPE_NUM; // 6
                break;
            default:
                break;
        }
        return integerValue;
    }

    /**
     * Converts a integer type value (used by JCo) to the corresponding
     * {@link FieldType} value.
     * 
     * @param type
     *            the integer value that shall be converted.
     * @return Returns the corresponding {@link FieldType} value.
     */
    public static FieldType fieldTypeFromInt(final int type) {
        FieldType fType = null;
        fType=getFieldTypeForNum(type);
        if(fType==null)
        {
            switch (type)
            {
                case JCoMetaData.TYPE_BCD:
                    fType = FieldType.BCD; // 2
                    break;
                case JCoMetaData.TYPE_BYTE:
                    fType = FieldType.BYTE; // 4
                    break;
                case JCoMetaData.TYPE_CHAR:
                    fType = FieldType.CHAR; // 0
                    break;
                case JCoMetaData.TYPE_DATE:
                    fType = FieldType.DATE; // 1
                    break;
                case JCoMetaData.TYPE_STRING:
                    fType = FieldType.STRING; // 29
                    break;
                case JCoMetaData.TYPE_TIME:
                    fType = FieldType.TIME; // 3
                    break;
                case JCoMetaData.TYPE_XSTRING:
                    fType = FieldType.XSTRING; // 30
                    break;
                default:
                    throw new UnsupportedOperationException("The Fieldtype value "
                            + type + " does not exist!");
            }
        }
        return fType;
    }
    private static FieldType getFieldTypeForNum(int type) {
        FieldType fType=null;
        switch (type)
        {
            case JCoMetaData.TYPE_DECF16:
                fType = FieldType.DECF_16; // 23
                break;
            case JCoMetaData.TYPE_DECF34:
                fType = FieldType.DECF_34; // 24
                break;
            case JCoMetaData.TYPE_FLOAT:
                fType = FieldType.FLOAT; // 7
                break;
            case JCoMetaData.TYPE_INT:
                fType = FieldType.INT; // 8
                break;
            case JCoMetaData.TYPE_INT1:
                fType = FieldType.INT_1; // 10
                break;
            case JCoMetaData.TYPE_INT2:
                fType = FieldType.INT_2; // 9
                break;
            case JCoMetaData.TYPE_NUM:
                fType = FieldType.NUM; // 6
                break;
            default:
                break;
        }
        return fType;
    }
}

    
