package Lab2_features;

import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.InputStream;

public class Validator {
    public static void validate(Source source, Schema schema) throws Exception {
        javax.xml.validation.Validator validator = schema.newValidator();
        try {
            validator.validate(source);
            System.out.println("xml is valid.");
        } catch (Exception ex) {
            System.out.println("xml is not valid because ");
            System.out.println(ex.getMessage());
            throw new Exception("Invalid XML");
        }
    }

    public static void validate(String filename, Schema schema) throws Exception {
        javax.xml.validation.Validator validator = schema.newValidator();
        Source source = new StreamSource(filename);
        try {
            validator.validate(source);
            System.out.println("xml is valid.");
        } catch (Exception ex) {
            System.out.println("xml is not valid because ");
            System.out.println(ex.getMessage());
            throw new Exception("Invalid XML");
        }
    }
}