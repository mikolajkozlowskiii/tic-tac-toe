package com.javamaster.service;

import com.javamaster.exception.InvalidGameException;
import com.javamaster.exception.InvalidParamException;
import com.javamaster.exception.NotFoundException;
import com.javamaster.model.Game;
import com.javamaster.model.GamePlay;
import com.javamaster.model.Player;
import com.javamaster.model.TicToe;
import com.javamaster.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.javamaster.model.GameStatus.*;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(Player player) {
        Game game = createNewGame(player);

        updateGameStorage(game);

        return game;
    }

    private Game createNewGame(Player player) {
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);
        return game;
    }

    public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException {
        Game game = findGameById(gameId);

        addSecondPlayerToGame(player2, game);

        updateGameStorage(game);
        return game;
    }

    private void updateGameStorage(Game game) {
        GameStorage.getInstance().putGame(game);
    }

    private Game findGameById(String gameId) throws InvalidParamException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id doesn't exist");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);
        checkIsGameAvailable(game);
        return game;
    }

    private void checkIsGameAvailable(Game game) throws InvalidGameException {
        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game is not valid anymore");
        }
    }

    private void addSecondPlayerToGame(Player player2, Game game) {
        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {
        Game game = findRandomGame();

        addSecondPlayerToGame(player2, game);

        updateGameStorage(game);
        return game;
    }

    private Game findRandomGame() throws NotFoundException {
        return GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW))
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        checkDoesGameExists(gamePlay.getGameId());

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());

        checkIsGameFinished(game);

        updateGameBoard(gamePlay, game);

        updateGameStorage(game);
        return game;
    }

    private void updateGameBoard(GamePlay gamePlay, Game game) {
        int[][] board = game.getBoard();
        board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();

        boolean xWinner = checkWinner(game.getBoard(), TicToe.X);
        boolean oWinner = checkWinner(game.getBoard(), TicToe.O);

        if (xWinner) {
            game.setWinner(TicToe.X);
        } else if (oWinner) {
            game.setWinner(TicToe.O);
        }
    }

    private void checkIsGameFinished(Game game) throws InvalidGameException {
        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }
    }

    private void checkDoesGameExists(String gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found " + gameId);
        }
    }

    private Boolean checkWinner(int[][] board, TicToe ticToe) {
        int[] boardArray = new int[9];
        int counterIndex = 0;
        for (int[] ints : board) {
            for (int anInt : ints) {
                boardArray[counterIndex] = anInt;
                counterIndex++;
            }
        }

        int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
        for (int[] winCombination : winCombinations) {
            int counter = 0;
            for (int i : winCombination) {
                if (boardArray[i] == ticToe.getValue()) {
                    counter++;
                    if (counter == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
