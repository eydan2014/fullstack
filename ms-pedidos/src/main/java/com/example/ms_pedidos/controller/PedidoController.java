package com.example.ms_pedidos.controller;

import com.example.ms_pedidos.dto.ApiResponse;
import com.example.ms_pedidos.dto.PedidoRequestDTO;
import com.example.ms_pedidos.model.Pedido;
import com.example.ms_pedidos.service.PedidoService;

import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

//http://localhost:8081/swagger-ui/index.html
@Tag(name = "Pedidos", description = "Operaciones relacionadas con pedidos")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // LISTAR TODOS LOS PEDIDOS

    @Operation(
            summary = "Listar pedidos",
            description = "Retorna todos los pedidos registrados en el sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedidos listados correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Pedido>>> listarPedidos() {

        List<Pedido> pedidos = pedidoService.listarPedidos();

        return ResponseEntity.ok(
                ApiResponse.<List<Pedido>>builder()
                        .success(true)
                        .message("Pedidos listados correctamente")
                        .data(pedidos)
                        .build()
        );
    }

    // OBTENER PEDIDO POR ID

    @Operation(
            summary = "Obtener pedido por ID",
            description = "Busca un pedido usando su identificador"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
public ResponseEntity<ApiResponse<EntityModel<Pedido>>> obtenerPedido(
        @PathVariable Integer id
) {

    Pedido pedido = pedidoService.obtenerPedido(id);

    EntityModel<Pedido> recurso = EntityModel.of(pedido);

    recurso.add(
            linkTo(methodOn(PedidoController.class).obtenerPedido(id))
                    .withSelfRel()
    );

    recurso.add(
            linkTo(methodOn(PedidoController.class).listarPedidos())
                    .withRel("all")
    );

    recurso.add(
            linkTo(methodOn(PedidoController.class).actualizarEstado(id, "PAGADO"))
                    .withRel("update")
    );

    recurso.add(
            linkTo(methodOn(PedidoController.class).eliminarPedido(id))
                    .withRel("delete")
    );

    return ResponseEntity.ok(
            ApiResponse.<EntityModel<Pedido>>builder()
                    .success(true)
                    .message("Pedido encontrado")
                    .data(recurso)
                    .build()
    );
}

    // CREAR PEDIDO

    @Operation(
            summary = "Crear pedido",
            description = "Crea un nuevo pedido, calcula el total y aplica cupón si corresponde"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupón no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Pedido>> crearPedido(
            @Valid @RequestBody PedidoRequestDTO dto
    ) {

        Pedido pedidoCreado = pedidoService.crearPedido(dto);

        return ResponseEntity.ok(
                ApiResponse.<Pedido>builder()
                        .success(true)
                        .message("Pedido creado correctamente")
                        .data(pedidoCreado)
                        .build()
        );
    }

    // ACTUALIZAR ESTADO

    @Operation(
            summary = "Actualizar estado del pedido",
            description = "Actualiza el estado de un pedido existente"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<Pedido>> actualizarEstado(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Nuevo estado del pedido", example = "PAGADO")
            @RequestParam String estado
    ) {

        Pedido pedidoActualizado = pedidoService.actualizarEstado(id, estado);

        return ResponseEntity.ok(
                ApiResponse.<Pedido>builder()
                        .success(true)
                        .message("Estado actualizado correctamente")
                        .data(pedidoActualizado)
                        .build()
        );
    }

    // ELIMINAR PEDIDO

   @Operation(
        summary = "Eliminar pedido",
        description = "Elimina un pedido usando su identificador"
)
@ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Pedido eliminado correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
})
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminarPedido(
        @Parameter(description = "ID del pedido", example = "1")
        @PathVariable Integer id
) {

    pedidoService.eliminarPedido(id);

    return ResponseEntity.noContent().build();
}
}