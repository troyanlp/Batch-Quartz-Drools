package com.everis.bootquartz.service;

import javax.annotation.PreDestroy;

import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.springframework.stereotype.Service;

import com.everis.bootquartz.model.ExamResult;

@Service
public class DroolsService {

	private KieSession ksession;
	private static StatelessKnowledgeSession session;

	public DroolsService() {
		// KieServices is the factory for all KIE services
		// KieServices ks = KieServices.Factory.get();
		//
		// // From the kie services, a container is created from the classpath
		// KieContainer kc = ks.getKieClasspathContainer();
		//
		// // From the container, a session is created based on
		// // its definition and configuration in the META-INF/kmodule.xml file
		// ksession = kc.newKieSession("point-rulesKS");

		KieServices ks = KieServices.Factory.get();
		KnowledgeBase knowledgeBase;
		try {
			knowledgeBase = createKnowledgeBaseFromSpreadsheet();
			session = knowledgeBase.newStatelessKnowledgeSession();
			// From the kie services, a container is created from the classpath
			KieContainer kc = ks.getKieClasspathContainer();
			ksession = kc.newKieSession("point-rulesKS");
			System.out.println("SET UP DEL DROOLS SERVICE");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void execute(ExamResult exam) {
		// ksession.insert(exam);
		// ksession.fireAllRules();
		session.execute(exam);
	}

	private static KnowledgeBase createKnowledgeBaseFromSpreadsheet() throws Exception {

		DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();

		dtconf.setInputType(DecisionTableInputType.XLS);

		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		knowledgeBuilder.add(ResourceFactory.newClassPathResource("rules/spreadsheets/rules.xls"), ResourceType.DTABLE,
				dtconf);

		if (knowledgeBuilder.hasErrors()) {
			throw new RuntimeException(knowledgeBuilder.getErrors().toString());
		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		return knowledgeBase;

	}

	@PreDestroy
	public void cleanUp() {
		// ksession.dispose();
		System.out.println("CLEAN UP DEL SERVICIO DE DROOLS");
	}

}
