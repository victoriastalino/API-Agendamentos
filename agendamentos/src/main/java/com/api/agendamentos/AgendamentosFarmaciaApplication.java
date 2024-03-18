package com.api.agendamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AgendamentosFarmaciaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendamentosFarmaciaApplication.class, args);
		System.out.println("Aplicação iniciada");
	}
}


