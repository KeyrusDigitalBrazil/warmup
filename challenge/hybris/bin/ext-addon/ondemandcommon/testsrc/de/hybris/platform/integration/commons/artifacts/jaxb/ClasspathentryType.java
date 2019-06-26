
package de.hybris.platform.integration.commons.artifacts.jaxb;

import org.apache.commons.lang.builder.EqualsBuilder;

import javax.xml.bind.annotation.*;

import static org.apache.commons.lang.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang.builder.ToStringStyle.MULTI_LINE_STYLE;


/**
 * <p>Java class for classpathentryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="classpathentryType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="kind" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exported" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="combineaccessrules" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classpathentryType", propOrder = {
    "value"
})
public class ClasspathentryType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "kind")
    protected String kind;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAttribute(name = "exported")
    protected String exported;
    @XmlAttribute(name = "combineaccessrules")
    protected String combineaccessrules;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the exported property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExported() {
        return exported;
    }

    /**
     * Sets the value of the exported property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExported(String value) {
        this.exported = value;
    }

    /**
     * Gets the value of the combineaccessrules property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCombineaccessrules() {
        return combineaccessrules;
    }

    /**
     * Sets the value of the combineaccessrules property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCombineaccessrules(String value) {
        this.combineaccessrules = value;
    }


   @Override
   public String toString() {
      return reflectionToString(this, MULTI_LINE_STYLE);
   }

   @Override
   public int hashCode() {
      return reflectionHashCode(this);
   }

   @Override
   public boolean equals(Object that) {
      return EqualsBuilder.reflectionEquals(this, that);
   }
}
