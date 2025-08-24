package br.com.dio.ui;

import br.com.dio.dto.BoardResponseDTO;
import br.com.dio.dto.CreateBoardDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardQueryService;
import br.com.dio.service.BoardService;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.*;

public class MainMenu {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardService boardService = new BoardService();
    private final BoardQueryService boardQueryService = new BoardQueryService();

    public void execute() throws SQLException {
        System.out.println("\n" + ANSI_BLUE + "==================================" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "  BEM-VINDO AO GERENCIADOR DE BOARDS" + ANSI_RESET);
        System.out.println(ANSI_BLUE + "==================================" + ANSI_RESET);

        int option;
        do {
            System.out.println("\n" + ANSI_CYAN + "MENU PRINCIPAL" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "1." + ANSI_RESET + " Criar um novo board");
            System.out.println(ANSI_BLUE + "2." + ANSI_RESET + " Selecionar um board existente");
            System.out.println(ANSI_BLUE + "3." + ANSI_RESET + " Listar todos os boards");
            System.out.println(ANSI_BLUE + "4." + ANSI_RESET + " Excluir um board");
            System.out.println(ANSI_RED + "0." + ANSI_RESET + " Sair");

            System.out.print("\n" + ANSI_YELLOW + "Digite a opção desejada: " + ANSI_RESET);

            try {
                option = Integer.parseInt(scanner.nextLine().trim());

                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> listAllBoards();
                    case 4 -> deleteBoard();
                    case 0 -> {
                        System.out.println("\n" + ANSI_GREEN + "Obrigado por usar o Gerenciador de Boards! Até mais!" + ANSI_RESET);
                        System.exit(0);
                    }
                    default -> System.out.println(ANSI_RED + "\n❌ Opção inválida. Por favor, escolha uma opção do menu." + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "\n⚠️  Por favor, digite apenas números para selecionar uma opção." + ANSI_RESET);
                option = -1;
            } catch (Exception e) {
                System.out.println(ANSI_RED + "\n❌ Ocorreu um erro: " + e.getMessage() + ANSI_RESET);
                option = -1;
            }

        } while (option != 0);
    }

    private void listAllBoards() throws SQLException {
        System.out.println("\n" + ANSI_CYAN + "=== LISTA DE BOARDS ===" + ANSI_RESET);
        List<BoardEntity> boards = boardQueryService.findAllBoards();

        if (boards.isEmpty()) {
            System.out.println(ANSI_YELLOW + "Nenhum board encontrado." + ANSI_RESET);
            return;
        }

        System.out.println(ANSI_BLUE + "ID  | Nome do Board" + ANSI_RESET);
        System.out.println("----+------------------");
        boards.forEach(board ->
                System.out.printf("%-3d | %s%n", board.getId(), board.getName())
        );
    }

    private void createBoard() throws SQLException {
        System.out.println("\n" + ANSI_CYAN + "=== CRIAR NOVO BOARD ===" + ANSI_RESET);

        System.out.print("\n" + ANSI_YELLOW + "Digite o nome do seu board: " + ANSI_RESET);
        String boardName = scanner.nextLine().trim();

        if (boardName.isEmpty()) {
            System.out.println(ANSI_RED + "\n❌ O nome do board não pode estar vazio." + ANSI_RESET);
            return;
        }

        CreateBoardDTO createBoardDTO = CreateBoardDTO.withName(boardName);

        System.out.print("\n" + ANSI_YELLOW + "Deseja adicionar colunas personalizadas? (S/N): " + ANSI_RESET);
        String addColumns = scanner.nextLine().trim().toUpperCase();

        if ("S".equals(addColumns)) {
            System.out.print("Quantas colunas adicionais você deseja adicionar? ");
            try {
                int additionalColumns = Integer.parseInt(scanner.nextLine().trim());
                if (additionalColumns < 0) {
                    System.out.println(ANSI_RED + "\n⚠️  O número de colunas não pode ser negativo. Nenhuma coluna adicional será adicionada." + ANSI_RESET);
                } else {
                    for (int i = 0; i < additionalColumns; i++) {
                        System.out.printf("\n" + ANSI_YELLOW + "Nome da coluna %d: " + ANSI_RESET, i + 1);
                        String columnName = scanner.nextLine().trim();
                        if (!columnName.isEmpty()) {
                            createBoardDTO = createBoardDTO.withCustomColumn(columnName);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "\n⚠️  Número inválido. Nenhuma coluna adicional será adicionada." + ANSI_RESET);
            }
        }

        try (var connection = getConnection()) {
            BoardResponseDTO response = boardService.createBoard(connection, createBoardDTO);
            System.out.println("\n" + ANSI_GREEN + "✅ Board criado com sucesso!" + ANSI_RESET);
            System.out.println(ANSI_BLUE + "ID: " + ANSI_RESET + response.id());
            System.out.println(ANSI_BLUE + "Nome: " + ANSI_RESET + response.name());
            System.out.println(ANSI_BLUE + "Criado em: " + ANSI_RESET + response.createdAt());
        } catch (SQLException e) {
            System.out.println(ANSI_RED + "\n❌ Erro ao criar o board: " + e.getMessage() + ANSI_RESET);
            throw e;
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("\n" + ANSI_CYAN + "=== SELECIONAR BOARD ===" + ANSI_RESET);
        System.out.print("\n" + ANSI_YELLOW + "Digite o ID do board que deseja selecionar: " + ANSI_RESET);

        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            try (var connection = getConnection()) {
                var board = boardQueryService.findById(connection, id);
                if (board != null) {
                    System.out.println(ANSI_GREEN + "\n✅ Board selecionado com sucesso!" + ANSI_RESET);
                    new BoardMenu(board).execute();
                } else {
                    System.out.println(ANSI_RED + "\n❌ Nenhum board encontrado com o ID: " + id + ANSI_RESET);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "\n⚠️  Por favor, digite um ID válido." + ANSI_RESET);
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("\n" + ANSI_CYAN + "=== EXCLUIR BOARD ===" + ANSI_RESET);
        System.out.print("\n" + ANSI_YELLOW + "Digite o ID do board que deseja excluir: " + ANSI_RESET);

        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            try (var connection = getConnection()) {
                boolean deleted = boardService.deleteBoard(connection, id);
                if (deleted) {
                    System.out.println(ANSI_GREEN + "\n✅ Board excluído com sucesso!" + ANSI_RESET);
                } else {
                    System.out.println(ANSI_RED + "\n❌ Nenhum board encontrado com o ID: " + id + ANSI_RESET);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(ANSI_RED + "\n⚠️  Por favor, digite um ID válido." + ANSI_RESET);
        }
    }
}