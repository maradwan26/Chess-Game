package com.example.chessgameproject;

// for putting pics of pieces on board
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// to store pieces
import java.util.ArrayList;

// javafx imports
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private static Scene scene;
    private static Group gridGroup;     // for grid
    private static Group pieceGroup;    // for pieces
    private static Group highlightGroup;    // for highlighted square
    private static Pane pane;

    // for images:
    private static InputStream stream;
    private static Image image;
    private static ImageView imageView;

    // to find position when mouse clicks
    private static int[] coordinates;

    // list of pieces for easy access
    private static ArrayList<Piece> pieces = new ArrayList<Piece>();

    // king declarations
    private static Piece whiteKing;
    private static Piece blackKing;
    private static Piece king;

    // to store current turn (white/black)
    private static String turn;

    // to see if current piece is ready to move
    private static boolean toMove = false;
    private static Piece curPiece;

    // placeholder used to call isOccupied method
    private static Piece p = new Piece(-1, -1, "n/a", "placement", "filename");

    // for possible en passant
    private static int enPassant = -1;

    // image files:
    private static String whiteRookFile = "src/main/resources/com/example/chessgameproject/images/whiteRook.png";
    private static String blackRookFile = "src/main/resources/com/example/chessgameproject/images/blackRook.png";
    private static String whiteKnightFile = "src/main/resources/com/example/chessgameproject/images/whiteKnight.png";
    private static String blackKnightFile = "src/main/resources/com/example/chessgameproject/images/blackKnight.png";
    private static String whiteBishopFile = "src/main/resources/com/example/chessgameproject/images/whiteBishop.png";
    private static String blackBishopFile = "src/main/resources/com/example/chessgameproject/images/blackBishop.png";
    private static String whiteQueenFile = "src/main/resources/com/example/chessgameproject/images/whiteQueen.png";
    private static String blackQueenFile = "src/main/resources/com/example/chessgameproject/images/blackQueen.png";
    private static String whiteKingFile = "src/main/resources/com/example/chessgameproject/images/whiteKing.png";
    private static String blackKingFile = "src/main/resources/com/example/chessgameproject/images/blackKing.png";
    private static String whitePawnFile = "src/main/resources/com/example/chessgameproject/images/whitePawn.png";
    private static String blackPawnFile = "src/main/resources/com/example/chessgameproject/images/blackPawn.png";

    @Override
    public void start(Stage stage) throws IOException {
        gridGroup = new Group();
        pieceGroup = new Group();
        highlightGroup = new Group();
        pane = new Pane(gridGroup, pieceGroup, highlightGroup);

        // board construction:
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {        // 8x8 board
                Rectangle r = new Rectangle();
                coordinates = coordinateFormula(i, j);   // find top left corner of rectangle
                r.setX(coordinates[0]);
                r.setY(coordinates[1]);
                r.setWidth(100);
                r.setHeight(100);
                r.setStroke(Color.BLACK);   // black outline around squares

                // alternate colors between light and dark brown
                if (count % 2 == 0) r.setFill(Color.rgb(227, 193, 111));
                else r.setFill(Color.rgb(184, 139, 74));

                gridGroup.getChildren().add(r);
                count++;
            }
            count++;
        }
        setUpPieces();
        drawBoard();

        // declare scene after everything has been drawn and set up
        scene = new Scene(pane, 850,850);

        stage.setScene(scene);
        stage.setTitle("Chess");
        stage.setResizable(false);
        stage.show();

        startGame();
    }
    private void setUpPieces() {
        // sets all the pieces in their proper places
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {     // 8x8 board
                Piece piece = null;
                // BLACK SIDE
                if (j == 0) {  // top of board (black side)
                    if (i == 0 || i == 7) {
                        piece = new Piece(i, j,"black", "rook", blackRookFile);
                    }
                    if (i == 1 || i == 6) {
                        piece = new Piece(i, j, "black", "knight", blackKnightFile);
                    }
                    if (i == 2 || i == 5) {
                        piece = new Piece(i, j, "black", "bishop", blackBishopFile);
                    }
                    if (i == 3) {
                        piece = new Piece(i, j, "black", "queen", blackQueenFile);
                    }
                    if (i == 4) {
                        piece = new Piece(i, j, "black", "king", blackKingFile);
                        blackKing = piece;
                    }
                }
                if (j == 1) {
                    piece = new Piece(i, j, "black", "pawn", blackPawnFile);
                }

                // WHITE SIDE
                if (j == 6) {
                    piece = new Piece(i, j, "white", "pawn", whitePawnFile);
                }
                if (j == 7) { // bottom of board (white side)
                    if (i == 0 || i == 7) {
                        piece = new Piece(i, j, "white", "rook", whiteRookFile);
                    }
                    if (i == 1 || i == 6) {
                        piece = new Piece(i, j, "white", "knight", whiteKnightFile);
                    }
                    if (i == 2 || i == 5) {
                        piece = new Piece(i, j, "white", "bishop", whiteBishopFile);
                    }
                    if (i == 3) {
                        piece = new Piece(i, j, "white", "queen", whiteQueenFile);
                    }
                    if (i == 4) {
                        piece = new Piece(i, j, "white", "king", whiteKingFile);
                        whiteKing = piece;
                    }
                }
                if (piece != null) {
                    pieces.add(piece);
                }
            }
        }
    }
    private void highlightSquare(int x, int y) {
        // highlights clicked square

        highlightGroup.getChildren().clear();
        Rectangle r = new Rectangle();
        coordinates = coordinateFormula(x, y);
        r.setX(coordinates[0]);
        r.setY(coordinates[1]);
        r.setWidth(100);
        r.setHeight(100);
        r.setFill(Color.rgb(0, 255, 0));    // green
        r.setOpacity(0.19);

        highlightGroup.getChildren().add(r);
    }

    private void startGame() {
        turn = "white";     // white goes first

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // get coordinates of where mouse was clicked
                int x = (int)event.getX();
                int y = (int)event.getY();
                int[] square = findSquare(x, y);
                x = square[0];
                y = square[1];

                highlightSquare(x, y);

                // if turn is white, then white king and vice versa
                king = turn.equals("white") ? whiteKing: blackKing;

                if (!toMove) {toMove = setPiece(x, y, toMove);}

                else {
                    for (Location location: curPiece.getMoves()) {
                        if (location.getX() == x && location.getY() == y) {
                            // piece is killed; must remove it from board
                            Piece occupiedPiece = p.isOccupied(pieces, new Location(x, y));
                            if (occupiedPiece != null) {pieces.remove(occupiedPiece);}

                            String type = curPiece.type;    // for castling evaluation
                            if (type.equals("rook") || type.equals("king")) {curPiece.hasMoved = true;}

                            // check for castling and enPassant
                            checkCastled(x);
                            checkEnPassant(x, y);
                            // move piece to new location
                            curPiece.setX(x);
                            curPiece.setY(y);

                            checkPromotion(y);
                            // clear groups to prepare for redraw
                            pieceGroup.getChildren().clear();
                            highlightGroup.getChildren().clear();
                            //switch turns
                            turn = turn.equals("white") ? "black": "white";
                            king = turn.equals("white") ? whiteKing: blackKing;

                            king.check = king.inCheck(pieces, king);    // check if king is in check
                            checkConditions(pieces, turn); // look for stalements or checkmate
                            flipBoard();
                            try {
                                drawBoard();
                            } catch (FileNotFoundException e) {e.printStackTrace(); System.out.println("paths might be wrong !!");}
                        }
                    }
                    toMove = setPiece(x, y, toMove);
                }
            }
        });
    }




    private int[] coordinateFormula(int x, int y) {
        // given x and y coordinate, find location of top left part of an image
        // 25 = border space
        int[] res = new int[2];
        res[0] = 25+100*x;
        res[1] = 25+100*y;
        return res;
    }


    private void setImage(String pathname, int i, int j) throws FileNotFoundException {
        // deliver pictures of the pieces to the board
        try {
            stream = new FileInputStream(pathname);
            image = new Image(stream);
            imageView = new ImageView();
            imageView.setImage(image);
            coordinates = coordinateFormula(i, j);
            imageView.setX(coordinates[0]);
            imageView.setY(coordinates[1]);
            pieceGroup.getChildren().add(imageView);
        } catch (FileNotFoundException e) {System.out.println("cannot find file"); e.printStackTrace();}
    }

    private int[] findSquare(int x, int y) {
        // returns the coordinates of the square clicked given clicked mouse coordinates
        // works in an opposing fashion to coordinateFormula
        int[] res = new int[2];
        res[0] = (x-25)/100;
        res[1] = (y-25)/100;
        return res;
    }
    private boolean setPiece(int x, int y, boolean toMove) {
        // find whether or not user selected piece matches their colour
        try {
            Piece piece = p.isOccupied(pieces, new Location(x, y));
            if (piece.colour.equals(turn)) {
                curPiece = piece;
                curPiece.setMoves(pieces, curPiece, king, true);
                if (curPiece.type.equals("pawn") && enPassant != -1 && curPiece.y == 3) {
                    curPiece.addMove(pieces, king, new Location(enPassant, 2));
                }
                return true;
            }
        } catch (NullPointerException e) {e.getMessage();}
        return false;
    }

    private void checkCastled(int x) {
        // checks to see if king has castled
        if (curPiece.getType().equals("king")) {
            Piece rookToTransfer;
            // RIGHT SIDE CASTLE
            if (curPiece.x - x == -2) {
                rookToTransfer = curPiece.isOccupied(pieces, new Location(7, 7));
                rookToTransfer.x = x - 1;
            }
            // LEFT SIDE CASTLE
            if (curPiece.x - x == 2) {
                rookToTransfer = curPiece.isOccupied(pieces, new Location(0, 7));
                rookToTransfer.x = x + 1;
            }
        }
    }
    private void checkEnPassant(int x, int y) {
        // checks if a pawn can enPassant another pawn
        if (enPassant == x) {
            Location location1 = new Location(x, y);
            Location location2 = new Location(curPiece.x, curPiece.y);
            double distance = location1.distance(location1, location2);
            if (distance > 1.0 && distance < 2.0) {
                Piece enPassantPiece = p.isOccupied(pieces, new Location(enPassant, 3));
                if (enPassantPiece != null & enPassantPiece.type.equals("pawn")) {
                    pieces.remove(enPassantPiece);
                }
            }
            enPassant = -1;
        }
        if (curPiece.type.equals("pawn")) {
            if (curPiece.y - y == 2) {      // a pawn that moves 2 squares can be enpassanted
                enPassant = curPiece.x;
                // flip enPassant col value
                int xDifference = Math.abs(3 - enPassant);
                if (enPassant <= 3) {enPassant = 3 + xDifference;}
                else {enPassant = 4 - xDifference;}
            }
        } else {enPassant = -1;}
    }

    private void checkPromotion(int y) {
        // checks if pawn reached the other side to get promoted
        if (curPiece.type.equals("pawn") && curPiece.y == 0) {
            curPiece.type = "queen";
            if (curPiece.colour.equals("white")) {curPiece.setFileString(whiteQueenFile);}
            else {curPiece.setFileString(blackQueenFile);}
        }
    }

    private void checkConditions(ArrayList<Piece> pieces, String turn) {
        // evaluate whether the opposing king is in checkmated/stalemate
        if (king.checkMate(pieces, turn)) {
            String otherColour = turn.equals("white") ? "black": "white";
            System.out.println("Checkmate for " + otherColour);
        }
        if (king.staleMate(pieces, turn)) {System.out.println("Stalemate");}
    }
    private void flipBoard() {
        // flip along vertical and horizontal axis
        for (Piece piece: pieces) {
            int xDifference = Math.abs(3 - piece.x);
            if (piece.x <= 3) {piece.x = 4 + xDifference;}
            else {piece.x = 4 - xDifference;}
            int yDifference = Math.abs(3 - piece.y);
            if (piece.y <= 3) {piece.y = 4 + yDifference;}
            else {piece.y = 4 - yDifference;}
        }
    }
    private void drawBoard() throws FileNotFoundException {
        for (Piece piece: pieces) {
            setImage(piece.getFileString(), piece.getX(), piece.getY());
        }
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        launch();
    }
}

