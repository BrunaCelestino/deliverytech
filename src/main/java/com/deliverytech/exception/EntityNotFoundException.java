package com.deliverytech.exception;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s com ID %S não encontrado", entityName, id));
    }

     public EntityNotFoundException(String entityName) {
        super(String.format(" Não foram encontrado nenhum %s", entityName));
    }

}
