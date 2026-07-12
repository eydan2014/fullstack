package com.example.ms_cupones.repository;

import com.example.ms_cupones.model.Cupon;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@ActiveProfiles("test")
class CuponRepositoryTest {

    @Autowired
    private CuponRepository cuponRepository;

    @Test
    void guardarCupon_debeGuardarCorrectamente() {

        Cupon cupon = crearCupon();

        Cupon guardado = cuponRepository.save(cupon);

        assertNotNull(guardado.getId());
        assertEquals("CAFE10", guardado.getCodigo());
        assertEquals("PORCENTAJE", guardado.getTipoDescuento());
        assertTrue(guardado.getActivo());
    }

    @Test
    void buscarPorId_cuandoExiste_debeRetornarCupon() {

        Cupon cupon = crearCupon();

        Cupon guardado = cuponRepository.save(cupon);

        Optional<Cupon> resultado = cuponRepository.findById(guardado.getId());

        assertTrue(resultado.isPresent());
        assertEquals("CAFE10", resultado.get().getCodigo());
    }

    @Test
    void buscarPorCodigo_cuandoExiste_debeRetornarCupon() {

        Cupon cupon = crearCupon();

        cuponRepository.save(cupon);

        Optional<Cupon> resultado = cuponRepository.findByCodigo("CAFE10");

        assertTrue(resultado.isPresent());
        assertEquals("CAFE10", resultado.get().getCodigo());
    }

    @Test
    void listarCupones_debeRetornarTodosLosCupones() {

        Cupon cupon1 = crearCupon();

        Cupon cupon2 = crearCupon();
        cupon2.setCodigo("DESC2000");
        cupon2.setTipoDescuento("MONTO");
        cupon2.setValor(new BigDecimal("2000"));

        cuponRepository.save(cupon1);
        cuponRepository.save(cupon2);

        List<Cupon> cupones = cuponRepository.findAll();

        assertEquals(2, cupones.size());
    }

    @Test
    void eliminarCupon_debeEliminarCorrectamente() {

        Cupon cupon = crearCupon();

        Cupon guardado = cuponRepository.save(cupon);

        cuponRepository.delete(guardado);

        Optional<Cupon> resultado = cuponRepository.findById(guardado.getId());

        assertTrue(resultado.isEmpty());
    }

    private Cupon crearCupon() {

        Cupon cupon = new Cupon();
        cupon.setCodigo("CAFE10");
        cupon.setDescripcion("Descuento de 10%");
        cupon.setTipoDescuento("PORCENTAJE");
        cupon.setValor(new BigDecimal("10"));
        cupon.setMontoMinimo(new BigDecimal("3000"));
        cupon.setFechaInicio(LocalDate.of(2026, 1, 1));
        cupon.setFechaFin(LocalDate.of(2026, 12, 31));
        cupon.setActivo(true);
        cupon.setUsosMaximos(100);
        cupon.setUsosActuales(0);

        return cupon;
    }
}
