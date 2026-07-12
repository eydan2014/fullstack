package com.example.ms_cupones.controller;

import com.example.ms_cupones.dto.ApiResponse;
import com.example.ms_cupones.dto.AplicarCuponRequestDTO;
import com.example.ms_cupones.dto.AplicarCuponResponseDTO;
import com.example.ms_cupones.dto.CuponRequestDTO;
import com.example.ms_cupones.model.Cupon;
import com.example.ms_cupones.service.CuponService;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cupones", description = "Operaciones relacionadas con cupones de descuento")
@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final CuponService cuponService;

    @Operation(
            summary = "Listar cupones",
            description = "Retorna todos los cupones registrados en el sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupones listados correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Cupon>>> listarCupones() {

        List<Cupon> cupones = cuponService.listarCupones();

        return ResponseEntity.ok(
                ApiResponse.<List<Cupon>>builder()
                        .success(true)
                        .message("Cupones listados correctamente")
                        .data(cupones)
                        .build()
        );
    }

    @Operation(
            summary = "Obtener cupón por ID",
            description = "Busca un cupón usando su identificador"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
public ResponseEntity<ApiResponse<EntityModel<Cupon>>> obtenerCupon(
        @PathVariable Integer id
) {

    Cupon cupon = cuponService.obtenerCupon(id);

    EntityModel<Cupon> recurso = EntityModel.of(cupon);

    recurso.add(
            linkTo(methodOn(CuponController.class).obtenerCupon(id))
                    .withSelfRel()
    );

    recurso.add(
            linkTo(methodOn(CuponController.class).listarCupones())
                    .withRel("all")
    );

    recurso.add(
            linkTo(methodOn(CuponController.class).actualizarCupon(id, null))
                    .withRel("update")
    );

    recurso.add(
            linkTo(methodOn(CuponController.class).eliminarCupon(id))
                    .withRel("delete")
    );

    return ResponseEntity.ok(
            ApiResponse.<EntityModel<Cupon>>builder()
                    .success(true)
                    .message("Cupón encontrado")
                    .data(recurso)
                    .build()
    );
}

    @Operation(
            summary = "Obtener cupón por código",
            description = "Busca un cupón usando su código"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón encontrado por código"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ApiResponse<Cupon>> obtenerPorCodigo(
            @Parameter(description = "Código del cupón", example = "CAFE10")
            @PathVariable String codigo
    ) {

        Cupon cupon = cuponService.obtenerPorCodigo(codigo);

        return ResponseEntity.ok(
                ApiResponse.<Cupon>builder()
                        .success(true)
                        .message("Cupón encontrado por código")
                        .data(cupon)
                        .build()
        );
    }

    @Operation(
            summary = "Crear cupón",
            description = "Crea un nuevo cupón de descuento con los datos enviados"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto con datos existentes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Cupon>> crearCupon(
            @Valid @RequestBody CuponRequestDTO dto
    ) {

        Cupon cupon = cuponService.crearCupon(dto);

        return ResponseEntity.ok(
                ApiResponse.<Cupon>builder()
                        .success(true)
                        .message("Cupón creado correctamente")
                        .data(cupon)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar cupón",
            description = "Actualiza los datos de un cupón existente"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cupon>> actualizarCupon(
            @Parameter(description = "ID del cupón", example = "1")
            @PathVariable Integer id,

            @Valid @RequestBody CuponRequestDTO dto
    ) {

        Cupon cupon = cuponService.actualizarCupon(id, dto);

        return ResponseEntity.ok(
                ApiResponse.<Cupon>builder()
                        .success(true)
                        .message("Cupón actualizado correctamente")
                        .data(cupon)
                        .build()
        );
    }

    @Operation(
            summary = "Cambiar estado del cupón",
            description = "Activa o desactiva un cupón existente"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado del cupón actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Cupon>> cambiarEstado(
            @Parameter(description = "ID del cupón", example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Estado del cupón", example = "false")
            @RequestParam Boolean activo
    ) {

        Cupon cupon = cuponService.cambiarEstado(id, activo);

        return ResponseEntity.ok(
                ApiResponse.<Cupon>builder()
                        .success(true)
                        .message("Estado del cupón actualizado correctamente")
                        .data(cupon)
                        .build()
        );
    }

    @Operation(
            summary = "Validar cupón",
            description = "Valida si un cupón puede aplicarse a un monto de pedido"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón validado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cupón inválido o datos incorrectos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/validar")
    public ResponseEntity<ApiResponse<AplicarCuponResponseDTO>> validarCupon(
            @Valid @RequestBody AplicarCuponRequestDTO dto
    ) {

        AplicarCuponResponseDTO response = cuponService.validarCupon(dto);

        return ResponseEntity.ok(
                ApiResponse.<AplicarCuponResponseDTO>builder()
                        .success(true)
                        .message("Cupón validado correctamente")
                        .data(response)
                        .build()
        );
    }

    @Operation(
            summary = "Aplicar cupón",
            description = "Aplica un cupón a un monto de pedido y calcula el total final con descuento"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupón aplicado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Cupón inválido o datos incorrectos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "El cupón no cumple una regla de negocio"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/aplicar")
    public ResponseEntity<ApiResponse<AplicarCuponResponseDTO>> aplicarCupon(
            @Valid @RequestBody AplicarCuponRequestDTO dto
    ) {

        AplicarCuponResponseDTO response = cuponService.aplicarCupon(dto);

        return ResponseEntity.ok(
                ApiResponse.<AplicarCuponResponseDTO>builder()
                        .success(true)
                        .message("Cupón aplicado correctamente")
                        .data(response)
                        .build()
        );
    }

    @Operation(
        summary = "Eliminar cupón",
        description = "Elimina un cupón usando su identificador"
)
@ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Cupón eliminado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminarCupon(
        @Parameter(description = "ID del cupón", example = "1")
        @PathVariable Integer id
) {

    cuponService.eliminarCupon(id);

    return ResponseEntity.noContent().build();
}
}