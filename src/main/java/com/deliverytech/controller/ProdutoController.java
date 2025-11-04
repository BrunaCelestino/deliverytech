package com.deliverytech.controller;

import com.deliverytech.dto.request.ProdutoRequest;
import com.deliverytech.dto.response.ProdutoResponse;
import com.deliverytech.exception.EntityNotFoundException;
import com.deliverytech.exception.ErrorResponse;
import com.deliverytech.model.Produto;
import com.deliverytech.model.Restaurante;
import com.deliverytech.service.ProdutoService;
import com.deliverytech.service.RestauranteService;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;
    private final RestauranteService restauranteService;

    @Operation(summary = "Cadastra um novo produto", description = "Cria um novo produto e o associa a um restaurante")
    @ApiResponse(responseCode = "200", description = "Produto cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))    
    @PostMapping
    public ResponseEntity<ProdutoResponse> cadastrar(@Valid @RequestBody ProdutoRequest request) {
        Restaurante restaurante = restauranteService.buscarPorId(request.getRestauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", request.getRestauranteId()));

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .categoria(request.getCategoria())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .disponivel(true)
                .restaurante(restaurante)
                .build();

        Produto salvo = produtoService.cadastrar(produto);
        return ResponseEntity.ok(new ProdutoResponse(
                salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getDescricao(), salvo.getPreco(), salvo.getDisponivel()));
    }

    @Operation(summary = "Lista produtos por restaurante", description = "Retorna uma lista de todos os produtos de um restaurante específico.")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados")
    @ApiResponse(responseCode = "404", description = "Produtos não encontrados.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class))) 
    @GetMapping("/restaurante/{restauranteId}")
    public List<ProdutoResponse> listarPorRestaurante(@PathVariable Long restauranteId) {
        List<ProdutoResponse> list = produtoService.buscarPorRestaurante(restauranteId).stream()
                .map(p -> new ProdutoResponse(p.getId(), p.getNome(), p.getCategoria(), p.getDescricao(), p.getPreco(), p.getDisponivel()))
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            throw new EntityNotFoundException("Restaurante", restauranteId);
        }

        return list;
    }

    @Operation(summary = "Atualiza um produto", description = "Atualiza os dados de um produto existente a partir do seu ID.")
    @ApiResponse(responseCode = "200", description = "Produto atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para atualizar",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class))) 
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        Produto atualizado = Produto.builder()
                .nome(request.getNome())
                .categoria(request.getCategoria())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .build();
        Produto salvo = produtoService.atualizar(id, atualizado);
        return ResponseEntity.ok(new ProdutoResponse(salvo.getId(), salvo.getNome(), salvo.getCategoria(), salvo.getDescricao(), salvo.getPreco(), salvo.getDisponivel()));
    }

    @Operation(summary = "Altera a disponibilidade de um produto", description = "Altera o status de um produto (disponível/indisponível) a partir do seu ID.")
    @ApiResponse(responseCode = "204", description = "Status do pedido atualizado, sem retorno.")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado.",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))     
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<Void> alterarDisponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.noContent().build();
    }

    @Cacheable("produtos")
    @Timed(value = "produtos.buscar", histogram = true)
    @GetMapping("/produtos")
    public Page<Produto> buscar(Pageable pageable) {
        return produtoService.listar(pageable);
    }

}
