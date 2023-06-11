package com.example.chessgameproject;
import java.util.ArrayList;

public class Piece extends Location {
    protected boolean alive;
    protected String colour;
    protected ArrayList<Location> moves;
    protected String filename;
    protected String type;
    protected boolean check;
    protected boolean hasMoved;

    public Piece(int x, int y, String colour, String type, String filename) {
        super(x, y);    // set location attributes
        this.colour = colour;
        this.alive = true;
        this.type = type;

        if (type.equals("rook")) {
            this.hasMoved = false;
        }
        this.filename = filename;
        this.moves = new ArrayList<Location>();
    }

    // getters and setters:
    public String getFileString() {
        return this.filename;
    }
    public void setFileString(String filename) {
        this.filename = filename;
    }
    public String getType() {
        return type;
    }
    public ArrayList<Location> getMoves() {
        return moves;
    }
    //

    public void addMove(ArrayList<Piece> pieces, Piece king, Location location) {
        // to deal with enpassant move:
        int x = this.x;
        int y = this.y;
        this.x = location.x;
        this.y = location.y;
        if (!inCheck(pieces, king)) {
            this.moves.add(location);
        }
        this.x = x;
        this.y = y;
    }

    private ArrayList<Location> setMoves(ArrayList<Piece> pieces, Piece piece, Piece king, boolean lookForCheck, boolean flipped) {
        // function also checks if move is possible, but if type = "pawn" then its diagonals must be checked, too
        ArrayList<Location> res = new ArrayList<Location>();
        String otherColour = piece.colour.equals("white") ? "black": "white";
        Location downRight = new Location(x + 1, y + 1);
        if (isOccupied(pieces, downRight) != null) {
            helper(pieces, downRight, res, otherColour);
        }
        Location downLeft = new Location(x-1, y+1);
        if (isOccupied(pieces, downLeft) != null) {
            helper(pieces, downLeft, res, otherColour);
        }
        this.moves = res;
        return res;
    }

    public void setMoves(ArrayList<Piece> pieces, Piece piece, Piece king, boolean lookForCheck) {
        // find corresponding moves for a selected piece
        ArrayList<Location> res = new ArrayList<Location>();
        String otherColour = piece.colour.equals("white") ? "black" : "white";
        switch (piece.type) {
            case "pawn": // set pawn moves
                pawnMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            case "knight": // set knight moves
                knightMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            case "bishop": // set bishop moves
                bishopMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            case "rook": // set rook moves
                rookMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            case "queen": // set queen moves
                queenMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            case "king": // set king moves
                kingMoves(pieces, piece, king, lookForCheck, res, otherColour);
                break;
            default:
                System.out.println("something went horribly wrong :( (setMoves method in Piece.java file)");
                break;
        }
    }


    private ArrayList<Location> pawnMoves(ArrayList<Piece> pieces, Piece piece, Piece king, boolean lookForCheck,
                                          ArrayList<Location> res, String otherColour) {
        // up 1
        Location upOne = new Location(x, y - 1);
        if (y > 0 && isOccupied(pieces, upOne) == null) {
            res.add(upOne);
            // up 2 (starting position)
            Location upTwo = new Location(x, y - 2);
            if (y == 6 && isOccupied(pieces, upTwo) == null) {
                res.add(upTwo);
            }
        }
        // check diagonal(s)
        Location upRight = new Location(x + 1, y - 1);
        if (isOccupied(pieces, upRight) != null) {
            helper(pieces, upRight, res, otherColour);
        }
        Location upLeft = new Location(x - 1, y - 1);
        if (isOccupied(pieces, upLeft) != null) {
            helper(pieces, upLeft, res, otherColour);
        }

        if (lookForCheck) {
            res = findChecks(res, piece, king, pieces, x, y);
        }
        this.moves = res;
        return res;

    }

    private ArrayList<Location> knightMoves(ArrayList<Piece> pieces, Piece piece, Piece king, boolean lookForCheck,
                                            ArrayList<Location> res, String otherColour) {
        Location location;
        // up 1, left 2
        location = new Location(x - 2, y - 1);
        helper(pieces, location, res, otherColour);

        // up 2, left 1
        location = new Location(x - 1, y - 2);
        helper(pieces, location, res, otherColour);

        // up 1, right 2
        location = new Location(x + 2, y - 1);
        helper(pieces, location, res, otherColour);

        // up 2, right 1
        location = new Location(x + 1, y - 2);
        helper(pieces, location, res, otherColour);

        // down 1, left 2
        location = new Location(x - 2, y + 1);
        helper(pieces, location, res, otherColour);

        // down 2, left 1
        location = new Location(x - 1, y + 2);
        helper(pieces, location, res, otherColour);

        // down 1, right 2
        location = new Location(x + 2, y + 1);
        helper(pieces, location, res, otherColour);

        // down 2, right 1
        location = new Location(x + 1, y + 2);
        helper(pieces, location, res, otherColour);

        if (lookForCheck) {
            res = findChecks(res, piece, king, pieces, x, y);
        }

        this.moves = res;
        return res;
    }

    private ArrayList<Location> bishopMoves(ArrayList<Piece> pieces, Piece piece, Piece king,
                                            boolean lookForCheck, ArrayList<Location> res, String otherColour) {
        boolean upRight, upLeft, downRight, downLeft;
        upRight = upLeft = downRight = downLeft = true;
        Location location;
        for (int i = 1; i <= 7; i++) {
            // up and right
            if (upRight) {
                location = new Location(x + i, y - i);
                if (!helper(pieces, location, res, otherColour)) {
                    upRight = false;
                }
            }
            // up and left
            if (upLeft) {
                location = new Location(x - i, y - i);
                if (!helper(pieces, location, res, otherColour)) {
                    upLeft = false;
                }
            }
            // down and right
            if (downRight) {
                location = new Location(x + i, y + i);
                if (!helper(pieces, location, res, otherColour)) {
                    downRight = false;
                }
            }
            // down and left
            if (downLeft) {
                location = new Location(x - i, y + i);
                if (!helper(pieces, location, res, otherColour)) {
                    downLeft = false;
                }
            }
        }
        if (lookForCheck) {
            res = findChecks(res, piece, king, pieces, x, y);
        }
        this.moves = res;
        return res;
    }

    private ArrayList<Location> rookMoves(ArrayList<Piece> pieces, Piece piece, Piece king, boolean lookForCheck,
                                         ArrayList<Location> res, String otherColour) {
        Location location;
        boolean up, down, left, right;
        up = down = left = right = true;
        for (int i = 1; i <= 7; i++) {
            // up
            if (up) {
                location = new Location(x, y - i);
                if (!helper(pieces, location, res, otherColour)) {
                    up = false;
                }
            }
            // down
            if (down) {
                location = new Location(x, y + i);
                if (!helper(pieces, location, res, otherColour)) {
                    down = false;
                }
            }
            // right
            if (right) {
                location = new Location(x + i, y);
                if (!helper(pieces, location, res, otherColour)) {
                    right = false;
                }
            }
            // left
            if (left) {
                location = new Location(x - i, y);
                if (!helper(pieces, location, res, otherColour)) {
                    left = false;
                }
            }
        }
        if (lookForCheck) {
            res = findChecks(res, piece, king, pieces, x, y);
        }
        this.moves = res;
        return res;
    }

    private ArrayList<Location> queenMoves(ArrayList<Piece> pieces, Piece piece, Piece king,
                                           boolean lookForCheck, ArrayList<Location> res, String otherColour) {
        bishopMoves(pieces, piece, king, lookForCheck, res, otherColour);
        rookMoves(pieces, piece, king, lookForCheck, res, otherColour);
        this.moves = res;
        return res;
    }

    private ArrayList<Location> kingMoves(ArrayList<Piece> pieces, Piece piece, Piece king,
                                          boolean lookForCheck, ArrayList<Location> res, String otherColour) {
        // limit queenMoves to 1 square
        queenMoves(pieces, piece, king, lookForCheck, res, otherColour);
        ArrayList<Location> toRemove = new ArrayList<Location>();
        for (Location location : res) {
            if (distance(location, new Location(this.x, this.y)) >= 1.5) {      // 1 move diagonally = distance: sqrt(2)
                toRemove.add(location);
            }
        }
        for (Location loc : toRemove) {
            res.remove(loc);
        }
        if (lookForCheck) {
            res = findChecks(res, piece, king, pieces, x, y);
            res = findCastleMoves(pieces, king, res);
        }
        this.moves = res;
        return res;
    }

    private ArrayList<Location> findCastleMoves(ArrayList<Piece> pieces, Piece king, ArrayList<Location> res) {
        int x = this.x;
        int y = this.y;
        if (!king.hasMoved) {
            boolean rightSideCastle = true;
            boolean leftSideCastle = true;
            for (int i = 1; i <= 2; i++) {
                if (isOccupied(pieces, new Location(x + i, this.y)) == null) {
                    this.x = x + i;      // king side
                    if (inCheck(pieces, king)) {
                        rightSideCastle = false;
                    }
                } else {
                    rightSideCastle = false;
                }

                if (isOccupied(pieces, new Location(x - i, this.y)) == null) {
                    this.x = x - i;     // queen side
                    if (inCheck(pieces, king)) {
                        leftSideCastle = false;
                    }
                } else {
                    leftSideCastle = false;
                }
            }
            // check right
            try {
                Piece rightPiece = isOccupied(pieces, new Location(this.x + 1, y));
                if (!(rightPiece.type.equals("rook") && !rightPiece.hasMoved)) {
                    rightSideCastle = false;
                }
            } catch (NullPointerException e) {
                e.getMessage();
            }

            // check left
            try {
                Piece leftPiece = isOccupied(pieces, new Location(this.x - 1, y));
                if (!(leftPiece.type.equals("rook") && !leftPiece.hasMoved)) {
                    leftSideCastle = false;
                }
            } catch (NullPointerException e) {
                e.getMessage();
            }

            this.x = x;
            this.y = y;

            if (rightSideCastle) {
                res.add(new Location(this.x + 2, y));
            }
            if (leftSideCastle) {
                res.add(new Location(this.x - 2, y));
            }
        }
        return res;
    }

    private boolean helper(ArrayList<Piece> pieces, Location location, ArrayList<Location> res, String otherColour) {
        // this helper function checks to see if a corresponding move of a piece is valid or not
        Piece occupiedPiece;

        if (inBounds(location)) {
            occupiedPiece = isOccupied(pieces, location);
            if (occupiedPiece == null) {
                res.add(location);
                return true;
            } else if (occupiedPiece.colour.equals(otherColour)) {
                res.add(location);
                return false;
            }
        }
        return false;
    }

    public Piece isOccupied(ArrayList<Piece> pieces, Location location) {
        // checks if a square is currently occupied by a piece and returns that piece
        for (Piece piece : pieces) {
            if (piece.x == location.x && piece.y == location.y) {
                return piece;
            }
        }
        return null;
    }

    public ArrayList<Location> findChecks(ArrayList<Location> res, Piece piece, Piece king, ArrayList<Piece> pieces, int x, int y) {
        // check all the king's possible moves and if there is a check, remove that location from possible moves
        ArrayList<Location> toRemove = new ArrayList<Location>();
        for (Location location : res) {
            Piece occupiedPiece = isOccupied(pieces, location);
            if (occupiedPiece != null) {
                occupiedPiece.alive = false;
            }

            piece.x = location.x;
            piece.y = location.y;

            if (inCheck(pieces, king)) {
                toRemove.add(location);
            }
            try {
                occupiedPiece.alive = true;
            } catch (NullPointerException e) {
            }
        }
        for (Location location:toRemove) {res.remove(location);}
        piece.x =x;
        piece.y =y;
        return res;
    }
    public boolean inCheck(ArrayList<Piece> pieces, Piece king) {
        // check if a move causes check, done by seeing all opponents moves for all pieces
        String otherColour = king.colour.equals("white") ? "black": "white";
        for (Piece piece: pieces) {
            if (piece.colour.equals(otherColour) && piece.alive) {
                if (piece.type.equals("pawn")) {piece.setMoves(pieces, piece, null, false, true);}
                else {piece.setMoves(pieces, piece, null, false);}
                for (Location location: piece.getMoves()) {
                    if (king.x == location.x && king.y == location.y) {return true;}
                }
            }
        }
        return false;
    }

    public boolean checkMate(ArrayList<Piece> pieces, String turn) {
        // checks if there are no more moves for all the pieces, if so, the king is in checkmate; opponent wins
        int counter = 0;
        for (Piece piece: pieces) {
            if (piece.colour.equals(turn)) {
                piece.setMoves(pieces, piece, this, true);
                counter = counter + piece.getMoves().size();
            }
        }
        return counter == 0 && this.check;
    }

    public boolean staleMate(ArrayList<Piece> pieces, String turn) {
        // checks if there is a stalemate
        int counter = 0;
        for (Piece piece: pieces) {
            if (piece.colour.equals(turn)) {
                piece.setMoves(pieces, piece, this, true);
                counter = counter + piece.getMoves().size();
            }
        }
        return counter == 0 && !this.check;
    }
            }
