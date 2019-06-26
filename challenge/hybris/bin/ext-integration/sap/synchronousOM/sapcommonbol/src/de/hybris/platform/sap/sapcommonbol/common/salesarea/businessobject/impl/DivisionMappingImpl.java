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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.impl;

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;

/**
 * Class contains the mapping information for the sales organisation and division <br>
 *
 * @version 1.0
 */
public class DivisionMappingImpl implements DivisionMapping {
  
   
    protected String divisionForCustomers;
    protected String divisionForConditions;
    protected String divisionForDocumentTypes;
    
    
    @Override
    public String getDivisionForCustomers() {
        return divisionForCustomers;
    }
    
    @Override
    public void setDivisionForCustomers(String divisionForCustomers) {
        this.divisionForCustomers = divisionForCustomers;
    }
    
    @Override
    public String getDivisionForConditions() {
        return divisionForConditions;
    }
    
    @Override
    public void setDivisionForConditions(String divisionForConditions) {
        this.divisionForConditions = divisionForConditions;
    }
    
    @Override
    public String getDivisionForDocumentTypes() {
        return divisionForDocumentTypes;
    }
    
    @Override
    public void setDivisionForDocumentTypes(String divisionForDocumentTypes) {
        this.divisionForDocumentTypes = divisionForDocumentTypes;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((divisionForConditions == null) ? 0 : divisionForConditions.hashCode());
        result = prime * result
                 + ((divisionForCustomers == null) ? 0 : divisionForCustomers.hashCode());
        result = prime * result
                 + ((divisionForDocumentTypes == null) ? 0 : divisionForDocumentTypes.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
    	boolean value=true;
        if (this == obj){
        	return true;
        }
        if (obj == null){
        	return false;
        }
        if (getClass() != obj.getClass()){
        	return false;
        }
        value = divisionChecking(obj);
        return value;
    }
	private boolean divisionChecking(Object obj) {
		boolean value=true; 
		DivisionMappingImpl other = (DivisionMappingImpl) obj;
        if (divisionForConditions == null) {
            if (other.divisionForConditions != null){
            	value=false;
            }
        }
        else if (!divisionForConditions.equals(other.divisionForConditions)){
        	value=false;
        }
        if (divisionForCustomers == null) {
            if (other.divisionForCustomers != null){
            	value=false;
            }
        }
        else if (!divisionForCustomers.equals(other.divisionForCustomers)){
        	value=false;
        }
        if (divisionForDocumentTypes == null) {
            if (other.divisionForDocumentTypes != null){
            	value=false;
            }
        }
        else if (!divisionForDocumentTypes.equals(other.divisionForDocumentTypes)){
        	value=false;
        }
		return value;
	}
    
    @Override
    public String toString() {
        return "DivisionMappingImpl [divisionForConditions=" + divisionForConditions
               + ", divisionForCustomers=" + divisionForCustomers + ", divisionForDocumentTypes="
               + divisionForDocumentTypes + "]";
    }
}
