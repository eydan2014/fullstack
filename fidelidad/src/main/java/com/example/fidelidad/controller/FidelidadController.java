package com.example.fidelidad.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fidelidad.dto.FidelidadRequestDTO;
import com.example.fidelidad.exception.ApiResponse;
import com.example.fidelidad.model.Fidelidad;
import com.example.fidelidad.service.FidelidadService;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Fidelidad", description = "Operaciones para la acumulación y consulta de puntos de la cafetería")
@RestController
@RequestMapping("/fidelidad")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FidelidadController {

    private final FidelidadService fidelidadService;

    @Operation(summary = "Acreditar puntos de compra", description = "Recibe el monto de una compra y calcula los puntos acumulados para el cliente.")
    @PostMapping("/acreditar")
    public ResponseEntity<ApiResponse<Object>> acreditarPuntos(@Valid @RequestBody FidelidadRequestDTO req) {
        log.info("[Controler] Peticion remota para acreditar puntos. usuarios:{}, Monto de compra: ${}",
                req.getUsuario(), req.getMonto());
        fidelidadService.agregarPuntos(req.getUsuario(), req.getMonto());
        log.info("[CONTROLLER] Proceso de acreditación finalizado con éxito para el usuario: {}", req.getUsuario());
        return ResponseEntity.ok( 
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("Puntos acreditados exitosamente")
                        .build()
        );
    }

    
    @Operation(summary = "Obtener puntos por ID de usuario", description = "Busca el balance actual de puntos de un usuario y genera enlaces navegables.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Consulta realizada de forma exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "El usuario no posee un registro de puntos activo")
    })
    @GetMapping("/{usuario}") 
    public ResponseEntity<ApiResponse<EntityModel<Fidelidad>>> obtener(@PathVariable String usuario) { 
        log.info("[CONTROLLER] Consultando balance de puntos de forma individual para el usuario: {}", usuario);

        
        Fidelidad fidelidad = fidelidadService.obtenerPuntos(usuario);

        EntityModel<Fidelidad> recurso = EntityModel.of(fidelidad);
  
        recurso.add(linkTo(methodOn(FidelidadController.class).obtener(usuario)).withSelfRel()); 
        recurso.add(linkTo(methodOn(FidelidadController.class).acreditarPuntos(null)).withRel("update")); 
        
        return ResponseEntity.ok(
                ApiResponse.<EntityModel<Fidelidad>>builder()
                        .success(true)
                        .message("Balance obtenido con navegación hipermedia")
                        .data(recurso) 
                        .build()
        );
    }
}