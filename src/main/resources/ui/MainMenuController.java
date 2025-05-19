package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class MainMenuController {

    @FXML private Button btnTexasSingle;
    @FXML private Button btnTexasMulti;
    @FXML private Button btnBlackjack;
    @FXML private Button btnExit;

    @FXML
    private void onTexasSingleClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/TexasSingle.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnTexasSingle.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Texas Hold'em - Single Player");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTexasMultiClick() {
        System.out.println("🌐 Starting Texas Hold'em (Multiplayer)...");
    }

    @FXML
    private void onBlackjackClick() {
        System.out.println("🂡 Starting Blackjack (Single Player)...");
    }

    @FXML
    private void onExitClick() {
        System.exit(0);
    }
}