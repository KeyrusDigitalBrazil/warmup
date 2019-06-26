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

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;

/**
 * Class defines the key for the distribution channel mapping. <br>
 *
 * @version 1.0
 */
public class DivisionMappingKeyImpl implements DivisionMappingKey {
   
    protected String salesOrg;
    protected String division;
    
    @Override
    public String getSalesOrg() {
        return salesOrg;
    }
    
    @Override
    public void setSalesOrg(String salesOrg) {
        this.salesOrg = salesOrg;
    }
    
    @Override
    public String getDivision() {
        return division;
    }
    
    @Override
    public void setDivision(String division) {
        this.division = division;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((division == null) ? 0 : division.hashCode());
        result = prime * result + ((salesOrg == null) ? 0 : salesOrg.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
    	boolean value=true;
        if (this == obj){
        	value=true;
        }
        if (obj == null){
        	value=false;
        }
        if (obj != null && getClass() != obj.getClass()){
        	value=false;
        	}
        DivisionMappingKeyImpl other = (DivisionMappingKeyImpl) obj;
        if (division == null) {
            if (other !=null && other.division != null){
            	value=false;
            }
        }
        else if (other !=null && !division.equals(other.division)){
        	value=false;
        }
        if (salesOrg == null) {
            if (other !=null && other.salesOrg != null){
            	value=false;
            }
        }
        else if (other !=null && !salesOrg.equals(other.salesOrg)){
        	value=false;
        }
        return value;
    }
    @Override
    public String toString() {
        return "DivisionMappingKeyImpl [division=" + division + ", salesOrg=" + salesOrg + "]";
    }
    
}
