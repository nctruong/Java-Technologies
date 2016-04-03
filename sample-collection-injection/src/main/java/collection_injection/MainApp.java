package collection_injection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		CollectionSample obj = (CollectionSample) context.getBean("collectionSample");

		obj.getAddressList();
		obj.getAddressSet();
		obj.getAddressMap();
		obj.getAddressProp();
	}
}
