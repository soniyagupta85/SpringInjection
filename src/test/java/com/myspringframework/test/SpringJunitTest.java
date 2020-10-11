package com.myspringframework.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.soniya.framework.fordependency.SpringInjection;
import com.soniya.framework.testclasses.toload.A3;

public class SpringJunitTest {
	SpringInjection bean = SpringInjection.getInstance();
	public static final String correctSpringConfigFilePath="./src/main/resources/SpringConfig.xml";
	public static final String wrongSpringConfigFilePath="./src/main/resources/SpringConfiggggg.xml";
	public static final String CorrectBean1="A1";
	public static final String wrongNameOfBean="A1111";
	
	@Before
	public void setUp() throws Exception {
		//assertNotNull(bean);
		SpringInjection.filePathApplicationContext(correctSpringConfigFilePath);
		System.out.println("XML loaded");
	}

	
	  @Test public void checkforCorrectFilePath() {
	  SpringInjection.filePathApplicationContext(correctSpringConfigFilePath);
	  System.out.println("checkforCorrectFilePath executed");
	  }
	 
	@Test(expected = NullPointerException.class)
	  public void wrongFilePath() throws Exception {
		SpringInjection.filePathApplicationContext(wrongSpringConfigFilePath);
		System.out.println("wrongFilePath executed");
	}
	
	  @Test(expected = com.soniya.framework.fordependency.SpringException.class)
	  public void wrongBeanId() throws Exception { A3 bean3 = (A3)
	SpringInjection.getBeanById(wrongNameOfBean);
	  System.out.println("Wrong Bean Id->"+"wrongNameOfBean");
	  System.out.println("wrongBeanId executed");
	  }
	
}
