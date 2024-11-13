package file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jaxb.schema.STLSheet;

public class XmlFileLoader {

    private static final String XML_PACKAGE_NAME = "jaxb.schema";

//    public static STLSheet loadXmlFile(String filePath)
//    {
//        try {
//            XmlValidator.valideXmlFile(filePath);
//            InputStream inputStream = new FileInputStream(filePath);
//            STLSheet stlSheet = deserializeFrom(inputStream);
//            return stlSheet;
//
//        } catch (JAXBException e) {
//            throw new RuntimeException("Error processing the XML file: " + e.getMessage(), e);
//        } catch (FileNotFoundException e){
//            throw new RuntimeException(e);
//        }
//    }

    public static STLSheet loadXmlFile(InputStream fileInputStream)
    {
        try {
            STLSheet stlSheet = deserializeFrom(fileInputStream);
            return stlSheet;
        } catch (JAXBException e) {
            throw new RuntimeException("Error processing the XML file: " + e.getMessage(), e);
        }
    }


    private static STLSheet deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (STLSheet) u.unmarshal(in);
    }
}
