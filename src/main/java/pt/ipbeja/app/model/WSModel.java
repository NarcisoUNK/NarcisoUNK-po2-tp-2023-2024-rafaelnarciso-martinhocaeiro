    package pt.ipbeja.app.model;

    import javafx.scene.control.Button;

    import java.io.*;
    import java.util.*;

    public class WSModel {

        private final List<List<String>> lettersGrid;
        private final List<List<Button>> buttonGrid;
        private WSView wsView;
        private static final int BOARD_SIZE = 10; // Tamanho do tabuleiro
        private final List<String> words = new ArrayList<>(); // List to store words from file
        private final Set<String> foundWords = new HashSet<>(); // Set to store found words
        private final Map<Character, Integer> letterScores = new HashMap<>();

        public WSModel(String filePath) {
            this.lettersGrid = new ArrayList<>();
            this.buttonGrid = new ArrayList<>(); // Initialize button grid
            initializeGrid();
            initializeButtonGrid(); // Initialize button grid
            readWordsFromFile(filePath);
            fillRemainingPositionsRandomly();
            initializeLetterScores();
        }

        private void initializeLetterScores() {
            // Assign scores to each letter
            letterScores.put('A', 1);
            letterScores.put('B', 2);
            // Add more letters and their scores as needed
        }

        private int calculateWordScore(String word) {
            int score = 0;
            for (char letter : word.toCharArray()) {
                // Look up the letter's score in the map
                score += letterScores.getOrDefault(Character.toUpperCase(letter), 0);
            }
            return score;
        }

        private void initializeGrid() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                List<String> row = new ArrayList<>();
                for (int j = 0; j < BOARD_SIZE; j++) {
                    row.add(null); // Inicializa o tabuleiro com espaços vazios
                }
                lettersGrid.add(row);
            }
        }

        private void initializeButtonGrid() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                List<Button> row = new ArrayList<>();
                for (int j = 0; j < BOARD_SIZE; j++) {
                    Button button = new Button();
                    row.add(button); // Add new Button to each position
                }
                buttonGrid.add(row);
            }
        }

        private void readWordsFromFile(String filePath) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }
                System.out.println(words);
                Collections.shuffle(words); // Embaralha as palavras
                distributeWordsOnBoard(words);
                fillRemainingPositionsRandomly();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void distributeWordsOnBoard(List<String> words) {
            Random random = new Random();
            for (String word : words) {
                placeWordOnBoard(word, random);
            }
        }

        private boolean canPlaceWordAtPosition(String word, int startX, int startY, boolean horizontal) {
            int wordLength = word.length();
            if ((horizontal && startX + wordLength <= BOARD_SIZE) || (!horizontal && startY + wordLength <= BOARD_SIZE)) {
                for (int j = 0; j < wordLength; j++) {
                    int row = horizontal ? startY : startY + j;
                    int col = horizontal ? startX + j : startX;
                    if (row >= BOARD_SIZE || col >= BOARD_SIZE || lettersGrid.get(row).get(col) != null) {
                        return false; // Há sobreposição com outra palavra ou excedeu os limites do tabuleiro
                    }
                }
                return true; // Não há sobreposição, a palavra pode ser colocada nesta posição
            }
            return false; // A palavra não cabe na posição atual do tabuleiro
        }

        private void placeWordOnBoard(String word, Random random) {
            boolean placed = false;
            while (!placed) {
                boolean horizontal = random.nextBoolean(); // Determina aleatoriamente a orientação da palavra
                int startX = random.nextInt(BOARD_SIZE); // Início da distribuição de palavras na horizontal
                int startY = random.nextInt(BOARD_SIZE); // Início da distribuição de palavras na vertical
                int diagonalDirection = random.nextInt(2); // Determina a direção diagonal (0 para esquerda para direita, 1 para direita para esquerda)
                if (canPlaceWordAtPosition(word, startX, startY, horizontal, diagonalDirection)) {
                    distributeWord(word, startX, startY, horizontal, diagonalDirection);
                    placed = true;
                }
            }
        }
        private boolean canPlaceWordAtPosition(String word, int startX, int startY, boolean horizontal, int diagonalDirection) {
            int wordLength = word.length();
            if (horizontal) {
                if (startX + wordLength <= BOARD_SIZE) {
                    for (int j = 0; j < wordLength; j++) {
                        int col = startX + j;
                        int row = startY;
                        if (row >= BOARD_SIZE || col >= BOARD_SIZE || lettersGrid.get(row).get(col) != null) {
                            return false; // Há sobreposição com outra palavra ou excedeu os limites do tabuleiro
                        }
                    }
                    return true; // Não há sobreposição, a palavra pode ser colocada nesta posição
                }
            } else {
                if (startY + wordLength <= BOARD_SIZE) {
                    if (diagonalDirection == 0) {
                        for (int j = 0; j < wordLength; j++) {
                            int col = startX + j;
                            int row = startY + j;
                            if (row >= BOARD_SIZE || col >= BOARD_SIZE || lettersGrid.get(row).get(col) != null) {
                                return false; // Há sobreposição com outra palavra ou excedeu os limites do tabuleiro
                            }
                        }
                    } else {
                        for (int j = 0; j < wordLength; j++) {
                            int col = startX - j;
                            int row = startY + j;
                            if (row >= BOARD_SIZE || col < 0 || lettersGrid.get(row).get(col) != null) {
                                return false; // Há sobreposição com outra palavra ou excedeu os limites do tabuleiro
                            }
                        }
                    }
                    return true; // Não há sobreposição, a palavra pode ser colocada nesta posição
                }
            }
            return false; // A palavra não cabe na posição atual do tabuleiro
        }



        private void distributeWord(String word, int startX, int startY, boolean horizontal, int diagonalDirection) {
            int wordLength = word.length();
            if (horizontal) {
                for (int j = 0; j < wordLength; j++) {
                    int col = startX + j;
                    int row = startY;
                    lettersGrid.get(row).set(col, String.valueOf(word.charAt(j)));
                }
            } else {
                if (diagonalDirection == 0) {
                    for (int j = 0; j < wordLength; j++) {
                        int col = startX + j;
                        int row = startY + j;
                        lettersGrid.get(row).set(col, String.valueOf(word.charAt(j)));
                    }
                } else {
                    for (int j = 0; j < wordLength; j++) {
                        int col = startX - j;
                        int row = startY + j;
                        lettersGrid.get(row).set(col, String.valueOf(word.charAt(j)));
                    }
                }
            }
        }

        private void fillRemainingPositionsRandomly() {
            Random random = new Random();
            char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (lettersGrid.get(i).get(j) == null) {
                        lettersGrid.get(i).set(j, String.valueOf(alphabet[random.nextInt(26)]));
                    }
                }
            }
        }

        public int nLines() {
            return this.lettersGrid.size();
        }

        public int nCols() {
            return this.lettersGrid.get(0).size();
        }

        public void registerView(WSView wsView) {
            this.wsView = wsView;
        }

        public String textInPosition(Position position) {
            return this.lettersGrid.get(position.line()).get(position.col());
        }

        public boolean allWordsWereFound() {
            return foundWords.size() == words.size();
        }

        public String wordFound(String word) {
            if (words.contains(word)) {
                foundWords.add(word);
                int wordScore = calculateWordScore(word);
                return word + " = " + wordScore + " pontos";
            } else {
                return null;
            }
        }

        // Método para obter a mensagem de pontuação
        public String getScoreMessage() {
            int totalWords = words.size();
            int foundWordsCount = foundWords.size();
            double score = ((double) foundWordsCount / totalWords) * 100;
            return String.format("Words Found: %d/%d (%.2f%%)", foundWordsCount, totalWords, score);
        }


        public void writeScoreToFile() {
            String scoreMessage = getScoreMessage();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
                writer.write(scoreMessage + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isFirstAndLastOfWord(Position firstPosition, Position lastPosition) {
            int minRow = Math.min(firstPosition.line(), lastPosition.line());
            int maxRow = Math.max(firstPosition.line(), lastPosition.line());
            int minCol = Math.min(firstPosition.col(), lastPosition.col());
            int maxCol = Math.max(firstPosition.col(), lastPosition.col());

            // Verifica a palavra na horizontal
            if (minRow == maxRow) {
                StringBuilder selectedWord = new StringBuilder();
                for (int col = minCol; col <= maxCol; col++) {
                    selectedWord.append(lettersGrid.get(minRow).get(col));
                }
                String selectedWordStr = selectedWord.toString();
                System.out.println("Selected horizontal word: " + selectedWordStr);
                String foundWord = wordFound(selectedWordStr);
                if (foundWord != null) {
                    if (allWordsWereFound()) {
                        writeScoreToFile();
                        String scoreMessage = getScoreMessage();
                        wsView.update(new MessageToUI(List.of(), scoreMessage));
                    }
                    return true;
                }
            }

            // Verifica a palavra na vertical
            if (minCol == maxCol) {
                StringBuilder selectedWord = new StringBuilder();
                for (int row = minRow; row <= maxRow; row++) {
                    selectedWord.append(lettersGrid.get(row).get(minCol));
                }
                String selectedWordStr = selectedWord.toString();
                System.out.println("Selected vertical word: " + selectedWordStr);
                String foundWord = wordFound(selectedWordStr);
                if (foundWord != null) {
                    if (allWordsWereFound()) {
                        writeScoreToFile();
                        String scoreMessage = getScoreMessage();
                        wsView.update(new MessageToUI(List.of(), scoreMessage));
                    }
                    return true;
                }
            }

            // Verifica a palavra na diagonal (esquerda para a direita)
            StringBuilder selectedWordDiagonal1 = new StringBuilder();
            int rowDiagonal1 = minRow;
            int colDiagonal1 = minCol;
            while (rowDiagonal1 <= maxRow && colDiagonal1 <= maxCol) {
                selectedWordDiagonal1.append(lettersGrid.get(rowDiagonal1).get(colDiagonal1));
                rowDiagonal1++;
                colDiagonal1++;
            }
            String selectedWordStrDiagonal1 = selectedWordDiagonal1.toString();
            System.out.println("Selected diagonal word (left to right): " + selectedWordStrDiagonal1);
            String foundWordDiagonal1 = wordFound(selectedWordStrDiagonal1);
            if (foundWordDiagonal1 != null) {
                if (allWordsWereFound()) {
                    writeScoreToFile();
                    String scoreMessage = getScoreMessage();
                    wsView.update(new MessageToUI(List.of(), scoreMessage));
                }
                return true;
            }

            // Verifica a palavra na diagonal (direita para a esquerda)
            StringBuilder selectedWordDiagonal2 = new StringBuilder();
            int rowDiagonal2 = minRow;
            int colDiagonal2 = maxCol;
            while (rowDiagonal2 <= maxRow && colDiagonal2 >= minCol) {
                selectedWordDiagonal2.append(lettersGrid.get(rowDiagonal2).get(colDiagonal2));
                rowDiagonal2++;
                colDiagonal2--;
            }
            String selectedWordStrDiagonal2 = selectedWordDiagonal2.toString();
            System.out.println("Selected diagonal word (right to left): " + selectedWordStrDiagonal2);
            String foundWordDiagonal2 = wordFound(selectedWordStrDiagonal2);
            if (foundWordDiagonal2 != null) {
                if (allWordsWereFound()) {
                    writeScoreToFile();
                    String scoreMessage = getScoreMessage();
                    wsView.update(new MessageToUI(List.of(), scoreMessage));
                }
                return true;
            }

            return false;
        }


        public String wordWithWildcardFound(String word) {
            // Implementação do método para verificar se a palavra com coringa foi encontrada
            // Deve retornar a palavra encontrada ou null se não foi encontrada
            return null; // Modifique conforme necessário
        }

    }

