package com.soniya.framework.fordependency;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SpringInjection {

	private static final SpringInjection sInjection = new SpringInjection();
	private static Map<String, Object[]> beanXMLDetails;
	private static Map<String, Object> beanClassesDetails = new HashMap<String, Object>();

	public static void filePathApplicationContext(String filePath) {
		Document document = null;
		try {
			document = loadXMLFilePath(filePath);
		} catch (ParserConfigurationException e) {
			// e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		NodeList nList = document.getElementsByTagName("bean");
		beanXMLDetails = allBeanXMLDetails(nList);
	}

	public static Document loadXMLFilePath(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(filePath));
		return document;
	}

	private static Map<String, Object[]> allBeanXMLDetails(NodeList nList) {
		Map<String, Object[]> beanMap = new HashMap<String, Object[]>();

		for (int temp = 0; temp < nList.getLength(); temp++) { // this loop is for <bean>

			Map<String, Map<String, String>> attributeMap = null;
			List<Map<String, String>> propertiesListOfMap = new ArrayList<Map<String, String>>();

			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				attributeMap = extractAttributes(node);
				if (node.hasChildNodes()) {
					NodeList childNoded = node.getChildNodes();
					for (int childIndex = 0; childIndex < childNoded.getLength(); childIndex++) { // this loop is for
																									// <property>
						Node child = childNoded.item(childIndex);
						for (Map<String, String> childAttributes : extractAttributes(child).values()) {
							if (childAttributes.size() > 0) {
								propertiesListOfMap.add(childAttributes);
							}
						}
					}
				}
			}
			String key = null;
			for (String set : attributeMap.keySet()) {
				key = set;
			}
			validateAttributes(key, attributeMap.get(key), propertiesListOfMap);
			beanMap.put(key, new Object[] { attributeMap.get(key), propertiesListOfMap });
		}
		return beanMap;
	}

	private static void validateAttributes(String key, Map<String, String> attrMap,
			List<Map<String, String>> propertiesMap) {
		if (attrMap.get("id") == null) {
			throw new SpringException("'id' attribute is missing");
		}
		if (attrMap.get("class") == null) {
			throw new SpringException("'class' attribute is missing for bean id '" + key + "'");
		}

		for (Map<String, String> propMap : propertiesMap) {
			if (propMap.get("name") == null) {
				throw new SpringException("'name' attribute is missing for bean id '" + key + "'");
			}
			if (propMap.get("value") == null && propMap.get("ref") == null) {
				throw new SpringException("'name' attribute is missing for bean id '" + key + "'");
			}
		}
	}

	private static Map<String, Map<String, String>> extractAttributes(Node node) {
		Map<String, Map<String, String>> nodeDetailsMap = new HashMap<String, Map<String, String>>();
		String id = "";
		Map<String, String> attributeMap = new HashMap<String, String>();
		if (node.hasAttributes()) {
			NamedNodeMap nodeMap = node.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				Node tempNode = nodeMap.item(i);
				if (tempNode.getNodeName().equalsIgnoreCase("id")) {
					id = tempNode.getNodeValue();
				}
				attributeMap.put(tempNode.getNodeName().toLowerCase(), tempNode.getNodeValue());
			}
		}
		nodeDetailsMap.put(id, attributeMap);
		return nodeDetailsMap;
	}

	public static <T> Object getBeanById(String beanId) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

		Object[] beanInfo = beanXMLDetails.get(beanId);
		if (beanInfo == null) {
			throw new SpringException("'" + beanId + "' Bean Not Found Exception");
		}

		Map<String, String> beanAttributes = (Map<String, String>) beanInfo[0];
		List<Map<String, String>> beanProperties = (List<Map<String, String>>) beanInfo[1];

		String scope = beanAttributes.get("scope");

		Object beanClass = beanClassesDetails.get(beanId);
		if (beanClass != null) {
			return beanClass;
		}

		Object classObj = createClass(beanAttributes, beanProperties);
		if ("singleton".equals(scope)) {
			beanClassesDetails.put(beanId, classObj);
		}
		return classObj;
	}

	private static <T> Object createClass(Map<String, String> beanAttributes, List<Map<String, String>> beanProperties)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		Class classToCreate = Class.forName(beanAttributes.get("class"));

		Map<String, Object[]> classPropertiesMap = new HashMap<String, Object[]>();
		for (Map<String, String> map : beanProperties) {
			String fieldName = map.get("name");
			Object val = map.get("ref") == null ? map.get("value") : getBeanById(map.get("ref"));
			Object[] valueAndClassType = new Object[] { val, map.get("class") };
			classPropertiesMap.put(fieldName, valueAndClassType);
		}

		// @SuppressWarnings("unchecked")
		T t = (T) setPropertiesToClass(classToCreate, classPropertiesMap);
		return t;
	}

	private static <T> T setPropertiesToClass(Class<T> inputClassName, Map<String, Object[]> beanInfoMap)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		T t = (T) inputClassName.newInstance();

		for (Entry<String, Object[]> entry : beanInfoMap.entrySet()) {
			String fieldName = entry.getKey();

			Object[] obj = entry.getValue();
			Object value = obj[0];
			String classType = (String) obj[1];
			classType = classType != null ? "class " + classType : classType;

			if (Boolean.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Boolean.valueOf(value.toString()));
			} else if (Byte.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Byte.valueOf(value.toString()));
			} else if (Character.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, value.toString().charAt(0));
			} else if (Short.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Short.valueOf(value.toString()));
			} else if (Integer.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Integer.valueOf(value.toString()));
			} else if (Long.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Long.valueOf(value.toString()));
			} else if (Float.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Float.valueOf(value.toString()));
			} else if (Double.class.toString().equals(classType)) {
				PropertyUtils.setProperty(t, fieldName, Double.valueOf(value.toString()));
			} else {
				PropertyUtils.setProperty(t, fieldName, value);
			}
		}
		return t;
	}

	private SpringInjection() {
		// do nothing here
	}

	public static SpringInjection getInstance() {
		return sInjection;
	}
}
