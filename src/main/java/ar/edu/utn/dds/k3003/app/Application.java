package ar.edu.utn.dds.k3003.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Bean;

// Importaciones nuevas para MCP
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import ar.edu.utn.dds.k3003.tools.IncentivosTools;

@SpringBootApplication(scanBasePackages = "ar.edu.utn.dds.k3003")
@EnableJpaRepositories(basePackages = "ar.edu.utn.dds.k3003.repositories") 
@EntityScan(basePackages = "ar.edu.utn.dds.k3003.dominio") 
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  // Este Bean es la clave: le inyecta tus herramientas a Spring AI para que encienda el endpoint
  @Bean
  public ToolCallbackProvider incentivosToolCallbacks(IncentivosTools tools) {
      return MethodToolCallbackProvider.builder().toolObjects(tools).build();
  }
}