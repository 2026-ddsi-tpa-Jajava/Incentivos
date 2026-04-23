package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.incentivos.CategoriaDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationStubsConfig {

    @Bean
    public FachadaDonadoresYEntidades fachadaDonadoresYEntidadesStub() {
        InvocationHandler handler = (Object proxy, Method method, Object[] args) -> switch (method.getName()) {
            case "buscarDonadorPorID" -> stubDonador((String) args[0]);
            case "setFachadaIncentivos" -> null;
            default -> unsupported(method);
        };
        return (FachadaDonadoresYEntidades) Proxy.newProxyInstance(
            FachadaDonadoresYEntidades.class.getClassLoader(),
            new Class<?>[] {FachadaDonadoresYEntidades.class},
            handler);
    }

    @Bean
    public FachadaDonaciones fachadaDonacionesStub() {
        InvocationHandler handler = (Object proxy, Method method, Object[] args) -> switch (method.getName()) {
            case "buscarPorDonadorYFechaInicio" -> List.of();
            case "buscarProductoPorID" -> stubProducto((String) args[0]);
            case "setFachadaDonadoresYEntidades", "setFachadaLogistica" -> null;
            default -> unsupported(method);
        };
        return (FachadaDonaciones) Proxy.newProxyInstance(
            FachadaDonaciones.class.getClassLoader(),
            new Class<?>[] {FachadaDonaciones.class},
            handler);
    }

    private static DonadorDTO stubDonador(String donadorID) {
        return new DonadorDTO(
            donadorID,
            "Nombre",
            "Apellido",
            0,
            "mail@example.com",
            "000",
            "Domicilio",
            EstadoDonadorEnum.VERIFICADO,
            CategoriaDonadorEnum.OCASIONAL.name());
    }

    private static ProductoDTO stubProducto(String productoID) {
        return new ProductoDTO(productoID, "Producto", "Descripcion", "categoria-stub", null);
    }

    private static Object unsupported(Method method) {
        throw new UnsupportedOperationException("Stub no implementado para " + method.getName());
    }
}
