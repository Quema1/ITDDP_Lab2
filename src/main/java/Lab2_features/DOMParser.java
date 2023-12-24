package Lab2_features;

import generated.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DOMParser {
	private static boolean logEnabled = true;

	private static void log(Object o) {
		if (logEnabled) {
			System.out.println(o);
		}
	}

	private Client parseClient(Node node) throws ParseException, DatatypeConfigurationException {
		Client client = new Client();
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			Node id = attrs.getNamedItem(Constants.ATTR_ID);
			client.setId(Integer.parseInt(id.getTextContent()));
		}

		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			if (Constants.TAG_CLIENT_NAME.equals(item.getLocalName())) {
				client.setName(item.getTextContent());
			} else if (Constants.TAG_CLIENT_SURNAME.equals(item.getLocalName())) {
				client.setSurname(item.getTextContent());
			} else if (Constants.TAG_CLIENT_DATE_OF_BIRTH.equals(item.getLocalName())) {
				client.setDateOfBirth(parseDate(item));
			} else if (Constants.TAG_CLIENT_PHOTO.equals(item.getLocalName())) {
				client.setPhoto(item.getTextContent());
			} else if (Constants.TAG_SUBSCRIPTION.equals(item.getLocalName())) {
				client.setCurrentSubscription(parseSubscription(item));
			}
		}
		return client;
	}
	private static Coach parseCoach(Node item) {
		Coach coach = new Coach();
		NodeList nodes = item.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node oItem = nodes.item(i);
			if (Constants.TAG_COACH_NAME.equals(oItem.getLocalName())) {
				if (oItem.getTextContent() != null)
					coach.setName(oItem.getTextContent());
			} else if (Constants.TAG_COACH_SURNAME.equals(oItem.getLocalName())) {
				if (oItem.getTextContent() != null)
					coach.setSurname(oItem.getTextContent());
			}else if (Constants.TAG_COACH_EXPERIENCE.equals(oItem.getLocalName())) {
				if (oItem.getTextContent() != null)
					coach.setExperience(new BigDecimal(oItem.getTextContent()));
			}
		}
		return coach;
	}

	private static Subscription parseSubscription(Node item) throws DatatypeConfigurationException, ParseException {
		Subscription subscrption = new Subscription();
		NodeList nodes = item.getChildNodes();
		if (item.hasAttributes()) {
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			Node oItem = nodes.item(i);
			if (Constants.TAG_SUBSCRIPTION_PRICE.equals(oItem.getLocalName())) {
				subscrption.setPrice(new BigDecimal(oItem.getTextContent()));
			} else if (Constants.TAG_SUBSCRIPTION_START.equals(oItem.getLocalName())) {
				subscrption.setStartDate(parseDate(oItem));
			} else if (Constants.TAG_SUBSCRIPTION_END.equals(oItem.getLocalName())) {
				subscrption.setEndDate(parseDate(oItem));
			} else if (Constants.TAG_COACH.equals(oItem.getLocalName())) {
			subscrption.setCoach(parseCoach(oItem));
		}
		}
		return subscrption;
	}

	private static XMLGregorianCalendar parseDate(Node item) throws ParseException, DatatypeConfigurationException {
		String dateFormatted = item.getTextContent();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date date = format.parse(dateFormatted);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar xmlGregCal =  DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		return xmlGregCal;
	}

	public Clients parse(InputStream in, Schema schema)
			throws ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException, ParseException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

		// set the validation against schema
		dbf.setSchema(schema);

		DocumentBuilder db = dbf.newDocumentBuilder();

		// setup validation error handler
		// the preferred way is the throwing an exception
		db.setErrorHandler(new DefaultHandler() {
			@Override
			public void error(SAXParseException e) throws SAXException {
				System.err.println(e.getMessage()); // log error
//				throw e;
			}
		});

		// get the top of the xml tree
		Document root = db.parse(in);

		List<Client> clients = new ArrayList<>();

		// get the root element of the xml document
		NodeList clientsList = root.getElementsByTagNameNS("https://it.nure.ua/sport", "client");
		for (int i = 0; i < clientsList.getLength(); i++) {
			clients.add(parseClient(clientsList.item(i)));
		}

		var res = new Clients();
		res.getClient().addAll(clients);
		return res;
	}

	public static void setCoach(Coach coach, Element el, Document doc) {
		if (coach == null)
			return;

		Element coachName= doc.createElement(Constants.TAG_COACH_NAME);
		coachName.setTextContent(coach.getName());
		el.appendChild(coachName);

		Element coachSurname= doc.createElement(Constants.TAG_COACH_SURNAME);
		coachSurname.setTextContent(coach.getSurname());
		el.appendChild(coachSurname);

		Element coachExperience= doc.createElement(Constants.TAG_COACH_EXPERIENCE);
		coachExperience.setTextContent((coach.getExperience()).toString());
		el.appendChild(coachExperience);

	}

	public static void setSubscription(Subscription subscription, Element el, Document doc) {
		if (subscription == null)
			return;
		Element price = doc.createElement(Constants.TAG_SUBSCRIPTION_PRICE);
		price.setTextContent(subscription.getPrice().toString());
		el.appendChild(price);

		Element startDate = doc.createElement(Constants.TAG_SUBSCRIPTION_START);
		startDate.setTextContent(subscription.getStartDate().toString());
		el.appendChild(startDate);

		Element endDate = doc.createElement(Constants.TAG_SUBSCRIPTION_END);
		endDate.setTextContent(subscription.getStartDate().toString());
		el.appendChild(endDate);
	}

	public void marshal(Clients clientsObj, Schema schema, OutputStream out) throws ParserConfigurationException, TransformerException, IOException {
		// Create a new Document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setSchema(schema);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		var clients = clientsObj.getClient();

		// Create the root element
		Element root = doc.createElementNS(Constants.CLIENTS_NAMESPACE_URI, Constants.TAG_FITNESS_CLUB);
		doc.appendChild(root);
		Element root2 = doc.createElement(Constants.TAG_CLIENTS);
		root.appendChild(root2);

		// Loop through the list of Order objects and create XML elements for each
		for (Client client : clients) {
			Element clientElement = doc.createElement(Constants.TAG_CLIENT);
			// Create elements for order details
			Element client_name = doc.createElement(Constants.TAG_CLIENT_NAME);
			client_name.setTextContent(client.getName());
			Element client_surname = doc.createElement(Constants.TAG_CLIENT_SURNAME);
			client_surname.setTextContent(client.getSurname());
			Element photo = doc.createElement(Constants.TAG_CLIENT_PHOTO);
			photo.setTextContent(String.valueOf(client.getPhoto()));
			Element dateOfBirth = doc.createElement(Constants.TAG_CLIENT_DATE_OF_BIRTH);
			dateOfBirth.setTextContent(client.getDateOfBirth().toXMLFormat());

			Element coach = doc.createElement(Constants.TAG_COACH);
			setCoach(client.getCurrentSubscription().getCoach(), coach, doc);

			Element subscription = doc.createElement(Constants.TAG_SUBSCRIPTION);
			setSubscription(client.getCurrentSubscription(), subscription, doc);

			subscription.appendChild(coach);

			clientElement.appendChild(client_name);
			clientElement.appendChild(client_surname);
			clientElement.appendChild(photo);
			clientElement.appendChild(dateOfBirth);

			clientElement.appendChild(subscription);
			//clientElement.appendChild(coach);

			root2.appendChild(clientElement);
		}

		// Create a Transformer to write the DOM to the output stream
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// Marshal the DOM to the output stream
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result);
	}

	public static Clients demarshall(InputStream in, Schema schema, DOMParser domParser) throws ParserConfigurationException, SAXException, IOException, DatatypeConfigurationException, ParseException {
		// Create against validation schema
		Clients clients = domParser.parse(in, schema);
		return clients;
	}

	public static void main(String[] args) throws Exception {
		DOMParser domParser = new DOMParser();
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		String fileInput = args[0];
		String schemaFile = args[1];
		String fileOutput = args[2];
		Schema schema = sf.newSchema(new File(schemaFile));
		System.out.println("< DOM PARSER >");

		InputStream in = new FileInputStream(fileInput);
		Validator.validate(fileInput, schema);
		OutputStream out = new FileOutputStream(fileOutput);
		System.out.println("-------------------------------");
		System.out.println("Clients: \n");
		var clients = demarshall(in, schema, domParser);
		for (Client client : clients.getClient()) {
			System.out.println(client);
		}
		System.out.println("---------------------------------");
		System.out.println("Marshalling to " + fileOutput);
		domParser.marshal(clients, schema, out);
		Validator.validate(fileOutput, schema);
	}
}
