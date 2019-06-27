
package de.hybris.platform.integration.commons.artifacts.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for classpathType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="classpathType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classpathentry" type="{}classpathentryType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classpathType", propOrder = {
    "classpathentry"
})
public class ClasspathType {

    protected List<ClasspathentryType> classpathentry;

    /**
     * Gets the value of the classpathentry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classpathentry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClasspathentry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClasspathentryType }
     * 
     * 
     */
    public List<ClasspathentryType> getClasspathentry() {
        if (classpathentry == null) {
            classpathentry = new ArrayList<ClasspathentryType>();
        }
        return this.classpathentry;
    }

}
