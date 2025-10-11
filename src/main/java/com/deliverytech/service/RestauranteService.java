package com.deliverytech.service;

import com.deliverytech.model.Restaurante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestauranteService {
    Restaurante cadastrar(Restaurante restaurante);
    Optional<Restaurante> buscarPorId(Long id);
    List<Restaurante> buscarPorCategoria(String categoria);
    Restaurante atualizar(Long id, Restaurante restauranteAtualizado);
    Page<Restaurante> listarTodos(Pageable pageable);

}
