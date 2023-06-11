module com.example.chessgameproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chessgameproject to javafx.fxml;
    exports com.example.chessgameproject;
}