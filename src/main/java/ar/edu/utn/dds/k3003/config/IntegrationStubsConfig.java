package ar.edu.utn.dds.k3003.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.clients.FachadaDonacionesHttp;
import ar.edu.utn.dds.k3003.clients.FachadaDonadoresYEntidadesHttp;

@Configuration
public class IntegrationStubsConfig {

    @Bean
    public FachadaDonadoresYEntidades fachadaDonadoresYEntidades() {
        // Puerto típico o URL de Render de tu compañero de Donadores y Entidades
        return new FachadaDonadoresYEntidadesHttp("https://agusb1101-donadores-entidades.onrender.com/");
    }

    @Bean
    public FachadaDonaciones fachadaDonaciones() {
        // Puerto típico o URL de Render de tu compañero de Donaciones
        return new FachadaDonacionesHttp("https://donaciones-5u8i.onrender.com/");
    }
}