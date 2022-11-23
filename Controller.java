import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Controller implements Initializable {
  public Socket socket;
  public DataInputStream dataInputStream;
  public PrintStream printStream;
  private static final int PLAY_1 = 1;
  private static final int PLAY_2 = 2;
  private static final int EMPTY = 0;
  private static final int BOUND = 90;
  private static final int OFFSET = 15;
  @FXML
  private Pane base_square;
  @FXML
  private Rectangle game_panel;
  private static boolean TURN = false;
  public boolean my_turn = false;
  private static final int[][] chessBoard = new int[3][3];
  private static final boolean[][] flag = new boolean[3][3];

  public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.printStream = new PrintStream(socket.getOutputStream());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
        game_panel.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / BOUND);
            int y = (int) (event.getY() / BOUND);
            if (refreshBoard(x, y)) {
                if (gameState(x, y)) {
                    System.out.println("You win.");
                }
                TURN = !TURN;
            }
        });
  }

  private boolean refreshBoard(int x, int y) {
        if (chessBoard[x][y] == EMPTY) {
            chessBoard[x][y] = TURN ? PLAY_1 : PLAY_2;
            drawChess();
            return true;
        }
        return false;
  }
  private void drawChess() {
        for (int i = 0; i < chessBoard.length; i++) {
            for (int j = 0; j < chessBoard[0].length; j++) {
                if (flag[i][j]) {
                    // This square has been drawing, ignore.
                    continue;
                }
                switch (chessBoard[i][j]) {
                    case PLAY_1:
                        drawCircle(i, j);
                        break;
                    case PLAY_2:
                        drawLine(i, j);
                        break;
                    case EMPTY:
                        // do nothing
                        break;
                    default:
                        System.err.println("Invalid value!");
                }
            }
        }
  }

  private void drawCircle(int i, int j) {
        Circle circle = new Circle();
        base_square.getChildren().add(circle);
        circle.setCenterX(i * BOUND + BOUND / 2.0 + OFFSET);
        circle.setCenterY(j * BOUND + BOUND / 2.0 + OFFSET);
        circle.setRadius(BOUND / 2.0 - OFFSET / 2.0);
        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        flag[i][j] = true;
  }

  private void drawLine(int i, int j) {
        Line line_a = new Line();
        Line line_b = new Line();
        base_square.getChildren().add(line_a);
        base_square.getChildren().add(line_b);
        line_a.setStartX(i * BOUND + OFFSET * 1.5);
        line_a.setStartY(j * BOUND + OFFSET * 1.5);
        line_a.setEndX((i + 1) * BOUND + OFFSET * 0.5);
        line_a.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_a.setStroke(Color.BLUE);

        line_b.setStartX((i + 1) * BOUND + OFFSET * 0.5);
        line_b.setStartY(j * BOUND + OFFSET * 1.5);
        line_b.setEndX(i * BOUND + OFFSET * 1.5);
        line_b.setEndY((j + 1) * BOUND + OFFSET * 0.5);
        line_b.setStroke(Color.BLUE);
        flag[i][j] = true;
  }

  private boolean gameState(int x, int y) {
        int temp = chessBoard[x][y];
        for (int i = 0; i < 3; i++) {
            if (chessBoard[i][0] == temp) {
                if (chessBoard[i][0] == chessBoard[i][1] && chessBoard[i][1] == chessBoard[i][2]) {
                    return true;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (chessBoard[0][i] == temp) {
                if (chessBoard[0][i] == chessBoard[1][i] && chessBoard[1][i] == chessBoard[2][i]) {
                    return true;
                }
            }
        }
        if (chessBoard[0][0] == temp) {
            if (chessBoard[0][0] == chessBoard[1][1] && chessBoard[1][1] == chessBoard[2][2]) {
                return true;
            }
        }
        if (chessBoard[0][2] == temp) {
            if (chessBoard[0][2] == chessBoard[1][1] && chessBoard[1][1] == chessBoard[2][0]) {
                return true;
            }
        }
        return false;
    }
}
