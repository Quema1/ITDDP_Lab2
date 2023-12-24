package Lab2_features;

import generated.Clients;
import generated.FitnessClub;
import jakarta.xml.bind.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class JAXBParser {
	public static FitnessClub loadPosts(final String xmlFileName,
									final String xsdFileName, Class<?> objectFactory) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(objectFactory);
		Unmarshaller unmarshaller = jc.createUnmarshaller();

		// obtain schema
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		if (xsdFileName != null) { // <-- setup validation on
			Schema schema = null;
			if ("".equals(xsdFileName)) {
				// setup validation against XSD pointed in XML
				schema = sf.newSchema();
			} else {
				// setup validation against external XSD
				schema = sf.newSchema(new File(xsdFileName));
			}

			unmarshaller.setSchema(schema); // <-- set XML schema for validation

			// set up handler
			unmarshaller.setEventHandler(new ValidationEventHandler() {
				// this method will be invoked if XML is NOT valid
				@Override
				public boolean handleEvent(ValidationEvent event) {
					System.err.println("====================================");
					System.err.println(xmlFileName + " is NOT valid against "
							+ xsdFileName + ":\n" + event.getMessage());
					System.err.println("====================================");
					return true;
				}
			});
		}

		// do unmarshal
		FitnessClub fitnessClub = (FitnessClub) unmarshaller.unmarshal(new File(xmlFileName));
		return fitnessClub; // <-- filled container
	}

	public static void savePosts(FitnessClub fitnessClub, final String xmlFileName,
								 final String xsdFileName, Class<?> objectFactory) throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance(objectFactory);
		Marshaller marshaller = jc.createMarshaller();

		// obtain schema
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// setup validation against XSD
		if (xsdFileName != null) {
			Schema schema = sf.newSchema(new File(xsdFileName));

			marshaller.setSchema(schema);
			marshaller.setEventHandler(new ValidationEventHandler() {
				@Override
				public boolean handleEvent(ValidationEvent event) {
					System.err.println("====================================");
					System.err.println(xmlFileName + " is NOT valid against "
							+ xsdFileName + ":\n" + event.getMessage());
					System.err.println("====================================");
					return false;
				}
			});
		}
		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, Constants.SCHEMA_LOCATION__URI);
		marshaller.marshal(fitnessClub, new File(xmlFileName));
	}
	
	public static void main(String[] args) throws Exception {
		String fileInput = args[0];
		String schemaFile = args[1];
		String fileOutput = args[2];
		System.out.println("--== JAXB Parser ==--");
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(schemaFile));
		Validator.validate(fileInput, schema);
		FitnessClub fitnessClub = loadPosts(fileInput, schemaFile, Constants.OBJECT_FACTORY);

		System.out.println("====================================");
		System.out.println("Here is the posts: \n" + fitnessClub);
		System.out.println("====================================");

		try {
			savePosts(fitnessClub, fileOutput, schemaFile, Constants.OBJECT_FACTORY);
			Validator.validate(fileOutput, schema);
		} catch (Exception ex) {
			System.err.println("====================================");
			System.err.println("Object tree not valid against XSD.");
			System.err.println(ex.getClass().getName());
			System.err.println("====================================");
		}
	}
}