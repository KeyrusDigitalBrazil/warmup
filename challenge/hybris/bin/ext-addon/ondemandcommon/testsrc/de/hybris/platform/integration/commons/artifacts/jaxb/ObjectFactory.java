
package de.hybris.platform.integration.commons.artifacts.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.hybris.platform.integration.commons.artifacts.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Classpath_QNAME = new QName("", "classpath");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.hybris.platform.integration.commons.artifacts.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ClasspathType }
     * 
     */
    public ClasspathType createClasspathType() {
        return new ClasspathType();
    }

    /**
     * Create an instance of {@link ClasspathentryType }
     * 
     */
    public ClasspathentryType createClasspathentryType() {
        return new ClasspathentryType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClasspathType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "classpath")
    public JAXBElement<ClasspathType> createClasspath(ClasspathType value) {
        return new JAXBElement<ClasspathType>(_Classpath_QNAME, ClasspathType.class, null, value);
    }

}
