package com.deliverytech.controller;

import com.deliverytech.dto.request.RestauranteRequest;
import com.deliverytech.dto.response.RestauranteResponse;
import com.deliverytech.exception.EntityNotFoundException;
import com.deliverytech.exception.ErrorResponse;
import com.deliverytech.model.Restaurante;
import com.deliverytech.service.RestauranteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento de restaurantes")
@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;


    @Operation(summary = "Cadastra um novo restaurante", description = "Cria um novo restaurante no sistema.")
    @ApiResponse(responseCode = "201", description = "Restaurante cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))     
    @PostMapping
    public ResponseEntity<RestauranteResponse> cadastrar(@Valid @RequestBody RestauranteRequest request) {
        Restaurante restaurante = Restaurante.builder()
                .nome(request.getNome())
                .telefone(request.getTelefone())
                .categoria(request.getCategoria())
                .taxaEntrega(request.getTaxaEntrega())
                .tempoEntregaMinutos(request.getTempoEntregaMinutos())
                .ativo(true)
                .build();
        Restaurante salvo = restauranteService.cadastrar(restaurante);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(salvo.getId())
            .toUri();

        return ResponseEntity.created(location).body(new RestauranteResponse(
                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getTelefone(),
                salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(), salvo.getAtivo()));
    }

    @Operation(summary = "Listar todos os restaurantes", description = "Retorna uma lista paginada de todos os restaurantes.")
    @ApiResponse(responseCode = "200", description = "Restaurantes encontrados")
    @ApiResponse(responseCode = "404", description = "Restaurantes não encontrados.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))  
    @GetMapping
    public Page<RestauranteResponse> listarTodos(
         @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer pageSize
    ) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<RestauranteResponse> pages =  restauranteService.listarTodos(pageable)
                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()));
    
        if (pages.getTotalElements() == 0) {
            throw new EntityNotFoundException("restaurante");

        }

        return pages;
    }

    @Operation(summary = "Busca um restaurante por ID", description = "Retorna os detalhes de um restaurante específico pelo ID.")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))      
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        return restauranteService.buscarPorId(id)
                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
    }

    @Operation(summary = "Busca restaurantes por categoria", description = "Retorna uma lista de restaurantes que pertencem a uma categoria específica.")
    @ApiResponse(responseCode = "200", description = "Restaurantes encontrados")
    @ApiResponse(responseCode = "404", description = "Restaurantes não encontrados.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))  
    @GetMapping("/categoria/{categoria}")
    public List<RestauranteResponse> buscarPorCategoria(@PathVariable String categoria) {
        List<RestauranteResponse> list =  restauranteService.buscarPorCategoria(categoria).stream()
                .map(r -> new RestauranteResponse(r.getId(), r.getNome(), r.getCategoria(), r.getTelefone(), r.getTaxaEntrega(), r.getTempoEntregaMinutos(), r.getAtivo()))
                .collect(Collectors.toList());
        
        if (list.isEmpty()) {
            throw new EntityNotFoundException("restaurante");
        }

        return list;
    }

    @Operation(summary = "Atualiza um restaurante", description = "Atualiza os dados de um restaurante existente a partir do seu ID.")
    @ApiResponse(responseCode = "200", description = "Restaurantes atualizado")
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))  
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequest request) {
        Restaurante atualizado = Restaurante.builder()
                .nome(request.getNome())
                .telefone(request.getTelefone())
                .categoria(request.getCategoria())
                .taxaEntrega(request.getTaxaEntrega())
                .tempoEntregaMinutos(request.getTempoEntregaMinutos())
                .build();
        Restaurante salvo = restauranteService.atualizar(id, atualizado);
        return ResponseEntity.ok(new RestauranteResponse(salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getTelefone(), salvo.getTaxaEntrega(), salvo.getTempoEntregaMinutos(), salvo.getAtivo()));
    }
}
