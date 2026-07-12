package com.example.ms_cocina.Controller;

import com.example.ms_cocina.dto.ApiResponse;
import com.example.ms_cocina.dto.CocinaRequestDTO;
import com.example.ms_cocina.Model.TicketCocina;
import com.example.ms_cocina.Service.CocinaService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Cocina", description = "Operaciones relacionadas con los tickets de cocina")
@RestController
@RequestMapping("/api/cocina")
@RequiredArgsConstructor
public class CocinaController {

    private final CocinaService cocinaService;

    @Operation(
            summary = "Listar tickets de cocina",
            description = "Retorna todos los tickets de cocina registrados en el sistema"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tickets listados correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketCocina>>> listarTickets() {

        List<TicketCocina> tickets = cocinaService.listarTickets();

        return ResponseEntity.ok(
                ApiResponse.<List<TicketCocina>>builder()
                        .success(true)
                        .message("Tickets de cocina listados correctamente")
                        .data(tickets)
                        .build()
        );
    }

    @Operation(
            summary = "Obtener ticket por ID",
            description = "Busca un ticket de cocina usando su identificador"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
public ResponseEntity<ApiResponse<EntityModel<TicketCocina>>> obtenerTicket(
        @PathVariable Integer id
) {

    TicketCocina ticket = cocinaService.obtenerTicket(id);

    EntityModel<TicketCocina> recurso = EntityModel.of(ticket);

    recurso.add(
            linkTo(methodOn(CocinaController.class).obtenerTicket(id))
                    .withSelfRel()
    );

    recurso.add(
            linkTo(methodOn(CocinaController.class).listarTickets())
                    .withRel("all")
    );

    recurso.add(
            linkTo(methodOn(CocinaController.class).actualizarEstado(id, "PREPARANDO"))
                    .withRel("update")
    );

    recurso.add(
            linkTo(methodOn(CocinaController.class).eliminarTicket(id))
                    .withRel("delete")
    );

    return ResponseEntity.ok(
            ApiResponse.<EntityModel<TicketCocina>>builder()
                    .success(true)
                    .message("Ticket de cocina encontrado")
                    .data(recurso)
                    .build()
    );
}

    @Operation(
            summary = "Obtener ticket por ID de pedido",
            description = "Busca el ticket de cocina asociado a un pedido específico"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket encontrado por pedido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket no encontrado para el pedido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<ApiResponse<TicketCocina>> obtenerPorPedidoId(
            @Parameter(description = "ID del pedido asociado al ticket", example = "1")
            @PathVariable Integer pedidoId
    ) {

        TicketCocina ticket = cocinaService.obtenerPorPedidoId(pedidoId);

        return ResponseEntity.ok(
                ApiResponse.<TicketCocina>builder()
                        .success(true)
                        .message("Ticket encontrado por pedido")
                        .data(ticket)
                        .build()
        );
    }

    @Operation(
            summary = "Listar tickets por estado",
            description = "Retorna los tickets de cocina filtrados por estado"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tickets filtrados correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<List<TicketCocina>>> listarPorEstado(
            @Parameter(description = "Estado del ticket", example = "PENDIENTE")
            @PathVariable String estado
    ) {

        List<TicketCocina> tickets = cocinaService.listarPorEstado(estado);

        return ResponseEntity.ok(
                ApiResponse.<List<TicketCocina>>builder()
                        .success(true)
                        .message("Tickets filtrados por estado")
                        .data(tickets)
                        .build()
        );
    }

    @Operation(
            summary = "Crear ticket de cocina",
            description = "Crea un nuevo ticket de cocina validando los datos enviados"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket creado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<TicketCocina>> crearTicket(
            @Valid @RequestBody CocinaRequestDTO dto
    ) {

        TicketCocina ticket = cocinaService.crearTicket(dto);

        return ResponseEntity.ok(
                ApiResponse.<TicketCocina>builder()
                        .success(true)
                        .message("Ticket de cocina creado correctamente")
                        .data(ticket)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar estado del ticket",
            description = "Actualiza el estado de un ticket de cocina existente"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<TicketCocina>> actualizarEstado(
            @Parameter(description = "ID del ticket de cocina", example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Nuevo estado del ticket", example = "PREPARANDO")
            @RequestParam String estado
    ) {

        TicketCocina ticket = cocinaService.actualizarEstado(id, estado);

        return ResponseEntity.ok(
                ApiResponse.<TicketCocina>builder()
                        .success(true)
                        .message("Estado actualizado correctamente")
                        .data(ticket)
                        .build()
        );
    }

    @Operation(
            summary = "Actualizar observación del ticket",
            description = "Actualiza la observación de un ticket de cocina existente"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Observación actualizada correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/observacion")
    public ResponseEntity<ApiResponse<TicketCocina>> actualizarObservacion(
            @Parameter(description = "ID del ticket de cocina", example = "1")
            @PathVariable Integer id,

            @Parameter(description = "Nueva observación del ticket", example = "Sin azúcar")
            @RequestParam String observacion
    ) {

        TicketCocina ticket = cocinaService.actualizarObservacion(id, observacion);

        return ResponseEntity.ok(
                ApiResponse.<TicketCocina>builder()
                        .success(true)
                        .message("Observacion actualizada correctamente")
                        .data(ticket)
                        .build()
        );
    }

    @Operation(
            summary = "Eliminar ticket de cocina",
            description = "Elimina un ticket de cocina usando su identificador"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket eliminado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarTicket(
            @Parameter(description = "ID del ticket de cocina", example = "1")
            @PathVariable Integer id
    ) {

        cocinaService.eliminarTicket(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Ticket de cocina eliminado correctamente")
                        .build()
        );
    }
}