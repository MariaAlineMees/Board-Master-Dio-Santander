package br.com.dio.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO para resposta de operações relacionadas a um board.
 * Contém informações detalhadas sobre o board, incluindo suas colunas.
 */
public record BoardResponseDTO(

    /**
     * ID único do board
     */
    Long id,

    /**
     * Nome do board
     */
    String name,

    /**
     * Data e hora de criação do board
     */
    OffsetDateTime createdAt,

    /**
     * Lista de colunas do board
     */
    List<BoardColumnResponseDTO> columns
) {

    /**
     * Cria um novo DTO de resposta com base em um BoardDetailsDTO existente.
     *
     * @param boardDetails DTO com os detalhes do board
     * @param createdAt Data e hora de criação do board
     * @return Novo DTO de resposta
     */
    public static BoardResponseDTO fromBoardDetails(BoardDetailsDTO boardDetails, OffsetDateTime createdAt) {
        List<BoardColumnResponseDTO> columns = boardDetails.columns().stream()
            .map(column -> new BoardColumnResponseDTO(
                column.id(),
                column.name(),
                column.kind().name(),
                column.cardsAmount()
            ))
            .toList();

        return new BoardResponseDTO(
            boardDetails.id(),
            boardDetails.name(),
            createdAt,
            columns
        );
    }

    /**
     * DTO para representar uma coluna na resposta do board.
     */
    public record BoardColumnResponseDTO(
        Long id,
        String name,
        String kind,
        int cardsCount
    ) {}
}