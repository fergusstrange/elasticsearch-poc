package com.ferguss;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class SpringAppTests {

	@Autowired
    private HelloService helloService;

	@Test
	@Ignore
	public void test() throws Exception {
		helloService.createIndex();
		indexSomeTests(false);
		indexSomeTests(false);
		indexSomeTests(false);
		indexSomeTests(true);
		int searches = helloService.search();
		System.out.println(searches + " searches matched.");
		Assert.assertTrue(searches > 0);
	}

	private void indexSomeTests(final boolean isRun) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					helloService.indexSomeTests();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		if(isRun) {
			thread.run();
		} else {
			thread.start();
		}
	}

	@Test
	public void testSearch() {
		helloService.search();
	}

}
