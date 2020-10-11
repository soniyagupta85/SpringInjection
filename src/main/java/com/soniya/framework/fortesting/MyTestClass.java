package com.soniya.framework.fortesting;

import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;
import com.soniya.framework.fordependency.SpringInjection;
import com.soniya.framework.testclasses.toload.A1;
import com.soniya.framework.testclasses.toload.A2;
import com.soniya.framework.testclasses.toload.A3;
import com.soniya.framework.testclasses.toload.A4;

public class MyTestClass {
	
	static final Logger logger = Logger.getLogger(MyTestClass.class);
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		 

		// SpringInjection bean = SpringInjection.getInstance();
		//SpringInjection bean = SpringInjection.getInstance();
		SpringInjection.filePathApplicationContext("./src/main/resources/SpringConfig.xml");

		logger.info("Bean 1....");
		A1 bean1 = (A1) SpringInjection.getBeanById("a1");
		logger.info(bean1.getFirstname());
		logger.info(bean1.getNickname());


		logger.info("\n\n\nBean 2....");
		A2 bean2 = (A2) SpringInjection.getBeanById("a2");
		logger.info(bean2.getName());
		logger.info(bean2.getA1obj().getFirstname());
		logger.info(bean2.getA1obj().getNickname());
		logger.info(bean2.getA3obj().getFirstname());
		logger.info(bean2.getA3obj().getHeight());
		

		logger.info("\n\n\nBean 3....");
		A3 bean3 = (A3) SpringInjection.getBeanById("a3");
		logger.info(bean3.getFirstname());
		logger.info(bean3.getHeight());
		logger.info("singleton a3 loaded first time and hashcode->"+bean3.hashCode());
		
		logger.info("\n\n\nBean 3....");
		A3 bean4 = (A3) SpringInjection.getBeanById("a3");
		logger.info(bean4.getFirstname());
		logger.info(bean4.getHeight());
		logger.info("singleton a3 loaded second time and hashcode->"+bean4.hashCode());
		
		
		logger.info("Bean 4....");
		A4 bean5 = (A4) SpringInjection.getBeanById("a4");
		logger.info(bean5.getFirstname());
		logger.info(bean5.getNickname());
		logger.info("prototype a4 loaded first time and hashcode->"+bean5.hashCode());
		
		logger.info("Bean 4....");
		A4 bean6 = (A4) SpringInjection.getBeanById("a4");
		logger.info(bean6.getFirstname());
		logger.info(bean6.getNickname());
		logger.info("prototype a4 loaded second time and hashcode->"+bean6.hashCode());
		
	}
}
