package com.manuel.bootquartz.service;

import javax.annotation.PreDestroy;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import com.manuel.bootquartz.model.ExamResult;

@Service
public class DroolsService {

	private KieSession ksession;

	public DroolsService() {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		// From the container, a session is created based on
		// its definition and configuration in the META-INF/kmodule.xml file
		ksession = kc.newKieSession("point-rulesKS");
		System.out.println("SET UP DEL DROOLS SERVICE");
	}

	public void execute(ExamResult exam) {
		ksession.insert(exam);
		ksession.fireAllRules();
	}

	@PreDestroy
	public void cleanUp() {
		ksession.dispose();
		System.out.println("CLEAN UP DEL SERVICIO DE DROOLS");
	}

}
