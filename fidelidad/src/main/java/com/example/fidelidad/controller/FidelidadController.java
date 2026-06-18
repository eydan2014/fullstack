package com.example.fidelidad.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.fidelidad.dto.FidelidadRequestDTO;
import com.example.fidelidad.exception.ApiResponse;
import com.example.fidelidad.service.FidelidadService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Fidelidad", description = "Operaciones para la acumulación y consulta de puntos de la cafetería")
@RestController
@RequestMapping("/fidelidad")
@RequiredArgsConstructor
@Slf4j
public class FidelidadController {

    private final FidelidadService fidelidadService;

    @Operation(summary = "Acreditar puntos de compra", description = "Recibe el monto de una compra y calcula los puntos acumulados para el cliente.")
    @PostMapping("/acreditar")
    public ResponseEntity<ApiResponse<Void>> acreditarPuntos(@RequestBody FidelidadRequestDTO req) {
        log.info("[Controler] Peticion remota para acreditar puntos. usuarios:{}, Monto de compra: ${}",
                req.getUsuario(), req.getMonto());
        fidelidadService.agregarPuntos(req.getUsuario(), req.getMonto());
        log.info("[CONTROLLER] Proceso de acreditación finalizado con éxito para el usuario: {}", req.getUsuario());
        return ResponseEntity.ok( 
                ApiResponse.<Void>builder()
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
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ApiResponse<EntityModel<FidelidadRequestDTO>>> obtener(@PathVariable Long id) {
        log.info("[CONTROLLER] Consultando balance de puntos de forma individual para ID: {}", id);
        
    
        FidelidadRequestDTO mockData = new FidelidadRequestDTO();
        mockData.setUsuario(id.toString());
            EntityModel<FidelidadRequestDTO> recurso = EntityModel.of(mockData);
        
              recurso.add(linkTo(methodOn(FidelidadController.class).obtener(id)).withSelfRel()); 
        recurso.add(linkTo(methodOn(FidelidadController.class).acreditarPuntos(null)).withRel("update")); 
        
        return ResponseEntity.ok(
                ApiResponse.<EntityModel<FidelidadRequestDTO>>builder()
                        .success(true)
                        .message("Balance obtenido con navegación hipermedia")
                        .data(recurso) 
                        .build()
        );
    }
}