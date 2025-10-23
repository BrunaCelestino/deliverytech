package com.deliverytech.controller;

import com.deliverytech.dto.request.ClienteRequest;
import com.deliverytech.dto.response.ClienteResponse;
import com.deliverytech.dto.response.PageResponse;
import com.deliverytech.exception.EntityNotFoundException;
import com.deliverytech.exception.ErrorResponse;
import com.deliverytech.model.Cliente;
import com.deliverytech.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    @Operation(summary = "Cadastra um novo cliente", description = "Cria um novo cliente no sistema.")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody ClienteRequest request) {
        logger.info("Cadastro de cliente iniciado: {}", request.getEmail());

        Cliente cliente = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .ativo(true)
                .build();

        Cliente salvo = clienteService.cadastrar(cliente);

        logger.debug("Cliente salvo com ID {}", salvo.getId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(salvo.getId())
            .toUri();

        return ResponseEntity
                .created(location)
                .body(new ClienteResponse(salvo.getId(), 
                salvo.getNome(), salvo.getEmail(), salvo.getAtivo()));
    }

    @Operation(summary = "Listar todos os clientes ativos", description = "Retorna uma lista paginada de todos os clientes com status ativo.")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @ApiResponse(responseCode = "404", description = "Nenhum cliente foi encontrado",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping
    public PageResponse<ClienteResponse> listar(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.info("Listando todos os clientes ativos");

        Integer pageAtualizada = page == 0 ? 0 : (page - 1);

        Pageable pageable = PageRequest.of(pageAtualizada, pageSize);
        Page<Cliente> clientePage = clienteService.listarAtivos(pageable);

        if (clientePage.getTotalElements() == 0) {
            throw new EntityNotFoundException("cliente");
        }

        PageResponse<ClienteResponse> clienteResponse = new PageResponse<ClienteResponse>(
                clientePage.stream().map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo())).toList(),
                clientePage.getTotalElements(),
                clientePage.getTotalPages(),
                clientePage.getPageable().getPageSize(),
                page == 0 ? 0 : clientePage.getPageable().getPageNumber() + 1);

    return clienteResponse;
    }

    @Operation(summary = "Listar todos os clientes ativos", description = "Endpoint simplificado. Retorna uma lista não paginada de todos os clientes com status ativo.")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @ApiResponse(responseCode = "404", description = "Nenhum cliente foi encontrado",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))    
    @GetMapping("/clientes") // Mapeia a URL http://localhost:8080/clientes
    public ResponseEntity<List<ClienteResponse>> listarClientesNoEndpointSimples(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.info("Acessando o endpoint simplificado /clientes");

        Pageable pageable = PageRequest.of(page, pageSize);

        List<ClienteResponse> list = clienteService.listarAtivos(pageable).stream()
                .map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new EntityNotFoundException("cliente");
        }

        return ResponseEntity.ok(list);
    
    }

    @Operation(summary = "Buscar um cliente por ID", description = "Retorna os detalhes de um cliente específico pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum cliente foi encontrado",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable Long id) {
        logger.info("Buscando cliente com ID: {}", id);
        return clienteService.buscarPorId(id)
                .map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));
    }

    @Operation(summary = "Atualiza um cliente", description = "Atualiza os dados de um cliente existente a partir do seu ID.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado")
    @ApiResponse(responseCode = "404", description = "Cliente não foi encontrado",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        logger.info("Atualizando cliente ID: {}", id);

        Cliente atualizado = Cliente.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .build();

        Cliente salvo = clienteService.atualizar(id, atualizado);

        return ResponseEntity
                .ok(new ClienteResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getAtivo()));
    }

    @Operation(summary = "Ativa ou desativa um cliente", description = "Altera o status de um cliente (ativo/inativo) a partir do seu ID.")
    @ApiResponse(responseCode = "204", description = "Cliente atualizado e nada foi retornado")
    @ApiResponse(responseCode = "404", description = "Cliente não foi encontrado",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> ativarDesativar(@PathVariable Long id) {
        logger.info("Alterando status do cliente ID: {}", id);
        clienteService.ativarDesativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Status Api", description = "Obtêm status da API")
    @ApiResponse(responseCode = "200", description = "API está online")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/status")
    public ResponseEntity<String> status() {
        logger.debug("Status endpoint acessado");
        int cpuCores = Runtime.getRuntime().availableProcessors();
        logger.info("CPU cores disponíveis: {}", cpuCores);
        return ResponseEntity.ok("API está online");
    }
}
