package org.jtester.dbtest.service;

import org.jtester.fortest.hibernate.AddressService;
import org.jtester.fortest.hibernate.UserService;
import org.jtester.testng.JTester;
import org.jtester.unitils.jmock.Mock;
import org.testng.annotations.Test;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

@SpringApplicationContext( { "classpath:/org/jtester/fortest/hibernate/project.xml" })
@Test(groups = { "JTester" })
public class UserServiceTest_Mock1 extends JTester {
	@SpringBean("userService")
	// @TestedObject
	private UserService userService;

	@Mock(injectInto = "userService")
	// @InjectIntoByType
	private AddressService addressService;

	@Test
	public void findAddress() {
		want.object(addressService).notNull();
		want.object(userService).notNull();
		checking(new Je() {
			{
				will.call.one(addressService).findAddress();
				will.returns.value("文二路120#");
			}
		});
		String address = userService.findAddress();
		want.string(address).contains("120#");
	}

	@Test
	public void findAddress02() {
		want.object(addressService).notNull();
		want.object(userService).notNull();
		checking(new Je() {
			{
				will.call.one(addressService).findAddress();
				will.returns.value("文二路120#");
			}
		});
		String address = userService.findAddress();
		want.string(address).contains("120#");
	}
}
