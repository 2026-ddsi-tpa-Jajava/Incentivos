package ar.edu.utn.dds.k3003.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.clients.FachadaDonacionesHttp;
import ar.edu.utn.dds.k3003.clients.FachadaDonadoresYEntidadesHttp;

@Configuration
public class IntegrationStubsConfig {

    @Value("${URL_DONADORES_ENTIDADES:https://agusb1101-donadores-entidades.onrender.com}")
    private String urlDonadoresYEntidades;

    @Value("${URL_DONACIONES:https://donaciones-5u8i.onrender.com}")
    private String urlDonaciones;

    @Bean
    public FachadaDonadoresYEntidades fachadaDonadoresYEntidades() {
        return new FachadaDonadoresYEntidadesHttp(urlDonadoresYEntidades);
    }

    @Bean
    public FachadaDonaciones fachadaDonaciones() {
        return new FachadaDonacionesHttp(urlDonaciones);
    }
}