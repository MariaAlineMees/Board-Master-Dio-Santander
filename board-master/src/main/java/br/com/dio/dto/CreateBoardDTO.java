package br.com.dio.dto;

import br.com.dio.persistence.entity.BoardColumnKindEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para criação de um novo board.
 * Contém as informações necessárias para criar um novo board com suas colunas iniciais.
 */
public record CreateBoardDTO(

        /**
         * Nome do board a ser criado.
         * Não pode estar em branco e deve ter entre 3 e 100 caracteres.
         */
        @NotBlank(message = "O nome do board é obrigatório")
        @Size(min = 3, max = 100, message = "O nome do board deve ter entre 3 e 100 caracteres")
        String name,

        /**
         * Lista de colunas personalizadas a serem adicionadas ao board.
         * Pode ser vazia se não houver colunas personalizadas.
         */
        List<@NotBlank(message = "O nome da coluna não pode estar em branco")
        @Size(min = 2, max = 50, message = "O nome da coluna deve ter entre 2 e 50 caracteres")
                String> customColumns
) {

    /**
     * Cria um novo DTO de criação de board com o nome especificado.
     *
     * @param name Nome do board
     * @return Novo DTO de criação de board sem colunas personalizadas
     */
    public static CreateBoardDTO withName(String name) {
        return new CreateBoardDTO(name, List.of());
    }

    /**
     * Adiciona uma coluna personalizada ao DTO.
     *
     * @param columnName Nome da coluna personalizada
     * @return Novo DTO com a coluna adicionada
     */
    public CreateBoardDTO withCustomColumn(String columnName) {
        var newColumns = new java.util.ArrayList<>(customColumns != null ? customColumns : List.of());
        newColumns.add(columnName);
        return new CreateBoardDTO(this.name, newColumns);
    }
}