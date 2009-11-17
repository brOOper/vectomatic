package com.alonsoruibal.chess;

import com.alonsoruibal.chess.bitboard.BitboardAttacks;
import com.alonsoruibal.chess.bitboard.BitboardUtils;
import com.alonsoruibal.chess.hash.ZobristKey;
import com.alonsoruibal.chess.log.Logger;
import com.alonsoruibal.chess.movegen.LegalMoveGenerator;
import com.alonsoruibal.chess.movegen.MoveGenerator;

/**
 * 
 * @author Alberto Alonso Ruibal
 */
public class Board {
	private static final Logger logger = Logger.getLogger("Board");

	public static final int MAX_MOVES = 1024;
	
	// Bitboard arrays
	public long whites        = 0;
	public long blacks        = 0;
	public long pawns         = 0;
	public long rooks         = 0;
	public long queens        = 0;
	public long bishops       = 0;
	public long knights       = 0;
	public long kings         = 0;
	public long flags         = 0;
	
	public int fiftyMovesRule = 0;
	public int moveNumber     = 0;
	public long key[]           = {0, 0};
	
	// History array indexed by moveNumber
	public long keyHistory[][]; // to detect draw by treefold
	public int moveHistory[];
	public long whitesHistory[];
	public long blacksHistory[];
	public long pawnsHistory[];
	public long rooksHistory[];
	public long queensHistory[];
	public long bishopsHistory[];
	public long knightsHistory[];
	public long kingsHistory[];
	public long flagsHistory[];
	public int fiftyMovesRuleHistory[];

	// Flags: must be changed only when Moving!!!
	private static final long FLAG_TURN                             = 0x0001L;
	private static final long FLAG_WHITE_DISABLE_KINGSIDE_CASTLING  = 0x0002L;
	private static final long FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING = 0x0004L;
	private static final long FLAG_BLACK_DISABLE_KINGSIDE_CASTLING  = 0x0008L;
	private static final long FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING = 0x0010L;
	private static final long FLAG_CHECK							= 0x0020L;
	private static final long FLAGS_PASSANT                         = 0x0000ff0000ff0000L; // Position on boarch in which is captured
	
	/**
	 * Also computes zobrish key
	 */
	public void startPosition() {
		setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 0");
	}
	
	public long getKey() {
		return key[0] ^ key[1];
	}
	
	/**
	 * An alternative key to avoid collisions in tt
	 * @return
	 */
	public long getKey2() {
		return key[0] ^ ~key[1];
	}
	
	public int getMoveNumber() {
		return moveNumber;
	}

	/**
	 * 
	 * @return true if white moves
	 */
	public final boolean getTurn() {
		return (flags & FLAG_TURN) == 0;
	}
	public boolean getWhiteKingsideCastling() {
		return (flags & FLAG_WHITE_DISABLE_KINGSIDE_CASTLING) == 0;
	}
	public boolean getWhiteQueensideCastling() {
		return (flags & FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING) == 0;
	}
	public boolean getBlackKingsideCastling() {
		return (flags & FLAG_BLACK_DISABLE_KINGSIDE_CASTLING) == 0;
	}
	public boolean getBlackQueensideCastling() {
		return (flags & FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING) == 0;
	}
	public long getPassantSquare() {
		return flags & FLAGS_PASSANT;
	}
	public boolean getCheck() {
		return (flags & FLAG_CHECK) != 0;
	}
	
	public long getAll() {
		return whites | blacks;
	}
	
	public long getMines() {
		return (flags & FLAG_TURN) == 0 ? whites : blacks;
	}
	
	public long getOthers() {
		return (flags & FLAG_TURN) == 0 ? blacks : whites;
	}

	public char pieceAt(long square) {
		char p = ((pawns   & square) != 0 ? 'p' :
			 ((queens  & square) != 0 ? 'q' : 
			 ((rooks   & square) != 0 ? 'r' :
			 ((bishops & square) != 0 ? 'b' : 
			 ((knights & square) != 0 ? 'n' : 
			 ((kings   & square) != 0 ? 'k' : '.'))))));
		return ((whites & square) != 0 ? Character.toUpperCase(p) : p);
	}

	/**
	 * Converts board to its fen notation
	 */
	public String getFen() {
		StringBuffer sb = new StringBuffer();
		long i = BitboardUtils.A8;
		int j = 0;
		while (i != 0) {
			char p = pieceAt(i);
			if (p=='.')	j++;			
			if ((j != 0) && (p!='.' || ((i & BitboardUtils.b_r) != 0))) {
				sb.append(j);
				j = 0;
			}
			if (p!='.')	sb.append(p);			
			if ((i != 1) && (i & BitboardUtils.b_r) != 0) sb.append("/");
			i >>>= 1;
		}
		sb.append(" ");
		sb.append((getTurn() ? "w" : "b"));
		sb.append(" ");
		if (getWhiteQueensideCastling()) sb.append("Q");
		if (getWhiteKingsideCastling()) sb.append("K");
		if (getBlackQueensideCastling()) sb.append("q");
		if (getBlackKingsideCastling()) sb.append("k");
		if (!getWhiteQueensideCastling() && !getWhiteKingsideCastling() &&
			!getBlackQueensideCastling() && !getBlackKingsideCastling()) sb.append("-");
		sb.append(" ");
		sb.append((getPassantSquare() != 0 ? BitboardUtils.square2Algebraic(getPassantSquare()) : "-"));
		sb.append(" ");
		sb.append(fiftyMovesRule);
		sb.append(" ");
		sb.append(moveNumber);
		return sb.toString();
	}

	/** 
	 * loads board from a fen notation
	 * @param fen
	 */
	public void setFen(String fen) {
		moveNumber = 0;
		fiftyMovesRule = 0;
		
		int i = 0;
		long j = BitboardUtils.A8;
		//StringTokenizer st = new StringTokenizer(fen);
		String[] tokens = fen.split("[ \\t\\n\\x0B\\f\\r]+");
		String board = tokens[0];
		while ((i < board.length()) && (j != 0)) {
			char p = board.charAt(i++);
			if (p != '/') {
				int number = 0;
				try {
					number = Integer.parseInt(String.valueOf(p));
				} catch (Exception e) {}
				
				for (int k = 0; k < (number == 0 ? 1 : number); k++) {
					whites  = (whites  & ~j) | ((number == 0) && (p == Character.toUpperCase(p)) ? j : 0);
					blacks  = (blacks  & ~j) | ((number == 0) && (p == Character.toLowerCase(p)) ? j : 0);
					pawns   = (pawns   & ~j) | (Character.toUpperCase(p) == 'P' ? j : 0);
					rooks   = (rooks   & ~j) | (Character.toUpperCase(p) == 'R' ? j : 0);
					queens  = (queens  & ~j) | (Character.toUpperCase(p) == 'Q' ? j : 0);
					bishops = (bishops & ~j) | (Character.toUpperCase(p) == 'B' ? j : 0);
					knights = (knights & ~j) | (Character.toUpperCase(p) == 'N' ? j : 0);
					kings   = (kings   & ~j) | (Character.toUpperCase(p) == 'K' ? j : 0);
					j >>>= 1;
					if (j==0) break; // security 
				}
			}
		}
		// Now the rest ...
		String turn = tokens[1];
		flags = FLAG_WHITE_DISABLE_KINGSIDE_CASTLING | FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING |
		    FLAG_BLACK_DISABLE_KINGSIDE_CASTLING | FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING;
		if ("b".equals(turn)) flags |= FLAG_TURN;
		if (tokens.length > 2) {
			String promotions = tokens[2];
			if (promotions.indexOf("K")>=0) flags &= ~FLAG_WHITE_DISABLE_KINGSIDE_CASTLING;
			if (promotions.indexOf("Q")>=0) flags &= ~FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING;
			if (promotions.indexOf("k")>=0) flags &= ~FLAG_BLACK_DISABLE_KINGSIDE_CASTLING;
			if (promotions.indexOf("q")>=0) flags &= ~FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING;
			if (tokens.length > 3) {
				String passant = tokens[3];
				flags |= FLAGS_PASSANT & BitboardUtils.algebraic2Square(passant);
				if (tokens.length > 4) {
					String fiftyRuleString = tokens[4];
					fiftyMovesRule = Integer.valueOf(fiftyRuleString);		
					if (tokens.length > 5) {
						String moveNumberString = tokens[5];
						moveNumber = Integer.valueOf(moveNumberString);
					}
				}
			}
		}
		// Finally set zobrish key and check flags
		key = ZobristKey.getKey(this);
		setCheckFlags(getTurn());
		// and save history
		resetHistory();
		saveHistory(0);
	}
	
	/**
	 * Prints board in one string
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int j = 8;
		long i = BitboardUtils.A8;
		while (i != 0) { 
			sb.append(pieceAt(i));
			sb.append(" ");
			if ((i & BitboardUtils.b_r) != 0) {
				sb.append(j--);
				sb.append("\n");
			}
			i >>>= 1;
		}
		sb.append("a b c d e f g h  ");
		sb.append((getTurn() ? "white move\n" : "blacks move\n"));
//		sb.append(" " +getWhiteKingsideCastling()+getWhiteQueensideCastling()+getBlackKingsideCastling()+getBlackQueensideCastling());

		return sb.toString();
	}

	private void resetHistory() {
		whitesHistory  = new long[MAX_MOVES];
		blacksHistory  = new long[MAX_MOVES];
		pawnsHistory   = new long[MAX_MOVES];
		knightsHistory = new long[MAX_MOVES];
		bishopsHistory = new long[MAX_MOVES];
		rooksHistory   = new long[MAX_MOVES];
		queensHistory  = new long[MAX_MOVES];
		kingsHistory   = new long[MAX_MOVES];
		flagsHistory   = new long[MAX_MOVES];
		keyHistory     = new long[MAX_MOVES][2];
		fiftyMovesRuleHistory = new int[MAX_MOVES];
		moveHistory    = new int[MAX_MOVES];
	}
	
	private void saveHistory(int move) {
		moveHistory[moveNumber]    = move;
		whitesHistory[moveNumber]  = whites;
		blacksHistory[moveNumber]  = blacks;
		pawnsHistory[moveNumber]   = pawns;
		knightsHistory[moveNumber] = knights;
		bishopsHistory[moveNumber] = bishops;
		rooksHistory[moveNumber]   = rooks;
		queensHistory[moveNumber]  = queens;
		kingsHistory[moveNumber]   = kings;
		flagsHistory[moveNumber]   = flags;
		keyHistory[moveNumber][0]  = key[0];
		keyHistory[moveNumber][1]  = key[1];
		fiftyMovesRuleHistory[moveNumber] = fiftyMovesRule; 
	}
	
	public int[] getMoveHistory() {
		return moveHistory;
	}
	
	public int getLastMove() {
		if (moveNumber == 0) return 0;
		return moveHistory[moveNumber-1];
	}
	
	/**
	 * Moves and also updates the board's zobrish key
	 * verify legality, if not legal undo move and return false
	 * 0 is the null move
	 * 
	 * @param move
	 * @return
	 */
	public boolean doMove(int move) {
//		logger.debug("Before move: \n" + toString() + "\n " + Move.toStringExt(move));
		
		if (move == -1) return false;
		// Save history
		saveHistory(move);
		
		long from = Move.getFromSquare(move);
		long to = Move.getToSquare(move);
		int fromIndex = Move.getFromIndex(move);
		int toIndex = Move.getToIndex(move);
		int moveType = Move.getMoveType(move);
		int pieceMoved = Move.getPieceMoved(move);
		boolean capture = Move.getCapture(move);
		boolean turn = getTurn();
		int color = (turn ? 0 : 1);

		fiftyMovesRule++; // Count consecutive moves without capture or without pawn move
		moveNumber++; // Count Ply moves

		if (move != 0) {
		
		if ((from & getMines()) == 0) {
			logger.error("Origin square not valid");
			logger.debug("\n" + toString());
			logger.debug("Move = " + Move.toStringExt(move));
			Move.printMoves(moveHistory, 0, moveNumber);
			throw new IllegalArgumentException("Origin square not valid:" + Move.toStringExt(move));
		}
		
		long moveMask = from | to; // Move is as easy as xor with this mask (exceptions are in captures, promotionsans passant captures)
		
		if ((flags & FLAGS_PASSANT) != 0) { // Remove passant flags
			key[1-color] ^= ZobristKey.passantFile[BitboardUtils.getFile(flags & FLAGS_PASSANT)];
		}
		
		// Is it is a capture, remove pieces in destination square
		if (capture) {
			fiftyMovesRule = 0;
			// Passant Pawn captures remove captured pawn, put the pawn in to
			int toIndexCapture = toIndex;
			if (moveType == Move.TYPE_PASSANT) {
				to = (getTurn() ? (to >>> 8) : (to << 8));
				toIndexCapture += (getTurn() ? -8 : 8);
			}
			key[1-color] ^= ZobristKey.getKeyPieceIndex(toIndexCapture, pieceAt(to));

			whites  &= ~to;
			blacks  &= ~to;
			pawns   &= ~to;
			queens  &= ~to;
			rooks   &= ~to;
			bishops &= ~to;
			knights &= ~to;
		}
		// Remove passant flags
		flags &= ~FLAGS_PASSANT;
		
		// Pawn movements
		switch (pieceMoved) {
		case Move.PAWN:
			fiftyMovesRule = 0;
			// Set new passant flags if pawn is advancing two squares (marks the destination square where the pawn can be captured)
			// Set only passant flags when the other side can capture
			if (((from<<16) & to) != 0 && (BitboardAttacks.pawnUpwards[toIndex - 8] & pawns & getOthers()) != 0) { // white
				flags |= (from<<8);	
			}
			if (((from>>>16) & to) != 0 && (BitboardAttacks.pawnDownwards[toIndex + 8] & pawns & getOthers()) != 0) { // blask
				flags |= (from>>>8);
			}
			if ((flags & FLAGS_PASSANT) != 0) {
				key[color] ^= ZobristKey.passantFile[BitboardUtils.getFile(flags & FLAGS_PASSANT)];	
			}
			
			if (moveType == Move.TYPE_PROMOTION_QUEEN ||
				moveType == Move.TYPE_PROMOTION_KNIGHT ||
				moveType == Move.TYPE_PROMOTION_BISHOP ||
				moveType == Move.TYPE_PROMOTION_ROOK) { // Promotions: change the piece
				pawns &= ~from;
				key[color] ^= ZobristKey.pawn[color][fromIndex];
				switch(moveType) {
				case Move.TYPE_PROMOTION_QUEEN:
					queens |= to; 
					key[color] ^= ZobristKey.queen[color][toIndex];
					break;
				case Move.TYPE_PROMOTION_KNIGHT:
					knights |= to; 
					key[color] ^= ZobristKey.knight[color][toIndex];
					break;
				case Move.TYPE_PROMOTION_BISHOP:
					bishops |= to;
					key[color] ^= ZobristKey.bishop[color][toIndex];
					break;
				case Move.TYPE_PROMOTION_ROOK:
					rooks |= to;
					key[color] ^= ZobristKey.rook[color][toIndex];
					break;
				}
			} else {
				pawns ^= moveMask;
				key[color] ^= ZobristKey.pawn[color][fromIndex] ^ ZobristKey.pawn[color][toIndex];
			}
			break;
		case Move.ROOK:
			rooks ^= moveMask;
			key[color] ^= ZobristKey.rook[color][fromIndex] ^ ZobristKey.rook[color][toIndex];
			break;
		case Move.BISHOP:
			bishops ^= moveMask;
			key[color] ^= ZobristKey.bishop[color][fromIndex] ^ ZobristKey.bishop[color][toIndex];
			break;
		case Move.KNIGHT:
			knights ^= moveMask;
			key[color] ^= ZobristKey.knight[color][fromIndex] ^ ZobristKey.knight[color][toIndex];
			break;
		case Move.QUEEN:
			queens ^= moveMask;
			key[color] ^= ZobristKey.queen[color][fromIndex] ^ ZobristKey.queen[color][toIndex];
			break;
		case Move.KING:			// if castling, moves rooks too
			long rookMask = 0;
			switch (moveType) {
			case Move.TYPE_KINGSIDE_CASTLING:
				rookMask = (getTurn() ? 0x05L : 0x0500000000000000L);
				key[color] ^= ZobristKey.rook[color][toIndex-1] ^ ZobristKey.rook[color][toIndex+1];
				break;
			case Move.TYPE_QUEENSIDE_CASTLING:
				rookMask = (getTurn() ? 0x90L : 0x9000000000000000L);
				key[color] ^= ZobristKey.rook[color][toIndex-1] ^ ZobristKey.rook[color][toIndex+2];
				break;
			}
			if (rookMask != 0) {
				if (getTurn()) whites ^= rookMask; else blacks ^= rookMask;
				rooks ^= rookMask;
			}
			kings ^= moveMask;
			key[color] ^= ZobristKey.king[color][fromIndex] ^ ZobristKey.king[color][toIndex];
		}
		// Move pieces in colour fields
		if (getTurn()) whites ^= moveMask; else blacks ^= moveMask;
		
		// Tests to disable castling
		if ((moveMask & 0x0000000000000009L) != 0 && (flags & FLAG_WHITE_DISABLE_KINGSIDE_CASTLING) == 0) {
			flags |= FLAG_WHITE_DISABLE_KINGSIDE_CASTLING;
			key[0] ^= ZobristKey.whiteKingSideCastling;
		}
		if ((moveMask & 0x0000000000000088L) != 0 && (flags & FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING) == 0) {
			flags |= FLAG_WHITE_DISABLE_QUEENSIDE_CASTLING;
			key[0] ^= ZobristKey.whiteQueenSideCastling;
		}
		if ((moveMask & 0x0900000000000000L) != 0 && (flags & FLAG_BLACK_DISABLE_KINGSIDE_CASTLING) == 0) {
			flags |= FLAG_BLACK_DISABLE_KINGSIDE_CASTLING;
			key[1] ^= ZobristKey.blackKingSideCastling;
		}
		if ((moveMask & 0x8800000000000000L) != 0 && (flags & FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING) == 0) {
			flags |= FLAG_BLACK_DISABLE_QUEENSIDE_CASTLING;
			key[1] ^= ZobristKey.blackQueenSideCastling;
		}
		}
		// Change turn
		flags ^= FLAG_TURN;
		key[0] ^= ZobristKey.whiteMove;
		
		// TODO remove
//		long aux[] = ZobristKey.getKey(this);
//		if (key[0] != aux[0] || key[1] != aux[1]) {
//			System.out.println("Zobrist key Error");
//			logger.debug("\n" + toString());
//			logger.debug("Move = " + Move.toStringExt(move));
//			Move.printMoves(moveHistory, 0, moveNumber);
//			System.exit(-1);
//			key = aux;
//		}
		
		if (isValid(!turn)) {
			setCheckFlags(!turn);
			return true;
		} else {
			undoMove();
			return false;
		}			
	}
	
	/**
	 * Checks is a state is valid
	 * Basically, not entering own king in check 
	 */
	private boolean isValid(boolean turn) {
		return (!BitboardAttacks.isSquareAttacked(this, kings & getOthers(), !turn));
	}
	
	private void setCheckFlags(boolean turn) {
		// Set check flags
		if (BitboardAttacks.isSquareAttacked(this, kings & getMines(), turn)) {
			flags |= FLAG_CHECK;
		} else {
			flags &= ~FLAG_CHECK;
		}
	}
	
	public void undoMove() {
		undoMove(moveNumber - 1);
	}
	
	/**
	 * 
	 */
	public void undoMove(int moveNumber) {
		if (moveNumber < 0) return;
		this.moveNumber = moveNumber;
		
		whites  = whitesHistory[moveNumber];
		blacks  = blacksHistory[moveNumber];
		pawns   = pawnsHistory[moveNumber];
		knights = knightsHistory[moveNumber];
		bishops = bishopsHistory[moveNumber];
		rooks   = rooksHistory[moveNumber];
		queens  = queensHistory[moveNumber];
		kings   = kingsHistory[moveNumber];
		flags   = flagsHistory[moveNumber];
		key[0]  = keyHistory[moveNumber][0];
		key[1]  = keyHistory[moveNumber][1];
		fiftyMovesRule = fiftyMovesRuleHistory[moveNumber];
	}

	/**
	 * 0 no, 1 whites won, -1 blacks won, 99 draw
	 * 
	 */
	public int isEndGame() {
		int endGame = 0;
		MoveGenerator legalMoveGenerator = new LegalMoveGenerator();
		int moves[] = new int[256];
		if (legalMoveGenerator.generateMoves(this, moves, 0) == 0) {
			if (getCheck()) endGame = (getTurn() ? -1 : 1);
			else endGame = 99;
		} else if (isDraw()) {
			endGame = 99;
		}
		return endGame;
	}	

	public boolean isMate() {
		int endgameState = isEndGame();
		return endgameState == 1 || endgameState == -1;
	}
	
	/**
	 * checks draw by fiftymoves rule and threefold repetition
	 */
	public boolean isDraw() {
		boolean draw = false;
		if (fiftyMovesRule >= 50) draw = true;
		int repetitions = 0;
		for (int i = 0; i< (moveNumber -1); i++) {
			if (keyHistory[i][0] == key[0] &&
				keyHistory[i][1] == key[1]) repetitions++;
		}
		if (repetitions >= 3) draw = true;
		return draw;
	}

	
//	long getLeastValuablePiece (long attadef, enumColor bySide, enumPiece &piece)
//	{
//	   for (piece = nWhitePawn + bySide; piece <= nWhiteKing + bySide; piece += 2) {
//	      U64 subset = attadef & pieceBB[piece];
//	      if ( subset )
//	         return subset & -subset; // single bit
//	   }
//	   return 0; // empty set
//	}
//	
//	public int see( int toIndex, int target, int frSq, int aPiece)
//	{
//	   int gain[] = new int[32];
//	   int d = 0;
//	   long mayXray = pawns | bishops | rooks | queens;
//	   long fromSet = 1 << frSq;
//	   long occ     = getAll();
//	   long attadef = BitboardAttacks.getIndexAttacks(this, toIndex);
//	   gain[d]      = value[target];
//	   do {
//	      d++; // next depth and side
//	      gain[d]  = value[aPiece] - gain[d-1]; // speculative store, if defended
//	      attadef ^= fromSet; // reset bit in set to traverse
//	      occ     ^= fromSet; // reset bit in temporary occupancy (for x-Rays)
//	      if ( (fromSet & mayXray) != 0)
//	         attadef |= considerXrays(occ, ..);
//	      fromSet  = getLeastValuablePiece (attadef, d & 1, aPiece);
//	   } while (fromSet != 0);
//	   while (--d != 0)
//	      gain[d-1]= -Math.max (-gain[d-1], gain[d]);
//	   return gain[0];
//	}
	
}