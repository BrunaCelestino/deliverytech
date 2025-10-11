package com.deliverytech.controller;

import com.deliverytech.dto.request.ClienteRequest;
import com.deliverytech.dto.response.ClienteResponse;
import com.deliverytech.dto.response.PageResponse;
import com.deliverytech.model.Cliente;
import com.deliverytech.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

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

        return ResponseEntity
                .ok(new ClienteResponse(salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getAtivo()));
    }

    @GetMapping
    public PageResponse<ClienteResponse> listar(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.info("Listando todos os clientes ativos");

        Integer pageAtualizada = page == 0 ? 0 : (page - 1);

        Pageable pageable = PageRequest.of(pageAtualizada, pageSize);
        Page<Cliente> clientePage = clienteService.listarAtivos(pageable);

        PageResponse<ClienteResponse> clienteResponse = new PageResponse<ClienteResponse>(
                clientePage.stream().map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo())).toList(),
                clientePage.getTotalElements(),
                clientePage.getTotalPages(),
                clientePage.getPageable().getPageSize(),
                page == 0 ? 0 : clientePage.getPageable().getPageNumber() + 1);

    return clienteResponse;
    }

    @GetMapping("/clientes") // Mapeia a URL http://localhost:8080/clientes
    public List<ClienteResponse> listarClientesNoEndpointSimples(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        logger.info("Acessando o endpoint simplificado /clientes");

        Pageable pageable = PageRequest.of(page, pageSize);
        return clienteService.listarAtivos(pageable).stream()
                .map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable Long id) {
        logger.info("Buscando cliente com ID: {}", id);
        return clienteService.buscarPorId(id)
                .map(c -> new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getAtivo()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Cliente com ID {} não encontrado", id);
                    return ResponseEntity.notFound().build();
                });
    }

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

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> ativarDesativar(@PathVariable Long id) {
        logger.info("Alterando status do cliente ID: {}", id);
        clienteService.ativarDesativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        logger.debug("Status endpoint acessado");
        int cpuCores = Runtime.getRuntime().availableProcessors();
        logger.info("CPU cores disponíveis: {}", cpuCores);
        return ResponseEntity.ok("API está online");
    }
}
